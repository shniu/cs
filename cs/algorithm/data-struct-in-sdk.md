# Data Struct in SDK

* TreeMap
* 基数树 [Radix Trees](https://en.wikipedia.org/wiki/Radix_tree)
  * [Radix Tree 在 Redis 中的应用](http://mysql.taobao.org/monthly/2019/04/03/)
  * [Radix tree go 实现](https://github.com/armon/go-radix)
  * [The difference between trie and radix tree](https://stackoverflow.com/questions/14708134/what-is-the-difference-between-trie-and-radix-trie-data-structures)
* ART \([Adaptive Radix Trees](https://db.in.tum.de/~leis/papers/ART.pdf)\) 可变基数树 / 自适应基数树
  * [ART Java 实现](https://github.com/rohansuri/adaptive-radix-tree)
  * [ART GO 实现](https://github.com/plar/go-adaptive-radix-tree)， [另外一个实现](https://github.com/kellydunn/go-art)
  * [https://www.shuzhiduo.com/A/amd0ljb1dg/](https://www.shuzhiduo.com/A/amd0ljb1dg/)
  * [Multi-ART](https://zhuanlan.zhihu.com/p/65414186)， [Github 上的实现](https://github.com/UncP/aili)

![Radix Tree vs. Adaptive Radix Tree](../../.gitbook/assets/image%20%2863%29.png)

在有序数据结构的空间中，Radix树特别有趣，因为它们的高度和时间复杂度取决于 key 长度（k），而不是树中已经存在的 key 数量（n）。在极度庞大的数据集时代，当n比k增长得更快时，拥有一个与n无关的时间复杂度是非常有吸引力的。

然而，由于每个内部节点都有固定数量的子节点，它们一直被空间消耗过大的问题所困扰。

ART通过根据内部节点实际拥有的子节点数量，自适应地改变内部节点的大小来解决这个问题。随着内部节点的子节点数量的增加/减少，ART会将内部节点增长/缩小为4、16、48、256中的一种大小。

