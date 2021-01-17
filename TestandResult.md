## 栈帧结构

```c
| ...           |
|               | <- 栈顶 %sp
| 表达式栈 ...  |
| 表达式栈      |
| 局部变量 ...  |
| 局部变量      |
| 虚拟机参数... | 
| 虚拟机参数    | <- 被调用者栈底 %bp 
|===============|===
| 调用参数 ...  | v
| 调用参数      | |
| 返回值        | |
| 中间结果      | 调用者栈
| ...           | ^ 
|===============|===
//调用函数，先空出返回值，参数，虚拟机先不管，局部变量
//call id:u32 调用编号为 id 的函数|用函数数组下标调
//callname id:u32 调用名称 与 编号为 id 的全局变量相同的函数|用全局变量id调
fn fib(x: int) -> int {
    if x<=1 {
        return 1;
    }
    let result: int = fib(x - 1);
    result = result + fib(x - 2);
    return result;
}

fn main() -> int {
    let i: int = 0;
    let j: int;
    j = getint();
    while i < j {
        putint(i);
        putchar(32);
        putint(fib(i));
        putln();
        i = i + 1;
    }
    return 0;
}
static: 66 69 62 (`fib`)
static: 67 65 74 69 6E 74 (`getint`)
static: 70 75 74 69 6E 74 (`putint`)
static: 70 75 74 63 68 61 72 (`putchar`)
static: 70 75 74 69 6E 74 (`putint`)
static: 70 75 74 6C 6E (`putln`)
static: 6D 61 69 6E (`main`)
static: 5F 73 74 61 72 74 (`_start`)
fn [7] 0 0 -> 0 {
    0: StackAlloc(1)//给main一个返回值空间
    1: Call(2)
    2: PopN(1)
}
fn [0] 1 1 -> 1 { //let result | x: int |int 
    0: ArgA(1) //加载 1 处参数/返回值的地址 x: int
    1: Load64 // x的值
    2: Push(1)
    3: CmpI //比较有符号整数 lhs 和 rhs 大小  if x<=1 x
    4: SetGt //如果 lhs > 0 则推入 1，否则 0
    5: Not //取反 如果为真为1
    6: BrTrue(1)//如果 `test` 非 0 则跳转偏移 `off`
    7: Br(4)//`br`off:i32无条件跳转偏移 `off`
    8: ArgA(0)//加载 off 个 slot 处参数/返回值的地址 这里是返回值地址
    9: Push(1)
   10: Store64 
   11: Ret //return 1
   12: ArgA(1)//x
   13: Load64
   14: ArgA(1)//x
   15: Load64
   16: AddI//x+x;
   17: PopN(1)//弹栈
   18: Br(0)//无条件跳转偏移 `off`
   19: LocA(0)//加载 off 个 slot 处局部变量的地址|局部变量result
   20: StackAlloc(1)//在当前栈顶分配 size 个 slot，初始化为 0 返回值
   21: ArgA(1)//x
   22: Load64//x
   23: Push(1)
   24: SubI//x-1 这里是把x-1作为参数放在返回值的下面，0|x-1|分别在栈的01
   25: Call(1)
   26: Store64//resule=返回值
   27: LocA(0)
   28: LocA(0)
   29: Load64
   30: StackAlloc(1)//返回值
   31: ArgA(1)//x
   32: Load64
   33: Push(2)
   34: SubI//x-2 
   35: Call(1)
   36: AddI 
   37: Store64
   38: ArgA(0)//加载 off 个 slot 处参数/返回值的地址
   39: LocA(0)//result
   40: Load64/
   41: Store64
   42: Ret
}

fn [6] 2 0 -> 1 {
    0: LocA(0)
    1: Push(0)
    2: Store64
    3: LocA(1)
    4: StackAlloc(1)
    5: CallName(1)
    6: Store64
    7: Br(0)
    8: LocA(0)//i
    9: Load64
   10: LocA(1)//j
   11: Load64
   12: CmpI//i<j
   13: SetLt
   14: BrTrue(1)
   15: Br(22)
   16: StackAlloc(0)
   17: LocA(0)
   18: Load64
   19: CallName(2)
   20: StackAlloc(0)
   21: Push(32)
   22: CallName(3)
   23: StackAlloc(0)
   24: StackAlloc(1)
   25: LocA(0)
   26: Load64
   27: Call(1)
   28: CallName(4)
   29: StackAlloc(0)
   30: CallName(5)
   31: LocA(0)
   32: LocA(0)
   33: Load64
   34: Push(1)
   35: AddI
   36: Store64
   37: Br(-30)
   38: ArgA(0)
   39: Push(0)
   40: Store64
   41: Ret
}
------------
fn main() -> int {
    let i: int = 0;
    let j: int;
    j = getint();
    while i < j {
        i = i + 1;
    }
    return 0;
}
static: 67 65 74 69 6E 74 (`getint`)
static: 6D 61 69 6E (`main`)
static: 5F 73 74 61 72 74 (`_start`)
fn [2] 0 0 -> 0 {
    0: StackAlloc(1)
    1: Call(1)
    2: PopN(1)
}
fn [1] 2 0 -> 1 {
    0: LocA(0)
    1: Push(0)
    2: Store64
    3: LocA(1)
    4: StackAlloc(1)
    5: CallName(0)
    6: Store64
    7: Br(0)
        //开始判断
    8: LocA(0)//i
    9: Load64
   10: LocA(1)//j
   11: Load64
   12: CmpI//i<j
   13: SetLt//如果 lhs < 0 则推入 1，否则 0
   14: BrTrue(1)//为真跳过下一
   15: Br(7)//假跳出 跳出指令位置为15 跳22-15
       //block
   16: LocA(0)
   17: LocA(0)
   18: Load64
   19: Push(1)
   20: AddI
   21: Store64
       
   22: Br(-15)//跳回去 22跳到8 22-8+1=-15
   23: ArgA(0)
   24: Push(0)
   25: Store64
   26: Ret
}
----------------
fn main() -> int {
    let i: int = 0;
    let j: int =1 ;
    if i < j {
        i = i + 1;
    }
    if i < j {
        i = i + 1;
    }else if i == j {
        i = i + 1;
    }else if i < 0 {
        i = i + 1;
    }else{
        i = i + 1;
    }
    return 0;
}

static: 6D 61 69 6E (`main`)

static: 5F 73 74 61 72 74 (`_start`)


fn [1] 0 0 -> 0 {
    0: StackAlloc(1)
    1: Call(1)
    2: PopN(1)
}

fn [0] 2 0 -> 1 {
    0: LocA(0)
    1: Push(0)
    2: Store64
    3: LocA(1)
    4: Push(1)
    5: Store64
        //if
    6: LocA(0)
    7: Load64
    8: LocA(1)
    9: Load64
   10: CmpI //i<j
   11: SetLt//<
   12: BrTrue(1)
   13: Br(7) //20-13=7
       
   14: LocA(0)
   15: LocA(0)
   16: Load64
   17: Push(1)
   18: AddI
   19: Store64
   20: Br(0)
       //if
   21: LocA(0)
   22: Load64
   23: LocA(1)
   24: Load64
   25: CmpI
   26: SetLt
   27: BrTrue(1)
   28: Br(7)
   29: LocA(0)
   30: LocA(0)
   31: Load64
   32: Push(1)
   33: AddI
   34: Store64
   35: Br(38)
       
   36: LocA(0)
   37: Load64
   38: LocA(1)
   39: Load64
   40: CmpI
   41: Not
   42: BrTrue(1)
   43: Br(7)
   44: LocA(0)
   45: LocA(0)
   46: Load64
   47: Push(1)
   48: AddI
   49: Store64
   50: Br(22)
   51: LocA(0)
   52: Load64
   53: Push(0)
   54: CmpI
   55: SetLt
   56: BrTrue(1)
   57: Br(7)
   58: LocA(0)
   59: LocA(0)
   60: Load64
   61: Push(1)
   62: AddI
   63: Store64
   64: Br(7)
   65: LocA(0)
   66: LocA(0)
   67: Load64
   68: Push(1)
   69: AddI
   70: Store64
   71: Br(0)
   72: Br(0)
   73: Br(0)
   74: ArgA(0)
   75: Push(0)
   76: Store64
   77: Ret
}
------------
    if i < j {
        i;
    }else if i == j {
        i;
    }else{
        i;
    }
	//if i < j
    6: LocA(0)
    7: Load64
    8: LocA(1)
    9: Load64
   10: CmpI //i<j
   11: SetLt
   12: BrTrue(1)
   13: Br(4) //跳到block结束
       //block
   14: LocA(0)
   15: Load64
   16: PopN(1)
   17: Br(17) //跳到所有if结束
       
      // else if i == j 
   18: LocA(0)
   19: Load64
   20: LocA(1)
   21: Load64
   22: CmpI
   23: Not
   24: BrTrue(1)
   25: Br(4)
       //block
   26: LocA(0)
   27: Load64
   28: PopN(1)
   29: Br(4)//跳到所有判断结束 34 33-29=4
       
       //else 不做判断直接block
   30: LocA(0)
   31: Load64
   32: PopN(1)
   33: Br(0)
       
   34: Br(0)//？？？
   35: ArgA(0)
   36: Push(0)
   37: Store64
   38: Ret
```



```java
//作用域设计  LinkedList<Integer> ActScope
//全局作用域        		 ActScope->0 每个0都是这一级的全局
//函数总作用域    	    ActScope->fn_id 包括参数，一层变量
//函数内嵌套块作用域       ActScope->fn_id->作用域嵌套层 包括参数，
    1  |  fn fib_iter(x: int) -> int {   // ActScope->1->0 //函数全局|func_symbolList[0]
    |  |      let last_val: int = 1;	// ActScope->1->0 //0层
  2 |  |      let counter: int = x - 2; // ActScope->1->0 //0层
  | |  |      while counter > 0 {	    // ActScope->1->0->0|func_symbolList[1]
3 |    |          let x: int = cur_val + last_val; // ActScope->1->0->0
- |    |      }
- |           while counter > 0 {	    // ActScope->1->0->1||func_symbolList[2]
3 |    |          let x: int = cur_val + last_val; // ActScope->1->0->1
| |    |          last_val = cur_val;
     			 while counter > 0 {   // ActScope->1->0->1->0||func_symbolList[3]
                    let x: int = cur_val + last_val; // ActScope->1->0->1->0
                  }
| |    |          cur_val = x;          // ActScope->1->0->1||func_symbolList[2]
- |    |      }
  | |  |      return cur_val;
  - -  |  }
//建立符号表
	//进入函数1，新建ActScope->1||func_symbolList[0]
	//参数分析		 ActScope->1->0||func_symbolList[0]
	//函数块分析		ActScope->1->0||func_symbolList[0]
	//语句块分析开始：ActScope->1->0->0||func_symbolList[1]
	//语句块分析结束：ActScope->1->0   ||func_symbolList[0]
	
//寻找符号表，
	/** 全局符号表 */
    HashMap<String, SymbolEntry> symbolTable = new HashMap<>();
    /** 当前block符号表*/
    HashMap<String, SymbolEntry> now_block_symbolTable = new HashMap<>();
	//当前正在编译的函数
    Function nowFunc;
	//函数符号表，每个块有一个按块出现顺序排列
	public ArrayList<HashMap<String, SymbolEntry>> func_symbolList;
	//例：ActScope->1->0->1->1||func_symbolList[3]找在ActScope->1->0里面的变量。
	//首先只能在上级中找，其次看func_symbolList隔了几个，加一个临时变量
	HashMap<String, SymbolEntry> temp_found_block_symbolTable =func_symbolList.get(下标);
	//下标要看ActScope，首先当前block中ActScope->1->0->1->0
	//找不到向上，那么
//你是个傻子嘛，这不是栈是什么？
{						// 1->0 		||0
|    {}					// 1->0->1  	 ||1
|    					// 1->0  		 ||0
|    {					// 1->0->2 	      ||2
|    |    {				// 1->0->2->0	  ||3
|    |    |    5		 // 1->0->2->0	   ||3
|    |    |    {}		 // 1->0->2->0->0  ||4
|    |    }				// 1->0->2->0	  ||3
|    |    				// 1->0->2	      ||3
|    |    {}			// 1->0->2->1      ||5
|    }
}
```



```C
let i: int = 8;
let y: int = i;
fn main() -> int {
    putint(y);
    return 0;
}
------------
static: 0 0 0 0 0 0 0 0 (`\u{0}\u{0}\u{0}\u{0}\u{0}\u{0}\u{0}\u{0}`)
static: 0 0 0 0 0 0 0 0 (`\u{0}\u{0}\u{0}\u{0}\u{0}\u{0}\u{0}\u{0}`)
static: 70 75 74 69 6E 74 (`putint`)
static: 6D 61 69 6E (`main`)
static: 5F 73 74 61 72 74 (`_start`)
fn [4] 0 0 -> 0 {// 函数名称在全局变量中的位置/返回值/参数/局部变量
    0: GlobA(0)//加载第 n 个全局变量/常量的地址 i
    1: Push(8)//将 num 压栈
    2: Store64//弹栈 1:addr, 2:val 把 val：i 存入 addr
    3: GlobA(1)//加载y地址
    4: GlobA(0)//加载x的地址
    5: Load64//弹1:addr压1:val从 addr 加载 64 位 value 压栈 弹x压x的值
    6: Store64//弹栈 1:addr, 2:val把 val-y 存入 addr
    7: StackAlloc(1)//在当前栈顶分配 size 个 slot，初始化为 0
    8: Call(1)//call 1 main
    9: PopN(1)//弹栈 1
}
fn [3] 0 0 -> 1 {
    0: StackAlloc(0)
    1: GlobA(1)
    2: Load64
    3: CallName(2)
    4: ArgA(0)
    5: Push(0)
    6: Store64
    7: Ret
}
```

```c
//表达式
const a: int=1;
let b: int=a;
let c: int=2;
let h: int=a+(b+2)*(b+2*c);

fn main() -> int {
    putint(h);
    return 0;
}
------------
const a: int=1;

fn main() -> int {
    let b: int=a;
    let c: int=2;
    let h: int=a+(b+2)*(b+2*c);
    putint(h);
    return 0;
}
fn [2] 3 0 -> 1 {
    0: LocA(0)//LocA(off) 加载第off 个 slot 处局部变量的地址
    1: GlobA(0)
    2: Load64//加载a
    3: Store64//let b: int=a;
    4: LocA(1)//第二个局部变量c
    5: Push(2)
    6: Store64//int=2;
        //let h: int=a+(b+2)*(b+2*c);
    7: LocA(2)//第三个局部变量h
    8: GlobA(0)
    9: Load64
   10: LocA(0)
   11: Load64
   12: Push(2)
   13: AddI
   14: LocA(0)
   15: Load64
   16: Push(2)
   17: LocA(1)
   18: Load64
   19: MulI
   20: AddI
   21: MulI
   22: AddI
   23: Store64
   24: StackAlloc(0)
   25: LocA(2)
   26: Load64
   27: CallName(1)
   28: ArgA(0)
   29: Push(0)
   30: Store64
   31: Ret
}

fn [6] 0 0 -> 0 {
    0: GlobA(0)//压地址
    1: Push(1)//压数值
    2: Store64//弹栈，存数值到地址 const a: int=1;
    3: GlobA(1)
    4: GlobA(0)
    5: Load64 //加载栈顶地址值换成值
    6: Store64//let b: int=a;
    7: GlobA(2)
    8: Push(2)//let c: int=2;
    9: Store64
        //let h: int=a+(b+2)*(b+2*c);
   10: GlobA(3)//压h的地址  let h:
   11: GlobA(0)
   12: Load64//加载a a
   13: GlobA(1)
   14: Load64//加载b a+(b
   15: Push(2)//压2  
   16: AddI //运算 a+(b+2)
   17: GlobA(1) 
   18: Load64 //加载b a+(b+2)*(b
   19: Push(2) //压2 a+(b+2)*(b+2
   20: GlobA(2) 
   21: Load64 //加载c  a+(b+2)*(b+2*c)
   22: MulI
   23: AddI
   24: MulI
   25: AddI
   26: Store64 //得到结果  GlobA(3) result Store64
       //let h: int=a+(b+2)*(b+2*c);
   27: StackAlloc(1)
   28: Call(1)
   29: PopN(1)
}

fn [5] 0 0 -> 1 {
    0: StackAlloc(0)
    1: GlobA(3)
    2: Load64
    3: CallName(4)
    4: ArgA(0)
    5: Push(0)
    6: Store64
    7: Ret
}
begin
    const a = 1;
    var b=a;
    var c=2;
    var h=a+(b+2)*(b+2*c);
    print(h);
end
//栈stark[0][1]abc
LIT 1 //压栈 a=1 0//a
LOD 0 //压stack[0]入栈 1//ab
LIT 2 //压栈 2//abc
LOD 0 //压stack[0]入栈 a 3//abca
LOD 1 //压stack[1]入栈 b 4//abcab
LIT 2 //压栈 2 5//abcab2
ADD //b+2  4//abca(b+2)
LOD 1//压b 5//abca(b+2)b
LIT 2//压2 6//abca(b+2)b2
LOD 2//压c 7//6//abca(b+2)b2c
MUL//2*c 6//abca(b+2)b(2+c)
ADD//b+2*c 5//abca(b+2)(b+(2+c))
MUL//(b+2)*(b+2*c) 4
ADD//a+(b+2)*(b+2*c) 3
LOD 3//压h 3
WRT
```

