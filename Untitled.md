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

//我的static: 112 117 116 105 110 116 ('putint')
static: 112 117 116 105 110 116 ('putint')
static: 109 97 105 110 ('main')
static: 95 115 116 97 114 116 ('_start')
fn [3] 0 0 -> 0 {
    0: stackalloc 0
    1: call 1
}
fn [2] 0 0 -> 0 {
    0: push 114514
    1: brtrue 1
    2: br 75
if 114514 { 

    3: push 1919810
    4: brtrue 1
    5: br 71
 if 1919810 {
 
    6: push 1
    7: push 2
    8: cmpi
    9: brtrue 1
    10: br 65
  if 1 != 2 {
  
    11: push 3
    12: push 3
    13: cmpi
    14: not
    15: brtrue 1
    16: br 58
    17: push 114
    18: brtrue 1
    19: br 54
    20: push 514
    21: brtrue 1
    22: br 50
    23: push 2
    24: push 1
    25: negi
    26: cmpi
    27: setgt
    28: brtrue 1
    29: br 42
    30: push 1
    31: push 19
    32: cmpi
    33: setlt
    34: brtrue 1
    35: br 35
    36: push 3
    37: push 3
    38: cmpi
    39: setlt
    40: not
    41: brtrue 1
    42: br 27
    43: push 5
    44: push 5
    45: cmpi
    46: setgt
    47: not
    48: brtrue 1
    49: br 19
    50: push 0
    51: brtrue 1
    52: br 8
    53: push 1
    54: brtrue 1
    55: br 4
    56: stackalloc 0
    57: push 1919
    58: callname 0
    59: br -7
    60: br 6
    61: brtrue 1
    62: br 4
    63: stackalloc 0
    64: push 810
    65: callname 0
    66: br 0
    67: ret
    68: br -26
    69: br -34
    70: br -41
    71: br -49
    72: br -53
    73: br -57
    74: br 0
    75: br 0
    
  }    76: br 0
    
 }    77: br 0
    
}    78: ret



}
```

