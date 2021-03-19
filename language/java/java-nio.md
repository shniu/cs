---
description: 关于 Java NIO
---

# Java NIO

* [ ] [Linux IO 基础](https://xie.infoq.cn/article/0e36ad9712c8d9ad8f7a7c570)
* [ ] [Java 中的两种 IO 模型](https://xie.infoq.cn/article/e8ab7c9020253b83355c10661)
* [ ] [多种IO模型](https://xie.infoq.cn/article/1f44643161e1666b6a30b85e7)
* [ ] [彻底看破Java NIO](https://xie.infoq.cn/article/b9baa25c9d506e4a1cb459fe0?y=qun0522)
* [ ] [搞懂 Buffer](https://xie.infoq.cn/article/9e57819677d77f9a34852f6e9)

### ByteBuffer

Buffer 在 Java 里表示一块缓冲区，是一个线性的、指定原始类型的有限元素序列；Buffer 的本质是：capacity, limit, position。可以这么理解 Buffer 是在 Java 中实现的对一块连续内存的读写封装，它提供了一些列的接口来操作这块内存，这个内存在 Java 里可以抽象的理解为一个字节数组 `byte[]` 。

### MappedByteBuffer

> A direct byte buffer whose content is a memory-mapped region of a file. \(一个直接的字节缓冲区，其内容是文件的内存映射区域。\)

`MappedByteBuffer` 是通过 `FileChannel.map(...)` 进行创建的，一个映射的字节缓冲区和它所代表的文件映射一直有效，直到缓冲区本身被垃圾回收；映射的字节缓冲区的内容可以在任何时候改变，例如，如果映射文件的相应区域的内容被这个程序或其他程序改变了。 这种变化是否会发生，以及何时发生，都是取决于操作系统的，因此没有说明。映射的字节缓冲区的全部或部分可能在任何时候变得不可访问，例如如果映射的文件被截断。 试图访问映射的字节缓冲区中不可访问的区域不会改变缓冲区的内容，但会在访问时或以后的某个时间引起一个未指明的异常。 因此，强烈建议采取适当的预防措施，避免本程序或同时运行的程序对映射文件进行操作，但读取或写入文件内容除外。映射的字节缓冲区在其他方面的表现与普通的直接字节缓冲区没有区别。

### mmap

要理解 MappedByteBuffer 就需要很好的理解操作系统提供的内存映射技术 mmap，在 Linux 系统中提供了系统调用 `mmap` 来实现这项技术，`mmap` 可以将文件或者设备映射到内存中，当调用 `mmap` 时，在调用进程的虚拟内存空间中会分配一块内存做映射，如果不指定内存映射的起始内存地址，内核会选择一个合理的地址，同时 `mmap` 需要传入一个要被映射的文件的 fd，当 `mmap` 调用完成后，就把这个进程的这块虚拟内存映射到了磁盘上的一个具体文件上，它们之间就建立了实际的关联，这个时候就可以关闭掉 fd，因为后续对这块内存的操作就是对磁盘上文件的操作，就不会再去走文件系统的那套流程了；内存映射之间的最小操作单位也是页，映射分为几个阶段：

1. 进程启动映射过程，并在虚拟地址空间中创建一个虚拟的文件映射区
2. 调用内核的映射函数，实现物理文件和虚拟地址之间的映射关系
3. 映射关系建立后，只有在进程实际访问这个虚拟的文件映射区时才会真正的读写磁盘上被映射的文件，如果发现要读写的页不在内存中，就会引发缺页异常，然后从磁盘中把该页数据加载到内存中；这个过程有可能会用到 OS 的 Buffer，因为是直接操作的块设备

> mmap是一种内存映射文件的方法，即将一个文件或者其它对象映射到进程的地址空间，实现文件磁盘地址和进程虚拟地址空间中一段虚拟地址的一一对映关系。实现这样的映射关系后，进程就可以采用指针的方式读写操作这一段内存，而系统会自动回写脏页面到对应的文件磁盘上，即完成了对文件的操作而不必再调用read,write等系统调用函数。相反，内核空间对这段区域的修改也直接反映用户空间，从而可以实现不同进程间的文件共享

资料参考：

1. [mmap 是什么，怎么用](https://www.cnblogs.com/huxiao-tee/p/4660352.html)
2. [Linux man mmap](https://man7.org/linux/man-pages/man2/mmap.2.html)
3. [Java mmap](https://cloud.tencent.com/developer/article/1031860)
4. [Java 中的内存映射](https://leokongwq.github.io/2019/09/12/java-mmap.html) 
5. [Java File mmap 总结](https://blog.csdn.net/zhxdick/article/details/81130102)



