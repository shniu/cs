# 两个线程交替打印奇偶

## 经典问题

### 用两个线程交替打印奇数和偶数问题

> 问题描述： 有两个线程分别输出奇数和偶数，编写程序使得输出顺序是1, 2, 3, 4, 5, 6, 7, 8, 9, 一直到 100

这是一道笔试题，经常出现，能非常好的考察对并发编程的掌握程度，当然实现方式也是多样的。

#### 思路1 用 synchronized 以及等待通知机制解决

分析如下：

1. 需要启动两个线程A和B
2. 共享资源是要打印的数字
3. 线程A打印奇数，线程B打印偶数，线程A打印完之后通知线程B并等待线程B打印完
4. 线程A或者B能够收到对方的通知，并继续打印自己的数字

```java
// 互斥资源：要打印的数字
// 互斥锁：共享一把锁即可，如 Object lock = new Object();
// 线程要求的条件：两个线程交替执行
// 何时等待：打印完自己的数字等待对方打印
// 何时通知：打印安自己的数字通知对方打印
public class PrintEvenOdd {
    private static volatile int number = 1;
    private static int MAX = 100;

    public static void main(String[] args) {
        // 互斥锁
        Object lock = new Object();

        Runnable runnable = () -> {
            synchronized (lock) {
                while (number <= MAX) {
                    System.out.println(number++);
                    lock.notifyAll();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {}
                }

                lock.notifyAll();
            }
        };

        // 启动奇数线程
        new Thread(runnable, "奇数线程").start();

        // 启动偶数线程
        new Thread(runnable, "偶数线程").start();
    }
}
```

#### 思路2 利用 synchronized 或者 Lock 接口结合判断奇偶的方式

思路1 利用了等待-通知机制，是我认为的最优秀的解决方法了。判断奇偶的思路更容易理解。

```java
public class PrintEvenOdd {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();

        // 奇数线程
        new Thread(new PThread(lock, 0)).start();

        // 偶数线程
        new Thread(new PThread(lock, 1)).start();
    }
}

class PThread implements Runnable {
    private Lock lock;
    private int flag;

    private PThread() {
    }

    public PThread(Lock lock, int flag) {
        this.lock = lock;
        this.flag = flag;
    }

    // 共享资源
    private static int number = 0;
    private static int MAX = 100;

    @Override
    public void run() {
        while (true) {
            lock.lock();
            if (number > MAX) {
                lock.unlock();
                return;
            }

            // 判断奇偶
            if (number % 2 == flag) {
                System.out.println(number);
                number++;
            }

            lock.unlock();
        }
    }
}
```

#### 思路3 可以利用 Lock 和 Condition 交替打印

这种方式本质上还是等待-通知机制，也非常棒。

```java
public class PrintEvenOdd3 {
    private static volatile int number = 1;
    private static int MAX = 100;

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        // 奇数 Condition
        Condition oddCond = lock.newCondition();
        // 偶数 Condition
        Condition evenCond = lock.newCondition();

        Runnable runnable = () -> {
            while (true) {
                lock.lock();

                if (number > MAX) {
                    oddCond.signalAll();
                    evenCond.signalAll();
                    lock.unlock();
                    return;
                }

                // odd
                if (number % 2 == 1) {
                    System.out.println(Thread.currentThread().getName() + number);
                    number++;
                    // notify 偶数 Condition
                    evenCond.notifyAll();
                    try {
                        oddCond.await();
                    } catch (Exception e) {}
                } else {  // even
                    System.out.println(Thread.currentThread().getName() + number);
                    number++;
                    // notify 奇数 Condtion
                    oddCond.notifyAll();
                    try {
                        evenCond.await();
                    } catch (Exception e) {}
                }

                lock.unlock();
            }
        };

        // start odd thread
        new Thread(runnable, "odd").start();
        // start even thread
        new Thread(runnable, "even").start();
    }
}
```

