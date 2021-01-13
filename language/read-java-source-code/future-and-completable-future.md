# 异步计算 Future

Future 代表的是一个异步计算的结果，在这个对象上提供了检测结果状态的方法，以及取计算结果的方式，并且只能通过 get 的方式来取到结果，同时必须等到结果准备好之后才能取到。Future 代表的异步计算结果也支持取消操作，但是不一定能取消成功，因为有可能已经计算好了。`FutureTask` 就是 Future 的一个实现

Executor 也是一个抽象，它将任务的提交从每个任务应该如何执行的细节中解耦出来，任务执行的细节包括使用哪个线程、线程如何调度、任务的分配策略、任务异常处理策略等，这样在使用线程执行任务时就有了非常方便的入口，同时应用程序使用 Executor 的抽象接口来完成面向具体业务场景的开发，JDK 的底层实现者提供好用的 Executor 实现（典型的面向接口编程、基于抽象而非具体实现），因为 Executor 的实现一是具备通用性，二是在并发控制方面非常复杂，不具备深厚的并发编程功底是做不好的。

Executor 只是任务执行器的一个抽象，并没有直接定义这个执行器必须是异步的，同步执行器也是允许的；通常情况下，执行器中的任务是在其他的线程中执行的，而非调用 Executor 的当前线程；`ExecutorService` 是 Executor 的一个实现，同时 `ThreadPoolExecutor` 提供了一个线程池版本的实现。

FutureTask 和 CompletableFuture 是 Future 的两个实现，其中 CompletableFuture 是一个更好的支持异步编程的实现，在 Future 的基础上提供了对算子的支持。

### FutureTask

FutureTask 代表了一个可被取消的异步计算任务，该类实现了Future 接口，比如提供了启动和取消任务、查询任务是否完成、获取计算结果的接口。

FutureTask 任务的结果只有当任务完成后才能获取，并且只能通过 get 系列方法获取，当结果还没出来时，线程调用 get 系列方法会被阻塞。另外，一旦任务被执行完成，任务将不能重启，除非运行时使用了runAndReset 方法。FutureTask 中的任务可以是 Callable 类型，也可以是 Runnable 类型（因为 FutureTask 实现了 Runnable 接口）,  FutureTask 类型的任务可以被提交到线程池执行。

* [ ] 基本使用

Java 在并发编程的实现中有一个固定的模型：状态、队列和 CAS，根据不同的应用场景灵活的使用这些核心元素来达到并发的控制。

#### FutureTask 中的状态

```java
private volatile int state;
private static final int NEW          = 0;
private static final int COMPLETING   = 1;
private static final int NORMAL       = 2;
private static final int EXCEPTIONAL  = 3;
private static final int CANCELLED    = 4;
private static final int INTERRUPTING = 5;
private static final int INTERRUPTED  = 6;
```

state 属性是整个 FutureTask 的核心属性，它代表了任务在运行过程中的状态，随着任务的执行，状态将不断地进行转变；任务的起始状态都是 NEW，中间状态为 COMPLETING / INTERRUPTING，其他为终止状态；任务的中间状态是一个瞬态，它非常的短暂，而且**任务的中间态并不代表任务正在执行，而是任务已经执行完了，正在设置最终的返回结果，所以**只要state不处于 `NEW` 状态，就说明任务已经执行完毕。

#### FutureTask 中的队列

```java
/** Treiber stack of waiting threads */
private volatile WaitNode waiters;
static final class WaitNode {
    volatile Thread thread;
    volatile WaitNode next;
    WaitNode() { thread = Thread.currentThread(); }
}
```

队列的实现是一个单向链表，它表示**所有等待任务执行完毕的线程的集合**。当获取结果的线程获取结果，任务还没有执行完成时，获取结果的线程就会在一个等待队列中挂起，直到任务执行完毕被唤醒，waiters 就是这个等待队列。winters 的设计比较有意思，他被设计成为一个单向链表结构，在实际运行中，它的行为表现的像一个 Treiber 栈，如下图：

![](../../.gitbook/assets/image%20%2891%29.png)

#### FutureTask 的 CAS 操作

CAS操作主要针对3个属性，包括`state`、`runner`和`waiters`，说明这3个属性基本是会被多个线程同时访问的。其中`state`属性代表了任务的状态，`waiters`属性代表了指向栈顶节点的指针。`runner`属性代表了执行FutureTask中的“Task”的线程。为什么需要一个属性来记录执行任务的线程呢？这是为了中断或者取消任务做准备的，只有知道了执行任务的线程是谁，我们才能去中断它。

#### 分析 FutureTask 的方法

* get\(\)

```java
// FutureTask 的获取结果的方法
public V get() throws InterruptedException, ExecutionException {
    int s = state;
    // FutureTask 的初始状态是 NEW，如果任务的状态还处在 NEW 或者 COMPLETING 的状态
    // 就等待结果
    if (s <= COMPLETING)
        // 等待任务处理完成
        s = awaitDone(false, 0L);
    return report(s);
}

private int awaitDone(boolean timed, long nanos)
    throws InterruptedException {
    // deadline 用来控制超时时间
    final long deadline = timed ? System.nanoTime() + nanos : 0L;
    // 调用 get 时，任务可能还没结束，q 就是表示一个等待任务的线程节点，可能会被
    // 加入到 Treiber 栈中
    WaitNode q = null;
    // 是否已经入队列
    boolean queued = false;
    // 自旋
    for (;;) {
        // 这里是为了响应调用任务的线程的中断
        if (Thread.interrupted()) {
            // 如果触发了中断，如果当前节点已经加入到 treiber stack 中的话，就删除掉
            removeWaiter(q);
            throw new InterruptedException();
        }

        int s = state;
        // 当前如果已经到了终态，就返回结果
        if (s > COMPLETING) {
            if (q != null)
                q.thread = null;
            return s;
        }
        // 如果任务状态正在设置中的话，说明任务已经执行完成了，这时理论上会非常块到达终态
        // 此时调用 yield 是获取任务结果的线程主动让出 CPU，然后后续再竞争 CPU，以期任务
        // 线程已经将结果处理完成
        else if (s == COMPLETING) // cannot time out yet
            Thread.yield();
            // 获取到 CPU 后，继续从这里开始执行
        else if (q == null)
            // 说明 q 既没有入队，而且还在等待任务结果，而且是第一次进到这里
            // 就创建一个新的等待节点，节点里的 thread 是当前等待任务结果的线程
            // 然后通过自旋进入到下一次循环和判断逻辑里
            q = new WaitNode();
        else if (!queued)
            // 到这里说明已经初始化了 node，但是还没有入队
            // 执行入队的动作，这里使用了 CAS 操作, 意思是
            // 将 q 的 next 指向原来的头，把新的头指向新节点 q
            queued = UNSAFE.compareAndSwapObject(this, waitersOffset,
                                                 q.next = waiters, q);
        else if (timed) {
            // 等待任务的线程是否设置了超时时间
            nanos = deadline - System.nanoTime();
            if (nanos <= 0L) {
                removeWaiter(q);
                return state;
            }
            LockSupport.parkNanos(this, nanos);
        }
        else
            // 如果没有设置超时线程，就一直阻塞，直到任务完成后，调用任务线程调用 unpark
            LockSupport.park(this);
    }
}
```

* finishCompletion

```java
private void finishCompletion() {
    // assert state > COMPLETING;
    for (WaitNode q; (q = waiters) != null;) {
        if (UNSAFE.compareAndSwapObject(this, waitersOffset, q, null)) {
            for (;;) {
                Thread t = q.thread;
                if (t != null) {
                    q.thread = null;
                    LockSupport.unpark(t);
                }
                WaitNode next = q.next;
                if (next == null)
                    break;
                q.next = null; // unlink to help gc
                q = next;
            }
            break;
        }
    }

    done();

    callable = null;        // to reduce footprint
}
```

* run\(\)

#### FutureTask 的局限

FutureTask 虽然提供了用来检查任务是否执行完成、等待任务执行结果、获取任务执行结果的方法，但是这些特色并不足以让我们写出简洁的并发代码，比如它并不能清楚地表达多个 FutureTask 之间的关系。另外，为了从 Future 获取结果，我们必须调用 get\(\) 方法，而该方法还是会在任务执行完毕前阻塞调用线程，显然这不是我们想要的全部。我们需要：

* 可以将两个或者多个异步计算结合在一起变成一个，这包含两个或者多个异步计算是相互独立的情况，也包含第二个异步计算依赖第一个异步计算结果的情况
* 对反应式编程的支持，也就是当任务计算完成后能进行通知，并且可以以计算结果作为一个行为动作的参数进行下一步计算，而不是仅仅提供调用线程以阻塞的方式获取计算结果。
* 可以通过编程的方式手动设置（代码的方式）Future的结果；FutureTask不能实现让用户通过函数来设置其计算结果，而是在其任务内部来进行设置
* 可以等多个Future对应的计算结果都出来后做一些事情

参考：

1. [FutureTask 源码解读](https://segmentfault.com/a/1190000016572591)

### CompletableFuture

CompletableFuture 是一个可以通过编程方式显式地设置计算结果和状态以便让任务结束的 Future，并且其可以作为一个CompletionStage（计算阶段），当它的计算完成时可以触发一个函数或者行为；当多个线程企图调用同一个CompletableFuture的complete、cancel方式时只有一个线程会成功。

* 当CompletableFuture任务完成后，同步使用任务执行线程来执行依赖任务结果的函数或者行为
* 所有异步的方法在没有显式指定Executor参数的情形下都是复用ForkJoinPool. commonPool\(\)线程池来执行
* 所有CompletionStage方法的实现都是相互独立的，以便一个方法的行为不会因为重载了其他方法而受影响

RxJava

### 参考

* [Java 异步编程](https://weread.qq.com/web/reader/44332cc071a486a7443c539kc81322c012c81e728d9d180)

