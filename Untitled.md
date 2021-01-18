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

