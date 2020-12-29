---
description: '#复杂度分析'
---

# 算法中的复杂度分析

我们需要知道为什么要复杂度分析，因为在程序未写出来之前是没有办法运行的，如果都到了程序写出来之后才能验证快不快，着实浪费时间和精力，所以在编程之前就粗略估计时空开销，这个过程就是复杂度分析。

### 时间复杂度

#### 一般规则

* 有限次操作的时间复杂度是 `O(1)`, 有限次操作是指随着数据量的增加，操作次数不增加
* 单次 `for` 循环的时间复杂度是 `O(n)` , 表示和数据量的线性关系
* 嵌套 `m` 次 for 循环的时间复杂度是 `O(n^m)` 
* 树的高度是 `O(logn)` 
* 二分查找的时间复杂度是 `O(logn)`

复杂度分析的一些关键点：

* 复杂度分析的一般规则，也是一些简单的规则
* 一般有限次操作的时间复杂度是 O\(1\), 有限次操作是指随着数据量的增加，操作次数不增加
* 一般单次 for 循环的时间复杂度是 O\(n\), 表示和数据量的线性关系
* 一般嵌套 m 次 for 循环的时间复杂度是 O\(n^m\)
* 一般树的高度的时间复杂度是 O\(logn\)
* 一般对于二叉查找树的二分查找时间复杂度是 O\(logn\)
* 一般对于一个堆，弹出堆顶元素后，重新堆化的时间复杂度是 O\(logn\)
* 复杂度分析的组合方式
* 嵌套的多层循环，时间复杂度和嵌套层数m有关系，一般是 O\(n^m\), 如冒泡排序O\(n^2\)
* 有时算法是分多步完成的，例如 Topk 问题可以分解为：新建k个元素的堆；然后遍历 n - k 个元素插入堆中；调整堆；时间复杂度的组合就是：O\(k\) + O\(n-k\)  _O\(logk\) = O\(n_logk\)。需要根据具体算法分清楚是加还是乘

```text
// 表示成公式
如果 T1(N) = O(f(n)), T2(N) = O(g(n)), 那么
a. T1(N) + T2(N) = max(O(f(n)), O(g(n)))
b. T1(N) * T2(N) = O(f(n)*g(n))

如果 T(N) 是一个k次多项式，则 T(N) = O(N^k)
```

* 复杂度分析的递归求解

递归的分析方法相对来讲要复杂一些，需要先找出终止条件、子问题和递归式，然后可以使用一些数学分析方法来求解。

```text
// 二分查找的例子
int binarySearch(int[] arr, int low, int hight, int target) {
    if (low > hight) return -1;
    int mid = low + (hight - low) >> 1;
    if (arr[mid] == target) return mid;
    if (arr[mid] > target) {
        return binarySearch(arr, low, mid - 1, target);
    } else {
        return binarySearch(arr, mid + 1, hight, target);
    }
}

// 下面是分析
T(n) = O(f(n)), f(n) 用来表示数据量是 n 时的复杂度公式（或者叫操作次数等），那么 f(1) = 1 (可以理解为 1 是常量级的)；
从代码可知，每次递归查找会将数据量减半，所以有 f(n) = f(n/2) + 1, 依次递推则有：
f(n) = f(n/2) + 1
f(n/2) = f(n/4) + 1
...
f(n/2^(m-1)) = f(n/2^m) + 1
左右分别相加得到，f(n) = f(n/2^m) + m (总共有m次递归调用)
当 n/2^m = 1 时，f(n/2^m) = 1, 达到常量级，可求出解；所以 m = logn
f(n) = 1 + logn, 所以 T(n) = O(logn)，二分查找的时间复杂度是 O(logn)
```

递归求解需要根据算法进行一步步的分析，得出递推式，进行详细推导。此外，还可以使用主定理来分析递归。

* 均摊分析

摊还分析，又叫平摊分析，是一种特殊情况下的复杂度分析方法。暂时请参考[这里](https://time.geekbang.org/column/article/40447)

* 例子：归并排序的复杂度分析

```text
// 归并排序复杂度分析
int mergeSort(int[] arr, int low, int high) {
    if (low >= high) return;
    int mid = low + (hight - low) >> 1;

    mergeSort(arr, low, mid)
    mergeSort(arr, mid + 1, high)
    merge(arr, low, mid, high)
}
// 合并函数, 将两个有序数组合并为一个有序数组
void merge(A[p...r], A[p...q], A[q+1...r]) {
  var i := p，j := q+1，k := 0 // 初始化变量 i, j, k
  var tmp := new array[0...r-p] // 申请一个大小跟 A[p...r] 一样的临时数组
  while i<=q AND j<=r do {
    if A[i] <= A[j] {
      tmp[k++] = A[i++] // i++ 等于 i:=i+1
    } else {
      tmp[k++] = A[j++]
    }
  }

  // 判断哪个子数组中有剩余的数据
  var start := i，end := q
  if j<=r then start := j, end:=r

  // 将剩余的数据拷贝到临时数组 tmp
  while start <= end do {
    tmp[k++] = A[start++]
  }

  // 将 tmp 中的数组拷贝回 A[p...r]
  for i:=0 to r-p do {
    A[p+i] = tmp[i]
  }
}

// 下面是分析
1. 当n=1时，mergeSort 计算次数是1， f(1) = 1
2. 归并排序，当n较大时，第一步递归左半部分，第二步递归右半部分，第三步merge两个有序数组为1个, merge 的时间复杂度是 O(n)
可知 f(n) = f(n/2) + f(n/2) + n 
 = 2f(n/2) + n 
 = 2(2f(n/4) + n/2) + n = 4f(n/4) + 2n
 = ...
 = 2^k * f(n/2^k) + k * n

 当 n/2^k = 1 时，可求解，所以 k = logn, 带入 f(n) = n + logn * n
 所以归并排序的时间复杂度是 O(nlogn)
```

复杂度分析需要多多练习，需要掌握复杂度分析的一般规则、组合规则、递归求解规则等，其中递归求解是比较复杂的分析方法。

