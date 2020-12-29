# Unsafe

### Unsafe

[Unsafe](http://hg.openjdk.java.net/jdk/jdk/file/a1ee9743f4ee/jdk/src/share/classes/sun/misc/Unsafe.java) 是位于 sun.misc 包下的一个类，主要**提供一些用于执行低级别、不安全操作的方法，如直接访问系统内存资源、自主管理内存资源等，这些方法在提升Java运行效率、增强Java语言底层资源操作能力方面起到了很大的作用**。但由于 Unsafe 类使 Java 语言拥有了类似 C 语言指针一样操作内存空间的能力，这无疑也增加了程序发生相关指针问题的风险。在程序中过度、不正确使用 Unsafe 类会使得程序出错的概率变大，使得 Java 这种安全的语言变得不再“安全”，因此对 Unsafe 的使用一定要慎重

> A collection of methods for performing low-level, unsafe operations. Although the class and all methods are public, use of this class is limited because only trusted code can obtain instances of it.

这是 Unsafe 类的注释，可见 JDK 是不推荐我们直接使用 Unsafe 的实例的，它是一系列低层次（比如针对内存的直接操作）操作的方法集合；所谓的 Unsafe 的含义是该类的实例可以绕开 JVM 的束缚直接操作内存，并且提供 CPU 指令 CAS 原子操作级别的支持，如果被一些不了解情况的人使用，会出现意想不到的异常情况，往往是致命的。

Java 的设计是不能直接操作底层操作系统，而是通过 native 方法去操作，这也是 JVM 的核心能力；但是 Unsafe 提供了一些更加高效的方式来使用 CPU 指令和操作内存空间。

#### Unsafe 实例

```java
// 私有化构造函数，禁止了直接 new
private Unsafe() {}
// 静态的不可变对象
private static final Unsafe theUnsafe = new Unsafe();
@CallerSensitive
public static Unsafe getUnsafe() {  // 获取Unsafe实例
    // 判断类加载器是不是 Bootstrap ClassLoader，不是的话直接抛出不安全的异常
    Class<?> caller = Reflection.getCallerClass();
    if (!VM.isSystemDomainLoader(caller.getClassLoader()))
        throw new SecurityException("Unsafe");
    return theUnsafe;
}

// 使用
Unsafe U = Unsafe.getUnsafe();
```

一般情况下我们很难使用 Unsafe.getUnsafe\(\)，因为它需要类的加载器是 Bootstrap 加载器，所以在实际开发中使用最多的是使用反射的方式：

```java
// 使用反射的方式来获取到 unsafe 的实例
private static Unsafe reflectGetUnsafe() {
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      return (Unsafe) field.get(null);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
}
```

#### Unsafe 的功能分类

![Unsafe &#x529F;&#x80FD;&#x5206;&#x7C7B;](../../.gitbook/assets/image%20%2884%29.png)

**对象操作**

```java
//返回对象成员属性在内存地址相对于此对象的内存地址的偏移量
public native long objectFieldOffset(Field f);
//获得给定对象的指定地址偏移量的值，与此类似操作还有：getInt，getDouble，getLong，getChar等
public native Object getObject(Object o, long offset);
//给定对象的指定地址偏移量设值，与此类似操作还有：putInt，putDouble，putLong，putChar等
public native void putObject(Object o, long offset, Object x);
//有序、延迟版本的putObjectVolatile方法，不保证值的改变被其他线程立即看到。只有在field被volatile修饰符修饰时有效
public native void putOrderedObject(Object o, long offset, Object x);
//绕过构造方法、初始化代码来创建对象
public native Object allocateInstance(Class<?> cls) throws InstantiationException;
// 本地方法，得搞明白 o 是什么，offset 又是什么
public native int getInt(Object o, long offset);
```

这个方法的是在给定的对象中，通过offset来获取对应位置成员变量的值。可以分为三种场景：

1. 获取实例对象的成员
2. 获取类的静态成员
3. 获取数组类型的成员

**CAS 操作**

`double-regiseter` 模式是一种双地址模式，需要 o 和 offset 一起配合才能找到要操作的对象（或者内存地址），JVM 堆中的对象一般需要这种方式来操作。

什么是 CAS?  即比较并替换，实现并发算法时常用到的一种技术。CAS 操作包含三个操作数 —— 内存位置、预期原值及新值。执行 CAS 操作的时候，将内存位置的值与预期原值比较，如果相匹配，那么处理器会自动将该位置值更新为新值，否则，处理器不做任何操作。我们都知道，CAS 是一条 CPU 的原子指令（cmpxchg指令），不会造成所谓的数据不一致问题，Unsafe 提供的 CAS 方法（如compareAndSwapXXX）底层实现即为 CPU 指令 cmpxchg。

```java
// 对象的 CAS 操作，o 的 offset 位置是一个对象成员，如果是excepted就更新为x
public final native boolean compareAndSwapObject(Object o, long offset,
                                                    Object expected,
                                                    Object x);
// 对象的 CAS 操作，o 的 offset 位置是一个int类型的成员
public final native boolean compareAndSwapInt(Object o, long offset,
                                                int expected,
                                                int x);
// 对象的 CAS 操作，o 的 offset 位置是一个long类型的成员
public final native boolean compareAndSwapLong(Object o, long offset,
                                                long expected,
                                                long x);

// 自旋+CAS的典型用法，lock-free算法的精髓所在
public final int getAndAddInt(Object o, long offset, int delta) {
    int v;
    // 自旋判断是期望的最新值就更新，如果不是就自旋，直到成功
    do {
        // 取到最新的值
        v = getIntVolatile(o, offset);
        // 尝试增加 delta，因为在多线程情况下，有可能存在竞态条件，使用自旋不断尝试
    } while (!compareAndSwapInt(o, offset, v, v + delta));
    return v;
}
// 和 getAndAddInt 类似
public final long getAndAddLong(Object o, long offset, long delta) {
    long v;
    do {
        v = getLongVolatile(o, offset);
    } while (!compareAndSwapLong(o, offset, v, v + delta));
    return v;
}
// 原子性的设置指定位置的值为新的值，同样是自旋 + CAS
public final int getAndSetInt(Object o, long offset, int newValue) {
    int v;
    do {
        v = getIntVolatile(o, offset);
    } while (!compareAndSwapInt(o, offset, v, newValue));
    return v;
}
public final long getAndSetLong(Object o, long offset, long newValue) {
    long v;
    do {
        v = getLongVolatile(o, offset);
    } while (!compareAndSwapLong(o, offset, v, newValue));
    return v;
}
 public final Object getAndSetObject(Object o, long offset, Object newValue) {
    Object v;
    do {
        v = getObjectVolatile(o, offset);
    } while (!compareAndSwapObject(o, offset, v, newValue));
    return v;
}
```

**线程调度操作**

```java
// 这是和线程调度相关的本地方法，和线程的状态切换有关系
// unpark 是将某个线程从 WAITING or TIMED_WAITING 状态激活，线程重新可以被调度，在就绪队列排队
// 竞争CPU
public native void unpark(Object thread);

// park 将某个线程从 Runnable 状态转成 WAITING or TIMED_WAITING，
// 线程等待被激活或者超时时间到后进入就绪队列
public native void park(boolean isAbsolute, long time);

// 方法 park、unpark 即可实现线程的挂起与恢复，将一个线程进行挂起是通过 park 方法实现的，
// 调用 park 方法后，线程将一直阻塞直到超时或者中断等条件出现；
// unpark 可以终止一个挂起的线程，使其恢复正常。
```

关于 `park` 和 `unpark` 的基本能力是线程的挂起和恢复，其中在 AQS 中得到充分的应用，通过 `LockSupport.park` 和 `LockSupport.unpark` 实现，而 `LockSupport` 又基于 `Unsafe` 实现。

**volatile 操作**

```java
// 从对象的指定偏移量处获取变量的引用，使用 volatile 的加载语义
public native Object getObjectVolatile(Object o, long offset);

// 存储变量的引用到对象的指定的偏移量处，使用 volatile 的存储语义
public native void    putObjectVolatile(Object o, long offset, Object x);
public native int     getIntVolatile(Object o, long offset);
public native void    putIntVolatile(Object o, long offset, int x);
// ...
```

`volatile` 的语义是禁用缓存，禁用指令重排序，底层是使用内存屏障的方式实现的；CPU 和内存之间还有一层高速缓存，CPU直接操作的数据是在缓存中的，有时候未必会及时刷新到内存中，而 `volatile` 类型的变量高速CPU，改了这个值要强制刷新到内存里，取这个值的时候也去内存再取最新的出来。这样在多线程并发的场景中，对于 `voliatile` 变量的写 happens-before 对这个变量的读，就解决了可见性问题。

这些方法让不是 `volatile` 的变量也有 `volatile` 的能力。

**内存屏障**

内存屏障是 ****CPU 或编译器在对内存随机访问的操作中的一个同步点，使得此点之前的所有读写操作都执行后才可以开始执行此点之后的操作，避免代码重排序。

```java
// 内存屏障，禁止load操作重排序。屏障前的load操作不能被重排序到屏障后，屏障后的load操作不能被重排序到屏障前
// 这就保证了对某个变量的读，然后才能写
public native void loadFence();
// 内存屏障，禁止store操作重排序。屏障前的store操作不能被重排序到屏障后，屏障后的store操作不能被重排序到屏障前
// 这就保证了对某个变量的写，然后才能读
public native void storeFence();
//内存屏障，禁止load、store操作重排序
public native void fullFence();
```

`StampedLock` 是一个改进版的读写锁，内部使用就使用了内存屏障，可以去看看。

**内存管理（堆外内存）**

```java
//分配内存, 相当于C++的malloc函数
public native long allocateMemory(long bytes);
//扩充内存
public native long reallocateMemory(long address, long bytes);
//释放内存
public native void freeMemory(long address);
//在给定的内存块中设置值
public native void setMemory(Object o, long offset, long bytes, byte value);
//内存拷贝
public native void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes);
//获取给定地址值，忽略修饰限定符的访问限制。与此类似操作还有: getInt，getDouble，getLong，getChar等
public native Object getObject(Object o, long offset);
//为给定地址设置值，忽略修饰限定符的访问限制，与此类似操作还有: putInt,putDouble，putLong，putChar等
public native void putObject(Object o, long offset, Object x);
//获取给定地址的byte类型的值（当且仅当该内存地址为allocateMemory分配时，此方法结果为确定的）
public native byte getByte(long address);
//为给定地址设置byte类型的值（当且仅当该内存地址为allocateMemory分配时，此方法结果才是确定的）
public native void putByte(long address, byte x);
```

主要针对堆外内存的分配、拷贝、释放、操作内存等；通常，我们在Java中创建的对象都处于堆内内存（heap）中，堆内内存是由 JVM 所管控的 Java 进程内存，并且它们遵循 JVM 的内存管理机制，JVM 会采用垃圾回收机制统一管理堆内存。与之相对的是堆外内存，存在于 JVM 管控之外的内存区域，Java 中对堆外内存的操作，依赖于 Unsafe 提供的操作堆外内存的 native 方法。

为什么要使用堆外内存？

1. 对垃圾回收停顿的改善。由于堆外内存是直接受操作系统管理而不是JVM，所以当我们使用堆外内存时，即可保持较小的堆内内存规模。从而在 GC 时减少回收停顿对于应用的影响。
2. 提升程序 I/O 操作的性能。通常在 I/O 通信过程中，会存在堆内内存到堆外内存的数据拷贝操作，对于需要频繁进行内存间数据拷贝且生命周期较短的暂存数据，都建议存储到堆外内存。

#### **参考资料**

* [Java魔法类：Unsafe应用解析](https://tech.meituan.com/2019/02/14/talk-about-java-magic-class-unsafe.html) \(重点阅读\)
* [JDK Unsafe 源码完全注释](https://my.oschina.net/editorial-story/blog/3019773) \(重点阅读\)
* [Unsafe source code](http://hg.openjdk.java.net/jdk/jdk/file/a1ee9743f4ee/jdk/src/share/classes/sun/misc/Unsafe.java)
* [Java Magic. Part 4: sun.misc.Unsafe](http://ifeve.com/sun-misc-unsafe/)
* [跟我一起剖析 Java 并发源码之 Unsafe](https://juejin.im/post/5921927c44d904006cca9720)
* [Java 中神奇的双刃剑 - Unsafe](https://www.cnblogs.com/throwable/p/9139947.html)
* [JVM 堆外内存分析](http://lovestblog.cn/blog/2015/05/12/direct-buffer/)

