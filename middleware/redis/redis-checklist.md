# Redis checklist

### 性能 Checklist

* Redis处理请求响应的线程模型是单线程的，所以对 Redis 的单次请求处理时间要尽可能的短，不然有可能会造成卡顿，尽量少做 O\(N\) 时间复杂度的事情，尽可能做接近 O\(1\) 时间复杂度的事情。比如 keys \* 就是一个 O\(N\) 的操作，当 key 的数量非常多时，会造成短暂的阻塞，keys \* 是遍历内存中的所有 key 并返回，scan 是一个很好的替代方案
* Redis的查询操作，是在内存操作的，所以很快，往往IO会成为问题，尽可能的让客户端做连接复用，如使用长连接、使用连接池，或者使用 Pipeline 合并多个 Redis 操作
* 尽量减少查询次数，可以用hash包裹多个string，比如存储用户信息，可以使用 hash，u:123 -&gt; {"name":"","age":12,"id":""...}，减少了查询次数，一次查询就取到所有数据
* Redis 是完全基于内存的，内存操作在100ns级别
* Redis 是基于内存的，所以内存资源是很宝贵的，尽可能优化kv的大小，比如key的设计，可以用简写，value做压缩等；对于大量的string小对象，可以考虑使用hash组合他们
* 避免 bigkey，避免操作 bigkey
* 大量key集中过期
* 淘汰策略：淘汰策略也是在主线程执行的，当内存超过Redis内存上限后，每次写入都需要淘汰一些key，也会造成耗时变长；
* AOF刷盘开启always机制：每次写入都需要把这个操作刷到磁盘，写磁盘的速度远比写内存慢，会拖慢Redis的性能；
* 主从全量同步生成RDB：虽然采用fork子进程生成数据快照，但fork这一瞬间也是会阻塞整个线程的，实例越大，阻塞时间越久
* 并发量非常大时，单线程读写客户端IO数据存在性能瓶颈，虽然采用IO多路复用机制，但是读写客户端数据依旧是同步IO，只能单线程依次读取客户端的数据，无法利用到CPU多核。Redis 6.0 有所改进，引入了多线程去处理网络 IO

### Redis 配置 Checklist

--



