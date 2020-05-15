# 三个线程交替打印

## 经典问题

### 三个线程交替打印A、B、C

> 问题描述: 有A，B，C三个线程，A线程输出A，B线程输出B，C线程输出C，要求，同时启动三个线程，按顺序输出ABC，循环100次。

#### 使用 Lock & Condition 解决

需要启动三个线程，分别负责打印 A B C，所以我们可以利用等待-通知机制控制打印顺序。

```java
public class PrintABC3 {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Condition a = lock.newCondition();
        Condition b = lock.newCondition();
        Condition c = lock.newCondition();

        new Thread(new PrintA(lock, a, b)).start();
        new Thread(new PrintB(lock, b, c)).start();
        new Thread(new PrintC(lock, c, a)).start();
    }
}

abstract class Print implements Runnable {
    private Lock lock;
    private Condition self;
    private Condition next;

    static int count = 1;
    private static int MAX = 100;

    public Print(Lock lock, Condition self, Condition next) {
        this.lock = lock;
        this.self = self;
        this.next = next;
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            if (count > MAX) {
                self.signalAll();
                next.signalAll();
                lock.unlock();
                return;
            }

            print();

            next.signal();
            try {
                self.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    abstract void print();
}

class PrintA extends Print {

    PrintA(Lock lock, Condition self, Condition next) {
        super(lock, self, next);
    }

    @Override
    void print() {
        System.out.print(count + ": A");
    }
}

class PrintB extends Print {

    PrintB(Lock lock, Condition self, Condition next) {
        super(lock, self, next);
    }

    @Override
    void print() {
        System.out.print("B");
    }
}

class PrintC extends Print {

    PrintC(Lock lock, Condition self, Condition next) {
        super(lock, self, next);
    }

    @Override
    void print() {
        System.out.println("C");
        count++;
    }
}
```

