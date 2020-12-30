# 位运算

## 位运算操作符

* 左移 &lt;&lt; 运算符，m &lt;&lt; n 表示把 m 左移 n 位，左移的时候最左边的 n 位被丢弃，同时在最右边补上 n 个 0。如 `00001010 << 2 = 00101000`
* 右移 &gt;&gt; 运算符，m &gt;&gt; n 表示把 m 右移 n 位，右移的时候最右边的 n 位丢弃，但是左边的处理分两种情况，如果 m 是一个无符号的整数，则用 0 填充最左边的 n 位，如 `00001010 >> 2 = 00000010` ；如果 m 是一个有符号的数值，则用数字的符号位填充最左边的 n 位，如 `10001010 >> 3 = 11110001`

以上两点比较重要。

```java
>>  右移
<<  左移
|   或
&   与
～  取反
^   异或

// 一些高级操作
x ^ 0 = 0
x ^ 1s = ~x
x ^ (~x) = 1s
x ^ x = 0
c = a ^ b => a ^ c = b, b ^ c = a
a ^ b ^ c = a ^ (b ^ c) = (a ^ b) ^ c

// 将 x 最右边的 n 位清零
x & (~0 << n)

// 获取 x 的第 n 位值是 0 还是 1
(x >> n) & 1  // x: 01101101  x>>3: 00001101 (x>>3)&00000001 -> 1
x & (1 << n) == 0

// 获取 x 的第 n 位的幂值
x & (1 << (n-1))

// 仅将第 n 位置 1
x | (1 << n)

// 仅将第 n 位置 0
x & (~(1 << n))

// 将 x 最高位至第 n 位（含）清零
x & ((1 << n) - 1)

// 将第 n 位至第0位（含）清零
x & (~((1<<(n+1)) - 1))

//
x & ~x = 0
```

## 有趣的位运算

* 判断奇偶

```java
x % 2 == 1  ->  (x & 1) == 1
x % 2 == 0  ->  (x & 1) == 0
```

* 除法

```java
x >> 1  ->  x / 2
mid = left + (right - left) / 2  ->  left + ((right - left) >> 1)
```

* 清零最低位的 1

```java
x = x & (x - 1)
```

* 得到最低位的 1

```java
x & -x
```

* 下面的操作对所有英文字符有效
* 利用与操作 & 和下划线将英文字符转换为大写
* 利用或操作 \| 和空格将英文字符转换为小写
* 利用异或操作 ^ 和空格进行英文字符大小写互换

```java
// 转小写
char var1 = 'a' | ' '; // var1 is a
char var2 = 'A' | ' '; // var2 is a

// 'a': 0110 0001
// ' ': 0010 0000
// 'A': 0100 0001
// 按位或： 0010 0000( ) | 0100 0001(A) -> 0110 0001(a)

// ---
// 转大写
char var3 = 'a' & '_';  // var3 is A
char var4 = 'A' & '_';  // var4 is A

// ---
// 大小写互换
char var5 = 'd' ^ ' '; // var5 is 'D'
char var6 = 'D' ^ ' '; // var6 is 'd'
```

* 判断两个数是否异号

这个技巧还是很实用的，利用的是补码编码的符号位。如果不用位运算来判断是否异号，需要使用 if else 分支，还挺麻烦的。读者可能想利用乘积或者商来判断两个数是否异号，但是这种处理方式可能造成溢出，从而出现错误。

```java
int x = -1, y = 2;
boolean f = ((x ^ y) < 0); // true

int x = 3, y = 2;
boolean f = ((x ^ y) < 0); // false
```

* 交换两个数

```java
int a = 1, b = 2;
a ^= b;
b ^= a;
a ^= b;
// 现在 a = 2, b = 1
```

* 加一

```java
int n = 1;
n = -~n;
// 现在 n = 2
```

* 减一

```java
int n = 2;
n = ~-n;
// 现在 n = 1
```

* 算法常用操作 n&\(n-1\)

作用是消除数字 n 的二进制表示中的最后一个 1。

计算汉明权重（Hamming Weight）

```java
// 返回 n 的二进制表示中有几个 1。因为 n & (n - 1) 可以消除最后一个 1，
// 所以可以用一个循环不停地消除 1 同时计数，直到 n 变成 0 为止。
int hammingWeight(uint32_t n) {
    int res = 0;
    while (n != 0) {
        n = n & (n - 1);
        res++;
    }
    return res;
}
```

* 判断一个数是不是 2 的指数

一个数如果是 2 的指数，那么它的二进制表示一定只含有一个 1

```java
2^0 = 1 = 0b0001
2^1 = 2 = 0b0010
2^2 = 4 = 0b0100
```

如果使用位运算技巧就很简单

```cpp
bool isPowerOfTwo(int n) {
    if (n <= 0) return false;
    return (n & (n - 1)) == 0;
}
```

## 参考

* [Bit Twiddling Hacks](https://graphics.stanford.edu/~seander/bithacks.html)  收集了几乎所有的位操作
* [常用位操作](https://labuladong.gitbook.io/algo/suan-fa-si-wei-xi-lie/chang-yong-de-wei-cao-zuo)
* [位运算](https://github.com/selfboot/LeetCode/tree/master/BitManipulation)

