
# 操作系统 Operating System

对 OS 的学习和总结，主要以 Linux 操作系统为主。

## 学习指南

OS 的前置知识

1. C语言
2. 数据结构和算法
3. 编译原理
4. 计算机组成原理

学习方法

- 态度，不要等一切都准备好了再前行
- 三遍学习法 + PPT 笔记法 （由于自己没有过基于Linux的API设计程序，也没有专门研究过OS，采用三遍学习法是比较合适的）

三遍学习法：先读薄，再读厚，最后读薄。

1. 先读薄：一开始不要太纠结细节，尤其是一些内核代码不要太在乎细节，也有可能看不懂；重点是了解OS的原理和流程
2. 再读厚：遇到不会的不懂的就要死磕到底，见山开路，遇水搭桥；还是要以OS为主线去了解那些不懂的知识点，一旦搞定那个不懂的知识点，就回来继续OS的主线部分；攻克知识点的时候，可以考虑使用ppt笔记法，将每个知识点都编号，一个一个攻克
3. 最后读薄：把这些知识真正的变成自己的，复习，然后自己尝试去做总结，做到融会贯通

## 内容

- [00 操作系统基础知识](00-操作系统基础知识.md)
- [01 系统初始化](01-系统初始化.md)
- [中断和异常](https://chyyuu.gitbooks.io/ucore_os_docs/content/lab1/lab1_3_3_2_interrupt_exception.html)

- 关于内存

1. [The 10 Operating System Concepts Software Developers Need to Remember](https://medium.com/cracking-the-data-science-interview/the-10-operating-system-concepts-software-developers-need-to-remember-480d0734d710)
2. [每个程序员都应该了解的内存知识 Part1](https://www.oschina.net/translate/what-every-programmer-should-know-about-memory-part1)
3. [每个程序员都应该了解的内存知识 Part2 CPU 高速缓存](https://www.oschina.net/translate/what-every-programmer-should-know-about-cpu-cache-part2)
4. [每个程序员都应该了解的内存知识 Part 3 虚拟内存](https://www.oschina.net/translate/what-every-programmer-should-know-about-virtual-memory-part3)
5. [What Every programmer should know about memory](https://lwn.net/Articles/250967/)
6. [Memory Barriers: a Hardware View for Software Hackers](http://irl.cs.ucla.edu/~yingdi/web/paperreading/whymb.2010.06.07c.pdf)

[Operating System Concepts 10th](https://codex.cs.yale.edu/avi/os-book/OS10/index.html)

## 参考资料

```text
Linux 内核手册 https://www.kernel.org/doc/html/latest/
Glibc https://www.gnu.org/software/libc/started.html

x86架构导读 http://www.cs.virginia.edu/~evans/cs216/guides/x86.html
```

- 动手试验

```text
https://github.com/chyyuu/os_course_info 清华大学操作系统课程(2019)
OS kernel labs for operating systems course in Tsinghua University https://github.com/chyyuu/ucore_os_lab
uCore OS实验指导书和源码网址 (2019)  https://chyyuu.gitbooks.io/ucore_os_docs/content/
http://www.xuetangx.com/courses/course-v1:TsinghuaX+30240243X+sp/courseware/be5b8d4fec0c4c329d19845020bc67b2/
试验环境 https://www.shiyanlou.com/courses/221/learning/?id=710

nasm https://cs.lmu.edu/~ray/notes/nasmtutorial/  http://www.nasm.us/
```

- video

```text
汇编从零开始到C语言: https://study.163.com/course/introduction.htm?courseId=1640004#/courseDetail?tab=1
汇编语言论坛 http://www.asmedu.net/bbs/forum.jsp
```

### 博客专栏

#### 汇编相关

- [x86 Assembly Guide](http://www.cs.virginia.edu/~evans/cs216/guides/x86.html)

### OS 相关的书

```text
《自己动手写操作系统》
《UNIX 环境高级编程》
《一个操作系统的实现》
《系统虚拟化原理与实现》
《深入理解Linux虚拟内存管理》
《深入理解Linux内核》
《深入Linux内核架构》
《穿越计算机的迷雾》
《程序员的自我修养：链接、装载与库》
《操作系统真象还原》
《操作系统设计与实现》
《x86汇编语言：从实模式到保护模式》
《linux内核设计的艺术图解》
《Linux设备驱动开发详解》
《Linux内核完全注释》
《Linux内核设计与实现》
《Linux多线程服务端编程》
《Linux 内核分析及编程》
《IBM PC汇编语言程序设计》
《深入理解计算机系统》
《性能之巅：洞悉系统、企业与云计算》
《Linux内核协议栈源代码解析》
《UNIX网络编程》
《Linux/UNIX系统编程手册》
《深入Linux设备驱动程序内核机制》
《深入理解Linux驱动程序设计》
《Linux Device Drivers》
《TCP/IP详解卷》
《The TCP/IP Guide》
《深入理解LINUX网络技术内幕》
《Linux内核源代码情景分析》
《UNIX/Linux系统管理技术手册》
Operating Systems: Three Easy Pieces http://pages.cs.wisc.edu/~remzi/OSTEP/
一个64位操作系统的设计与实现
从实模式到保护模式
汇编语言第三版
汇编语言：基于x86处理器
```
