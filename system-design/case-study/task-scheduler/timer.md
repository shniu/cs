---
description: 从源码层面分析 Timer
---

# Timer

Timer 是 JDK 提供的一个任务调度类，了解它的设计和实现有助于我们了解调度的基本设计思路。

Timer 在 JDK 中的解释是：它是一个线程调度工具类，以后台线程的形式调度未来的任务执行，这些任务可能是一次性调度，也可能是特定间隔的重复调度；每个 Timer 对象绑定了一个后台线程用于执行和当前 timer 绑定的所有任务，这些任务被串形执行。

#### Timer 的设计

JDK 中的 Timer 调度设计，编程入口是 Timer 和 TimerTask

![Timer &#x7684;&#x8BBE;&#x8BA1;](../../../.gitbook/assets/image%20%2817%29.png)

Timer 的设计是线程安全的，多个线程可以共用一个 Timer 而不需要使用额外的同步控制；但是 Timer 并不保证严格的实时保证，它是使用 `object.wait(long timeout)` 来调度任务的；Java 的 JUC 包中提供了`ScheduledThreadPoolExecutor` 来替代 `Timer` , 它提供了更多的能力。

#### 源码解读

`Timer` 的实现主要有三个部分组成：TimerTask 用来定义需要被执行的任务；TaskQueue 用来存储将要被执行的任务，可以添加很多个 TimerTask；TimerThread 是一个单线程，用来处理 TaskQueue 中需要被调度的任务；可见，Timer 的模型是单线程处理所有要被调度的任务，任务按照 Task 的 nextExecutionTime 排序的方式存储在一个优先级队列中，加入任务时，是把任务加入到任务队列中，何时真的被调度由 TimerThread 控制。

注意：这里的 TaskQueue 的实现是基于二叉堆实现的优先级队列，[关于数据结构堆看这里](../../../cs/algorithm/heap.md)

```java
// Timer.java
public class Timer {
    // 共享的优先级队列
    private final TaskQueue queue = new TaskQueue();
    private final TimerThread thread = new TimerThread(queue);
    // 在启动一个 Timer 时，默认会启动一个 TimerThread
    public Timer(String name, boolean isDaemon) {
        thread.setName(name);
        thread.setDaemon(isDaemon);
        // 启动 Timer 线程
        thread.start();
    }
    
    // ...
    // 将 TimerTask 加入任务队列，等待被调度
    private void sched(TimerTask task, long time, long period) {
        if (time < 0)
            throw new IllegalArgumentException("Illegal execution time.");

        // Constrain value of period sufficiently to prevent numeric
        // overflow while still being effectively infinitely large.
        if (Math.abs(period) > (Long.MAX_VALUE >> 1))
            period >>= 1;

        // queue 本身不是线程安全的，使用同步锁来支持并发
        synchronized(queue) {
            if (!thread.newTasksMayBeScheduled)
                throw new IllegalStateException("Timer already cancelled.");

            // 对 task 进行并发控制，防止多线程并发，加入到不同的 Timer 中，
            // 导致任务调度出错；也就是说一个 TimerTask 只能被成功加入一个 Timer 中调度
            synchronized(task.lock) {
                if (task.state != TimerTask.VIRGIN)
                    throw new IllegalStateException(
                        "Task already scheduled or cancelled");
                task.nextExecutionTime = time;
                task.period = period;
                task.state = TimerTask.SCHEDULED;
            }

            // 入队
            queue.add(task);
            // ?
            if (queue.getMin() == task)
                queue.notify();
        }
    }
}

// TaskQueue
// TaskQueue 是一个 TimerTask 的优先级队列，使用二叉堆实现，按照 nextExecutionTime 构造
// 小顶堆
class TaskQueue {
    private TimerTask[] queue = new TimerTask[128];
    private int size = 0;
    ...
}

// TimerThread
// 这个是任务调度器的执行线程，会监听任务队列
class TimerThread extends Thread {
    // 调度器执行线程是否有效的标志
    boolean newTasksMayBeScheduled = true;
    // 任务队列
    private TaskQueue queue;
    
    public void run() {
        try {
            // 任务监听
            mainLoop();
        } finally {
            // Someone killed this Thread, behave as if Timer cancelled
            // 如果该线程被强制杀死，被当作 Timer 取消的操作来处理
            synchronized(queue) {
                // 执行线程状态被修改，且队列被清空
                newTasksMayBeScheduled = false;
                queue.clear();  // Eliminate obsolete references
            }
        }
    }
    
    private void mainLoop() {
        while (true) {
            try {
                TimerTask task;
                boolean taskFired; // 任务是否被触发
                synchronized(queue) {
                    // Wait for queue to become non-empty
                    while (queue.isEmpty() && newTasksMayBeScheduled)
                        queue.wait();
                    if (queue.isEmpty())
                        break; // Queue is empty and will forever remain; die

                    // Queue nonempty; look at first evt and do the right thing
                    long currentTime, executionTime;
                    task = queue.getMin();
                    synchronized(task.lock) {
                        if (task.state == TimerTask.CANCELLED) {
                            queue.removeMin();
                            continue;  // No action required, poll queue again
                        }
                        currentTime = System.currentTimeMillis();
                        executionTime = task.nextExecutionTime;
                        if (taskFired = (executionTime<=currentTime)) {
                            if (task.period == 0) { // Non-repeating, remove
                                queue.removeMin();
                                task.state = TimerTask.EXECUTED;
                            } else { // Repeating task, reschedule
                                queue.rescheduleMin(
                                  task.period<0 ? currentTime   - task.period
                                                : executionTime + task.period);
                            }
                        }
                    }
                    if (!taskFired) // Task hasn't yet fired; wait
                        queue.wait(executionTime - currentTime);
                }
                if (taskFired)  // Task fired; run it, holding no locks
                    task.run();
            } catch(InterruptedException e) {
            }
        }
    }
}
```

Timer 设计的难点是队列在多线程环境下的线程安全，Timer 采用同步块的方式，`synchronized(queue)` 锁住队列后，进行操作；此外，TimerTask 的设计也需要注意 Task 应该只在一个 Timer 中被调度，以免出现并发问题，如果 一个 TimerTask 试图加入到两个 Timer 中，会抛出异常；TimerTask 的状态流转：

![TimerTask &#x72B6;&#x6001;&#x6D41;&#x8F6C;](../../../.gitbook/assets/image%20%2814%29.png)



