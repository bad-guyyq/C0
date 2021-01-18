```c
fn main()         // Hi!
          -> void // comment here
// do you miss me?
{
    // let i: int = 65535;
    let i: int = 0;
    let j: int = 1;
    putint(i+j); // this prints out i + j
    // return 0;
    // we should not return here
// } this is a fake end!
} // this is real
static: 70 75 74 69 6E 74 (`putint`)

static: 6D 61 69 6E (`main`)

static: 5F 73 74 61 72 74 (`_start`)




fn [2] 0 0 -> 0 {
    0: StackAlloc(0)
    1: Call(1)
}

fn [1] 2 0 -> 0 {
    0: LocA(0)
    1: Push(0)
    2: Store64
    3: LocA(1)
    4: Push(1)
    5: Store64
    6: StackAlloc(0)
    7: LocA(0)
    8: Load64
    9: LocA(1)
   10: Load64
   11: AddI
   12: CallName(0)
   13: Ret
}

static : 5f 73 74 61 72 74 ('_start')
static : 6d 61 69 6e ('main')
static : 70 75 74 69 6e 74 ('putint')
fn [0] 0 0 -> 0 {
    0: StackAlloc(1)
    1: Call(1)
    2: PopN(1)
}
fn [1] 1 0 -> 0 {
    0: LocA(0)
    1: Push(0)
    2: Store64
    3: LocA(1)
    4: Push(1)
    5: Store64
    6: StackAlloc(0)
    7: LocA(0)
    8: Load64
    9: LocA(1)
    10: Load64
    11: AddI
    12: CallName(2)
    13: Ret
}
72 30 3b 3e
00 00 00 01
00 00 00 01
写入每一个全局变量
01
00 00 00 06
5f 73 74 61 72 74
01
00 00 00 04
6d 61 69 6e
01
00 00 00 06
70 75 74 69 6e 74

函数
00 00 00 02
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 03
     1a 00 00 00 01
     48 00 00 00 01
     03 00 00 00 01
00 00 00 01
00 00 00 00
00 00 00 00
00 00 00 02
00 00 00 0e
     0a 00 00 00 00
     01 00 00 00 00 00 00 00 00
     17      0a 00 00 00 01
     01 00 00 00 00 00 00 00 01
     17      1a 00 00 00 00
     0a 00 00 00 00
     13      0a 00 00 00 01
     13      20      4a 00 00 00 02
     49

```

```c
///tests/0-basic/ac2-2-hello-world.o0"
--- stdout ---
fn main() -> void {
    putstr("=============\n
           | #  / \\ + \" |\n
           |  /: \" \" \' \\ \\ ^
           ||\n
           |/| \\ ?\" \'/ \\  !|\n
           | \\ r \\ #s | \n
           | \\ a / \\  /\\ \\ v|\n
           =============");
}
```

```
static: 70 75 74 63 68 61 72 (`putchar`)

static: 70 75 74 63 68 61 72 (`putchar`)

static: 70 75 74 6C 6E (`putln`)

static: 6D 61 69 6E (`main`)

static: 5F 73 74 61 72 74 (`_start`)


fn [4] 0 0 -> 0 {
    0: StackAlloc(0)
    1: Call(1)
}

fn [3] 2 0 -> 0 {
    0: LocA(0)
    1: Push(0)
    2: Store64
    3: Br(0)
    4: LocA(0)
    5: Load64
    6: Push(10)
    7: CmpI
    8: SetLt
    9: BrTrue(1)
   10: Br(59)
   11: LocA(1)
   12: Push(0)
   13: Store64
   14: Br(0)
   15: LocA(1)
   16: Load64
   17: Push(9)
   18: LocA(0)
   19: Load64
   20: SubI
   21: CmpI
   22: SetLt
   23: BrTrue(1)
   24: Br(10)
   25: StackAlloc(0)
   26: Push(32)
   27: CallName(0)
   28: LocA(1)
   29: LocA(1)
   30: Load64
   31: Push(1)
   32: AddI
   33: Store64
   34: Br(-20)
   35: LocA(1)
   36: Push(0)
   37: Store64
   38: Br(0)
   39: LocA(1)
   40: Load64
   41: Push(2)
   42: LocA(0)
   43: Load64
   44: MulI
   45: Push(1)
   46: AddI
   47: CmpI
   48: SetLt
   49: BrTrue(1)
   50: Br(10)
   51: StackAlloc(0)
   52: Push(42)
   53: CallName(1)
   54: LocA(1)
   55: LocA(1)
   56: Load64
   57: Push(1)
   58: AddI
   59: Store64
   60: Br(-22)
   61: StackAlloc(0)
   62: CallName(2)
   63: LocA(0)
   64: LocA(0)
   65: Load64
   66: Push(1)
   67: AddI
   68: Store64
   69: Br(-66)
   70: Ret
}
```

```
static: 66 69 62 (`fib`)

static: 67 65 74 69 6E 74 (`getint`)

static: 70 75 74 69 6E 74 (`putint`)

static: 70 75 74 69 6E 74 (`putint`)

static: 70 75 74 6C 6E (`putln`)

static: 6D 61 69 6E (`main`)

static: 5F 73 74 61 72 74 (`_start`)


fn [6] 0 0 -> 0 {
    0: StackAlloc(1)
    1: Call(2)
    2: PopN(1)
}

fn [0] 0 1 -> 1 {
	//if n < 0 
    0: ArgA(1)
    1: Load64
    2: Push(0)
    3: CmpI
    4: SetLt
    5: BrTrue(1)
    6: Br(5)
    
    // return -1;
    7: ArgA(0)
    8: Push(1)
    9: NegI
   10: Store64
   11: Ret
   
   if n == 0
   12: ArgA(1)
   13: Load64
   14: Push(0)
   15: CmpI
   16: Not
   17: BrTrue(1)
   18: Br(4)
   
   //return 0; 
   19: ArgA(0)
   20: Push(0)
   21: Store64
   22: Ret
   
   //return fib(n-2) + fib(n-1);
   23: ArgA(1)
   24: Load64
   25: Push(1)
   26: CmpI
   27: Not
   28: BrTrue(1)
   29: Br(4)
   30: ArgA(0)
   31: Push(1)
   32: Store64
   33: Ret
   34: ArgA(0)
   35: StackAlloc(1)
   36: ArgA(1)
   37: Load64
   38: Push(2)
   39: SubI
   40: Call(1)
   41: StackAlloc(1)
   42: ArgA(1)
   43: Load64
   44: Push(1)
   45: SubI
   46: Call(1)
   47: AddI
   48: Store64
   49: Ret
}

fn [5] 2 0 -> 1 {
    0: LocA(0)
    1: Push(0)
    2: Store64
    3: LocA(1)
    4: Push(0)
    5: Store64
    6: LocA(1)
    7: StackAlloc(1)
    8: CallName(1)
    9: Store64
   10: LocA(1)
   11: Load64
   12: Push(20)
   13: CmpI
   14: SetGt
   15: BrTrue(1)
   16: Br(4)
   17: LocA(1)
   18: Push(21)
   19: Store64
   20: Br(17)
   21: LocA(1)
   22: Load64
   23: Push(0)
   24: CmpI
   25: SetLt
   26: BrTrue(1)
   27: Br(8)
   28: StackAlloc(0)
   29: Push(1)
   30: NegI
   31: CallName(2)
   32: ArgA(0)
   33: Push(0)
   34: Store64
   35: Ret
   36: Br(0)
   37: Br(0)
   38: Br(0)
   39: LocA(0)
   40: Load64
   41: LocA(1)
   42: Load64
   43: CmpI
   44: SetGt
   45: Not
   46: BrTrue(1)
   47: Br(15)
   48: StackAlloc(0)
   49: StackAlloc(1)
   50: LocA(0)
   51: Load64
   52: Call(1)
   53: CallName(3)
   54: StackAlloc(0)
   55: CallName(4)
   56: LocA(0)
   57: LocA(0)
   58: Load64
   59: Push(1)
   60: AddI
   61: Store64
   62: Br(-24)
   63: ArgA(0)
   64: Push(0)
   65: Store64
   66: Ret
}
```

