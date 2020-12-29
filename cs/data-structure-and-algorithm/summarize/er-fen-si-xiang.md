# 二分思想

从一个例子开始，[两个人进行猜数游戏](https://leetcode-cn.com/problems/guess-number-higher-or-lower/)，其中一个人写下一个数字，另外一个人猜，每猜一个数，给这个人说大了还是小了，继续猜，比如猜一个100以内的数，写下的数是64，最多猜7次就可以猜到这个数，这里就使用了二分思想。

二分思想是一个应用很广泛的思想，比如对于一个有序数组，它能将查找效率从O\(n\)优化到O\(logn\)，因为每次可以将范围缩小为上一次的一半。这是在数组中的应用场景，我们以这个为基础来分析一下二分查找的时间复杂度。

对于一个有 n 个元素的有序数组中，每次查找后缩小数据范围为上一次的二分之一，所以有 

```text
n/2 , n/4 , n/8, … , n/(2^k)
```

当 n/\(2^k\) = 1 时，得到最终结果，则 k = logn，记作二分查找的时间复杂度 O\(logn\)，是一个非常高效的算法；举个例子，如果我们在一个40亿的数据中查找某个数，也只需要32次，相对于顺序查找效率提升了太多，可见其威力。

总结一下，二分查找是针对一个有序集合，每次通过将要查找的数据范围缩小为上一次的一半，直到找到目标值，或者区间缩小为0。二分查找正是在有序数组上应用了二分思想。

二分思想其实是一种思考问题的方法，为了加速查找效率而生，所谓的二分并不代表一定是二，也可以是三，可以是N，只是一种表述，表达的意思是以最快的速率将搜索数据的范围缩小。

#### 二分思想在有序数组上的应用及其变形

二分细想在数组上的实现算法是二分查找，[二分查找的一般实现](https://leetcode-cn.com/problems/binary-search/)，有几个需要注意的点

```java
public class BinarySearch {
    // 二分查找实现
    public static int search(int[] arr, int target) {
        int low = 0, high = arr.length - 1;

        // 这里的中止条件是 low <= high, 因为 high = arr.length - 1
        while (low <= high) {
            // 使用 low + (high - low) / 2, 而不使用 (high + low) / 2, 是因为 high + low 可能造成整型溢出
            // int mid = low + (high - low) / 2;  // 这种方式是可以的，不如位运算效率高
            int mid = low + ((high - low) >> 1);  // 这种方式是最优的，效率最高

            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] > target) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }

    // 利用递归实现二分查找
    public static int searchRecursive(int[] arr, int target) {
        return recurSearch(arr, target, 0, arr.length - 1);
    }
    private static int recurSearch(int[] arr, int target, int left, int right) {
        // terminator
        if (left > right) 
            return -1;

        int mid = left + ((right - left) >> 1);

        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] > target) {
            return recurSearch(arr, target, left, mid - 1);
        } else {
            return recurSearch(arr, target, mid + 1, right);
        }
    }
}
```

以上是一个常规的二分查找实现，这个数组中没有重复元素，查找给定值的元素，但是还有更难的：

1. 查找第一个值等于给定值的元素位置
2. 查找最后一个值等于给定值的元素位置
3. 查找第一个大于等于给定值的元素位置
4. 查找最后一个小于等于给定值的元素位置

  
这几个问题的代码都相对难写，代码实现如下：

```java
class BinarySearchExt {
    // 查找第一个值等于给定值的元素位置
    public static int searchFirst(int[] arr, int target) {
        int left = 0, high = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] > target) {
                right = mid - 1;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                if (mid == 0 || arr[mid - 1] != target) return mid;
                else high = mid - 1;
            }
        }

        return -1;
    }

    // 查找最后一个值等于给定值的元素位置
    public static int searchLast(int[] arr, int target) {
        int left = 0, high = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] > target) {
                right = mid - 1;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                if ((mid == arr.length - 1) || (arr[mid + 1] != target)) return mid;
                else left = mid + 1;
            }
        }
        return -1;
    }

    // 查找第一个大于等于给定值的元素位置
    public static int searchGte(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + ((right - left) >> 1);
            if (arr[mid] >= target) {
                if ((mid == 0) || (arr[mid - 1] < target)) return mid;
                else right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return -1;
    }

    // 查找最后一个小于等于给定值的元素位置
    public static int searchLte(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + ((right - left) >> 1);
            if (arr[mid] <= target) {
                if ((mid == arr.length - 1) || (arr[mid + 1] > target)) return mid;
                else left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }
}
```

做个总结，分析一下二分查找的应用场景：

1. 二分查找依赖于顺序表结构，如数组；在链表上直接运用二分查找效率低
2. 二分查找需要数据是有序的，乱序的数据集合中无法应用，因为没有办法二分；所以对于相对静态的数据，排序后应用二分查找的效率还是很不错的；而对于动态变化的数据集合，维护成本会很高
3. 数据量太小，发挥不出二分查找的威力；但是如果比较操作比较耗时，还是推荐使用二分查找
4. 数据量太大，内存放不下

一个思考题：如何在1000万整数中快速查找某个整数呢？要求内存限制是100M，可以使用二分查找，先对1000万整数分配一个数组，然后进行排序，然后再使用二分查找。其他的方法，可能无法满足内存限制的问题，比如散列表、跳表、AVL树等。

另外一个思考题：如何快速定位一个IP的归属地？假设有10万+的IP地址段和归属地的映射关系，我们先对IP地址段的起始地址转成整数后排序，利用二分查找“在有序数组中，查找最后一个小于等于给定值的元素位置“，这样就可以找到一个ip段，然后取出来判断是不是在这个段里，不在的话，返回未找到；否则返回对应的归属地。

#### 延伸之链表上的二分思想应用

我们分析说在链表上应用二分查找的效率很低，那么为什么呢？分析一下

假设有n个元素的有序链表，现在用二分查找搜索数据，第一次移动指针次数 n/2，第二次移动 n/4，一直到 1，所以总的移动次数相加就是 n-1 次可见时间复杂度是 O\(n\), 这个和顺序查找链表的时间复杂度 O\(n\) 是同级别的，其实二分查找比顺序查找的效率更低，因为它做了更多次无谓的指针移动

我们知道了二分思想直接应用到链表上是不可行的，有没有其他的办法，其实有，就是为有序链表增加多级索引，在搜索的时候根据索引应用二分思想。

跳表就是这样一种数据结构，它既可以维护链表的有序性，还可以动态更新删除数据，而且提供了 O\(logn\) 时间复杂度，但是相比于链表的 O\(1\) 空间复杂度，跳表是 O\(n\)  的时间复杂度。跳表如下

![](../../../.gitbook/assets/image%20%2886%29.png)

其核心思想是每 m 个节点\(m 可以是2，3，5…，根据实际情况指定\)就提取一个节点出来作为上级的索引节点，我们可以提取k层，这样在查询时就可以根据每层索引快速的查询到链表上的数据，整个的思想其实和AVL树是很像的，尤其是 B+ 树。

跳表支持的核心功能：

1. 动态插入一个数据  O\(logn\)
2. 动态删除一个数据  O\(logn\)
3. 查找一个数据  O\(logn\)
4. 按照区间查找数据  O\(logn\)
5. 迭代输出有序序列

其中，插入、删除、查找以及迭代输出有序序列，像红黑树这种近似AVL树也能够完成，但是按照区间输出这个功能，红黑树的效率没有跳表高。  
我们在实现跳表的时候，有一些关键点需要注意：

1. 选取跳表的最大索引层次以及如何选取多少个节点提取一个上级索引？一般情况下是选取16层/32层/64层，可根据实际的数据和应用常见来选择，以免层数太少，数据量太大导致退化到链表的时间复杂度；此外，每个节点要建立几级索引，一般的做法是使用一个随机函数，这个函数要够随机，通过随机函数的计算得到该节点的最大层数
2. 在删除和插入数据时，跳表需要动态维护索引和数据，要尽量保证索引大小和数据大小的平衡性
3. 此外，和红黑树相比，跳表的实现难度要小于红黑树，代码实现更加可读、不易出错，同时还更加灵活，通过改变索引构建策略有效平衡执行效率和内存消耗

  
跳表是一个高性能的数据结构，在Redis中得到了应用。Redis 的有序集合的底层数据结构就有用到跳表\(Skiplist\)；

#### 延伸之树上的二分思想应用

AVL和B+树

#### 延伸之图上的二分思想应用

二分图

#### 延伸之排序算法中二分思想的影子

归并和快速排序

#### 二分查找常见的算法题目

[搜索插入位置](https://leetcode-cn.com/problems/search-insert-position)，这是一道二分查找的直接应用实现如下

```java
class Solution {
    public int searchInsert(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        
        int pos = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                pos = mid;
                break;
            } else if (nums[mid] > target) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return pos == -1 ? left : pos;
    }
}
```

[搜索二维矩阵](https://leetcode-cn.com/problems/search-a-2d-matrix/)

[搜索二维矩阵II](https://leetcode-cn.com/problems/search-a-2d-matrix-ii/)

[有序矩阵中第K小的元素](https://leetcode-cn.com/problems/kth-smallest-element-in-a-sorted-matrix/)

[两数相除](https://leetcode-cn.com/problems/divide-two-integers/)

[搜索旋转排序数组](https://leetcode-cn.com/problems/search-in-rotated-sorted-array/)

这个题目也是二分查找的一个拓展题目，代码实现如下

```java
class Solution {
    public int search(int[] nums, int target) {
        if (nums.length == 1) return nums[0] == target ? 0 : -1;
        int low = 0, high = nums.length - 1;
        
        while (low <= high) {
            int mid = low + (high - low) / 2;
            
            if (nums[mid] == target) return mid;
            
            // 这里是关键
            // nums[low] <= target && target < nums[mid] 表示 low mid 是有序的，且target在它们中间，需要将high向前移动
            // nums[low] > nums[mid] && target > nums[high] 表示 low ~ mid 是无序的，而且 target 比 high 位置的元素还要大，因为 mid ~ high 是有序的，所以必然在 low ~ mid 中间，移动high
            // nums[low] > nums[mid] && target < nums[mid] 表示 low ~ mid 是无序的, 而且 target 比mid位置处的值还要小，因为 mid ~ high 是有序的，所以必然在 low ~ mid 中间，移动high
            // 否则，就是移动low
            if ((nums[low] <= target && target < nums[mid]) ||
                  (nums[low] > nums[mid] && target > nums[high]) ||
                  (nums[low] > nums[mid] && target < nums[mid])) {
                // 这里是
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        
        return low == high && nums[low] == target ? low : -1;
    }
}
```

  
[搜索旋转排序数组II](https://leetcode-cn.com/problems/search-in-rotated-sorted-array-ii/)

[在排序数组中查找元素的第一个位置和最后一个位置](https://leetcode-cn.com/problems/find-first-and-last-position-of-element-in-sorted-array/)

Pow\(x,n\)x的平方根，如果是精确到小数后6位呢？

[有效的完全平方数](https://leetcode-cn.com/problems/valid-perfect-square/)

[寻找旋转排序数组中的最小值](https://leetcode-cn.com/problems/find-minimum-in-rotated-sorted-array/)

在半有序的数组中查找最小元素，可以参考之前[写的总结](https://github.com/algorithm004-01/algorithm004-01/issues/634)

```java
class Solution {
    // 关键是边界
    public int findMin(int[] nums) {
        int low = 0, high = nums.length - 1;
        int lastElement = nums[high];
        while (low < high) {
            int mid = low + ((high - low) >> 1);
            // 比最后一个元素小，说明转折点必定在mid的左边, 搜索左边
            if (nums[mid] < lastElement) high = mid;
            // 否则在右边
            else low = mid + 1;
        }
        return nums[low];
    }
}
```

[寻找峰值](https://leetcode-cn.com/problems/find-peak-element/)

[长度最小的子数组](https://leetcode-cn.com/problems/minimum-size-subarray-sum/)

[完全二叉树的节点个数](https://leetcode-cn.com/problems/count-complete-tree-nodes/)

[二叉搜索树第K小的元素](https://leetcode-cn.com/problems/kth-smallest-element-in-a-bst/)

[寻找重复数](https://leetcode-cn.com/problems/find-the-duplicate-number/)

[最长上升子序列](https://leetcode-cn.com/problems/longest-increasing-subsequence/)

[两个数组的交集](https://leetcode-cn.com/problems/intersection-of-two-arrays/)

[两个数组的交集II](https://leetcode-cn.com/problems/intersection-of-two-arrays-ii/)

[寻找右区间](https://leetcode-cn.com/problems/find-right-interval/)

[找到K个最接近的元素](https://leetcode-cn.com/problems/find-k-closest-elements/)

[基于时间的键值存储](https://leetcode-cn.com/problems/time-based-key-value-store/)

[在D天内送达包裹的能力](https://leetcode-cn.com/problems/capacity-to-ship-packages-within-d-days/)

[有效括号的嵌套深度](https://leetcode-cn.com/problems/maximum-nesting-depth-of-two-valid-parentheses-strings/)

[元素和小于等于阈值的正方形的最大边长](https://leetcode-cn.com/problems/maximum-side-length-of-a-square-with-sum-less-than-or-equal-to-threshold/)

####  参考资料

* [数据结构与算法之美-跳表](https://time.geekbang.org/column/article/42896)
* [Skiplist wiki](https://en.wikipedia.org/wiki/Skip_list)

