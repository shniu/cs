# 异步计算 Future

Future 代表的是一个异步计算的结果，在这个对象上提供了检测结果状态的方法，以及取计算结果的方式，并且只能通过 get 的方式来取到结果，同时必须等到结果准备好之后才能取到。Future 代表的异步计算结果也支持取消操作，但是不一定能取消成功，因为有可能已经计算好了。`FutureTask` 就是 Future 的一个实现

Executor 也是一个抽象，它将任务的提交从每个任务应该如何执行的细节中解耦出来，任务执行的细节包括使用哪个线程、线程如何调度、任务的分配策略、任务异常处理策略等，这样在使用线程执行任务时就有了非常方便的入口，同时应用程序使用 Executor 的抽象接口来完成面向具体业务场景的开发，JDK 的底层实现者提供好用的 Executor 实现（典型的面向接口编程、基于抽象而非具体实现），因为 Executor 的实现一是具备通用性，二是在并发控制方面非常复杂，不具备深厚的并发编程功底是做不好的。

Executor 只是任务执行器的一个抽象，并没有直接定义这个执行器必须是异步的，同步执行器也是允许的；通常情况下，执行器中的任务是在其他的线程中执行的，而非调用 Executor 的当前线程；`ExecutorService` 是 Executor 的一个实现，同时 `ThreadPoolExecutor` 提供了一个线程池版本的实现。

FutureTask 和 CompletableFuture 是 Future 的两个实现，其中 CompletableFuture 是一个更好的支持异步编程的实现，在 Future 的基础上提供了对算子的支持。

### FutureTask

FutureTask 是一个可取消的异步计算，它提供了 Future 的一个基本实现: 启动和取消一个异步计算



参考：

1. [FutureTask 源码解读](https://segmentfault.com/a/1190000016572591)

### CompletableFuture


