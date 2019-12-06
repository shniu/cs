
# 数据结构与算法

这是 CS 的一个核心课程，非常重要。

## 如何能学好

知乎上关于[如何学好数据结构？](https://www.zhihu.com/question/21318658)有非常多的回答，都很实用。总结一下就是：

1. 学习要有层次性，要多学几遍，每一遍的目标是不一样的
2. 学习要成体系，把零碎的知识点整合成一个稳固的知识体系
3. 学习理论，达到深度的理解，结合实际题目或者应用场景多练习
4. 需要刷题，要刷好几遍，一遍是不行的
5. 每天都要练习，日积月累，坚持下去
6. 动手写代码，动手写代码，动手写代码

## 梳理数据结构和算法知识体系

代码实现：[shniu/java-eco](https://github.com/shniu/java-eco)

- [动态规划](01-动态规划.md)

### 数组

- 定义及特点

一种线性数据结构，用一组连续的内存空间，来存储一组具有相同数据类型的数据

- 复杂度分析

数组插入 O(n) ; 无序数组插入且不考虑前后顺序的可以 O(1); 删除 O(n); 不考虑顺序的无序数组删除可以 O(1);
无序数组查找 O(n); 有序数组查找 O(logn)

- 操作
  
1. 基本的数组插入、删除、查找、下标索引等
2. 实现一个动态有序数组，支持插入、删除、查找等操作
3. 实现一个RingBuffer
4. 实现一个Resizable Array
5. 实现两个有序数组合并为一个

- 常见题目

1. 两数之和#1
2. 三数之和#15
3. 删除排序数组中的重复项#26
4. 加一#66
5. 合并两个有序数组#88
6. 旋转数组#189
7. 求众数#169
8. 移动零#283
9. 乘最多水的容器#11和接雨水问题#42

### 排序

排序算法是基础算法的一部分，也是必知必会的算法。

1. 手写实现冒泡排序，选择排序，插入排序，重点是归并排序，快速排序，堆排序
2. 分析他们的最坏、最好、平均时间复杂度，稳定性，原地排序，比较/数据交换次数
3. 了解希尔排序，计数排序，桶排序，基数排序

![排序](https://static001.geekbang.org/resource/image/1f/fd/1f6ef7e0a5365d6e9d68f0ccc71755fd.jpg)

典型问题：

- 在 O(n) 时间复杂度下找出第 K 大元素
- 现在有10个接口可访问日志文件，每个日志文件大小是300MB，每个文件里的日志都是按照时间戳从小到大排序的。现在希望将10个较小的日志文件合并为1个日志文件，合并之后的日志文件仍然按照从小到大的顺序排序。如果处理上述程序的机器的内存只有1GB，如何能“快速”地将这10个日志文件合并吗？

思路：从10个日志文件中每次批量读取一部分数据，假如是30M，每次遍历找到10个中的最小时间戳，然后写入排序就绪的临时数组，假如容量是400M，等临时数组满了之后，批量写入文件，同时当读入的某个数据空了之后，再批量读取数据到内存中；上面是一种思路，当然还有可优化的空间，另外一种思路是将数据批量复制到一个临时数组中做快排，数据的选取方式是取加载到内存的各数组最大值中的最小值，依次使用二分查找确定各数组小于等于该值的位置，然后批量复制过去做快排

- 假设有100个小文件，每个小文件的大小是100M，每个文件中存储的都是有序的字符串，将这100个小文件合并成一个有序的大文件？

思路：这个和上一个问题是一样的，根据上面的解法，可以利用堆来优化查找最小值的算法，每次取堆顶元素，放入大文件中，可提升查找性能

- 如何根据年龄给100万用户数据排序？
- 如何根据订单金额对10GB的订单数据进行排序，但是内存有限只有几百 MB？

思路：无法将数据一次性载入内存，可以考虑使用桶排序，对10GB数据根据订单金额划分为很多个桶，通过扫描数据可以得到订单金额最大值，假设是10万，可以分100桶，每个桶的范围是1000，批量加载一部分订单数据，根据划分规则，存储到100个桶中，桶之间是有序的，桶内再使用快速排序，如果某个桶的数据过大，再采用相同的策略进行桶排序

- 如果一个省的考生有100万，如何通过成绩快速排序得出名次？（提示：计数排序）
- 假设有10万个手机号码，希望将这10万个号码从小到大排序，如何快速的进行排序？（提示：基数排序。快排可以做到 O(nlogn)，使用基数排序可以做到O(n)）

#### 参考链接

- [十大经典算法](https://www.cnblogs.com/onepixel/p/7674659.html)

// todo 递归详解 回溯算法详解 二分查找思想 动态规划 图和图算法

## 算法题

详细请查看[算法题汇总整合](99-常见题目列表.md).

推荐阅读的书籍

```text
// 书单 https://pymlovelyq.github.io/posts/32a7f0eb/
算法导论
算法第四版
算法图解
大话数据结构
数据结构与算法分析
剑指offer
编程珠玑
编程之美
算法帝国
算法之美
数学之美
```

## 算法实现

- [All Algorithms implemented in Java](https://github.com/TheAlgorithms/Java)  star 19.5k，里面包含了许多常用的算法和数据结构的实现 √
- [陈皓的leetcode题解](https://github.com/haoel/leetcode)
- [120+ interactive Python coding interview challenges](https://github.com/donnemartin/interactive-coding-challenges)
- [一些经典的算法题解](https://github.com/Dev-XYS/Algorithms) 这里有各种算法的C++代码，任何人可以在自己的任何程序中使用
- [正确的姿势，学习的态度来刷 LeetCode：高效的代码、简洁的注释、精炼的总结。](https://github.com/selfboot/LeetCode)
- [LeetCode 算法题解](https://leetcode.wang/)  <https://windliang.cc/>
- [数据结构与算法之美专栏](https://github.com/wangzheng0822/algo)
- [Java : Algorithms and Data Structure](https://github.com/phishman3579/java-algorithms-implementation)
- [玩儿转算法面试](https://github.com/liuyubobobo/Play-with-Algorithm-Interview) 在慕课网上的课程《玩儿转算法面试》示例代码。课程的更多更新内容及辅助练习也将逐步添加进这个代码仓。

## 参考资源

- [常见算法复杂度分析](https://www.bigocheatsheet.com/)
- [极客时间算法训练营资料](https://pan.baidu.com/disk/home?#/all?vmode=list&path=%2F%E8%AF%BE%E7%A8%8B%2F%E7%AE%97%E6%B3%95%E4%B8%8E%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84)
- [我的印象笔记](https://app.yinxiang.com/fx/1cd152b8-dc5d-44e8-b1e4-3fa2d2f4dfb0)
- LeetCode
- [北大题库](http://poj.org/problemlist)
- [hihocoder.com](http://hihocoder.com)
- 算法训练营课程
- [coursera 普林斯顿算法课 Part1](https://www.coursera.org/learn/algorithms-part1) 难度相对低
- [coursera 普林斯顿算法课 Part2](https://www.coursera.org/learn/algorithms-part2) 难度相对低
- [斯坦福 算法 专项课程](https://www.coursera.org/specializations/algorithms) 难度相对中
- [麻省理工 算法导论](http://open.163.com/special/opencourse/algorithms.html) 难度相对中

### 博客

- [GeeksForGeeks: Graph Data Structure And Algorithms](https://www.geeksforgeeks.org/graph-data-structure-and-algorithms/) & [GeeksForGeeks 编程练习题，各大公司面试题](https://practice.geeksforgeeks.org/company-tags)
- [GeeksForGeeks: Greedy Algothrim](https://www.geeksforgeeks.org/greedy-algorithms/)
- [labuladong 的原创算法思考系列文章](https://labuladong.gitbook.io/algo/)  分析的非常好
- [为什么算法这么难？](http://mindhacks.cn/2011/07/10/the-importance-of-knowing-why-part3/)
- [正确的姿势，学习的态度来刷 LeetCode：高效的代码、简洁的注释、精炼的总结](https://github.com/selfboot/LeetCode)
- [知其所以然之永不遗忘的算法](https://selfboot.cn/2015/11/03/howto_find_algorithm/#%E4%B8%80%E4%B8%AA%E6%80%9D%E7%BB%B4%E5%8E%86%E7%A8%8B)
- [为什么算法这么难](http://mindhacks.cn/topics/algorithms/)
- [位运算](https://github.com/selfboot/LeetCode/tree/master/BitManipulation)
- [图论](https://coding.imooc.com/class/chapter/370.html) [Code on Github](https://github.com/liuyubobobo/Play-with-Graph-Algorithms)
- [结构之法 算法之道](https://blog.csdn.net/v_july_v?t=1)

### 动画演示

- [visualgo](https://visualgo.net/en) 算法演示
- [用动画的形式呈现解LeetCode题目的思路](https://github.com/MisterBooo/LeetCodeAnimation)
