# 并发编程

## 经典问题

### 互斥锁保护共享资源

从一个转账的例子开始，银行系统中，每个账户都有转账功能，现在我们把这个功能简化到单进程里完成（当然实际情况并非如此），问题就是如何保证很多账户并发转账的安全性，就是不能钱转走了，余额没有变化，不能多转也不能少扣。一个最简的代码模型如下：

```java
public class Account {
    private int balance;
    
    public synchronized void transfer(Account target, int amt) {
        if (this.balance > amt) {
            this.balance -= amt;
            target.balance += amt;
        }
    }
}
```

但是不幸的是上面的代码是有问题的，并不能保证安全的转账操作，因为 synchronized 的锁是 this，并不能保护多个 Account 对象里的资源，相当于有多把锁，不能构成互斥行为。

#### 改进1: 保证安全性

```java
public class Account {
    private int balance;
    private Object lock;
    
    private Account() {}
    public Account(Object lock) {
        this.lock = lock;
    }
    
    public void transfer(Account target, int amt) {
        synchronized (lock) {
            if (this.balance > amt) {
                this.balance -= amt;
                target.balance += amt;
            }
        }
    }
}
```

这个方案要求，传入的 lock 是同一个共享对象，否则无效。所以这种做法的缺陷是需要使用代码的地方多加注意，增加了心智负担，用 Account.class 做改进

```java
public class Account {
    private int balance;
    
    public void transfer(Account target, int amt) {
        synchronized (Account.class) {
            if (this.balance > amt) {
                this.balance -= amt;
                target.balance += amt;
            }
        }
    }
}
```

这样看起来好多了，但是同样存在问题，这种方式将所有对 Account 的转账操作全部串行化了，大大降低了效率。

#### 改进2：优化效率

为了提升效率，就不能使用 Account.class 这样的粗粒度锁，可以引入细粒度锁，如我们可以使用两个锁：

```java
// 其他代码省略
public void transfer(Account target, int amt) {
    synchronized (this) {  // 1
        synchronized (target) {   // 2
            if (this.balance > amt) {
                this.balance -= amt;
                target.balance += amt;
            }
        }
    }
}
```

但是，这断代码有问题，容易产生死锁，比如两个线程分别执行： 线程1 `A 转账 B 100` 和 线程2 `B 转账 A 300`，线程1 执行到位置 1 获取到 Accoun A 对象的 锁，进行上下文切换，这时线程2 执行，到位置1 获取到 Account B 对象的锁，这样线程1持有了A的锁，线程2持有了B的锁，都相互等待对方的锁，就造成死锁了。

- 破坏占用且等待的条件来修复 bug

产生死锁的原因之一是线程获得锁后在等待其他锁时，并不释放锁，那可以改成获取不到足够的锁，就把自己占有的锁释放掉

```java
// 让 Allocator 来承担资源的分配方, 保证是单例
public class Allocator {
    private Set<Object> als = new HashSet<>();
    
    // todo 缺少单例的实现 ...
    private Allocator() {}
    
    // 申请资源
    public synchronized boolean apply(Object from, Object to) {
        if (als.contains(from) || als.contains(to)) return false;
        
        als.add(from);
        als.add(to);
        return true;
    }
    
    // 释放资源
    public synchronzied void free(Object from, Object to) {
        als.remove(from);
        als.remove(to);
    }
}

public class Account {
    private Allocator allocator；
    private int balance;
    
    public void transfer(Account target, int amt) {
        // 利用循环检测资源是否就绪，一次性申请所有资源
        while (!allocator.apply(this, target) ;
        
        try {
            synchronized (this) {  // 1
                synchronized (target) {   // 2
                    if (this.balance > amt) {
                        this.balance -= amt;
                        target.balance += amt;
                    }
                }
            }
        } finally {
            allocator.free(this, target);
        }
        
    }
}
```

当然，这种方案是可行的，但是也存在缺点，稍后分析。

- 破坏不可抢占条件修复 bug

产生死锁的另一个原因是获取资源后，其他线程不能强行占有这个资源。那么我们可以考虑释放掉已经占有的资源。`synchronized` 并发原语并不能支持，因为它一旦申请不到资源就进入了阻塞状态，稍后做分析来找其他解决方案。

- 破坏循环等待条件修复 bug

产生死锁的另一个原因是循环等待，那么可以为资源进行编号，按序获取资源。

```java
public class Account {
    private int id;
    private int balance;
    
    public void transfer(Account target, int amt) {
        Account left = this;
        Account right = target;
        if (this.id > target.id) {
            left = target;
            right = this;
        }
        
        synchronized (left) {
            synchronized (right) {
                if (this.balance > amt) {
                    this.balance -= amt;
                    target.balance += amt;
                }
            }
        }
    }
}
```

#### 改进3: 继续优化性能

在 `破坏占用且等待的条件来修复 bug` 那一部分我们用了循环检测资源的就绪状态，但是这个方案并不完美，试想如果并发量大，转账账户重叠多的时候，就会耗费大量的CPU时间，这是空耗，浪费了资源。比较好的方式是等待-通知机制，当没有足够资源时就等待，有足够资源时就收到通知触发执行。

```java
public class Allocator {
    private Set<Object> als = new HashSet<>();
    
    // todo 缺少单例的实现 ...
    private Allocator() {}
    
    // 申请资源
    public synchronized boolean apply(Object from, Object to) {
        while (als.contains(from) || als.contains(to)) {
            try {
                wait();
            } catch(Exception e) {}
        }
        
        als.add(from);
        als.add(to);
    }
    
    // 释放资源
    public synchronzied void free(Object from, Object to) {
        als.remove(from);
        als.remove(to);
        notifyAll();
    }
}

public class Account {
    private Allocator allocator；
    private int balance;
    
    public void transfer(Account target, int amt) {
        // 利用等待通知机制，一次性申请所有资源，防止死锁
        allocator.apply(this, target);
        
        try {
            synchronized (this) {
                synchronized (target) {
                    if (this.balance > amt) {
                        this.balance -= amt;
                        target.balance += amt;
                    }
                }
            }
        } finally {
            // 释放资源
            allocator.free(this, target);
        }
        
    }
}
```

利用等待-通知机制，我们很好的解决了死循环检测空耗CPU的问题。

### 用两个线程交替打印奇数和偶数问题

> 问题描述：
> 有两个线程分别输出奇数和偶数，编写程序使得输出顺序是1, 2, 3, 4, 5, 6, 7, 8, 9, 一直到 100

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
// 何时等待：
// 何时通知：
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

