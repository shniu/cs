# 题目列表

## 基础知识点

### 栈，队列，优先队列，双端队列

* 栈的基本实现：用数组实现栈/用链表实现栈/数组实现的栈支持动态扩容
* 在 Golang 中栈并没有标准实现，可以手动实现一个栈用作底层库
* 编程实现一个浏览器的前进和后退功能
* 栈的应用：函数调用，浏览器前进后退，表达式求值，括号匹配
* 队列的基本实现：用数组/用链表分别实现顺序队列和链式队列
* 实现一个循环队列，其实就是 RingBuffer
* 优先队列实现原理，以及在Java和Golang中的实现
* 双端队列的实现原理
* 用栈实现队列，用队列实现栈

## LeetCode

* [155.最小栈](https://leetcode-cn.com/problems/min-stack/)
* [641.设计循环双端队列](https://leetcode-cn.com/problems/design-circular-deque)
* [20.有效的括号](https://leetcode-cn.com/problems/valid-parentheses/)
* [84.柱状图中最大的矩形](https://leetcode-cn.com/problems/largest-rectangle-in-histogram)
* [239.滑动窗口最大值](https://leetcode-cn.com/problems/sliding-window-maximum/)
* [50.Pow\(x,n\)](https://leetcode-cn.com/problems/powx-n/)
* [78.子集](https://leetcode-cn.com/problems/subsets/)
* [169.求众数](https://leetcode-cn.com/problems/majority-element)  有很多种解法
* [17.电话号码的字母组合](https://leetcode-cn.com/problems/letter-combinations-of-a-phone-number/)
* [51.N皇后](https://leetcode-cn.com/problems/n-queens/)
* [200.岛屿数量](https://leetcode-cn.com/problems/number-of-islands)
* [36.有效的数独](https://leetcode-cn.com/problems/valid-sudoku/)
* [102.二叉树的层次遍历](https://leetcode-cn.com/problems/binary-tree-level-order-traversal) [英文站](https://leetcode.com/problems/binary-tree-level-order-traversal)

要点：二叉树的层次遍历，思想接近图的BFS。实现方式1，使用queue做辅助进行遍历；实现方式2，使用递归，将层次信息和结果信息做追踪

* [433.最小基因变化](https://leetcode-cn.com/problems/minimum-genetic-mutation/) [英文站](https://leetcode.com/problems/minimum-genetic-mutation/)

要点：和单词接龙的题目相似，在基因库中使用BFS算法进行搜索，记录访问的字符串和使用队列进行BFS；优化的方向是使用双向的BFS，这样会更快一些。

* [127.单词接龙](https://leetcode-cn.com/problems/word-ladder/) [英文站](https://leetcode.com/problems/word-ladder/)

要点：对问题做转化，转变成基于图的BFS或者基于图的双向BFS

* [126.单词接龙II](https://leetcode-cn.com/problems/word-ladder-ii/) [英文站](https://leetcode.com/problems/word-ladder-ii/)
* [22.括号生成](https://leetcode-cn.com/problems/generate-parentheses) [英文站](https://leetcode.com/problems/generate-parentheses)
* [515.在每个树行中找最大值](https://leetcode-cn.com/problems/find-largest-value-in-each-tree-row)
* 零钱兑换 \(322\)

  给定不同面额的硬币 coins 和一个总金额 amount。编写一个函数来计算可以凑成总金额所需的最少的硬币个数。如果没有任何一种硬币组合能组成总金额，返回 -1。

滑动窗口问题 接雨水问题 数字组合问题 单词接龙 平方根

## 算法相关面试题

* 求一棵树中，从点1开始遍历完树中所有点的最短距离，所有相邻点之间的距离是1 \(美团面试题\)

理解题意： 思路： 实现：

* 二分图判定
* 最短路径算法，BFS Dijkstra 等，输出最短路径

