# 操作系统基础知识

## 基础知识

### 汇编

* 二进制和汇编语言

汇编指令：汇编语言指令 机器码：由0和1构成的二进制串 编译器：将汇编指令翻译成机器码（机器指令），将一套表示系统翻译成另外一套表示系统

汇编语言：汇编指令，伪指令，符号体系

* 汇编指令存放在哪？

汇编指令和数据存放在内存中，以二进制形式存在，指令和数据在内存中是没有区别的

* 内存的最小单位

1 Byte

* CPU 如何对内存进行读写的

简单来说就是CPU通过控制总线，数据总线，地址总线来操作内存的

* 寻址能力

寻找内存地址的能力；地址总线的位数决定了能够表示地址范围的能力，也就是寻址能力

8086 的寻址能力是 1M，80386 的寻址能力是4G

## x86 Assembly Guide

```bash
http://www.cs.virginia.edu/~evans/cs216/guides/x86.html
https://book.douban.com/subject/20492528/
https://padamthapa.com/blog/my-first-x86-64-assembly-in-macos/
https://cs.lmu.edu/~ray/notes/nasmtutorial/
```

