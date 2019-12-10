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



