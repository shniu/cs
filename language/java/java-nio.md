---
description: 关于 Java NIO
---

# Java NIO

* [ ] [Linux IO 基础](https://xie.infoq.cn/article/0e36ad9712c8d9ad8f7a7c570)
* [ ] [Java 中的两种 IO 模型](https://xie.infoq.cn/article/e8ab7c9020253b83355c10661)
* [ ] [多种IO模型](https://xie.infoq.cn/article/1f44643161e1666b6a30b85e7)
* [ ] [彻底看破Java NIO](https://xie.infoq.cn/article/b9baa25c9d506e4a1cb459fe0?y=qun0522)
* [ ] [搞懂 Buffer](https://xie.infoq.cn/article/9e57819677d77f9a34852f6e9)

### MappedByteBuffer

> A direct byte buffer whose content is a memory-mapped region of a file. \(一个直接的字节缓冲区，其内容是文件的内存映射区域。\)

`MappedByteBuffer` 是通过 `FileChannel.map(...)` 进行创建的，一个映射的字节缓冲区和它所代表的文件映射一直有效，直到缓冲区本身被垃圾回收；映射的字节缓冲区的内容可以在任何时候改变，例如，如果映射文件的相应区域的内容被这个程序或其他程序改变了。 这种变化是否会发生，以及何时发生，都是取决于操作系统的，因此没有说明。映射的字节缓冲区的全部或部分可能在任何时候变得不可访问，例如如果映射的文件被截断。 试图访问映射的字节缓冲区中不可访问的区域不会改变缓冲区的内容，但会在访问时或以后的某个时间引起一个未指明的异常。 因此，强烈建议采取适当的预防措施，避免本程序或同时运行的程序对映射文件进行操作，但读取或写入文件内容除外。映射的字节缓冲区在其他方面的表现与普通的直接字节缓冲区没有区别。

### mmap

要理解 MappedByteBuffer 就需要很好的理解操作系统提供的内存映射技术 mmap，在 Linux 系统中提供了系统调用 `mmap` 来实现这项技术，`mmap` 可以将文件或者设备映射到内存中

