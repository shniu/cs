---
description: 关于 Java NIO
---

# Java NIO

* [ ] [Linux IO 基础](https://xie.infoq.cn/article/0e36ad9712c8d9ad8f7a7c570)
* [ ] [Java 中的两种 IO 模型](https://xie.infoq.cn/article/e8ab7c9020253b83355c10661)
* [ ] [多种IO模型](https://xie.infoq.cn/article/1f44643161e1666b6a30b85e7)
* [ ] [彻底看破Java NIO](https://xie.infoq.cn/article/b9baa25c9d506e4a1cb459fe0?y=qun0522)
* [x] [搞懂 Buffer](https://xie.infoq.cn/article/9e57819677d77f9a34852f6e9)

### ByteBuffer

Buffer 在 Java 里表示一块缓冲区，是一个线性的、指定原始类型的有限元素序列；Buffer 的本质是：capacity, limit, position。可以这么理解 Buffer 是在 Java 中实现的对一块连续内存的读写封装，它提供了一些列的接口来操作这块内存，这个内存在 Java 里可以抽象的理解为一个原始类型的数组，如 ByteBuffer 是对字节数组 `byte[]` 的封装。

要更深入的理解 Buffer，还需要下钻到操作系统层面，也就是 Buffer 如何分配内存以及该部分内存如何被回收，在抽象类 `Buffer` 中并没有定义，需要看具体的子类实现，也就是说 Buffer 的抽象并不关心是堆内存还是直接内存，依赖于具体的实现，Buffer 只关注它自己要做的事情（就是维护一块内存区域可读可写的范围、位置和上限等）， `Buffer` 中重要的接口：

```java
public abstract class Buffer {
    public final int position() {...}
    public final Buffer position(int newPosition){...}
    public final Buffer limit(int newLimit) {...}
    public final Buffer mark() {...}
    public final Buffer reset() {...}
    public final Buffer clear() {...}
    public final Buffer flip() {...}
    ...
}
```

ByteBuffer 是 Buffer 中的诸多实现中使用频率最高的。它同时扩展了 Buffer，提供了读写 Buffer 的接口

```text
public abstract byte get();
public abstract byte get(int index);
public abstract ByteBuffer put(byte b);
public abstract ByteBuffer put(byte b);
...
```

在 ByteBuffer 的实现中，有两类实现：堆内存 Buffer 和直接内存 Buffer

* 堆内存就不说了，直接在堆上分配，受 JVM 的垃圾回收机制管理，也同样占用堆内存的大小
* 直接内存，也叫堆外内存，可以通过 JVM 参数 `-XX:MaxDirectMemorySize` 来限制，**默认堆外内存大小是-Xmx减去一个Survivor区的内存量**

使用堆外内存有两个点需要关注，如何分配堆外内存和如何回收对外内存

```java
// 分配堆外内存
ByteBuffer.allocateDirect(capacity);

// 调用下面的方法
public static ByteBuffer allocateDirect(int capacity) {
    return new DirectByteBuffer(capacity);
}

// DirectByteBuffer 的构造函数里
DirectByteBuffer() {
    // ....
    try {
        // 调用 unsafe.allocateMemory 直接分配内存
        // 这里调用了 OS 提供的接口，在 Linux 下是 malloc 系统调用函数
        base = unsafe.allocateMemory(size);
    } catch (OutOfMemoryError x) {
        Bits.unreserveMemory(size, cap);
        throw x;
    }
    
    // ...

}
```

关于 unsafe 可以参考 [Java 源码阅读/Unsafe](../read-java-source-code/unsafe.md).

但是在回收堆外内存时，使用了 Cleaner，这里比较有意思，最终 Cleaner 是被 Reference Handler 线程监控，去调用 cleaner.clean\(\) 方法，clean 方法中调用的是 trunk.run\(\)，在 DirectByteBuffer 的场景里就是 Deallocator.run\(\)，Deallocator 实现了 Runnable 接口

```java
// 在 DirectByteBuffer 的构造函数里有如下定义
cleaner = Cleaner.create(this, new Deallocator(base, size, cap));

// 在 Reference.java 中启动了一个 Reference Handler 线程
public abstract class Reference<T> {
     static {
         Thread handler = new ReferenceHandler(tg, "Reference Handler");
        /* If there were a special system-only priority greater than
         * MAX_PRIORITY, it would be used here
         */
        handler.setPriority(Thread.MAX_PRIORITY);
        handler.setDaemon(true);
        handler.start();
     }
}

// ReferenceHandler.java
public void run() {
    while (true) {
        tryHandlePending(true);
    }
}

static boolean tryHandlePending(boolean waitForNotify) {
    //...
    // Fast path for cleaners
    if (c != null) {
        c.clean();
        return true;
    }
    //...
}

// Cleaner.clean()
try {
    // thunk 就是创建 Cleaner 对象时的 Runnable 实例
    this.thunk.run();
} ....

// Deallocator
class Deallocator {
    public void run() {
        if (address == 0) {
            // Paranoia
            return;
        }
        // 调用 unsafe 的 freeMemory
        unsafe.freeMemory(address);
        address = 0;
        Bits.unreserveMemory(size, capacity);
    }
}
```

从整个过程来看，堆外内存的回收由 ReferenceHandler 线程来控制，当然我们也可以手动回收 `directByteBuffer.getCleaner().clean()` 。

总结

ByteBuffer 没有什么神秘的，不管是堆内还是堆外，对于 Buffer 本身而言只是被 JVM 管理的方式不同，以及占用的内存区域是不一样的；此外，堆外内存和堆内的一个不同是在使用时，在 read 和 write 时可能会少一些内存 copy，比如 fileChannel 的 transferTo 和 transferFrom

### FileChannel



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



