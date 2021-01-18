0

```c

///tests/0-basic/ac2-1-christmas-tree.o0"
--- stdout ---
fn main() -> void {
    let c: int = 0;
    let i: int;
    while c < 10 {
        i = 0;
        while i < 9 - c {
            putchar(32);
            i = i + 1;
        }
        i = 0;
        while i < 2 * c + 1 {
            putchar(42);
            i = i + 1;
        }
        putln();
        c = c + 1;
    }
}

///tests/0-basic/ac2-2-hello-world.o0"
--- stdout ---
fn main() -> void {
    putstr("=============\n| #  /\\ + \" |\n|  /:\"\"\'\\\\^||\n|/|\\?\"\'/\\  !|\n|\\r \\ #s    |\n|\\a /\\  /\\\\v|\n=============");
}

//"/tests/0-basic/ac2-hello-world.o0"
- 多余的 / + 缺少的
+
Hello, world!
--- stdout ---
fn main() -> void {
    putstr("Hello, world!");
    putln();
}
[ 0 ]

//"/tests/0-basic/ac3-1-calc.o0"
- 多余的 / + 缺少的
+628
--- stdout ---
fn add(a: int, b: int) -> int {
    return a + b;
}

fn main() -> void {
    let x: int;
    let y: int;
    let z: int;
    x = getint();
    y = getint();
    z = add(x, y);
    putint(z);
}

///tests/0-basic/ac3-calc.o0"
--- stdout ---
fn main() -> void {
    let x: int = ----1;
    let y: int = 2;
    let z: int;
    z = x + y;
    putint(z);
}

///tests/0-basic/ac4-1-fastpow.o0"
--- stdout ---
fn is_odd(x: int) -> int {
    return (x / 2 * 2) - x;
}

fn fastpow(base: int, exp: int) -> int {
    let res: int = 1;
    if exp < 0 {
        return 0;
    }
    while exp > 0 {
        if is_odd(exp) {
            res = res * base;
        }
        base = base * base;
        exp = exp / 2;
    }
    return res;
}

fn main() -> void {
    let base: int;
    let exp: int;
    let count: int;
    count = getint();
    while count > 0 {
        base = getint();
        exp = getint();
        putint(fastpow(base,exp));
        putln();
        count = count - 1;
    }
}

///tests/0-basic/ac4-fib.o0"
--- stdout ---
fn fib(n: int) -> int {
	if n < 0 { return -1; }
	else if n == 0 { return 0; }
	else if n == 1 { return 1; }
	else { return fib(n-2) + fib(n-1); }
}

fn main() -> int {
	let i: int = 0;
	let max: int = 0;
	max = getint();
	if max > 20 { max = 21; }
	else if max < 0 {
		putint(-1);
		return 0;
	}
	else {}
	while i <= max {
		putint(fib(i));
        putln();
		i = i+1;
	}
	return 0;
}
//"/tests/0-basic/ac5-iffff.o0"
--- stdout ---
fn main() -> void {
    if 114514 {
        if 1919810 {
            if 1 != 2 {
                if 3 == 3 {
                    while 114 {
                        while 514 {
                            while 2 > -1 {
                                while 1 < 19 {
                                    while 3 >= 3 {
                                        while 5 <= 5 {
                                            if 0 {
                                                while 1 {
                                                    putint(1919);
                                                }
                                            } else {
                                                putint(810);
                                            }
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

///tests/0-basic/ac6-gcd.o0"
--- stdout ---
fn gcd(a: int, b: int) -> int {
    if a > b {
        return gcd(b, a);
    } else if a == 0 {
        return b;
    } else {
        return gcd(b - a, a);
    }
}

fn main() -> void {
    let count: int;
    let a: int;
    let b: int;

    count = getint();
    while count > 0 {
        a = getint();
        b = getint();
        putint(gcd(a, b));
        putln();
        count = count - 1;
    }
}

//"/tests/0-basic/ac7-1-global.o0"
- 多余的 / + 缺少的
+
1
+
1
+
1
+
2
+
3
+
4
+
1
+
1
--- stdout ---
let i: int = 2;

fn foo() -> int {
    let i: int = 1;
    putint(i);
    putln();
    return i;
}

fn bar() -> void {
    putint(i);
    putln();
    i = i + 1;
}

fn main() -> void {
    let i: int = 1;
    putint(i);
    putln();
    putint(foo());
    putln();
    bar();
    bar();
    bar();
    putint(foo());
    putln();
}

// "/tests/0-basic/ac7-global.o0"
- 多余的 / + 缺少的
+
114 514
+
514 114
--- stdout ---
let a: int = 0;
let b: int = 1;

fn swap() -> void {
    let temp: int = a;
    a = b;
    b = temp;
}

fn main() -> void {
    a = getint();
    b = getint();
    
    putint(a);
    putchar(32);
    putint(b);
    putln();

    swap();
    
    putint(a);
    putchar(32);
    putint(b);
}

///tests/0-basic/ac8-guess.o0"
--- stdout ---
fn main() -> void {
    let needle: int;
    let low: int = 0;
    let high: int = 1024;
    let mid: int;
    needle = getint();
    while low != high {
        mid = (low + high + 1) / 2;
        putint(mid);
        putln();
        if mid > needle {
            high = mid;
        } else if mid == needle {
            low = mid;
            high = mid;
        } else {
            low = mid;
        }
    }
}

//"/tests/0-basic/ac9-hanoi.o0"
- 多余的 / + 缺少的
+
1 A B
+
2 A C
+
1 B C
+
3 A B
+
1 C A
+
2 C B
+
1 A B
+
4 A C
+
1 B C
+
2 B A
+
1 C A
+
3 B C
+
1 A B
+
2 A C
+
1 B C
+
5 A B
+
1 C A
+
2 C B
+
1 A B
+
3 C A
+
1 B C
+
2 B A
+
1 C A
+
4 C B
+
1 A B
+
2 A C
+
1 B C
+
3 A B
+
1 C A
+
2 C B
+
1 A B
+
6 A C
+
1 B C
+
2 B A
+
1 C A
+
3 B C
+
1 A B
+
2 A C
+
1 B C
+
4 B A
+
1 C A
+
2 C B
+
1 A B
+
3 C A
+
1 B C
+
2 B A
+
1 C A
+
5 B C
+
1 A B
+
2 A C
+
1 B C
+
3 A B
+
1 C A
+
2 C B
+
1 A B
+
4 A C
+
1 B C
+
2 B A
+
1 C A
+
3 B C
+
1 A B
+
2 A C
+
1 B C
--- stdout ---
fn move(level: int, a: int, c: int) -> void {
    putint(level);
    putchar(32);
    putchar(a);
    putchar(32);
    putchar(c);
    putln();
}

fn hanoi(level: int, a: int, b: int, c: int) -> void {
    if level == 1 {
        move(level, a, c);
    } else {
        hanoi(level-1, a, c, b);
        move(level, a, c);
        hanoi(level-1, b, a, c);
    }
}

fn main() -> void {
    hanoi(6, 65, 66, 67);
}


//tests/0-basic/ac10-while-fantasy.o0"
--- stdout ---
let g: int;
let h: int;
let f: int;
let e: int;

fn EightWhile() -> int{
  let a: int;
  let b: int;
  let c: int;
  let d: int;
  a = 5;
  b = 6;
  c = 7;
  d = 10;
  while (a < 20) {
    a = a + 3;
    while(b < 10){
      b = b + 1;
      while(c == 7){
        c = c - 1;
        while(d < 20){
          d = d + 3;
          while(e > 1){
            e = e-1;
            while(f > 2){
              f = f -2;
              while(g < 3){
                g = g +10;
                while(h < 10){
                  h = h + 8;
                }
                h = h-1;
              }
              g = g- 8;
            }
            f = f + 1;
          }
          e = e + 1;
        }
        d = d - 1;
      }
      c = c + 1;
    }
    b = b - 2;
  }
  
  return (a + (b + d) + c)-(e + d - g + h);
}

fn main() -> void {
  g = 1;
  h = 2;
  e = 4;
  f = 6;
  putint(EightWhile());
}

///tests/0-basic/ac11-huge-function.o0"
- 多余的 / + 缺少的
+
28
+
22
+
118
--- stdout ---
let a0: int;
let a1: int;
let a2: int;
let a3: int;
let a4: int;
let a5: int;
let a6: int;
let a7: int;
let a8: int;
let a9: int;
let a10: int;
let a11: int;
let a12: int;
let a13: int;
let a14: int;
let a15: int;
let a16: int;
let a17: int;
let a18: int;
let a19: int;
let a20: int;
let a21: int;
let a22: int;
let a23: int;
let a24: int;
let a25: int;
let a26: int;
let a27: int;
let a28: int;
let a29: int;
let a30: int;
let a31: int;

let a32: int;
let a33: int;
let a34: int;
let a35: int;
let a36: int;
let a37: int;
let a38: int;
let a39: int;

fn testParam8(a0: int, a1: int, a2: int, a3: int,
        a4: int, a5: int, a6: int, a7: int) -> int {
    return a0 + a1 + a2 + a3 + a4 + a5 + a6 + a7;
}

fn testParam16(a0: int, a1: int, a2: int, a3: int,
        a4: int, a5: int, a6: int, a7: int,
        a8: int, a9: int, a10: int, a11: int,
        a12: int, a13: int, a14: int, a15: int) -> int{
    return a0 + a1 + a2 - a3 - a4 - a5 - a6 - a7 +
            a8 + a9 + a10 + a11 + a12 + a13 + a14 + a15;
}

fn testParam32(a0: int, a1: int, a2: int, a3: int,
        a4: int, a5: int, a6: int, a7: int,
        a8: int, a9: int, a10: int, a11: int,
        a12: int, a13: int, a14: int, a15: int,
        a16: int, a17: int, a18: int, a19: int,
        a20: int, a21: int, a22: int, a23: int,
        a24: int, a25: int, a26: int, a27: int,
        a28: int, a29: int, a30: int, a31: int) -> int {
    return a0 + a1 + a2 + a3 + a4 + a5 + a6 + a7 +
            a8 + a9 + a10 + a11 + a12 + a13 + a14 + a15 +
            a16 + a17 - a18 - a19 - a20 - a21 - a22 + a23 +
            a24 + a25 + a26 + a27 + a28 + a29 + a30 + a31;
}

fn main() -> void{
    a0 = 0;
    a1 = 1;
    a2 = 2;
    a3 = 3;
    a4 = 4;
    a5 = 5;
    a6 = 6;
    a7 = 7;
    a8 = 8;
    a9 = 9;
    a10 = 0;
    a11 = 1;
    a12 = 2;
    a13 = 3;
    a14 = 4;
    a15 = 5;
    a16 = 6;
    a17 = 7;
    a18 = 8;
    a19 = 9;
    a20 = 0;
    a21 = 1;
    a22 = 2;
    a23 = 3;
    a24 = 4;
    a25 = 5;
    a26 = 6;
    a27 = 7;
    a28 = 8;
    a29 = 9;
    a30 = 0;
    a31 = 1;

    a32 = 4;
    a33 = 5;
    a34 = 6;
    a35 = 7;
    a36 = 8;
    a37 = 9;
    a38 = 0;
    a39 = 1;

    a0 = testParam8(a0, a1, a2, a3, a4, a5, a6, a7);
    putint(a0); putln();
    a0 = testParam16(a32, a33, a34, a35,
            a36, a37, a38, a39,
            a8, a9, a10, a11,
            a12, a13, a14, a15);
    putint(a0); putln();
    a0 = testParam32(a0, a1, a2, a3,
            a4, a5, a6, a7,
            a8, a9, a10, a11,
            a12, a13, a14, a15,
            a16, a17, a18, a19,
            a20, a21, a22, a23,
            a24, a25, a26, a27,
            a28, a29, a30, a31);
    putint(a0); putln();
}
```

1 //

```
"/tests/1-comment/ac2.o0"
- 多余的 / + 缺少的
+
// this is not a comment
--- stdout ---
fn main() -> void {
    putstr("// this is not a comment");
}
[ 0 ]
```

2 char

```

```

3 double

```

```

4 scope-nesting

```

```

5 declaration-plus

```

```

6 break

```

```

7 return

```

```

