# 线程生命周期与线程中断

### 线程中断

线程的 thread.interrupt\(\) 方法是中断线程，将会设置该线程的中断状态位，即设置为 true，中断的结果线程是死亡、还是等待新的任务或是继续运行至下一步，就取决于这个程序本身。

也就是说当我们调用了 thread.interrupt\(\) 并不意味着我们要终止这个线程，而是要让这个线程活跃起来，可能这个线程已经进入了阻塞状态，比如已经调用了 sleep, join, wait, condition.await 等方法，再调用 interrupt 后，这个线程会从阻塞状态变成就绪状态等待被调度，并且会设置中断标志位为 true；这个时候如果使用了 `try{ ... }catch(InterruptedException e)` 捕获异常，这个中断标志为会被重置为 false，这个一定要注意

```java
// 1. 如何判断线程是否被中断：
//  使用 Thread.currentThread().isInterrupted() 不会清除中断标志位
while(!Thread.currentThread().isInterrupted() && more work to do){
    do more work
}

```

如果一个线程处于了阻塞状态（如线程调用了 thread.sleep、thread.join、thread.wait、1.5中的condition.await、以及可中断的通道上的  I/O  操作方法后可进入阻塞状态），则在线程在检查中断标示时如果发现中断标示为 true，则会在这些阻塞方法（sleep、join、wait、1.5中的condition.await及可中断的通道上的 I/O 操作方法）**调用处抛出 InterruptedException 异常，并且在抛出异常后立即将线程的中断标示位清除，即重新设置为 false**。**抛出异常是为了线程从阻塞状态醒过来，并在结束线程前让程序员有足够的时间来处理中断请求**。

**Note**: synchronized 在获取锁的过程中是不能被中断的，也就是说如果产生了死锁，则不可能被中断。与synchronized 功能相似的 reentrantLock.lock\(\) 方法也是一样，它也不可中断的，即如果发生死锁，那么reentrantLock.lock\(\) 方法无法终止，如果调用时被阻塞，则它一直阻塞到它获取到锁为止。但是如果调用带超时的 tryLock 方法 reentrantLock.tryLock\(long timeout, TimeUnit unit\)，那么如果线程在等待时被中断，将抛出一个 InterruptedException 异常，这是一个非常有用的特性，因为它允许程序打破死锁。你也可以调用 `reentrantLock.lockInterruptibly()` 方法，它就相当于一个超时设为无限的 tryLock 方法。

没有任何语言方面的需求一个被中断的线程应该立即终止。中断一个线程只是为了引起该线程的注意，被中断线程可以决定如何应对中断。某些线程非常重要，以至于它们应该不理会中断，而是在处理完抛出的异常之后继续执行，但是更普遍的情况是，一个线程将把中断看作一个终止请求，这种线程的 run 方法遵循如下形式：

```java
// 1
try {
    // ...
    /*
     * 不管循环里是否调用过线程阻塞的方法如sleep、join、wait，这里还是需要加上
     * !Thread.currentThread().isInterrupted()条件，虽然抛出异常后退出了循环，显
     * 得用阻塞的情况下是多余的，但如果调用了阻塞方法但没有阻塞时，这样会更安全、更及时。
     */
    while (!Thread.currentThread().isInterrupted()&& more work to do) {
        do more work 
    }
} catch (InterruptedException e) {
    // 线程在 wait 或 sleep 期间被中断了
} finally {
    // 线程结束前做一些清理工作
}
    
// 2
while (!Thread.currentThread().isInterrupted()&& more work to do) {
    try {
        // ...
        sleep(delay);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();//重新设置中断标示
    }
}
```

#### 更好的处理线程中断

如果在底层捕获了 InterruptedException, 最好对他做处理，如果不知道要怎么处理，要抛给上层

```java
// 1
try {
    sleep(delay);
} catch (InterruptedException e) {
    // 不知道要做什么，就重新设置
    Thread.currentThread().interrupt();
}

// 2
// 直接抛出 InterruptedException
void mySubTask() throws InterruptedException {
    ...
    sleep(delay);
    ...
}

// 3 使用一个共享的中断信号量

```

* interrupt\(\) 方法是不能中断死锁线程的，因为锁定的位置根本无法抛出异常
* interrupt\(\) 方法是不能中断正在运行的线程的

#### IO 中断

如果线程在 I/O 操作进行时被阻塞，又会如何？I/O 操作可以阻塞线程一段相当长的时间，特别是牵扯到网络应用时。例如，服务器可能需要等待一个请求（request），又或者，一个网络应用程序可能要等待远端主机的响应。

实现此 InterruptibleChannel 接口的通道是可中断的：如果某个线程在可中断通道上因调用某个阻塞的  I/O  操作（常见的操作一般有这些：serverSocketChannel. accept\(\)、socketChannel.connect、socketChannel.open、socketChannel.read、socketChannel.write、fileChannel.read、fileChannel.write）而进入阻塞状态，而另一个线程又调用了该阻塞线程的 interrupt 方法，这将导致该通道被关闭，并且已阻塞线程接将会收到 ClosedByInterruptException，并且设置已阻塞线程的中断状态。另外，如果已设置某个线程的中断状态并且它在通道上调用某个阻塞的 I/O 操作，则该通道将关闭并且该线程立即接收到 ClosedByInterruptException；并仍然设置其中断状态。

```java
// Demo
class Example6 extends Thread {
    volatile ServerSocket socket;

    public static void main(String args[]) throws Exception {
        Example6 thread = new Example6();
        System.out.println("Starting thread...");
        thread.start();
        Thread.sleep(3000);
        System.out.println("Asking thread to stop...");
        
        // 调用 interrupt 方法
        Thread.currentThread().interrupt();
        
        // 调用close方法
        thread.socket.close();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        System.out.println("Stopping application...");
    }

    public void run() {
        try {
            socket = new ServerSocket(8888);
        } catch (IOException e) {
            System.out.println("Could not create the socket...");
            return;
        }
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("Waiting for connection...");
            try {
                socket.accept();
            } catch (IOException e) {
                System.out.println("accept() failed or interrupted...");
                // 重新设置中断标示位
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Thread exiting under request...");
    }
}

```

#### 结论

Java 的中断是一种协作机制，也就是说调用线程对象的 interrupt 方法并不一定就中断了正在运行的线程，它只是要求线程自己在合适的时机中断自己。每个线程都有一个 boolean 的中断状态（这个状态不在Thread 的属性上），interrupt 方法仅仅只是将该状态置为 true。

比如对正常运行的线程调用 interrupt\(\) 并不能终止他，只是改变了 interrupt 标示符。

一般说来，如果一个方法声明抛出 InterruptedException，表示该方法是可中断的,比如 wait, sleep, join，也就是说可中断方法会对 interrupt 调用做出响应（例如 sleep 响应 interrupt 的操作包括清除中断状态，抛出InterruptedException）,异常都是由可中断方法自己抛出来的，并不是直接由interrupt方法直接引起的。

**Object.wait, Thread.sleep 方法，会不断的轮询监听 interrupted 标志位，发现其设置为 true 后，会停止阻塞并抛出 InterruptedException 异常，捕获到异常后，中断标志位会被清除。**

参考资料

* [Thread 的中断机制](https://www.cnblogs.com/onlywujun/p/3565082.html)

