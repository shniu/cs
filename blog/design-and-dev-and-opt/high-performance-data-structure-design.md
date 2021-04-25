# High Performance Data structure Design

### 场景描述

一个自动交易系统会一直监听股票交易所或者数字货币交易所的价格变动，以便找到价格估值较低的股票或数字货币，这些数据从交易所传来之后很可能被放入队列等待处理，但是在决定任何一个资产是否值得投资时，需要一定的时间，但是这个时候可能旧的价格已经被更新了，产生了最新的价格，而旧的价格就不是我们想要的了，因为只对最新的价格感兴趣，所以我们期望把旧的价格更新为新的价格，丢弃掉旧的价格。

也就是说有些场景下，我们需要一个缓冲区来存放等待消费的数据，但是这些数据有个特点是会随着时间被更新掉，而且旧的数据没有任何用处，还有可能由于是旧数据而做出错误的决策，比如在交易所的场景中，交易引擎产生的最新价格推送给订阅用户；或者高频交易的用户实时获取到交易价格后做交易决策等。这里需要一种数据结构，能满足以下需求：

1. 要保证线程安全，Buffer 需要在多个线程之间共享使用
2. 要保证 FIFO 的特点，不能破坏数据本来的顺序性，比如 A、B、C 依次放入 Buffer 中，如果接下来 B 又有更新，可以去更新 B 的值，但是不能影响 A、B、C 的读取顺序
3. 少量的重复数据是允许的，消费端做幂等保证
4. 性能尽可能的高，因为在交易系统中，低延迟是非常重要的，到达一定阶段，需要在各个环节做极致的优化；比如这个 Buffer 每秒钟能进行的操作次数 \(ops\)，使用不同的数据结构和算法以及并发控制，最终得到的性能差异会很明显

### V1 - 使用同步锁实现

最容易想到的方法是 synchronized 并且配合 LinkedHashMap: 其中 synchronized 用来做并发控制，LinkedHashMap 用来记录数据，并且保证了数据的添加顺序，简化后的代码如下：

```java
// ...
public class SynchronizedBuffer<K, V> implements Buffer<K, V> {
    private int capacity;
    private LinkedHashMap<K, V> maps;
    
    // ...

    @Override
    public synchronized boolean offer(K key, V value) {
        if (maps.containsKey(key)) {
            maps.put(key, value);
            return true;
        }
        
        if (maps.size() == capacity) {
            return false;
        }
        
        maps.put(key, value);
        return true;
    }
    
    @Override
    public synchronized int poll(Collection<? super V> bucket) {
        int size = maps.size();
        for (V v : maps.values()) {
            bucket.add(v);
        }
        maps.clear();
        return size;
    }
}
```

基本实现了我们想要的功能，我们使用一个生产者和一个消费者，进行 20 亿次的操作，最后测试出来的性能指标是 25 kOPS 左右，平均下来看一次存取操作耗时 40 微秒，这个指标不算好，我们做的事情其实很简单，分析可知，有两方面原因：

1. 我们使用了 synchronized 来控制并发访问，线程之间是互斥的，只有拿到锁的线程才可以执行，拿不到的要等待，可见锁冲突的概率比较大，很大一部分时间都花费在了锁竞争和锁等待上，也就是说这个锁的粒度太粗了，有优化空间
2. 我们使用 LinkedHashMap 这个数据结构，它的底层是 HashMap，然后把元素入 Map 的顺序用双向链表连接起来，HashMap 在组织数据时数组包含多个 bucket，具有相同 hash index 的元素使用链表或者红黑树来组织数据并放入同一个 bucket 中，它们在内存中的布局大概率不是连续的，在实际执行时 CPU 的 cache line 命中率有优化空间，也就是说很大概率在取某个元素时，需要到内存里把数据 load 到 CPU cache 中，不能很好的利用 CPU 的 cache line 的特性

### V2 - 使用 CAS 优化并发控制

针对上面的问题 1，一般优化的方向是使用 lock-free 算法来解决，在 Java 中可以使用在 Unsafe 中 CAS 相关的一些操作，它实现了 CPU 指令级别的原子操作 \(compare and swap\)

```java
public class CASBuffer<K, V> implements Buffer<K, V> {
    private int capacity;
    private AtomicReference<LinkedHashMap<K, V>> referenceMaps = new AtomicReference<>();
    // ...
    
    @Override
    public boolean offer(K key, V value) {
        boolean success = false;
        while (!success) {
            LinkedHashMap<K, V> current = referenceMaps.get();
            LinkedHashMap<K, V> modified = (LinkedHashMap<K, V>) current.clone();
            
            if (modified.containsKey(key)) {
                modified.put(key, value);
            } else if (modified.size() == capacity) {
                return false;
            } else {
                modified.put(key, value);
            }
            
            success = referenceMaps.compareAndSet(current, modified);
        }
        
        return true;
    }
    
    @Override
    public int poll(Collection<? super V> bucket) {
        LinkedHashMap<K, V> current = referenceMaps.getAndSet(new LinkedHashMap<K, V>(capacity));
        
        for (V v : current.values()) {
            bucket.add(v);
        }
        
        return current.size();
    }
}
```

针对上面的代码做相同的性能测试，发现性能指标在 0.2 kOPS, 还下降了，说明这个优化方向有问题，不可取；可能的原因是每次在 offer 数据的时候都需要做 clone 操作，是要做内存数据 copy 的，这个比较耗时，尤其是在元素比较多的时候，而且对整个 LinkedHashMap 做 CAS 操作的粒度还是太大了，重新理一下优化方向：

1. 使用 lock-free 来控制并发，并且控制并发的粒度要尽可能的小，能控制一个元素就不要控制多个元素
2. 使用 CPU cache line 更加友好的数据存储方式，比如数组

### V3 - 使用环形数组优化 CPU cache line 的命中率

那么如何利用环形数组来解决问题呢？首先，我们要把实际的元素存储在数组中，这样在 load 内存里的数据到 CPU cache 中就可以把相邻的元素也同时加载进去，省去了多次 load 内存的操作；其次，在写入和读取数据时需要位置指针，也就是下一次要写入的位置，和最新读取到的位置，可以使用 int 来表示即可，但是对于位置的更新需要做并发控制，这个时候我们就可以考虑使用 CAS 了

```java
public class ArrayBuffer<K, V> implements Buffer<K, V> {
    private volatile long nextWrite = 1;
    private final K[] keys;
    private final AtomicReferenceArray<V> referenceArray;
    private volatile long firstWrite = 1;
    private volatile long lastRead = 0;
    private int capacity;
    
    // ...
    
    @Override
    public boolean offer(K key, V value) {
        long nextWrite = this.nextWrite;
        for (long updatePosition = firstWrite; updatePosition < nextWrite; updatePosition++) {
            int index = computeIndex(updatePosition);
            if (key.equals(keys[index])) {
                referenceArray.set(index, value);
                if (updatePosition >= firstWrite) {
                    return true;
                } else {
                    break;
                }
            }
        }
        return add(key, value);
    }
    
    private boolean add(K key, V value) {
        if (isFull()) {
            return false;
        }
        
        long nextWrite = this.nextWrite;
        long index = computeIndex(nextWrite);
        
        keys[index] = key;
        referenceArray.set(index, value);
        
        this.nextWrite = nextWrite + 1;
        return true;
    }
    
    @Override
    public int poll(Collection<? super V> bucket) {
        long lastRead = this.lastRead;
        long nextWrite = this.nextWrite;
        firstWrite = nextWrite;
        
        for (long readIndex = lastRead + 1; readIndex < nextWrite; readIndex++) {
            int index = computeIndex(readIndex);
            // 注意这里：消费线程也执行了写入操作，更新了 keys 和 values，有优化空间
            keys[index] = null;
            bucket.add(referenceArray.getAndSet(index, null));
        }

        this.lastRead = nextWrite - 1;
        return (int) (nextWrite - lastRead - 1);
    }
    
    private int computeIndex(long value) {
        return ((int) value) % capacity;
    }
}
```

nextWrite 是 生产线程调用 offer 时更新，firstWrite 和 lastRead 是 消费线程调用 poll 时更新，但是消费线程需要对 nextWrite 保持可见性，生产线程需要对 lastRead 保持可见性，所以它们都使用 volatile 进行修饰即可；只要满足写入条件，就可以写入数据，但是对 value 数组的访问需要并发控制，因为生产者写入数据，要保证消费者的可见性，这里使用了 AtomicReferenceArray 对 普通数组做原子性操作的封装，`referenceArray.set(index, value)` 的内部使用了 putObjectVolatile

使用这种方案的最终指标是 25 mOPS，可见性能相对于 V1 提升了 1000 倍，效果很明显。这种方案使用数组提升了 CPU cache 的命中率，使用 volatile 来做并发控制的数据可见性，消除了锁同步导致的耗时；

但是在计算索引时，我们使用了除法取余的方式，这里可以考虑使用位运算进行优化：

```java
// mask: capacity - 1
// capacity 是 2 的幂次方
private int computeIndex(long value) {
    return ((int) value) & mask;
}
```

使用位运算的方式计算索引后，性能进一步提升到 34 mOPS，还是很有帮助的。

### V4 - 使用 Single Writer 原则

我们在前面的版本中，offer 是提供给生产者使用的，poll 是提供给消费者使用的，生产者负责写，消费者负责读，所以我们最好遵循这一原则，让生产者线程去更新已经使用过的数据，让 GC 及时将这些无用的对象回收。

```java
...
for (long readIndex = lastRead + 1; readIndex < nextWrite; readIndex++) {
    int index = computeIndex(readIndex);
    
    // 把这里的更新操作，移动到 offer 的方法中，这样在执行时生产者线程会主动去释放已经
    // 使用完的线程
    // keys[index] = null;
    // bucket.add(referenceArray.getAndSet(index, null));
    bucket.add(referenceArray.get(index));
}
...

// 为生产者线程添加一个已经清空过的最新位置，只有生产者线程会使用，所以不需要并发控制
private long lastCleaned = 0;

// 在每次添加元素之前先来检测以下是否需要清除使用过的元素
private void cleanUp() {
    long lastRead = this.lastRead;

    if (lastRead == lastCleaned) {
        return;
    }

    while (lastCleaned < lastRead) {
        int index = computeIndex(++lastCleaned);
        
        // gc help
        keys[index] = null;
        atomicReferenceArray.set(index, null);
    }
}
```

这样优化之后的性能指标可以达到 46mOPS。使用 Single Writer 原则在设计上更加合理，可维护性更好; 此外这种方式让修改只在一个线程中进行，避免因修改共享数据导致的多个 CPU Cache 中的数据失效问题，这样在处理数据时只需要本线程内修改数据后写会内存，而这个写回内存的操作不需要强制保证和其他线程的可见性，延迟可见就可以，所以这里也是一个优化点，使用具有 store-store 内存屏障语义的 lazySet 比使用具有 store-load 内存屏障语义的 volatile 在性能上更加好。

### V5 - 使用 lazySet 进一步提升性能

在更新 lastRead, nextWrite, referenceArray set null 时都是使用的 volatile 来实现可见性，它的问题是每次都必须把 CPU cache 中的数据写回内存，并把其他 CPU 核心中的 Cache 失效掉，这样一定程度上可能会影响性能；根据这几个变量的特点，即使晚几个 CPU 的时钟周期写回内存也是没有关系的，所以我们完全可以不强制使用 volatile，改用原子类的 lazySet，可以看这里的 [set 和 lazySet 的说明](https://blog.csdn.net/aitangyong/article/details/41577503).

> lazySet: "作为可能是Mustang的最后一个小JSR166后续工作，我们为Atomic类（AtomicInteger、AtomicReference等）添加了一个 "lazySet "方法。这是一个小众的方法，在使用非阻塞数据结构微调代码时有时会很有用。其语义是保证写的内容不会与之前的任何写的内容重新排序，但可能会与后续的操作重新排序（或者等价地，可能对其他线程不可见），直到其他一些 volatile 写或 synchronize 操作发生）。
>
> 主要的用例是仅仅为了避免长期的垃圾保留而将非阻塞数据结构中的节点的字段清空；它适用于如果其他线程在一段时间内看到非空值是无害的，但你想确保结构最终是GCable。在这种情况下，你可以通过避免空挥写的成本来获得更好的性能。沿着这样的思路，非基于引用的原子也有一些其他的用例，所以在所有的AtomicX类中都支持该方法。

最终版本：

```java
public final class CoalescingRingBuffer<K, V> implements CoalescingBuffer<K, V> {
    // 可写的位置
    private final AtomicLong nextWrite = new AtomicLong(1);
    // the last index that was nulled out by the producer
    // 我们需要清理掉已经被读取过的元素，我们使用了 Single Writer 原则，lastCleaned 用来
    // 记录上次被清理的位置，所以 lastCleaned 只有 producer 线程会使用，不需要做并发控制
    private long lastCleaned = 0; 
    // 记录由于 buffer 满导致的拒绝添加的次数
    private final AtomicLong rejectionCount = new AtomicLong(0);
    // 记录 key 的数组
    private final K[] keys;
    // 记录 value 的数组，value 是被共享的数据，所以需要保证可见性，这里使用数组引用原子类
    // 最终在 set 数据时用到了 volatile 
    private final AtomicReferenceArray<V> values;

    // 没有指定 key 的数据，默认使用这个作为 key
    @SuppressWarnings("unchecked")
    private final K nonCollapsibleKey = (K) new Object();
    // 掩码，capacity - 1, 处于性能考虑，使用位运算计算位置索引时使用
    private final int mask;
    // 容量，为了性能考虑，capacity 都是 2 的 n 次方
    private final int capacity;

    // the oldest slot that is is safe to write to
    // 第一个可以被安全写入的位置，会随着消费者的读取而变化
    // 以这个位置开始，到 nextWrite 这中间的数据可以被替换
    private volatile long firstWrite = 1;
    // the newest slot that it is safe to overwrite
    // 上次读取到的位置，这个位置之前的数据可以被覆盖
    private final AtomicLong lastRead = new AtomicLong(0); 

    @SuppressWarnings("unchecked")
    public CoalescingRingBuffer(int capacity) {
        this.capacity = nextPowerOfTwo(capacity);
        this.mask = this.capacity - 1;

        this.keys = (K[]) new Object[this.capacity];
        this.values = new AtomicReferenceArray<V>(this.capacity);
    }

    private int nextPowerOfTwo(int value) {
        return 1 << (32 - Integer.numberOfLeadingZeros(value - 1));
    }

    @Override
    public int size() {
        // loop until you get a consistent read of both volatile indices
        while (true) {
            long lastReadBefore     = lastRead.get();
            long currentNextWrite   = this.nextWrite.get();
            long lastReadAfter      = lastRead.get();
            // 处于一致性的考虑，确保在读取到 nextWrite 时，lastRead 的值没有被修改
            // 因为操作没有加锁，所以需要使用 spin 的方式进行检测
            if (lastReadBefore == lastReadAfter) {
                return (int) (currentNextWrite - lastReadBefore) - 1;
            }
        }
    }

    @Override
    public int capacity() {
        return capacity;
    }

    public long rejectionCount() {
        return rejectionCount.get();
    }

    public long nextWrite() {
        return nextWrite.get();
    }

    public long firstWrite() {
        return firstWrite;
    }

    @Override
    public boolean isEmpty() {
        return firstWrite == nextWrite.get();
    }

    @Override
    public boolean isFull() {
        return size() == capacity;
    }

    @Override
    public boolean offer(K key, V value) {
        long nextWrite = this.nextWrite.get();
        // firstWrite ~ nextWrite 之间的数据是可以被替换的
        for (long updatePosition = firstWrite; updatePosition < nextWrite; updatePosition++) {
            int index = mask(updatePosition);

            if (key.equals(keys[index])) {
                values.set(index, value);
                // check that the reader has not read beyond our update point yet
                if (updatePosition >= firstWrite) {  
                    return true;
                } else {
                    break;
                }
            }
        }

        return add(key, value);
    }

    @Override
    public boolean offer(V value) {
        return add(nonCollapsibleKey, value);
    }

    private boolean add(K key, V value) {
        // 如果已经满了，就拒绝添加
        if (isFull()) {
            rejectionCount.lazySet(rejectionCount.get() + 1);
            return false;
        }
        // 遵循 Single Writer 原则，无用数据的清理也让 producer 线程来做，进一步提升性能
        cleanUp();
        // 存储到数组中
        store(key, value);
        return true;
    }

    private void cleanUp() {
        long lastRead = this.lastRead.get();

        if (lastRead == lastCleaned) {
            return;
        }

        while (lastCleaned < lastRead) {
            int index = mask(++lastCleaned);
            keys[index] = null;
            // lazySet 提高性能，不需要强制立即刷新回内存，并让其他 cpu cache 中的数据失效
            // 这里只需要最终值被设置为 null 即可，晚一点没有关系
            values.lazySet(index, null);
        }
    }

    private void store(K key, V value) {
        long nextWrite = this.nextWrite.get();
        int index = mask(nextWrite);

        keys[index] = key;
        // 这里要使用 volatile 保证数据在 consumer 线程中也是可见的
        values.set(index, value);

        // 使用 lazySet 提高性能
        this.nextWrite.lazySet(nextWrite + 1);
    }

    @Override
    public int poll(Collection<? super V> bucket) {
        return fill(bucket, nextWrite.get());
    }

    @Override
    public int poll(Collection<? super V> bucket, int maxItems) {
        // 计算可以取的最大位置
        long claimUpTo = min(firstWrite + maxItems, nextWrite.get());
        return fill(bucket, claimUpTo);
    }

    private int fill(Collection<? super V> bucket, long claimUpTo) {
        // claimUpTo 是当次可取到数据的最大位置
        firstWrite = claimUpTo;
        long lastRead = this.lastRead.get();

        for (long readIndex = lastRead + 1; readIndex < claimUpTo; readIndex++) {
            int index = mask(readIndex);
            bucket.add(values.get(index));
            // values.set(index, null);
        }

        // 使用 lazySet 提高性能
        this.lastRead.lazySet(claimUpTo - 1);
        return (int) (claimUpTo - lastRead - 1);
    }

    private int mask(long value) {
        return ((int) value) & mask;
    }

}
```

### Reference

#### [ Single Writer Principle](https://mechanical-sympathy.blogspot.com/2011/09/single-writer-principle.html) - 单一写入者原则

在遇到多个写入者竞争共享资源时，一般有两种解决办法：一个是在竞争时使用互斥访问策略，一般是使用锁来实现，比如互斥量等；另外一种是使用乐观并发策略，比如 Compare-And-Swap

> 锁策略：Locking strategies require an arbitrator（仲裁）, usually the operating system kernel, to get involved when the contention occurs to decide who gains access and in what order.  This can be a very expensive process often requiring many more CPU cycles than the actual transaction to be applied to the business logic would use.  Those waiting to enter the [critical section](http://en.wikipedia.org/wiki/Critical_section)\(临界区\), in advance of performing the mutation must queue, and this queuing effect \([Little's Law](http://en.wikipedia.org/wiki/Little%27s_law)\) causes latency to become unpredictable and ultimately restricts throughput.

Most locking strategies are composed from optimistic strategies for changing the lock state or mutual exclusion primitive. \(大多数的锁策略是由改变锁状态的乐观策略或互斥原语组成的\)

好处：如果你的系统能够遵守这个单一写入者原则，那么每个执行上下文就可以把所有的时间和资源都用在处理目的逻辑上，而不会在处理争用问题上浪费周期和资源。 你也可以无限制的扩展，直到硬件饱和。 还有一个非常好的好处是，当在 x86/x64 这样的架构上工作时，在硬件层面上，他们有一个内存模型，据此，加载/存储内存操作有保留的顺序，因此，如果你严格遵守单作者原则，就不需要内存障碍。 **在x86/x64 上，根据内存模型，"load 可以与旧的 store 重新排序"，因此当多个线程跨核修改相同的数据时，需要设置内存屏障。 单一写入者原则避免了这个问题，因为它永远不用处理写入一个数据项的最新版本，而这个数据项可能已经被另一个线程写入，或者正在另一个核的存储缓冲区中。**

If a system is decomposed into components that keep their own relevant state model, without a central shared model, and all communication is achieved via message passing then you have a system without contention naturally. This type of system obeys the single writer principle if the messaging passing sub-system is not implemented as queues. If you cannot move straight to a model like this, but are finding scalability issues related to contention, then start by asking the question, “How do I change this code to preserve the Single Writer Principle and thus avoid the contention?”

The Single Writer Principle is that for any item of data, or resource, that item of data should be owned by a single execution context for all mutations. \(单一写入者原则适用于任何一项数据或资源，该数据项的所有修改都应该由同一个执行上下文拥有\)

#### [Atomic\*.lazySet is a performance win for single writer](http://psy-lob-saw.blogspot.com/2012/12/atomiclazyset-is-performance-win-for.html)

But if there is only a single writer we don't need to do that, as we know no one will ever change the data but us. And from that follows that strictly speaking lazySet is at the very least as correct as a volatile set for a single writer. \(如果只有一个单一写入者，也就是没有其他的人修改数据，只有我们自己；基于这个结论可以严格的说单一写入者模式下的 lazySet 和 volatile 具有同样正确的语义\)

At this point the question is when \(if at all\) will the value set be made visible to other threads. \(基于这个点问题就变成了被修改的值何时被其他线程看到\)

#### [How is lazySet in Java's Atomic\* classes implemented ?](https://stackoverflow.com/questions/8381440/how-is-lazyset-in-javas-atomic-classes-implemented)

#### [AtomicLong lazySet vs set ](https://stackoverflow.com/questions/1468007/atomicinteger-lazyset-vs-set)

