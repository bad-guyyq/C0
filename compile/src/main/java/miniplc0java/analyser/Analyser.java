package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.instruction.Function;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Pos;

import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.*;

public final class Analyser {
    Tokenizer tokenizer;
    ArrayList<Function> Func;
    ArrayList<String> GlobalSymbol;
    ArrayList<Function> stdlibFunc;
    //LinkedList<Integer> ActScope;//当前的作用域
    //ActScope.getFirst();//当前函数下标 ActScope.size()-1;当作用域层数 ActScope.get()得到标号
    /** 当前偷看的 token */
    Token peekedToken = null;
    /** 下一个变量的栈偏移 */
    int nextVariableOffset = 0;
    int nextGlobalOffset = 0;
    int nextParamOffset = 0;
    /**当前计算的式子的类型*/
    //当前正在编译的函数
    Function nowFunc;
    /** 全局符号表 */
    HashMap<String, SymbolEntry> symbolGlobalTable ;
    /** 当前block符号表*/
    HashMap<String, SymbolEntry> now_block_symbolTable;

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.Func= new ArrayList<>();
        this.GlobalSymbol = new ArrayList<>();
        this.stdlibFunc=new ArrayList<>();
        this.symbolGlobalTable = new HashMap<>();
    }
    //入口
    public ArrayList<Function> analyse() throws CompileError {
        analyseProgram();
        return Func;
    }

    /**程序分析*/
    private void analyseProgram() throws CompileError {
        // program -> decl_stmt* function*
        // 设置作用域，设置_start函数，定义全局变量,
        //ActScope.add(0);
        Function tempFunc = new Function();
        tempFunc.setFunction("_start",  0, 0);
        tempFunc.setlocSlots(0);
        Func.add(tempFunc);

        nowFunc=tempFunc;
        analyseGlobal();// 加入符号表,判读全局
        SymbolEntry func=addSymbol("_start", true,false, true,false, new Pos(0,0));
        func.setType("void");
        analyseFunction();
        // 'end'
        expect(TokenType.EOF);

        tempFunc=Func.get(0);
        tempFunc.setlocSlots(0);

        nowFunc=tempFunc;
        call_expr(null);
        GlobalSymbol.add("_start");
    }

    /**函数分析*/
    private void analyseFunction() throws CompileError{
        //decl_stmt -> let_decl_stmt | const_decl_stmt
        while(true){
            if (check(TokenType.Fn)) {
                function();
            }else{
                return;
            }
        }
    }
    private void function() throws CompileError{
        //function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
        //将函数的VariableOffset，重置当前作用域
        if(nextIf(TokenType.Fn)!=null){
//            if(ActScope.size()==1){
//                ActScope.set(0,ActScope.getFirst()+1);
//            }else{
//                throw new Error("ActScopeFuncSize");
//            }
            nextVariableOffset=0;
            nextParamOffset = 0;
        }else{
            throw new Error("function no fn");
        }

        //new一个函数，注意要最后才加入计数完局部变量之后才将函数加入符号表和函数数组
        nowFunc=new Function();
        //压入第一个符号表
        now_block_symbolTable=new HashMap<>();
        nowFunc.funcSymbolStack.add(now_block_symbolTable);

        //函数名
        Token nameToken = expect(TokenType.IDENT);
        String name = (String) nameToken.getValue();
        //函数参数
        fun_param();
        //函数返回值类型,准备放在函数变量的表中
        expect(TokenType.ARROW);
        Token typeToken = expect(TokenType.IDENT);
        String type = (String) typeToken.getValue();
        //声明部分先把函数定义好，为了循环调用先加上，最后再设置局部变量的数量。因为虚拟机自动分配局部变量空间不用担心
        if(SymbolEntry.checkType(type)==TokenType.VOID_LITERAL){
            nowFunc.setFunction(name,nextParamOffset,0);
        }else{
            nowFunc.setFunction(name,nextParamOffset,1);
        }
        // 加入符号表
        SymbolEntry func=addSymbol(name, true,false, true,false, nameToken.getStartPos());
        if(!func.setType(type)){
            throw new Error("TypeWrong");
        };

        Func.add(nowFunc);
        //函数块加载
        analyse_func_block();

        //将函数名最后加入全局表
        GlobalSymbol.add(name);
        //局部变量设置
        nowFunc.setlocSlots(nextVariableOffset);
    }

    /**块区域初始化，设置符号表*/
    private void analyse_func_block()throws CompileError{
        //是函数块不用新建符号表，符号表就是function()初始化参数过了的now_block_symbolTable
        analyse_block_stmt(true);
    }
    private void analyse_block_stmt(boolean isFunction)throws CompileError{
        //跳转需要记录添加指令的数量来确认off
        if(isFunction){//直接分析块
            block_stmt();
        }else{//不是函数，则压入并设置新的符号表
            now_block_symbolTable = new HashMap<>();
            nowFunc.funcSymbolStack.add(now_block_symbolTable);
            block_stmt();
            //块分析完后，删除符号表，到达上一个符号表
            nowFunc.funcSymbolStack.removeLast();
            now_block_symbolTable=nowFunc.funcSymbolStack.getLast();
        }
    }
    private void block_stmt()throws CompileError{
        expect(TokenType.L_BRACE);
        while(!check(TokenType.R_BRACE)){
            analyse_stmt();
        }
        expect(TokenType.R_BRACE);
    }

    /**语句分析自带；*/
    private void analyse_stmt()throws CompileError {
        if (check(TokenType.Let)||check(TokenType.Const)) {
            decl_stmt();
            return;
        }else if(check(TokenType.If)){
            if_stmt();
            return;
        }else if(check(TokenType.While)){
            while_stmt();
            return;
        }else if(check(TokenType.SEMICOLON)){
            empty_stmt();
        }else if(check(TokenType.Return)){
            return_stmt();
        }else if(check(TokenType.Break)){
            break_stmt();
        }else if(check(TokenType.Continue)){
            continue_stmt();
        }else{
            expr_stmt();
        }
        expect(TokenType.SEMICOLON);
    }

    //赋值语句
    private void decl_stmt()throws CompileError {
        //decl_stmt -> let_decl_stmt | const_decl_stmt
        while(true){
            if (check(TokenType.Let)) {
                let_decl_stmt();
                expect(TokenType.SEMICOLON);
            }else if(check(TokenType.Const)){
                const_decl_stmt();
                expect(TokenType.SEMICOLON);
            }else{
                return;
            }
        }
    }
    private void let_decl_stmt() throws CompileError{
        //let_decl_stmt -> 'let' IDENT ':' ty ('=' expr)? ';'
        if (nextIf(TokenType.Let) != null) {
            // 变量名
            Token nameToken = expect(TokenType.IDENT);
            String name = (String) nameToken.getValue();
            // *:号
            expect(TokenType.COLON);
            //*ty 设置变量类型
            Token typeToken = expect(TokenType.IDENT);
            String type = (String) typeToken.getValue();

            // 变量初始化了吗
            boolean isInitialized = false;
            // 下个 token 是等于号吗？如果是的话分析初始化
            if(nextIf(TokenType.ASSIGN)!=null){
                isInitialized=true;
            }
            // 加入符号表，请填写名字和当前位置（报错用）
            //是否初始化这里计入初始值算不算初始化过了
            //String name,boolean isFunction,  boolean isConstant, boolean isInitialized,boolean ipParam, Pos curPos
            SymbolEntry Variable=addSymbol(name, false, false,isInitialized,false,/* 当前位置 */ nameToken.getStartPos());
            if(!Variable.setType(type)){
                throw new Error("TypeWrong");
            }
            // 分析初始化的表达式
            if(isInitialized){
                if(Variable.isGlobal){
                    addInstruction(new Instruction(Operation.globa, Variable.getStackOffset()));
                }else{
                    addInstruction(new Instruction(Operation.loca, Variable.getStackOffset()));
                }
                //count_expr();有可能是函数
                TokenType rType=count_expr(null);
                if(!rType.equals(Variable.Type)){
                    throw new Error("let type wrong");
                }
                addInstruction(new Instruction(Operation.store64));
            }
        }
    }
    private void const_decl_stmt() throws CompileError{
        // const_decl_stmt -> 'const' IDENT ':' ty '=' expr ';'
        // 如果下一个 token 是 const 就继续
        if (nextIf(TokenType.Const) != null) {
            // 变量名
            Token nameToken = expect(TokenType.IDENT);

            // 加入符号表,加入时会判读全局
            String name = (String) nameToken.getValue();
            SymbolEntry Constant=addSymbol(name, false,true, true, false,nameToken.getStartPos());
            // *:号
            expect(TokenType.COLON);

            //*ty 设置变量类型
            Token typeToken = expect(TokenType.IDENT);
            String type = (String) typeToken.getValue();
            if(!Constant.setType(type)){
                throw new Error("TypeWrong");
            };
            // 常表达式  *这里之后改成analyseExpr
            if(Constant.isGlobal){
                addInstruction(new Instruction(Operation.globa, Constant.getStackOffset()));
            }else{
                addInstruction(new Instruction(Operation.loca, Constant.getStackOffset()));
            }
            //count_expr();有可能是函数
            TokenType rType=count_expr(null);
            if(!type.equals(rType)){
                throw new Error("let type wrong");
            }
            // *;全改到分析语句中有了
            //expect(TokenType.SEMICOLON);

            //栈顶现在为 变量地址 表达式值
            addInstruction(new Instruction(Operation.store64));
        }
    }

    private void if_stmt()throws CompileError {
        boolean isElse=false;
        if(check(TokenType.L_BRACE)){
            isElse=true;
        }else{
            expect(TokenType.If);
        }
        int boolOff=nowFunc.Body.size();//判断开始指令位置
        if(!isElse){//
            bool_expr();
        }
        addInstruction(new Instruction(Operation.brtrue,1));//满足条件越过下一条无条件跳转指令
        int brOff=nowFunc.Body.size();//跳出该if
        addInstruction(new Instruction(Operation.br));//值之后根据下标改

        analyse_block_stmt(false);//指令块分析

        //在当前block块后加一条跳出所有if的指令
        int backOff=nowFunc.Body.size();
        addInstruction(new Instruction(Operation.br));//值之后根据之后所有if分析完后改

        nowFunc.Body.set(brOff,new Instruction(Operation.br,backOff-brOff));//修改跳出该if
        if(nextIf(TokenType.Else)!=null){
            if_stmt();
        }
        //所有都分析完了修改跳出所有修换的值
        int nowOff=nowFunc.Body.size()-1;
        nowFunc.Body.set(backOff,new Instruction(Operation.br,nowOff-backOff));//修改跳出该if
    }
    private void while_stmt()throws CompileError {
        expect(TokenType.While);
        int boolOff=nowFunc.Body.size();//判断开始指令位置
        bool_expr();
        addInstruction(new Instruction(Operation.brtrue,1));//满足条件越过下一条无条件跳转指令
        int brOff=nowFunc.Body.size();//跳出循环指令位
        addInstruction(new Instruction(Operation.br));//值之后根据下标改
        analyse_block_stmt(false);//指令块分析
        int backoff=nowFunc.Body.size();
        addInstruction(new Instruction(Operation.br,-(backoff-boolOff+1)));//跳回判断语句
        nowFunc.Body.set(brOff,new Instruction(Operation.br,backoff-brOff));
    }
    private void return_stmt()throws CompileError {
        next();
        Token nameToken=peek();//next改成peek
        SymbolEntry funSymbol=foundSymbolByName(nowFunc.fucName);
        if(nameToken.getTokenType()==TokenType.SEMICOLON){//return;
            if(funSymbol.Type!=TokenType.VOID_LITERAL){
                throw new Error("noRetValue");
            }
        }else {
            addInstruction(new Instruction(Operation.arga,0));//返回值栈地址
            TokenType retType=count_expr(null);//返回值计算
            if(!retType.equals(funSymbol.Type)){
                throw new Error("retTypeWrong");
            }else{
                addInstruction(new Instruction(Operation.store64));//返回值存储
            }
        }
        addInstruction(new Instruction(Operation.ret));//返回
    }
    private void empty_stmt()throws CompileError {
        return;
    }
    private void break_stmt()throws CompileError {
        next();
    }
    private void continue_stmt()throws CompileError {
        next();
    }

    /**表达式语句*/
    private void expr_stmt()throws CompileError {//没有操作的计算需要popN再说
        TokenType retType = null;
        if (check(TokenType.IDENT)) {
            //先偷看变量
            Token nameToken = peek();
            String name = (String) nameToken.getValue();
            SymbolEntry nameSymbol=foundSymbolByName(name);
            next();
            if(check(TokenType.ASSIGN)){
                assign_expr(nameToken);
            }else{
                retType=count_expr(nameToken);
            }
        }else{//字符串|运算表达式|字符|变量常量;
            if(check(TokenType.STRING_LITERAL)){
                string_expr();
            }else{
                retType=count_expr(null);
            }
        }
        //return retType;
    }
    /**赋值表达式*/
    private void assign_expr(Token nameToken)throws CompileError {
        //assign_expr -> l_expr '=' expr  =右边可能是函数或者表达式还可能会有as
        SymbolEntry nameSymbol=changeVariable(nameToken);
        TokenType lExprType=nameSymbol.Type;
        expect(TokenType.ASSIGN);
        //将右边赋值给左边，同时检查类型
        TokenType rExprType=count_expr(null);;
        if(lExprType.equals(rExprType)){
            addInstruction(new Instruction(Operation.store64));
        }else{
            throw new Error("typeWrong");
        }
        //最后设置左边初始化
        nameSymbol.isInitialized=true;
    }

    /**标准函数调用,调用成功返回true
     * @param nameToken*/
    private void stdlib(Token nameToken)throws CompileError{
        String funcName = (String) nameToken.getValue();
        SymbolEntry nameSymbol=foundSymbolByName(funcName);
        //先看全局符号表里有没有加上该标准函数
        if(symbolGlobalTable.get(funcName)==null){//没有则加入全局中
            GlobalSymbol.add(funcName);
            stdlibFunc.add(add_stdlib(funcName));
        }
        //SymbolEntry funcSymbol=this.symbolGlobalTable.get(funcName);
        call_stdlib(funcName);
    }
    private Function add_stdlib(String stdlibName)throws CompileError {
        Function retFunc=new Function();
        SymbolEntry temp;
        temp=new SymbolEntry(true, true, false,false,getNextGlobalOffset());
        temp.setGlobal(true);
        switch (stdlibName){
            case "getint" :
                temp.setType("int");
                retFunc.setFunction(stdlibName,0,1);
                break;
            case "getdouble" :
                temp.setType("double");
                retFunc.setFunction(stdlibName,0,1);
                break;
            case "getchar" :
                temp.setType("char");
                retFunc.setFunction(stdlibName,0,1);
                break;
            case "putint" :
                retFunc.setFunction(stdlibName,1,0);
                retFunc.paramType.add(TokenType.UINT_LITERAL);
                break;
            case "putdouble" :
                retFunc.setFunction(stdlibName,0,1);
                break;
            case "putchar" :
                retFunc.setFunction(stdlibName,1,0);
                retFunc.paramType.add(TokenType.CHAR_LITERAL);
                break;
            case "putstr" :
                retFunc.setFunction(stdlibName,1,0);
                retFunc.paramType.add(TokenType.STRING_LITERAL);
                break;
            case "putln" :
                retFunc.setFunction(stdlibName,0,0);
                break;
            default:
                return null;
        }
        this.symbolGlobalTable.put(stdlibName,temp);
        return retFunc;
    }
    private void call_stdlib(String name)throws CompileError {
        //call_expr -> IDENT '(' call_param_list? ')'
        //会调用call说明func一定在符号表中所以这里就不用检查了
        SymbolEntry stdlibSymbol=foundSymbolByName(name);
        int funcOff=foundStdlibOffByName(name);
        Function callFunc=stdlibFunc.get(funcOff);
        //设置返回值栈
        if(callFunc.retSlots!=0){
            addInstruction(new Instruction(Operation.stackalloc,callFunc.retSlots));
        }
        call_stdlib_list(funcOff);
        int stdlib=foundGlobalByName(name);
        //标准函数用callname
        addInstruction(new Instruction(Operation.callname,stdlib));
    }
    private void call_stdlib_list(int funcOff)throws CompileError{
        /**call_param_list -> expr (',' expr)*/
        expect(TokenType.L_PAREN);
        if(stdlibFunc.get(funcOff).paramSlots==1){
            TokenType paramType=count_expr(null);
            //参数类型要一致,char参数可能为数字形式
            if (stdlibFunc.get(funcOff).paramType.get(0).equals(TokenType.CHAR_LITERAL) && paramType.equals(TokenType.UINT_LITERAL)) {
                ;
            }else if(!stdlibFunc.get(funcOff).paramType.get(0).equals(paramType)){
                throw new Error("paramTypeWrong");
            }
        }
        expect(TokenType.R_PAREN);
    }


    /**调用函数*/
    private void call_expr(Token ifNext)throws CompileError {
        //call_expr -> IDENT '(' call_param_list? ')'
        Token nameToken;
        String name;
        if(nowFunc.fucName.equals("_start")){
            name= "main";
        }else{
            //函数名被预读过。
            if (ifNext!=null){
                nameToken=ifNext;
            }else{
                nameToken=next();
            }
            name = (String) nameToken.getValue();
        }
        //会调用call说明func一定在符号表中所以这里就不用检查了
        SymbolEntry funcSymbol=foundSymbolByName(name);
        int funcOff=foundFuncOffByName(name);
        Function callFunc=Func.get(funcOff);
        //设置返回值栈
        if(callFunc.retSlots!=0){
            addInstruction(new Instruction(Operation.stackalloc,callFunc.retSlots));
        }
        if(!nowFunc.fucName.equals("_start")){
            call_param_list(funcOff);
        }

        //虽然函数加入全局变量的顺序为函数体结束，但是Func的顺序为定义顺序
        addInstruction(new Instruction(Operation.call,funcOff));
    }
    private void call_param_list(int funcOff)throws CompileError{
        /**call_param_list -> expr (',' expr)*/
        expect(TokenType.L_PAREN);
        if(Func.get(funcOff).paramType.size()!=0){
            int paramOff=0;
            call_param(funcOff,paramOff++);
            while (true) {
                // 参数名,
                Token op = peek();
                //没有逗号隔开
                if (op.getTokenType() != TokenType.COMMA) {
                    break;
                }
                // 运算符
                next();
                call_param(funcOff,paramOff++);
            }
            if(paramOff!=Func.get(funcOff).paramType.size()){
                throw new Error("paramNumberWrong");
            }
        }
        expect(TokenType.R_PAREN);
    }
    private void call_param(int funcOff,int paramOff) throws CompileError{
        //参数类型要一致
        TokenType paramType=count_expr(null);
        if(!Func.get(funcOff).paramType.get(paramOff).equals(paramType)){
            throw new Error("paramTypeWrong");
        }
    }

    //还未实现
    /*private void as_expr(Token nameToken)throws CompileError {
        //call_expr -> IDENT '(' call_param_list? ')'
        next();
    }*/
    private void string_expr()throws CompileError {
        //call_expr -> IDENT '(' call_param_list? ')'
        GlobalSymbol.add(next().getValue().toString());
    }

    /**运算与比较表达式，只会得到结果*/
    /**布尔比较式,分析表示式是否为真，为假时为0，结果放在栈顶。
     //是否为浮点数之后实现
     //    EQ,        //-> '=='
     //    NEQ,       //-> '!='
     //    LT,        //-> '<'
     //    GT,        //-> '>'
     //    LE,        //-> '<='
     //    GE,        //-> '>='*/
    private void bool_expr() throws CompileError{
        //expect(TokenType.L_PAREN);
        TokenType leftType=count_expr(null);
        Token nameToken=next();
        TokenType bool=nameToken.getTokenType();
        TokenType rightType=count_expr(null);

        if(!leftType.equals(rightType)){
            throw new Error("boolTypeWrong");
        }
        //if与while都是为真时不跳为真时跳，但是指令的跳转位置需要分析
        if(leftType.equals(TokenType.DOUBLE_LITERAL)){
            addInstruction(new Instruction(Operation.cmpf));
        }else{
            addInstruction(new Instruction(Operation.cmpi));
        }
        if(bool==TokenType.EQ){ //相等时为0，取反为真
            addInstruction(new Instruction(Operation.not));
        }else if(bool==TokenType.NEQ){//不相等时不为0，取反为真
            addInstruction(new Instruction(Operation.not));
        }else if(bool==TokenType.LT){//< 为真时得到-1
            addInstruction(new Instruction(Operation.setlt));//如果 lhs < 0 则推入 1，否则 0
        }else if(bool==TokenType.NEQ){//> 为真时得到1
            addInstruction(new Instruction(Operation.setgt));
        }else if(bool==TokenType.LE){//<= 为真时为-1或0，为假时为1
            addInstruction(new Instruction(Operation.setgt));//>0时为1
            addInstruction(new Instruction(Operation.not));//取反
        }else if(bool==TokenType.GE){//>= 为真时为0，1，为假时为-1
            addInstruction(new Instruction(Operation.setlt));//<0时为1
            addInstruction(new Instruction(Operation.not));//取反
        }else{
            throw new Error("*/error");
        }
    }
    //*表达式->项(+/-项)*
    private TokenType count_expr(Token ifNext) throws CompileError{
        //operator_expr -> expr binary_operator expr
        // 表达式 -> 项 (加法运算符 项)*
        // 项
        TokenType leftType=count_expr_Item(ifNext);
        while (true) {
            // 预读可能是运算符的 token
            Token op = peek();
            if (op.getTokenType() != TokenType.PLUS && op.getTokenType() != TokenType.MINUS) {
                break;
            }
            // 运算符
            next();
            // 项
            TokenType rightType=count_expr_Item(ifNext);
            if (!leftType.equals(rightType)) {
                throw new Error("countTypeWrong");
            }
            // 生成代码
            if (op.getTokenType() == TokenType.PLUS) {
                if(leftType.equals(TokenType.DOUBLE_LITERAL)){
                    addInstruction(new Instruction(Operation.addf));
                }else{
                    addInstruction(new Instruction(Operation.addi));
                }
            } else if (op.getTokenType() == TokenType.MINUS) {
                if(leftType.equals(TokenType.DOUBLE_LITERAL)){
                    addInstruction(new Instruction(Operation.subf));
                }else{
                    addInstruction(new Instruction(Operation.subi));
                }
            }else{
                throw new Error("*/error");
            }
        }
        if(nextIf(TokenType.As)!=null){
            TokenType changeType=SymbolEntry.checkType((String)next().getValue());
            if(changeType.equals(leftType)){
                ;
            }else{
                if(changeType.equals(TokenType.UINT_LITERAL)&&leftType.equals(TokenType.DOUBLE_LITERAL)){
                    addInstruction(new Instruction(Operation.ftoi));
                }else if(leftType.equals(TokenType.UINT_LITERAL)&&changeType.equals(TokenType.DOUBLE_LITERAL)){
                    addInstruction(new Instruction(Operation.itof));
                }else{
                    throw new Error("as Type can't match");
                }
            }
            leftType=changeType;
        }
        return leftType;
    }
    //*项->因子(*//因子)*
    private TokenType count_expr_Item(Token ifNext) throws CompileError{
        // 项 -> 因子 (乘法运算符 因子)*
        // 因子
        TokenType leftType=count_expr_Factor(ifNext);
        while (true) {
            // 预读可能是运算符的 token
            Token op = peek();
            if (op.getTokenType()!= TokenType.MINUS &&op.getTokenType()!= TokenType.DIV){
                break;
            }
            // 运算符
            next();
            // 因子
            TokenType rightType=count_expr_Factor(ifNext);
            // 生成代码
            if (!leftType.equals(rightType)) {
                throw new Error("countTypeWrong");
            }
            if (op.getTokenType() == TokenType.MINUS) {
                if(leftType.equals(TokenType.DOUBLE_LITERAL)){
                    addInstruction(new Instruction(Operation.mulf));
                }else{
                    addInstruction(new Instruction(Operation.muli));
                }
            } else if (op.getTokenType() == TokenType.DIV) {
                if(leftType.equals(TokenType.DOUBLE_LITERAL)){
                    addInstruction(new Instruction(Operation.divf));
                }else{
                    addInstruction(new Instruction(Operation.divi));
                }
            }else{
                throw new Error("*/error");
            }
        }
        return leftType;
    }
    //*因子->符号?(标识符|无符号整数|'(' 表达式 ')')
    private TokenType count_expr_Factor(Token ifNext) throws CompileError{
        boolean negate= false;
        TokenType type=null;
        SymbolEntry nameSymbol;
        Token nameToken;
        //由于预判可能将第一个计算已读，所以有两种情况，
        if (ifNext!=null){
            nameToken=ifNext;
        }else{
            nameToken=next();
        }
        //取反
        if (nameToken.getTokenType()==TokenType.MINUS) {
            negate = true;
            next();
            // 计算结果出来后加上neg指令
        }else if(nameToken.getTokenType()==TokenType.PLUS){
            //什么都不做
            next();
        }
        // 是标识符,函数或者变量，由于要判断
        // 加载标识符的值,返回标识符符号
        if (nameToken.getTokenType()==TokenType.IDENT) {
            String name=(String)nameToken.getValue();
            if(isStdlib(name)){
                stdlib(nameToken);
                nameSymbol=symbolGlobalTable.get(name);
            }else {
                nameSymbol=foundSymbolByName(name);
                if(nameSymbol==null){
                    throw new Error("foundSymbolByNameFalse");
                }else if(nameSymbol.isFunction){
                    call_expr(nameToken);
                }else{
                    nameSymbol=loadVariable(nameToken);
                }
            }
            type=nameSymbol.Type;
            if(nameSymbol==null){
                throw new Error("foundSymbolByNameFalse");
            }
        } else if (nameToken.getTokenType()==TokenType.UINT_LITERAL) {
            // 是整数
            // 加载整数值
            Object value = nameToken.getValue();
            value.toString();
            addInstruction(new Instruction(Operation.push, value));
            type=TokenType.UINT_LITERAL;
        } else if (nameToken.getTokenType()==TokenType.DOUBLE_LITERAL) {
            // 是浮点数
            // 加载整数值
            Double value = (Double)nameToken.getValue();
            addInstruction(new Instruction(Operation.push, value));
            type=TokenType.DOUBLE_LITERAL;
        } else if (nameToken.getTokenType()==TokenType.L_PAREN) {// 是(表达式)
            // 调用相应的处理函数
            type=count_expr(null);
            expect(TokenType.R_PAREN);
        } else { // 都不是，摸了
            //throw new ExpectedTokenError(List.of(TokenType.IDENT, TokenType.UINT_LITERAL, TokenType.L_PAREN), next());
            throw new Error("List.of(no token)");
        }
        if (negate) {
            if(type.equals(TokenType.DOUBLE_LITERAL)){
                addInstruction(new Instruction(Operation.negf));
            }
            else{
                addInstruction(new Instruction(Operation.negi));
            }
        }
        return type;
    }


    /**函数参数分析*/
    private void fun_param()throws CompileError{
        /*function_param_list -> function_param (',' function_param)*
        function_param -> 'const'? IDENT ':' ty
        设置作用域*/
        /*if(ActScope.size()==1){
            ActScope.add(0);
        }else{
            throw new Error("ActScopeFuncSize");
        }*/
        expect(TokenType.L_PAREN);
        if(nextIf(TokenType.R_PAREN)!=null){
            return;
        }
        param();
        while (true) {
            // 参数名,
            Token op = peek();
            //没有逗号隔开
            if (op.getTokenType() != TokenType.COMMA) {
                break;
            }
            // 运算符
            next();
            if(nextIf(TokenType.R_PAREN)!=null){
                return;
            }
            param();
        }
        expect(TokenType.R_PAREN);
    }
    private void param() throws CompileError{
        // IDENT ':' ty
        // 变量名
        boolean isConstant=false;
        if(nextIf(TokenType.Const)!=null){
            isConstant=true;
        }
        Token nameToken = expect(TokenType.IDENT);

        // 加入符号表,加入时会判读全局
        String name = (String) nameToken.getValue();
        SymbolEntry Constant=addSymbol(name, false,isConstant, true, true,nameToken.getStartPos());
        // *:号
        expect(TokenType.COLON);

        //*ty 设置变量类型
        Token typeToken = expect(TokenType.IDENT);
        String type = (String) typeToken.getValue();
        if(!Constant.setType(type)){
            throw new Error("TypeWrong");
        };
        nowFunc.paramType.add(Constant.Type);
    }

    /**全局变量分析*/
    private void analyseGlobal() throws CompileError{
        //decl_stmt -> let_decl_stmt | const_decl_stmt
        now_block_symbolTable=symbolGlobalTable;
        decl_stmt();
    }


    //工具方法
    /**函数里加指令*/
    private void addInstruction(Instruction temp)throws AnalyzeError{
        nowFunc.Body.add(temp);
    }
    /**是否是默认函数，是的话处理返回true，不是直接返回false*/
    private boolean isStdlib(String name)throws AnalyzeError{
        switch (name){
            case "getint" :
            case "getdouble" :
            case "getchar" :
            case "putint" :
            case "putdouble" :
            case "putchar" :
            case "putstr" :
            case "putln" :
                return true;
            default:
                return false;
        }
    }
    /**函数名找函数下标*/
    private int foundFuncOffByName(String funcName){
        int off=0;
        while(off<Func.size()){
            if(Func.get(off++).fucName.equals(funcName)){
                return off-1;
            }
        }
        return -1;
    }
    /**标准函数名找函数下标*/
    private int foundStdlibOffByName(String funcName){
        int off=0;
        while(off<stdlibFunc.size()){
            if(stdlibFunc.get(off++).fucName.equals(funcName)){
                return off-1;
            }
        }
        return -1;
    }

    /**全局找下标*/
    private int foundGlobalByName(String name){
        int off=0;
        while(off<GlobalSymbol.size()){
            if(GlobalSymbol.get(off++).equals(name)){
                return off-1;
            }
        }
        return -1;
    }
    /**查找符号表*/
    private SymbolEntry foundSymbolByName(String name)throws AnalyzeError{
        SymbolEntry symbol = now_block_symbolTable.get(name);
        //直接找到
        if(symbol!=null){
            return symbol;
        }
        //临时符号表
        HashMap<String, SymbolEntry> tempFoundBlockSymbolTable;
        //
        int offSymbolTable=nowFunc.funcSymbolStack.size()-1;
        // 当前符号表没有这个标识符,往上找
        while(symbol == null&&offSymbolTable>0) {
            offSymbolTable--;
            tempFoundBlockSymbolTable=nowFunc.funcSymbolStack.get(offSymbolTable);
            symbol=tempFoundBlockSymbolTable.get(name);
        }
        //还没找到去全局里面找
        if(symbol==null){
            symbol=symbolGlobalTable.get(name);
        }
        return symbol;
    }
    /**由ActScope查找符号表在符号表序列中的下标
     private int getTableOffset(LinkedList<Integer> ActScope)throws AnalyzeError{
     //ActScope
     int result=ActScope.get(1);
     return 0;
     }*/

    /**加载变量也就是先找到变量的地址然后把变量的值压到栈顶*/
    private SymbolEntry loadVariable(Token nameToken) throws AnalyzeError {
        String name = (String) nameToken.getValue();/* 快填 */
        SymbolEntry symbol = foundSymbolByName(name);
        if (symbol == null) {
            // 没有这个标识符
            throw new AnalyzeError(ErrorCode.NotDeclared, /* 当前位置 */ nameToken.getStartPos());
        } else if (!symbol.isInitialized) {
            // 标识符没初始化
            throw new AnalyzeError(ErrorCode.NotInitialized, /* 当前位置 */ nameToken.getStartPos());
        }
        //变量的位置
        int offset = getOffset(name, nameToken.getStartPos());
        if (symbol.isGlobal){
            addInstruction(new Instruction(Operation.globa, offset));
        }else if(symbol.isParam){
            addInstruction(new Instruction(Operation.arga, offset));
        }else{
            addInstruction(new Instruction(Operation.loca, offset));
        }
        addInstruction(new Instruction(Operation.load64));
        return symbol;
    }
    /**加载变量地址找到变量的地址并压入栈顶*/
    private SymbolEntry changeVariable(Token nameToken) throws AnalyzeError {
        String name = (String) nameToken.getValue();/* 快填 */
        SymbolEntry symbol = foundSymbolByName(name);
        if (symbol == null) {
            // 没有这个标识符
            throw new AnalyzeError(ErrorCode.NotDeclared, /* 当前位置 */ nameToken.getStartPos());
        }else if (symbol.isConstant) {
            // 变量为常量不能改
            throw new AnalyzeError(ErrorCode.AssignToConstant, /* 当前位置 */ nameToken.getStartPos());
        }else{
            //变量的位置
            int offset = getOffset(name, nameToken.getStartPos());
            if (symbol.isGlobal){
                addInstruction(new Instruction(Operation.globa, offset));
            }else if(symbol.isParam){
                addInstruction(new Instruction(Operation.arga, offset));
            }else{
                addInstruction(new Instruction(Operation.loca, offset));
            }
        }
        return symbol;
    }

    /** 添加符号表*/
    private SymbolEntry addSymbol(String name,boolean isFunction,  boolean isConstant, boolean isInitialized,boolean ipParam, Pos curPos) throws AnalyzeError {
        //全局变量
        if(now_block_symbolTable==symbolGlobalTable||isFunction){
            if (this.symbolGlobalTable.get(name) != null) {//重定义
                throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
            } else {
                SymbolEntry temp;
                if(!isFunction){//函数会在最后结束时加入全局表
                    GlobalSymbol.add(name);
                }
                temp=new SymbolEntry(isFunction, isConstant, isInitialized,ipParam,getNextGlobalOffset());
                temp.setGlobal(true);
                //temp.setActScope(this.ActScope);
                this.symbolGlobalTable.put(name,temp);
                return temp;
            }
        }else{//非全局
            return addBlockSymbol(name,isFunction, isConstant, isInitialized,ipParam,curPos);
        }

    }
    //Boolean isFunction,boolean isConstant, boolean isInitialized, boolean isParam,int stackOffset
    /**添加一个符号，非全局，设置作用域不用了，可以在函数符号表列里找*/
    private SymbolEntry addBlockSymbol(String name,boolean isFunction,  boolean isConstant,boolean isInitialized,boolean ipParam, Pos curPos) throws AnalyzeError {
        if (now_block_symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            SymbolEntry temp;
            int offset;
            if(ipParam){
                offset=getNextParamOffset();
            }else{
                offset= getNextVariableOffset();
            }
            temp=new SymbolEntry(isFunction, isConstant, isInitialized,ipParam,offset);
            //temp.setGlobal(false);默认是false
            //temp.setActScope(this.ActScope);
            now_block_symbolTable.put(name,temp);
            return temp;
        }
    }

    /**设置下一个变量的栈偏移*/
    private int getNextVariableOffset() {
        return this.nextVariableOffset++;
    }
    private int getNextGlobalOffset() {
        return this.nextGlobalOffset++;
    }
    private int getNextParamOffset() {
        return this.nextParamOffset++;
    }

    /**符号属性获取与设置方法*/
    /**获取变量在栈上的偏移*/
    private int getOffset(String name, Pos curPos) throws AnalyzeError {
        SymbolEntry nameSymbol=foundSymbolByName(name);
        if (nameSymbol== null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return nameSymbol.getStackOffset();
        }
    }
    /**
     * 获取变量是否是常量
     *
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 是否为常量
     * @throws AnalyzeError
     */
    private boolean isConstant(String name, Pos curPos) throws AnalyzeError {
        SymbolEntry entry = this.symbolGlobalTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.isConstant();
        }
    }
    /**
     * 设置符号为已赋值
     *
     * @param name   符号名称
     * @param curPos 当前位置（报错用）
     * @throws AnalyzeError 如果未定义则抛异常
     */
    private void initializeSymbol(String name, Pos curPos) throws AnalyzeError {
        SymbolEntry entry = this.symbolGlobalTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            entry.setInitialized(true);
        }
    }

    /**Token工具方法*/
    /**
     * 查看下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }
    /**
     * 获取下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            Token token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }
    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     *
     * @param tt
     * @return
     * @throws TokenizeError
     */
    private boolean check(TokenType tt) throws TokenizeError {
        Token token = peek();
        return token.getTokenType() == tt;
    }
    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     *
     * @param tt 类型
     * @return 如果匹配则返回这个 token，否则返回 null
     * @throws TokenizeError
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        Token token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }
    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     *
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        Token token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    //语法分析输出
    public String toAnalyserString() {
        StringBuffer output=new StringBuffer();
        for(int i=0;i<GlobalSymbol.size();i++){
            String global=GlobalSymbol.get(i);
            output.append(String.format("static: "));
            for(int j=0;j<global.length();j++){
                output.append(String.format("%d ",(int)global.charAt(j)));
            }
            output.append(String.format("('%s')\n",global));
        }

        Iterator<Function> it = Func.iterator();
        Function funIter;
        while(it.hasNext()){
            funIter=it.next();
            output.append(String.format("fn [%d] %d %d -> %d {\n",foundGlobalByName(funIter.fucName),funIter.locSlots,funIter.paramSlots,funIter.retSlots));
            output.append(String.format(funIter.toString()));
            output.append("}\n");
        }
        return output.toString();
    }
    //O0输出
    public void toO0(PrintStream outPrint){
        //output.println
        ArrayList<Byte> output=new ArrayList<>();
        outPrint.printf("%02x %02x %02x %02x\n",0x72,0x30,0x3b,0x3e);
        outPrint.printf("%02x %02x %02x %02x\n",0x00,0x00,0x00,0x01);

//        output.add((byte) 0x72);output.add((byte) 0x30);output.add((byte) 0x3b);output.add((byte) 0x3e);//magic
//        outPrint.println(output);
//        output.clear();
//        output.add((byte) 0x72);output.add((byte) 0x30);output.add((byte) 0x3b);output.add((byte) 0x3e);//magic
//        outPrint.println(output);
//        output.clear();

        // globals.count

        for(byte it:Instruction.intToByte4B(GlobalSymbol.size())){
            outPrint.printf("%02x ",it);
        }
        outPrint.println();

        for(int i=0;i<GlobalSymbol.size();i++){
            String global=GlobalSymbol.get(i);
            SymbolEntry globalSymbolByte=symbolGlobalTable.get(global);
            //is_const
            for(byte it:Instruction.intToByte1B(i)){
                outPrint.printf("%02x ",it);
            }
            outPrint.println();

            //为字符串
            if(globalSymbolByte==null||globalSymbolByte.isFunction){
                //value.count
                for(byte it:Instruction.intToByte4B(global.length())){
                    outPrint.printf("%02x ",it);
                }
                outPrint.println();
                //value.items
                for(int j=0;j<global.length();j++){
                    outPrint.printf("%c ",global.charAt(j));
                }
                outPrint.println();
            }else{
                //value.count
                for(byte it:Instruction.intToByte4B(8)){
                    outPrint.printf("%02x ",it);
                }
                outPrint.println();
                //value.items
                for(int j=0;j<8;j++){
                    outPrint.printf("%02x ",0x00);
                }
                outPrint.println();
            }
        }

        // functions.count
        for(byte it:Instruction.intToByte4B(Func.size())){
            outPrint.printf("%02x ",it);
        }
        outPrint.println();

        Iterator<Function> it = Func.iterator();
        Function funIter;
        while(it.hasNext()){
            funIter=it.next();
            // functions.name ret_slots param_slots loc_slots
            for(byte item:Instruction.intToByte4B(foundGlobalByName(funIter.fucName))){
                outPrint.printf("%02x ",item);
            }
            outPrint.println();
            for(byte item:Instruction.intToByte4B(funIter.locSlots)){
                outPrint.printf("%02x ",item);
            }
            outPrint.println();
            for(byte item:Instruction.intToByte4B(funIter.paramSlots)){
                outPrint.printf("%02x ",item);
            }
            outPrint.println();
            for(byte item:Instruction.intToByte4B(funIter.retSlots)){
                outPrint.printf("%02x ",item);
            }
            outPrint.println();

            // functions.body.count
            for(byte item:Instruction.intToByte4B(funIter.Body.size())){
                outPrint.printf("%02x ",item);
            }
            outPrint.println();
            funIter.toO0(outPrint);
        }
        return;
    }
}
