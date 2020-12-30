---
description: '#堆'
---

# Heap

### 堆 <a id="dui"></a>

什么是堆？‌

1. 堆是一个完全二叉树
2. 堆中的每个节点的值都必须大于等于（或者小于等于）其子树节点的值

‌

堆中的每个节点都大于等于其子树节点的值，这种堆叫大顶堆；堆中的每个节点都小于等于其子树节点的值，这种堆叫小顶堆

​‌

### 堆排序 <a id="dui-pai-xu"></a>

‌

如何使用堆进行排序‌

### 堆的应用 <a id="dui-de-ying-yong"></a>

‌

堆有哪些应用场景：‌

1. 优先级队列
2. 求中位数
3. 求 Top K 问题

‌

#### 合并有序小文件 <a id="he-bing-you-xu-xiao-wen-jian"></a>

> 假设有 100 个小文件，每个文件的大小是 100MB，每个文件中存储的都是有序的字符串。我们希望将这些 100 个小文件合并成一个有序的大文件。

​‌

#### 高性能定时器 <a id="gao-xing-neng-ding-shi-qi"></a>

> 实现一个高性能的定时器

​‌

#### 求 top k <a id="qiu-top-k"></a>

> 如何在一个包含 n 个数据的数组中，查找前 K 大数据呢？

​‌

#### 求中位数 <a id="qiu-zhong-wei-shu"></a>

> 如何求动态数据集合中的中位数?

​‌

#### 求热门榜 top 10 搜索关键词 <a id="qiu-re-men-bang-top-10-sou-suo-guan-jian-ci"></a>

> 假设有一个包含10亿搜索关键词的日志文件，如何快速找到热门榜 top10 的搜索关键词？

​‌

#### 新闻或视频站点中，求点击流中 top 10 的新闻或视频 <a id="xin-wen-huo-shi-pin-zhan-dian-zhong-qiu-dian-ji-liu-zhong-top-10-de-xin-wen-huo-shi-pin"></a>

> 有一个访问量非常大的视频网站，希望将点击量排名 top10 的视频取出来作为首页推荐，更新时间控制在1小时以内，该如何实现？

‌

* MVP 版本

​‌

![](https://gblobscdn.gitbook.com/assets%2F-M5PFXCiDtVclypn0iMK%2F-MBA5N0XJe2WAOQSKX-7%2F-MBA60DeEDcIzBAzHhN9%2Fimage.png?alt=media&token=b67d36a1-7a1b-4fce-8388-22ccfb050a68)

假设一个站点的视频量1亿，统计这些视频的点击次数，如果放在 Map 中存储，key 和 value 都为 int 类型，需要 10^8 \* 8 Byte = 800MB 的空间，算上指针等，预估 1.2 G；‌

分为两部分处理，API 服务负责接收点击请求，并存储到追加写的日志文件中，日志文件每 50 分钟产生一个，然后下一个50分钟写入新的文件中；另外有一个程序监控日志文件，从文件中不断的读数据，并进行统计，每次统计后，更新map的同时，也更新堆，这样在处理的过程中，堆中始终是 top 10，然后每隔一个小时，将堆中的数据写入外部存储中，如db或者内存数据库等‌

* 优化版 V1

​

![](https://gblobscdn.gitbook.com/assets%2F-M5PFXCiDtVclypn0iMK%2F-MBA5N0XJe2WAOQSKX-7%2F-MBA7_VZ9qwHuB_7hRc0%2Fimage.png?alt=media&token=9cde4447-913d-4254-b5fe-a10d7279545e)

​

​

