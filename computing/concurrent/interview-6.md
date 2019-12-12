# 万能钥匙：管程

管程是一种并发访问共享资源的方式，在并发编程领域应用广泛。

## 信号量和管程

最先引入并发概念的软件是操作系统，OS 的出现让计算机进入了一个前所未有的时代，而多任务管理让计算机更进一步；多任务管理的OS中，最大的价值体现是多个任务之间相互配合完成复杂的事情，这势必会出现多个任务访问同一个资源的情况出现，当某个任务访问完了共享资源又如何通知其他的任务来执行呢？这里就引出了并发编程的两个核心问题：互斥和同步。

### 信号量模型

信号量是最先提出的模型，用来解决多线程并发访问共享资源的互斥和同步问题。基本模型如下：

1. 声明一个共享资源的数量 `sem`
2. 提供一个 P 操作可以对 `sem` 进行 `-1` 操作，当 `sem < 0` 时，当前线程进入阻塞等待状态
3. 提供一个 V 操作对 `sem` 进行 `+1` 操作，当 `sem < 0` 时，唤醒一个线程

其中 p 操作和 v 操作都是原语，原语是完成某种功能且不被分割不被中断执行的操作序列，是可由硬件实现的原子操作。信号量的实现模型：

```c
// 信号量数据模型
// 共享资源数和等待资源的进程链表
typedef struct {
  int value;  // 共享资源数目
  struct process * L;  // 进程链表，记录等待该资源的进程
} semaphore;

// 信号量的封装操作
// 对信号量数据模型的两个操作
void wait(semaphore S) {  // 这个就是 P 操作
  S.value--;
  if (S.value < 0) {
    add this process to S.L
    block(S.L);  // block 是阻塞原语，线程或进程进入阻塞状态，由os和硬件配合实现
  }
}

void signal(semaphore S) { // 这个就是 V 操作
  S.value++;
  if (S.value < 0) {
    remove this process from S.L
    wakeup(S.L);  // wakeup 是线程唤醒原语
  }
}
```

以上配合起来就可以解决同步和互斥的问题。

- 解决互斥问题

```c
// 解决互斥问题，可以初始信号量为 1
semaphore S = 1;

P1() {
    // ...
    P(S);
    临界区代码
    V(S);
    // ...
}


P2() {
    // ...
    P(S);
    临界区代码
    V(S);
    // ...
}
```

可见，P1 和 P2 竞争 S，只有通过 P 操作的进程方可执行，另一个进程需要等待，这就实现了互斥执行。

- 解决同步问题

```c
// 解决同步问题，可以初始信号量为 0
semaphore S = 0;

P1() {
  // ...
  x; // 做一些事情
  V(S); // signal(S), wakeup 一个线程
}

P2() {
  // ...
  P(S); // wait(S); 等待资源可访问
  y; // 做一些自己的事情
}
```

可见 P1 执行 sgnal 来通知 P2 可以继续运行了，P2 一直阻塞在 P(S) 处。


信号量的缺点是：

1. p操作和v操作必须成对出现，不正确的使用容易出现bug
2. pv 操作大量分散在各个进程中，不易管理，易发生死锁

信号量提供了方便的机制处理进程同步，但不正确的使用信号量仍会导致时序错误，且难以检测。如：

1. 先对信号量 signal() 再 wait() 违反了互斥请求
2. 对信号量始终调用 wait() 将导致死锁
3. 一个进程遗漏了 wait() 或 signal() 将导致死锁且可能破坏互斥

### 管程模型 （Monitor）

信号量已经很好的解决了同步和互斥的问题，但是为什么还要引入管程呢？从上面分析中我们直到信号量有一些缺点，所以引入管程的目的是：

1. 把分散在各进程中的临界区集中起来进行管理
2. 防止进程有意或无意的违法同步操作
3. 便于用高级语言来书写程序，也便于程序正确性验证

其实，简单的理解一下就是管程比信号量封装性更好，更容易编程和管理。总结一下，管程是对共享资源和对共享资源的操作进行封装，让它来支持并发，和 OO 的思想很接近啊。具体是如何使用和如何实现的呢？

管程的基本模型是：

1. 一组共享资源
2. 对共享资源的操作过程，只能由这些过程来改变共享资源，外部不可修改
3. 初始化内部资源的初始化过程

管程的互斥访问完全由编译程序在编译时自动添加，无需程序员关注，而且保证正确。

管程的实现模型：

```c
// monitor 具有封装性，只能通过调用编程接口对其进行修改
monitor X {
    // 条件变量
    condition cond[];
    
    // 等待队列
    queue waitQueue;
    
    // 操作
    wait();
    signal();
}
```

参考如下图

![](https://static001.geekbang.org/resource/image/83/65/839377608f47e7b3b9c79b8fad144065.png)

- 使用管程

管程使用的基本套路：

```java
lock lock;
synchronized (lock) {
    while (条件不满足) {  // MESA 管程模型的编程范式
        lock.wait();
    }
    执行逻辑
}

synchronized (lock) {
    做一些事情
    lock.notifyAll();
}
```

- 三种管程模型

三种模型最主要的区别是在使用 notify 之后如何处理

1. Hasen 模型：要求 notify() 放在代码的最后，这样 T2 通知完 T1 后，T2 就结束了，然后 T1 再执行，这样就能保证同一时刻只有一个线程执行
2. Hoare 模型：T2 通知完 T1 后，T2 阻塞，T1 马上执行；等 T1 执行完，再唤醒 T2，也能保证同一时刻只有一个线程执行。但是相比 Hasen 模型，T2 多了一次阻塞唤醒操作
3. MESA 模型：T2 通知完 T1 后，T2 还是会接着执行，T1 并不立即执行，仅仅是从条件变量的等待队列进到入口等待队列里面。这样做的好处是 notify() 不用放到代码的最后，T2 也没有多余的阻塞唤醒操作。但是也有个副作用，就是当 T1 再次执行的时候，可能曾经满足的条件，现在已经不满足了，所以需要以循环方式检验条件变量

- Java 中 synchronized 管程模型的实现

Java 中的管程模型是采用的 MESA 模型，而 synchronized 的管程实现做了简化，如下图：

![synchronized monitor implemention](https://static001.geekbang.org/resource/image/57/fa/57e4d94e90226b70be3d57024f5333fa.png)

1. Java 中的 synchronized 修饰的代码块在编译器会自动插入加锁和解锁的代码，monitorenter 和 monitorexit
2. synchronized 只支持一个条件变量，也就是 synchorinzed 加锁的对象
3. synchronized 是一个简化的管程模型

综上所述，信号量和管程实现了相同的功能，在功能上是等价的。但是管程比信号量机制更易用，更不容易出bug，所以我们优先选择管程。

## 几个并发领域的经典问题

### 1. 生产者-消费者问题

> 问题描述:
> 一组生产者进程和一组消费者进程共享一个初始为空、大小为n的缓冲区，只有缓冲区没满时，生产者才能把消息放入到缓冲区，否则必须等待；只有缓冲区不空时，消费者才能从中取出消息，否则必须等待。由于缓冲区是临界资源，它只允许一个生产者放入消息，或者一个消费者从中取出消息。

使用管程来解决生产者和消费者问题：

1. monitor 提供 wait，signal 操作
2. 条件变量 notFull 和 条件变量 notEmpty
3. 缓冲区 buf，最大为 n
4. 定义 count 记录当前数量
5. 提供生产者和消费者的入口

```java
class Lock {
    void acquire();  // enqueue
    void release();   // dequeue
}

class Condition {
    void wait();
    void signal();
    void signalAll();
}

class Buffer {
    Lock lock;
    int count = 0;
    Condition notFull, notEmpty;
    int MAX = n;
    
    void add(c) {
        lock.acquire();
        while (count == MAX)
            notFull.wait(lock);
            
        Add c to the buffer;
        count++;
        notEmpty.signal();
        lock.release();
    }
    
    Item get() {
        lock.acquire();
        while (count == 0)
            notEmpty.wait(lock);
            
        Remove c from buffer;
        count--;
        notFull.signal();
        lock.release();
    }
}

Buffer buffer;

// 生产者
void producer() {
    while (true) {
        item = produce();
        buffer.add(item);
    }
}

// 消费者
void consumer() {
    while (true) {
        item = buffer.get();
    }
}
```

### 2. 读者-写者问题
### 3. 哲学家进餐问题
### 4. 吸烟者问题

