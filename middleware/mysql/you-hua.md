# 优化



* count 优化

![count &#x7684;&#x5DE5;&#x4F5C;&#x8FC7;&#x7A0B;&#xFF08;&#x7B80;&#x7248;&#xFF09;](../../.gitbook/assets/image%20%2839%29.png)

在使用 count 需要注意的是：一个优化点是在数据量大的时候，可以找一个字段长度相对较小的列做一个二级索引，这样 mysql 在统计的时候加载的数据量更小，IO需要的时间就更少；count 不计算 NULL 值

更进一步的计数优化，需要在架构上做更多的设计

![](../../.gitbook/assets/image%20%2840%29.png)

参考：

1. [select count 底层做了什么？](https://zhuanlan.zhihu.com/p/71333492)
2. [https://www.zhihu.com/question/34781415](https://www.zhihu.com/question/34781415)



