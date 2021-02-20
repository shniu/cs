# \[译\] 在 Redis 中存储数亿个简单键值对

在 Instagram, 出于遗留原因，需要将大约 3 亿张照片和创建它们的用户之间做映射，以便知道要查询哪个分片; 最终所有客户端和 API 应用程序都将进行更新以将全部信息传递给我们，但是仍然有很多客户端和 API 应用缓存了旧信息。需要找到一种解决方案：

1. 支持检索 key 并能迅速的返回
2. 尽可能的少占用内存
3. 非常适合现有的基础架构设施（注：最好用现在已经使用的中间件等）
4. 支持持久化，这样，如果服务器死机，我们就不必重新填充和初始化

解决此问题的一种简单解决方案是将它们简单地存储为数据库中的多个行，每一行都有 "Media ID" 和 "User ID" 两列。但是，这些 ID 不会更新（仅插入），不需要进行事务处理且与其他表没有任何关系，因此 SQL 数据库似乎有些太重了。

使用 Redis 也可以轻松的解决这个问题，并且在 Instagram 中 Redis 也被广泛使用。最开始的方案是为每个 Media ID 映射一个 User ID，如下:

```text
SET media:1155315 939
GET media:1155315
> 939
```

然而使用这种方案，我们发现 Redis 需要使用 70MB 的内存空间去存储 100 万数据，以此推测，存储 3 亿数据需要大约 21GB 的内存空间；明显这个空间对于单个 Redis 实例是有些大的，是否有更节省内存空间的办法呢？

Redis 有 Hash 数据结构，它可以很好的解决这个问题，具体做法是将 3 亿数据按照 Media ID 分成 1000 个桶，桶的计算方式是 `Media ID / 1000` 这种除法的计算方式，然后对每个桶的数据使用 Hash 数据结构进行存储，如下：

```text
# 1155315 / 1000 = 1155
HSET "mediabucket:1155" "1155315" "939"
HGET "mediabucket:1155" "1155315"
> "939"
```

其中，`mediabucket:1155` 是 Hash 的 key, `1155315` 是 field, 表示 Media ID，`939` 是 value, 表示 User ID；使用这种方式后，总共需要的内存是大约 5GB。

via: [Storing hundreds of millions of simple key-value pairs in Redis](https://instagram-engineering.com/storing-hundreds-of-millions-of-simple-key-value-pairs-in-redis-1091ae80f74c), Translate: shaohan.niu

本文结论：**在存储简单键值对时，如果少量的数据，无论使用 string 还是 hash，都是不错的选择；但是面对大量的数据时，比如上亿数据，这个时候我们就需要考虑存储空间了，我们使用 Redis 是为了提速，同时也要考虑占用的内存空间大小，这点很重要，使用 hash 就会节省很多内存空间。**

**额外的思考：为什么两种方案占用的内存空间差异这么大呢？第一种方案是使用 String 数据结构直接存储；第二种方案是使用 Bucket + Hash 数据结构 ??**

