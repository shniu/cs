# 并发编程

## 终止线程

中断状态是线程的一个标识位，中断操作是一种简便的线程间交互方式，而这种方式适合用来取消或停止任务。

线程的终止有两种情况：

1. 线程执行完自动进入终止状态，无需其他操作
2. 在线程A里优雅地终止线程B，所谓优雅就是能让线程B做一些线程停止的工作，而不是强制杀死

优雅终止有一套成熟的方案，叫做两阶段终止模式，如下：

1. 线程A向线程B发送终止命令
2. 线程B响应终止命令

在 Java 中，线程的状态有 Runnable, Waiting, Timed_Waiting, 当要终止一个程序时，线程有可能处在等待状态，需要有一种能力将线程从等待状态转换到Runnable状态，让线程能继续运行，Java 中提供了 interrupt(); 等到线程恢复运行后，一种方式是等待run()运行完毕，一般是提供一个线程标识位，用来标识该线程已经被中断，检测到后就可以处理中断程序。所以，Java 中的两阶段终止模式是：interrupt() 方法和线程终止状态位

### 一个实际例子

监控系统需要动态地采集一些数据，一般都是监控系统发送采集指令给被监控系统的监控代理，监控代理接收到指令之后，从监控目标收集数据，然后回传给监控系统；动态采集功能一般都会有终止操作。

- 实现

根据两阶段终止模式的实现：

```java
public class MonitorSystemProxy {
    // 保证只启动一次
    private boolean started = false;
    private Thread rptThread;

    // 开启采集功能
    public synchronized void start() {
        if (started) {
            return;
        }

        started = true;
        rptThread = new Thread(() -> {
            // 线程终止状态位，检测到 interrupt 指令就退出
            while (!Thread.currentThread().isInterrupted()) {
                // 采集上报的逻辑实现
                // report();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // 当 catch 到 InterruptedException 状态标识位被清空
                    // 重新设置状态位
                    Thread.currentThread().interrupt();
                }
            }

            started = false;
        });
        rptThread.start();
    }

    // 终止采集功能
    public synchronized void stop() {
        // 发送终止命令，interrupt()
        rptThread.interrupt();
    }
}
```

以上实现已经能基本工作了，但是有一个隐患，在于采集上报的逻辑里可能会有代码捕获 InterruptedException 异常，导致状态位被清空，Thread.currentThread().isInterrupted() 判断失效，因为我们可能用了第三方库，无法修改，所以为代码埋下了隐患。

- 更加好的实现方式

由于线程自身的状态标识位有可能出问题，只能我们自己独立维护一个是否中断的状态位。代码如下：

```java
public class MonitorSystemProxyOpt {
    // 自定义的中断标识位
    private boolean terminated = false;
    // 启动标识
    private boolean started = false;
    // 采集上报线程
    private Thread rptThread;

    // 开启采集功能
    public synchronized void start() {
        if (started) {
            return;
        }

        started = true;
        terminated = false;
        rptThread = new Thread(() -> {
            while (!terminated) {
                // 采集上报的逻辑实现
                // report();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // 重新设置状态位
                    Thread.currentThread().interrupt();
                }
            }

            started = false;
        });
        rptThread.start();
    }

    // 终止采集功能
    public synchronized void stop() {
        // 设置中断
        terminated = true;
        // 中断线程
        rptThread.interrupt();
    }
}
```

