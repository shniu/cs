# 系统初始化

## X86 架构

* 计算机工作模式

![&#x8BA1;&#x7B97;&#x673A;&#x5DE5;&#x4F5C;&#x6A21;&#x5F0F;](https://static001.geekbang.org/resource/image/fa/9b/fa6c2b6166d02ac37637d7da4e4b579b.jpeg)

核心点有几个：

1. CPU （Central Processing Unit\)：主要的工作单元，核心驱动力，实际发生计算的地方
2. 总线 \(Bus\)：提供高速通道，其实就是主板上的各种集成电路和线路，主要有系统总线、内存总线、IO总线、南北桥芯片等
3. 内存 \(Memory\): 保存 CPU 计算的中间结果，缓存临时数据，CPU 可以进行下一步计算
4. 其他IO设备 \(Input and Output\)：提供其他能力，连接在总线上，比如网络通信能力、键盘鼠标、显示器等

CPU和内存是完成计算任务的核心组件，如何配合工作？

CPU包括：运算单元、数据单元和控制单元。

![CPU&#x548C;&#x5185;&#x5B58;&#x5982;&#x4F55;&#x914D;&#x5408;&#x5DE5;&#x4F5C;](https://static001.geekbang.org/resource/image/3a/23/3afda18fc38e7e53604e9ebf9cb42023.jpeg)

有几个要点（更加细节的地方，放在以后了解，这里的目的是了解工作模式）：

1. 一段程序的二进制文件起初是在磁盘上的，当以进程的形式运行起来后，二进制文件被加载到内存放在了进程的代码段，同时进程里还有数据段，用来存放执行过程中产生的数据，当进程得到运行时，代码段里的二进制会被load到CPU中，其实这个二进制就是CPU能够操作的指令
2. 如果有多个进程，是如何区分的呢？CPU的控制单元做这个事情，在控制单元里有**指令起始地址寄存器**和**数据起始地址寄存器**，这个很重要，装载了哪个进程的起始地址就是在执行哪个进程，当然这里是可以切换的，这个就是进程切换，更换进程的上下文信息就是替换这里的寄存器里存储的信息
3. CPU和内存如何配合工作，结果如何写回内存？控制单元里有不同种类的寄存器，每种寄存器都用不同的用途，除了 2 中的寄存器，还有一个**指令指针寄存器\(IP\)**，这里存放了要执行的下一条指令的内存地址，指令分为两部分：做什么操作和操作哪些数据，做什么操作的部分会交给运算单元，操作哪些数据的部分交给数据单元，数据单元负责把数据从内存的数据段加载到数据寄存器中，最后运算单元就可以执行指令操作，然后把计算结果放回数据单元，然后会有专门的指令把数据写回内存。
4. 在这个过程中，内存和CPU之间的数据传输，靠的是总线，要明白总线分为两类：地址总线和数据总线，是分离的，地址总线负责指明要访问的数据在内存的地址；数据总线负责传输数据，把数据写回内存或者从内存中加载到CPU的数据单元；此外，总线的位数，对地址总线来讲是可以操作的地址范围，对数据总线来讲是一次可以加载的数据的最大位数
5. 开放、标准、兼容的架构

有一段X86架构的发展历史。

8086 的原理

![8086&#x67B6;&#x6784;](https://static001.geekbang.org/resource/image/2d/1c/2dc8237e996e699a0361a6b5ffd4871c.jpeg)

要点：

1. 数据单元寄存器用来暂存数据，包含8个16位通用寄存器\(register\): AX, BX, CX, DX, SP, BP, SI, DI; 其中AX, BX, CX, DX分为高8位和低8位，可以存储较短的数据
2. 控制单元寄存器：IP \(指令指针寄存器，指向代码段中的下一个指令的地址，根据它不断从内存中加载到CPU的**指令队列缓存器**，然后交给运算单元\), CS \(Code Segment Reigster, 代码段寄存器，存储进程代码段的起始地址\), DS \(Data Segment Register, 数据段寄存器，存储进程数据段的起始地址\), SS \(Stack Segment Reigster, 栈寄存器，和函数调用相关的操作，都与栈有关系\)
3. 在计算过程中需要用到内存中的数据，如何处理？首先通过DS获取到数据段的地址，段内的具体位置是使用偏移量\(Offset\)来计算的, 其中代码段的偏移量在IP中，数据段的偏移量在数据单元的通用寄存器中
4. 8086 的地址总线是20位，数据总线16位，各个寄存器也是16位的，如何得到20位的地址呢？这个需要做一次地址计算，将CS中16位的地址左移4位，就是20位，然后再加上IP里的偏移量，所以8086的寻址空间是 1M \(2^20\), 数据段的大小是 64K，也就是说 8086 只支持 1M 的内存大小

再来看 32 位处理器

随着计算机的发展，位数越来越宽。在 32 位处理器中，有 32 位地址总线

![32&#x4F4D;&#x5BC4;&#x5B58;&#x5668;](https://static001.geekbang.org/resource/image/e3/84/e3f4f64e6dfe5591b7d8ef346e8e8884.jpeg)

要点：

在原来的基础上扩展为 32 位，但是要考虑标准和兼容

1. 数据单元中的通用寄存器，扩展为32位，就是 EAX, EBX, ECX, EDX, ESP, EBP, ESI, EDI, 其中为了兼容，EAX, EBX, ECX, EDX 还是保留了AX, BX, CX, DX的高低位
2. 控制单元的IP，扩展为 32 位的 EIP，同时兼容 IP
3. 控制单元的段寄存器变动较大，段寄存器仍然保留16位，但是存储的不是段的起始地址，而是段描述符，实际的段的起始地址放在内存中的一个表格中；起始地址的计算就是通过段寄存器中的段描述符取出在内存中的实际的其实地址，一般情况下会将表格加载到CPU的段描述符缓存器中，提高访问速度
4. 32 位处理器中，由于段寄存器的改动，分为实模式和保护模式，实模式是使用原来的方式，保护模式可以发挥32位处理器的更强大能力

![x86&#x67B6;&#x6784;&#x4E0B;&#x5BC4;&#x5B58;&#x5668;&#x548C;&#x6BB5;&#x7684;&#x5DE5;&#x4F5C;&#x6A21;&#x5F0F;](https://static001.geekbang.org/resource/image/e2/76/e2e92f2239fe9b4c024d300046536d76.jpeg)

这个要牢记！！！

* 熟悉一些汇编语言指令

mov

```text
// 数据传送指令
// :格式 mov dest,src
mov AL,DH  // DH -> AL 传送8 bit位数据，寄存器之间的数据传送
mov EAX,ESI  // ESI -> EAX 传送 32 bit 位数据

mov [BX],AX  // 间接寻址，AX -> [BX] 传送 16 位数据到指定内存中
mov AL,BLOCK  // 直接寻址，BLOCK -> AL 传送 8 位内存中的数据到AL寄存器

MOV EAX,12345678H  // 立即数 12345678H -> EAX  32 位
```

call

```text
// 实现对一个函数的调用
// 1. 将当前的IP或CS和IP压入栈中; 2. 跳转
// https://www.felixcloutier.com/x86/call
// 格式：call 指令
```

jmp

```text
// jmp 指令告诉CPU要跳转的新位置，这个是goto语句的本质，需要加载一个新的IP和CS，然后跳转到新的位置开始执行代码
// 参考 http://www.math.uaa.alaska.edu/~afkjm/cs221/handouts/irvine4-5.pdf
```

int

```text
// 中断指令
// 格式：INT n
// 产生中断类型码为n的软中断，该指令包含中断操作码和中断类型码两部分，中断类型码n为8位，取值范围为0～255(00H～FFH), 总共可以表示 256 个中断描述符,
// 每个中断描述符包含一个中断服务地址
// 这里产生的是软中断，软中断的执行过程：
　　· 将标志寄存器FLAGS(或EFLAGS)压入堆栈；
　　· 清除TF和IF标志位；
　　· CS，IP/EIP压入堆栈；
　　· 实模式下，n×4获取中断矢量表地址指针；保护模式下，n×8获取中断描述符表地址指针；
　　· 根据地址指针，从中断矢量表或中断描述符表中取出中断服务程序地址送IP/EIP和CS中，控制程序转移去执行中断服务程序。
```

ret

```text
// 返回指令，从函数调用中返回，取栈顶的地址返回，伴随 call 指令使用
// https://c9x.me/x86/html/file_module_x86_id_280.html
```

add

```text
// 算数运算加法指令
// :格式 add dest,src
// ADD是将源操作数与目的操作数相加，结果传送到目的操作数
// 源操作数可以是通用寄存器、存储器或立即数。目的操作数可以是通用寄存器或存储器操作数。
add AH,AL
```

or

```text
// 逻辑或指令
// or dest,src
// 目的操作数和源操作数按位进行逻辑或运算，结果存目的操作数中。源操作数可以是通用寄存器、存储器或立即数。目的操作数可以是通用寄存器或存储器操作数。
or AX,BX
```

xor

```text
// xor dest,src
// 目的操作数和源操作数按位进行逻辑异或运算，结果送目的操作数。源操作数可以是通用寄存器、存储器或立即数。目的操作数可以是通用寄存器或存储器操作数。
xor AX,BX
```

shl

```text
// 逻辑左移指令
// SHL DEST,OPRD
// 按照操作数OPRD规定的移位位数，对目的操作数进行左移操作，最高位移入CF中。每移动一位，右边补一位0
```

shr

```text
// 逻辑右移指令
// SHR DEST,OPRD
// 按照操作数OPRD规定的移位位数，对目的操作数进行右移操作，最低位移至CF中。每移动一位，左边补一位0
```

push

```text
// 进栈指令
// PUSH   SRC
// 将源操作数压下堆栈，源操作数允许为16位或32位通用寄存器、存储器和立即数以及16位段寄存器。
// 当操作数数据类型为字类型，压栈操作使SP值减2；当数据类型为双字类型，压栈操作使SP值减4
    PUSH AX　　　　　　　　　　；通用寄存器操作数入栈(16位)
　　PUSH EBX　　　　　　　　　 ；通用寄存器操作数入栈(32位)
　　PUSH [SI]　　　　　　　　　；存储器操作数入栈(16位)
　　PUSH DWORD PTR [DI]　　　　；存储器操作数入栈(32位)
　　PUSHW 0A123H　　　　　　　 ；立即数入栈(16位)
　　PUSHD 20H　　　　　　　　　；立即数入栈(32位)
```

pop

```text
// 出栈指令
// pop dest
// 从栈顶弹出操作数送入目的操作数。目的操作数允许为16或32位通用寄存器、存储器和16位段寄存器。
// 当操作数数据类型为字类型，出栈操作使SP加2；当操作数数据类型为双字类型，出栈操作使SP加4。
```

inc

```text
// 加1指令
// inc dest
```

dec

```text
// 减1指令
// dec dest
```

sub

```text
// 减法指令
// sub dest,src
// 将源操作数与目的操作数相减，结果传送到目的操作数
// 源操作数可以是通用寄存器、存储器或立即数。目的操作数可以是通用寄存器或存储器操作数。
sub AL,80H
```

cmp

```text
// 比较指令
// 目的操作数减源操作数，结果不回送。源操作数为通用寄存器、存储器和立即数。目的操作数为通用寄存器、存储器操作数。
// CMP指令影响标志位为OF，SF，ZF，AF，PF，CF。
CMP CX,3
```

## BIOS 到 Bootloader

要点:

BIOS（基本输入输出系统） 的关键流程：

首先需要理解 ROM 和 RAM，BIOS 是固化在 ROM 中的，一般在主板上，ROM 会映射到内存的虚拟地址空间

1. 主板加电后，CPU会做一个重置的工作，将 CS 设置为 ROM 地址空间里的某个地址，如 0xFFFF，将IP设置为 0x0000；第一条指令就是从重置后的CS中拿到的，通过 JMP 指令跳到ROM中执行固化程序
2. BIOS 程序会检查硬件等
3. BIOS 建立一个中断向量表和注册中断服务程序，以便能响应键盘、鼠标等外设的中断请求
4. BIOS 做完自己的事情，接下来就要去寻找操作系统在哪，如果找不到，就停在这了

Bootloader 要点

1. 在 BIOS 中会指定启动盘，这个启动盘里就存放了操作系统的镜像文件等
2. 启动盘一般在磁盘的第一个扇区，占 512 字节，以 0xAA55 结束；这里的内容一般由 Grub2 指定
3. Grub2 安装第一个 boot.img 到第一个扇区，也叫主引导分区 MBR
4. BIOS 完成任务后，接着加载 MBR 中的代码到内存的 0x7c00 的地址来运行
5. 加载 boot.img 后，接着会加载 core.img, boot.img 主要是用来做引导，不做太多其他的事情
6. 接着在 core.img 之上做更多的事情，lzma\_decompress.img、diskboot.img、kernel.img 和一系列的模块
7. 接着 boog.img 控制权交给 diskboot.img -&gt; diskboot.img load 其他部分 -&gt; 执行 lzma\_decompress.img -&gt; 解压缩 kernel.img -&gt; lzma\_decompress.img 调用 real\_to\_prot 转为保护模式加载更多的东西，这整个过程加载的都是 grub 内核，而不是 Linux 内核
8. 进入保护模式后，先启用分段再启动分页，分段的目的是将段寄存器变成段选择子，指向某个段描述符，启动分页是为了管理更多的内存空间
9. 然后会做一些列的操作，直到调用 Linux 内核启动代码

![bootloader](https://static001.geekbang.org/resource/image/2b/6a/2b8573bbbf31fc0cb0420e32d07b196a.jpeg) ![bios&#x5230;boolloader](https://static001.geekbang.org/resource/image/0a/6b/0a29c1d3e1a53b2523d2dcab3a59886b.jpeg)

Grub 的一些参考资料

1. \[GRUB\]\([https://wiki.archlinux.org/index.php/GRUB\_\(%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87](https://wiki.archlinux.org/index.php/GRUB_%28%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87)\)\)
2. [GNU GRUB Manual 2.04](https://www.gnu.org/software/grub/manual/grub/html_node/index.html#SEC_Contents)
3. [GNU GRUB Manual 翻译](https://www.cnblogs.com/f-ck-need-u/p/7094693.html#auto_id_7)

## 内核初始化

![start\_kernel](https://static001.geekbang.org/resource/image/cd/01/cdfc33db2fe1e07b6acf8faa3959cb01.jpeg)

![&#x7CFB;&#x7EDF;&#x8C03;&#x7528;1](https://static001.geekbang.org/resource/image/71/e6/71b04097edb2d47f01ab5585fd2ea4e6.jpeg)

系统调用过程就是下面这样的

![&#x7CFB;&#x7EDF;&#x8C03;&#x7528;2](https://static001.geekbang.org/resource/image/d2/14/d2fce8af88dd278670395ce1ca6d4d14.jpg)

## 系统调用

// todo

