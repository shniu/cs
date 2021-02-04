# CoalescingRingBuffer 分析

Coalescing Ring Buffer 解决的问题是：有效地缓冲生产者和消费者线程之间的消息，在这种情况下，只有给定主题的最新值才是感兴趣的，所有其他的消息都可以被立即丢弃。举个例子：

一个自动交易系统会一直监听股票交易所或者数字货币交易所的价格变动，以便找到价格估值较低的股票或数字货币，这些数据从交易所传来之后很可能被放入队列等待处理，但是在决定任何一个资产是否值得投资时，需要一定的时间，但是这个时候可能旧的价格已经被更新了，产生了最新的价格，而旧的价格就不是我们想要的了，因为只对最新的价格感兴趣，所以我们期望把旧的价格更新为新的价格，丢弃掉旧的额价格；Coalescing Ring Buffer 就是来解决这个问题的，**它是一个缓冲区，可以容纳传入的价格更新，并检查是否可以更新一个现有的值，而不是在消费者准备好处理它们之前增长缓冲区**。

Coalescing Ring Buffer 借鉴了一些 Disruptor 的设计原则：

1. 使用数组作为数据结构，因为它们的内存位置
2. 使用无锁并发，它避免了内核仲裁
3. 采用单写原则，避免 cache line 争用

```java
public final class CoalescingRingBuffer<K, V> implements CoalescingBuffer<K, V> {

    private final AtomicLong nextWrite = new AtomicLong(1); // the next write index
    private long lastCleaned = 0; // the last index that was nulled out by the producer
    private final AtomicLong rejectionCount = new AtomicLong(0);
    private final K[] keys;
    private final AtomicReferenceArray<V> values;

    @SuppressWarnings("unchecked")
    private final K nonCollapsibleKey = (K) new Object();
    private final int mask;
    private final int capacity;

    private volatile long firstWrite = 1; // the oldest slot that is is safe to write to
    private final AtomicLong lastRead = new AtomicLong(0); // the newest slot that it is safe to overwrite

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

        for (long updatePosition = firstWrite; updatePosition < nextWrite; updatePosition++) {
            int index = mask(updatePosition);

            if(key.equals(keys[index])) {
                values.set(index, value);

                if (updatePosition >= firstWrite) {  // check that the reader has not read beyond our update point yet
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
        if (isFull()) {
            rejectionCount.lazySet(rejectionCount.get() + 1);
            return false;
        }

        cleanUp();
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
            values.lazySet(index, null);
        }
    }

    private void store(K key, V value) {
        long nextWrite = this.nextWrite.get();
        int index = mask(nextWrite);

        keys[index] = key;
        values.set(index, value);

        this.nextWrite.lazySet(nextWrite + 1);
    }

    @Override
    public int poll(Collection<? super V> bucket) {
        return fill(bucket, nextWrite.get());
    }

    @Override
    public int poll(Collection<? super V> bucket, int maxItems) {
        long claimUpTo = min(firstWrite + maxItems, nextWrite.get());
        return fill(bucket, claimUpTo);
    }

    private int fill(Collection<? super V> bucket, long claimUpTo) {
        firstWrite = claimUpTo;
        long lastRead = this.lastRead.get();

        for (long readIndex = lastRead + 1; readIndex < claimUpTo; readIndex++) {
            int index = mask(readIndex);
            bucket.add(values.get(index));
            values.set(index, null);
        }

        this.lastRead.lazySet(claimUpTo - 1);
        return (int) (claimUpTo - lastRead - 1);
    }

    private int mask(long value) {
        return ((int) value) & mask;
    }

}

```

1. via: [源码地址](https://github.com/LMAX-Exchange/LMAXCollections/blob/master/CoalescingRingBuffer/src/main/java/com/lmax/collections/coalescing/ring/buffer/CoalescingRingBuffer.java)
2. Blog is [The Coalescing Ring Buffer](https://nickzeeb.wordpress.com/2013/03/07/the-coalescing-ring-buffer/)
3. [Talk about Coalescing Ring Buffer](https://github.com/nickzeeb/LmaxTeachingCollections) and  [Talk's PPT](https://docs.google.com/presentation/d/1Yxw-9ZFM_maRORk_qGkdt9vqKKQNLjDSSd9DIOtbrIE/pub?start=false&loop=false&delayms=3000&slide=id.gd1b61aea_00)



