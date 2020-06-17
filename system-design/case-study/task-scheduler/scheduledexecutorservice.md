# ScheduledExecutorService

Executor 是一个任务执行器抽象，它提供了一种解耦机制：将任务的提交和任务的调度、执行分离，执行器的使用者只需要将任务通过编程接口提交到执行器中，并不需要关心任务本身是如何被管理的、如何被调度的、如何执行的等问题，大大降低了使用和心智负担；同时，Executor 也为我们屏蔽了直接创建线程，只需要这么使用：

```java
Executor executor = ...;
executor.execute(() -> {
   // task
});
```

Executor 的实现可以很灵活，可以使用单线程、可以使用线程池等等, execute 提交的任务会在未来的某个时间被执行。

ExecutorService 是一个接口，是对 Executor 的能力扩展，提供了很多额外的能力，如：

1. 返回 `Future` 对象来追踪一个或多个异步执行的任务
2. 管理 terminate 操作，比如优雅关闭执行器等
3. `ExecutorService` 可以被关闭，这样就会拒绝新的任务提交，关闭的操作有两个: `shutdown` 和 `shutdownNow` 

```java
public interface ExecutorService extends Executor {
    void shutdown();
    List<Runnable> shutdownNow();
    boolean isShutdown();
    boolean isTerminated();
    boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException;
    <T> Future<T> submit(Callable<T> task);
    <T> Future<T> submit(Runnable task, T result);
    Future<?> submit(Runnable task);
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException;
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                  long timeout, TimeUnit unit)
        throws InterruptedException;
    <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException;
    <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                    long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```

可见，ExecutorService 提供了更加丰富的功能，有执行器关闭的管理、任务提交可以追踪执行状态、可以批量提交任务并执行、可以关闭时等待一定时间等

ScheduledExecutorService 也是一个接口定义，在 ExecutorService 的基础之上提供了在给定延时的时间上执行任务的能力和周期性执行任务的能力；比如 `schedule` 和 `scheduleAtFixedRate` 等可以创建各种形式的延时执行任务，并返回任务对象来跟踪任务的执行状态

```java
public interface ScheduledExecutorService extends ExecutorService {
    // 创建并执行一次性任务，delay 可以指定延时执行的时间
    public ScheduledFuture<?> schedule(Runnable command,
                                       long delay, TimeUnit unit);
    public <V> ScheduledFuture<V> schedule(Callable<V> callable,
                                           long delay, TimeUnit unit);
    
    // 周期性执行任务调度
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                  long initialDelay,
                                                  long period,
                                                  TimeUnit unit);
    // 以固定的时延执行任务
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                     long initialDelay,
                                                     long delay,
                                                     TimeUnit unit);
}
```

而 `ScheduledThreadPoolExecutor` 是 `ScheduledExecutorService` 的线程池版本的实现，所以可以从两个方面来理解：线程池的实现和基于线程池的任务调度的实现。

// Todo

