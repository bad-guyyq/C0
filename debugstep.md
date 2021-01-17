##### 命令

```
# 编译出来一个包含所有依赖的 Jar (Java 存档包) 文件
gradle fatjar
java -jar build/libs/miniplc0java.jar -t  hello.plc0 -o output_t.txt
java -jar build/libs/miniplc0java.jar -l  hello.plc0 -o output_l.txt

```

```
let i: int = 0;
let j: int;
fn main() -> int {
    let i: int = 0;
    let j: int;
    j=i;
    return 0;
}

static: 0 0 0 0 0 0 0 0 (`\u{0}\u{0}\u{0}\u{0}\u{0}\u{0}\u{0}\u{0}`)
static: 0 0 0 0 0 0 0 0 (`\u{0}\u{0}\u{0}\u{0}\u{0}\u{0}\u{0}\u{0}`)
static: 6D 61 69 6E (`main`)
static: 5F 73 74 61 72 74 (`_start`)
fn [3] 0 0 -> 0 {
    0: GlobA(0)
    1: Push(0)
    2: Store64
    3: StackAlloc(1)
    4: Call(1)
    5: PopN(1)
}

fn [2] 2 0 -> 1 {
    0: LocA(0)
    1: Push(0)
    2: Store64
    3: LocA(1)
    4: LocA(0)
    5: Load64
    6: Store64
    7: ArgA(0)
    8: Push(0)
    9: Store64
   10: Ret
}

```

**二进制转换**

```c
fn _start 0 0 -> 0 {
    // 设置全局变量 1 的值为 1 + 1;
    globa 1
    push 1
    push 1
    add.i
    store.64
    // 调用 main
    call 4
    // 没有返回语句
}

// start
72 30 3b 3e // magic
00 00 00 01 // version

00 00 00 02 // globals.count

// globals[0]
00 // globals[0].is_const
00 00 00 03 // globals[0].value.count
00 01 02 // globals[0].value.items

// globals[1]
01 // globals[1].is_const
00 00 00 06 // globals[1].value.count
'_' 's' 't' 'a' 'r' 't' // globals[1].value.items

00 00 00 01 // functions.count

// functions[0]
00 00 00 01 // functions[0].name
00 00 00 00 // functions[0].ret_slots
00 00 00 00 // functions[0].param_slots
00 00 00 00 // functions[0].loc_slots
00 00 00 04 // functions[0].body.count
    // functions[0].body.items
    01 00 00 00 00 00 00 00 01 // Push(1)
    01 00 00 00 00 00 00 00 02 // Push(2)
    20 // AddI
    34 // NegI
// finish
```

```C
fn [1] 0 1 -> 1 {
    0: Push(0)
    1: PopN(1)
    2: ArgA(1)
    3: Load64
    4: Push(1)
    5: CmpI
    6: SetGt
    7: Not
    8: BrTrue(1)
    9: Br(4)
   10: ArgA(0)
   11: Push(1)
   12: Store64
   13: Ret
   14: ArgA(0)
   15: Push(2)
   16: Store64
   17: Ret
}
```

