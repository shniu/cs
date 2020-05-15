# 无锁方案 CAS

无锁方案高效的核心是 CPU 提供了 CAS\(Compare And Swap，即“比较并交换”\) 指令，由硬件直接支持，CAS 是一个原子指令。

[CAS Wiki](https://en.wikipedia.org/wiki/Compare-and-swap) 中有说明 CAS 是用于多线程中获取同步的原子指令，可以表示成以下形式：

```c
func cas(p : pointer to int, old : int, new : int) returns bool {
    if *p != old {
        return false
    }
    *p = new
    return true
}
```

如果利用 cas 来实现增加操作：

```c
function add(p : pointer to int, a : int) returns int {
    done = false
    // 这个是经典范式，自旋操作
    while not done {
        value = *p  // Even this operation doesn't need to be atomic.
        done = cas(p, value, value + a)
    }
    return value + a
}
```

使用 CAS 需要主要 ABA 问题。

## 原子类

Java 中的 JUC 提供了很多的原子类，如 AtomicLong.

```java
// AtomicLong 中的一个方法
final long getAndIncrement() {
    // 依赖的是 Unsafe 类的能力，CAS 功能由 JDK 提供了，我们只需要使用
    // Unsafe 就是 JDK 暴露出来的操作这些原子指令的接口
  return unsafe.getAndAddLong(
    this, valueOffset, 1L);
}

// Unsafe 类中的一个方法
public final long getAndAddLong(
  Object o, long offset, long delta){
  long v;
  do {
    // 读取内存中的值
    v = getLongVolatile(o, offset);
  } while (!compareAndSwapLong(
      o, offset, v, v + delta));
  return v;
}
//原子性地将变量更新为x
//条件是内存中的值等于expected
//更新成功则返回true
native boolean compareAndSwapLong(
  Object o, long offset,
  long expected,
  long x);
```

```java
// 可见经典范式是下面这样的
do {
  // 获取当前值
  oldV = xxxx；
  // 根据当前值计算新值
  newV = ...oldV...
} while(!compareAndSet(oldV,newV);
```

## 利用 CAS 来实现单例模式

单例模式的实现方式有很多种方式，大部分实现利用的是锁机制，如 synchronized，使用无锁方案也可以实现单例模式

```java
public class CASSingleton {
    // 原子类的对象引用
    private static final AtomicReference<CASSingleton> INSTANCE = new AtomicReference<>();

    // 私有化构造函数
    private CASSingleton() {
    }

    public static CASSingleton getInstance() {

        // 自旋，循环检查是否已经实例化
        for(;;) {
            CASSingleton instance = INSTANCE.get();
            if (null != instance) {
                return instance;
            }

            instance = new CASSingleton();
            // 利用 CAS，如果已经还是null，就设置为 instance；如果不是null，就循环检查
            if (INSTANCE.compareAndSet(null, instance)) {
                return instance;
            }
        }
    }
}
```

## Disruptor 中的无锁算法

Disruptor 是一个高性能的有界内存队列，其中入队的操作采用了无锁算法

```java
//生产者获取n个写入位置
do {
  //cursor类似于入队索引，指的是上次生产到这里
  current = cursor.get();
  //目标是在生产n个
  next = current + n;
  //减掉一个循环
  long wrapPoint = next - bufferSize;
  //获取上一次的最小消费位置
  long cachedGatingSequence = gatingSequenceCache.get();
  //没有足够的空余位置
  if (wrapPoint>cachedGatingSequence || cachedGatingSequence>current){
    //重新计算所有消费者里面的最小值位置
    long gatingSequence = Util.getMinimumSequence(
        gatingSequences, current);
    //仍然没有足够的空余位置，出让CPU使用权，重新执行下一循环
    if (wrapPoint > gatingSequence){
      LockSupport.parkNanos(1);
      continue;
    }
    //从新设置上一次的最小消费位置
    gatingSequenceCache.set(gatingSequence);
  } else if (cursor.compareAndSet(current, next)){
    //获取写入位置成功，跳出循环
    break;
  }
} while (true);
```

