# 03-第一次全方位认识贪心算法

这是训练营的第3周，本周的内容较上周少一些，主要涉及BFS，DFS，贪心算法和二分查找；其中二分查找的思想有在上周的总结中有所提及，做了一些分析，但贪心算法早在读大学时就有了解，可惜从来没有全方位的去学习和理解他适用的问题场景、他的优点和他的局限，本周特别的对贪心算法做了些功课。

在求解最优化问题的时候，需要经过一系列的步骤，在每个步骤中都会面临多种选择；对于其中的某些问题，可以简单高效的解决，相比于动态规划，贪心算法就是这样一类算法，在每一步中都做出当前来看最优的选择：通过局部最优的选择，来得出全局最优（可见，这种方式不是对所有问题都是有效的，而我们只需要关注那些有效的问题即可）。

### 贪心算法的基本要素

下面一段关于贪心算法的原理，出自《算法导论》贪心算法章节

> 贪心算法的一般性质：
>
> 1. 将最优化问题转化成这样的形式：对其做出一次选择后，只剩下一个子问题需要求解
> 2. 证明做出贪心选择后，原问题总是存在最优解，即贪心选择总是安全的
> 3. 证明做出贪心选择后，剩余的子问题满足性质：其最优解与贪心选择组合即可得到原问题的最优解，这样就得到了最优子结构

可见，使用贪心算法解决问题的核心是：1. 贪心选择后，剩下一个子问题而且局部最优解能够构造出全局最优解；2. 最优子结构，贪心选择后的子问题与贪心选择的最优解组合可以得到原问题的最优解；这么说是比较抽象

### 分析活动选择问题

从分析一个简单贪心算法的例子开始，活动选择问题是一个比较典型的应用贪心算法的例子，问题描述如下：

> 有 n 个活动，每个活动都有起始时间，这些活动使用同一个资源，这个资源在某个时刻只能提供一个活动使用。如果两个活动的起始时间不重复，则他们是兼容的。在这个活动集合中求出最大兼容活动集。 例子： start\[\] = {1, 3, 0, 5, 8, 5}; finish\[\] = {2, 4, 6, 7, 9, 9}; 输出：{0, 1, 3, 4} \(0 1 3 4 是下标\)

对于能应用贪心算法的问题，最大的难度是如何证明贪心选择是安全的，大部分情况下，我们都是凭借着归纳法、感觉和经验，实际上这是可以证明的。

我们利用贪心算法的一般性质来分析一下活动选择问题，首先我们需要选定贪心的立足点，也就是我们在什么角度上分解出当前选择和子问题，现在我们以最早结束时间为贪心的对象，因为直观上来讲，越早结束的活动，留给后面的活动选择子问题越多的时间，所以我们贪心的选择最早结束的活动，现在来看：

1. 做出一次选择后，是否只剩下一个子问题？是的，比如我们选择活动a1后，且a1是最早结束的活动，那么接下来的问题就是在剩下的集合中再选择一个最早结束的活动；依次类推，每一步都是重复之前的动作：做出当前看来最优的选择（最早结束的活动），求剩下活动集合的子问题的解
2. 针对这个问题，贪心选择是安全的吗？可以证明，针对活动选择问题，在做出贪心选择后，总是存在最优解
3. 是否能得到最优子结构？最优子结构，和最近重复性是有一些相似的，最优子结构就是可重复的子问题，这样就可以将复杂的问题拆解成更小的子问题的解

那么，求解活动选择问题的步骤如下：

1. 对所有的活动根据结束时间排序，从小到大
2. 选择第一个活动，因为他就是最早结束的活动
3. 接着往后找最早结束的活动，同时要满足该活动的开始时间要大于等于上一个选择的活动的结束时间，一直到没有活动可以选择

代码实现：

```java
// 假设活动已经是根据结束时间有序的
public class ActivitySelection {
    /**
     * 求解最大的兼容活动集合, 返回下标的集合
     * s 表示活动开始时间的集合
     * f 表示活动结束时间的集合
     * n 表示活动的总数
     */
    public List<Integer> maxActivites(int[] s, int[] f, int n) {
        List<Integer> res = new ArrayList<>();
        int selected = 0;
        res.add(selected);

        // 求剩余子问题的解
        for (int j = 0; j < n; j++) {
            // 该活动的开始时间要大于等于上一个选择的活动的结束时间
            // 就是下一个要选择的活动
            if (s[j] >= f[i]) {
                selected = j;
                res.add(selected);
            }
        }
        return res;
    }
}
```

### 贪心算法的实际应用

1. 哈夫曼编码
2. 最小生成树：Prim 算法和 Kruskal 算法
3. 最短路径：Dijkstra 算法

以上都是贪心算法的一些典型的实际应用，每一个总结起来都需要花费大量的精力，留在以后慢慢分析，在这里立一个 Flag.

此外，将贪心算法和动态规划放在一起做对比，放在学习完动态规划后的总结中。

### 相关题目练习

利用老师课上讲的利用贪心选择和最近重复性的原则，以及贪心算法的基本要素：证明贪心选择的安全性以及找出最优子结构，原问题的最优解可以通过组合当前的选择和子问题的最优解得到。

* [移掉 K 个数字](https://leetcode-cn.com/problems/remove-k-digits/)

问题描述：

> 给定一个以字符串表示的非负整数 num，移除这个数中的 k 位数字，使得剩下的数字最小。
>
> 1. num 的长度小于 10002 且 ≥ k
> 2. num 不会包含任何前导零

题解： 这道题的初步感觉是挺简单的，但是仔细想了之后发现并不简单。 可以把问题分解为：当前选择是移除1个数字使剩余后的数字最小；子问题是在剩余的数字中再选择一个1个数字使剩余的数字最小；可见具有重复性，而且是最近重复性，而且当前选择并不会影响将来的选择，都是选择最小的，使原问题的解最终最小

代码实现：

```java
public String removeKdigits(String num, int k) {
    if (k >= num.length()) return "0";

    char[] stack = new char[num.length()];
    int sPos = 0;

    for (char curr : num.toCharArray()) {
        // 移动到新的字符上时，都去找当前的最大值，移除掉
        while (k > 0 && sPos > 0 && stack[sPos] > curr) {
            sPos--;
            k--;
        }

        // 如果第一个位置是0的话，直接丢弃
        if (sPos != 0 || curr != '0') {
            stack[sPos++] = curr;
        }
    }

    String res = new String(stack, 0, sPos - k);
    return res.isEmpty() ? "0" : res;
}
```

* [无重叠区间](https://leetcode-cn.com/problems/non-overlapping-intervals/)

问题描述：

> 给定一个区间的集合，找到需要移除区间的最小数量，使剩余区间互不重叠。
>
> 1. 可以认为区间的终点总是大于它的起点。
> 2. 区间 \[1,2\] 和 \[2,3\] 的边界相互“接触”，但没有相互重叠。

* [任务调度器](https://leetcode-cn.com/problems/task-scheduler/)

### 参考

1. 算法导论
2. 数据结构与算法分析
3. [Greedy Algothrim](https://www.geeksforgeeks.org/greedy-algorithms/)

## 思考题

思考题：使用二分查找，寻找一个半有序数组 \[4, 5, 6, 7, 0, 1, 2\] 中间无序的地方

分析：

* 理解题目的意思

输入是一个半有序的数组，首先我们假设数组是升序后做了旋转后得到的，所以有一些特点：在断点处的前后都是有序的，但整体无序；第一个元素大于最后一个元素 输出是找到那个无序的索引位置，我们假设返回的是最小元素的位置索引i，自然的，i-1就是最大元素的位置索引

* 找解决思路

思路1：使用暴力方式，以此遍历数组中的每个元素，比较两个元素的大小，如果出现前面的值大于后面的值的地方，就是我们要找的；但是此种方法，时间复杂度是 O\(n\)，复杂度有些高，考虑是否有更优化的解法

思路2：虽然数组是整体无序的，但是部分是有序的，可以借助这个特性使用二分查找；已知 `nums[0] > nums[nums.length-1]`, 我们使用二分查找获取 `nums[mid]`, 让 `nums[mid]` 和 `nums[nums.length-1]` 做比较，如果 `nums[mid]` 大，说明断点出在 `mid` 之前，就调整 `low` 的位置；否则在 `mid` 之后，调整 `high` 的位置，这样持续下去，直到找到那个位置

此外，mid元素和位置0的元素做比较，道理是一样的

综合分析思路1和思路2，思路2的时间复杂度是 O\(logn\), 思路1的时间复杂度是O\(n\), 这个角度来看，思路2优于思路1，选择思路2做代码实现

* 编写代码

```java
public class HomeWork1 {
    /**
     * 查找半有序数组的最小元素的索引（同理就是找无序的地方）
     */
    public int findMinElementIndex(int[] nums) {
        int low = 0, high = nums.length - 1;
        int lastElement = nums[high];
        while (low < high) {
            int mid = low + ((high - low) >> 1);
            if (nums[mid] < lastElement) high = mid;
            else low = mid + 1;
        }
        return low;
    }
}
```

* 测试用例

```text
Test Case:
[10, 11, 12, 14, 1, 2, 3, 4, 5, 6, 7, 8, 9]
[10, 9]
[12, 13, 1, 2]
[12]
[12, 13, 14]
```

