---
description: '#Sorting'
---

# Sorting Algorithm

理清楚排序算法的分析、代码实现和工业级排序算法需要考虑的指标，甚至于可以分析一下 java（或者 golang）中排序算法是如何实现的。

排序在日常开发中经常用到，而往往我们都是使用库函数中的排序函数，大部分人可能不知道它的具体实现是如何的，它的效率怎么样，什么数据量的情况下它会有好的表现，什么数据量下它会变的更糟，它会占用多大的额外空间，比较或者移动数据的次数是多少等等。

## 如何分析一个排序算法

想要知道一个排序算法是否适合应用场景，就需要知道如何分析和评价一个排序算法。可以从以下几点进行考察：

* 复杂度分析（包括时间和空间）

需要同时考虑时间复杂度和空间复杂度，对与时间复杂度要从至少三个维度进行分析：最好、最坏和平均复杂度分析，以及各自情况下对应的数据特点是怎么样的。因为不同有序度的数据，对排序算法的性能表现是有影响的。比如冒泡排序，在接近有序的数据下，可能会提前结束循环，复杂度可能会接近 O\(n\)

* 考虑在特定数据规模下的细粒度分析，把复杂度的常数项、系数、阶数也做分析

实际的软件开发中，我们排序的可能是 10 个、100 个、1000 个这样规模很小的数据，所以，在对同一阶时间复杂度的排序算法性能对比的时候，我们就要把系数、常数、低阶也考虑进来

* 是否是原地排序算法（原地排序说明是否会使用很多的额外空间）
* 是否是稳定的排序算法（稳定排序说明如果待排序的序列中存在值相等的元素，经过排序之后，相等元素之间原有的先后顺序不变）
* 排序过程中比较、交换/移动数据的次数

## 各大排序算法代码实现

算法实现依赖的接口定义：

```java
public interface Sortable {
    void sort(Comparable[] arr);
    default boolean less(Comparable v, Comparable w) {
        //noinspection unchecked
        return v.compareTo(w) < 0;
    }

    default boolean greater(Comparable v, Comparable w) {
        //noinspection unchecked
        return v.compareTo(w) > 0;
    }
}
```

### 冒泡排序

```java
public class BubbleSort implements Sortable {

    @Override
    public void sort(Comparable[] arr) {
        if (arr.length <= 1) return;

        // 哨兵，来监控是否可以提前结束
        boolean flag;
        for (int i = 0; i < arr.length; i++) {
            flag = false;  // 重置状态
            for (int j = 0; j < arr.length - i - 1; j++) {
                //noinspection unchecked
                if (arr[j].compareTo(arr[j + 1]) > 0) {
                    Comparable temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    flag = true;  // 有数据交换
                }
            }
            // 在本轮中未发生数据交换，表示已经有序，退出
            if (!flag) break;
        }
    }
}
```

### 选择排序

```java
public class SelectionSort implements Sortable {

    @Override
    public void sort(Comparable[] arr) {
        // 记录每轮次的最小值的位置，最后做置换
        int minPosPerRound;
        Comparable tmp;
        // loop len=arr.length times
        for (int i = 0; i < arr.length; i++) {
            minPosPerRound = i;  // 重置状态
            // 找到剩余未排序元素的最小值的位置
            for (int j = i + 1; j < arr.length; j++) {
                // minPosPerRound 位置的元素比 j 位置的大，就更新 minPosPerRound
                // arr[minPosPerRound].compareTo(arr[j]) > 0 -> greater(arr[minPosPerRound], arr[j])
                if (greater(arr[minPosPerRound], arr[j])) {
                    minPosPerRound = j;
                }
            }
            // swap，把最小值往前交换
            if (minPosPerRound != i) {
                tmp = arr[i];
                arr[i] = arr[minPosPerRound];
                arr[minPosPerRound] = tmp;
            }
        }
    }
}
```

### 插入排序

```java
public class InsertionSort implements Sortable {

    @Override
    public void sort(Comparable[] arr) {

        int pos;
        Comparable curr;  // 记录每次遍历的当前元素
        for (int i = 1; i < arr.length; i++) {
            pos = i - 1;  // 开始比较的初始位置
            curr = arr[i];  // 当前元素，缓存下来

            // 在前i个有序的数据中找一个合适的插入位置，并移动元素
            // 比较好的处理方式是从后往前比较，找到那个位置
            // arr[pos].compareTo(curr) > 0  -> greater(arr[pos], curr)
            while (pos >= 0 && greater(arr[pos], curr)) {
                arr[pos + 1] = arr[pos];
                pos--;
            }
            arr[pos + 1] = curr;
        }
    }
}
```

### 归并排序

```java
public class MergeSort implements Sortable {

    @Override
    public void sort(Comparable[] arr) {
        if (arr.length <= 1) return;
        sort(arr, 0, arr.length - 1);
    }

    private void sort(Comparable[] arr, int lo, int hi) {
        // terminator
        if (lo >= hi) {
            return;
        }

        int mid = lo + ((hi - lo) >> 1);  // 分成两半
        sort(arr, lo, mid);  // 排序左边
        sort(arr, mid + 1, hi);  // 排序右边
        merge(arr, lo, mid, hi);  // 合并结果
    }

    // 将两个有序的数组合并为一个有序数组
    private void merge(Comparable[] arr, int lo, int mid, int hi) {
        int len = hi - lo + 1;
        // 新申请一个数组
        Comparable[] temp = new Comparable[len];
        int i = lo, j = mid + 1, k = 0;
        while (i <= mid && j <= hi) {
            // arr[i] > arr[j], 复制后面的数据到temp
            if (greater(arr[i], arr[j])) {
                temp[k++] = arr[j++];
            } else {
                temp[k++] = arr[i++];
            }
        }

        while (i <= mid) temp[k++] = arr[i++];
        while (j <= hi) temp[k++] = arr[j++];

        // copy
        System.arraycopy(temp, 0, arr, lo, len);
    }
}
```

### 快速排序

```java
public class QuickSort implements Sortable {
    @Override
    public void sort(Comparable[] arr) {
        if (arr.length <= 1) return;
        sort(arr, 0, arr.length - 1);
    }

    private void sort(Comparable[] arr, int left, int right) {
        // terminator
        if (left >= right) return;

        int pos = partition(arr, left, right); // 分区位置
        // drill down
        sort(arr, left, pos - 1);  // 左边
        sort(arr, pos + 1, right);  // 右边
    }

    // 分区函数
    private int partition(Comparable[] arr, int left, int right) {
        Comparable pivot = arr[right];
        int partitionPos = left;
        for (int i = left; i < right; i++) {
            if (less(arr[i], pivot)) {  // arr[i] < pivot
                // swap arr[partitionPos] & arr[i]
                Comparable t = arr[partitionPos];
                arr[partitionPos++] = arr[i];
                arr[i] = t;
            }
        }
        arr[right] = arr[partitionPos];
        arr[partitionPos] = pivot;
        return partitionPos;
    }
}
```

### 堆排序

```java
// Todo
```

* [ ] Java 排序函数分析 
* [ ] Golang 排序函数分析 
* [ ] 实现工业级排序算法要注意什么

### 外部排序

外部排序的定义：**是指大文件的排序，排序的数据在外部存储中（如磁盘），但是数据太大而无法一次性装入内存，需要在内存和外部存储之间进行多次数据交换，以达到排序的目的。**  
可见相对于外部排序，还有内部排序（如快排、归并排序等）；外部排序常用的算法是多路归并算法

> 一个外部排序典型的例子现在我要进行排序，不过需要排序的数据很大，有1000G那么大，但是我的机器内存只有2G大小，如何进行排序呢？

典型的外部排序，一般步骤是：

1. 对大文件分治处理得到多个归并段（也就是小文件是有序的）输出到外部存储
2. 对多个归并段进行合并生成一个全局有序的文件

在这个过程中，有哪些需要注意的地方呢？

* 把整个算法过程更加详细的描述一下

> a、从大文件中读取数据，每次读取2G；b、对2G的数据进行内部排序，可以使用快排等；c、将排好序的数据输出到一个单独的文件中；d、重复a~c，直到所有数据都处理完成（循环处理500次）；e、将多个归并段的数据读入内存（同时处理500个文件，或者一次处理100个，进行多轮归并），选择最小的数据；f、将排好序的数据输出到外部存储g、重复e～f，直到所有数据都处理完成

* 优化点1: 由于排序过程有多个阶段，读入2G的数据为阶段Load，排序2G的数据为阶段Sort，输出2G的数据为阶段Output，可以使用流水线并行处理；并行处理带来的问题有两个：需要引入多线程和可用内存需要被多个阶段均分（每个阶段667M内存）; 此外，Sort阶段可以使用快排，使用O\(nlogn\)的算法且是原地排序算法 --&gt; 可见，这个针对将大文件处理生成多个小文件
* 优化点2: 按照2G内存均分，需要至少500个归并段，如果能减少归并段的个数，也可以优化效率，可以使用[置换-选择算法](http://c.biancheng.net/view/3454.html)，让最终生成的归并段比500小；该算法的核心是使用小顶堆
* 优化点3: 得到多个排序好的归并段后，进行归并操作，但是几百个小文件，找最小值，每次都需要扫描500个文件，时间复杂度是 O\(n\), 是否有 O\(logn\) 的实现？有，可以借助最小堆，每次可以从堆中取堆顶元素输出；

伪代码实现

```java
// 外部排序的伪代码 ？
// 问题1 文件中存储的是什么数据？假如是按行存储的一个个数字
int segmentCount = 0;
void sort() {
    // 分治大文件，输出多个有序的小文件
   toMultiSortedSegment();
    
    // 多路归并排序小文件
    File finalSortedFile = new File("...");
    multiSegmentMerge(finalSortedFile);
}
void toMultiSortedSegment() {
    File bigFile;
    InputStream in = new FileInputStream(bigFile);
    BufferedReader br = new BufferedReader(in, 1024); // cache buffer
    
    int segmentSize = 2 * 1024 * 1024 * 1024 / 32; // 2GB
    int[] segment = new int[segmentSize];
    String line; int i = 0;
    while ((line = br.readLine(buffer)) != null)) {
        segment[i] = Integer.valueOf(line);
        i++;
        if (i >= segmentSize) {
            quickSort(segment);
            output(segment);
            i=0; // reset
        }
    }
    br.close();
}
// 内部排序
void quickSort(int[] segment) {
    // 自己实现
    // 或者使用系统自带的函数
    Arrays.sort(segment);
}
// 输出文件
void output(int[] segment) {
    segmentCount++;
    File file; // out.[1...n]
    FileOutputStream fos = new FileOutputStream(file);
    OutputStreamWriter osw = new OutputStreamWriter(fos);
    BufferedWriter bw = new BufferedWriter(osw, 2048);
    for (int i = 0; i < segment.size; i++) {
        bw.write(segment[i]);
        bw.newLine();
    }
    bw.flush();
    bw.close();
}

void multiSegmentMerge(File f) {
    int loadSizePerSegment = ?; // 1.5G / (segmentCount)
    int outputBufferSize = ?;// 500M
    int[] outputBuffer = new int[outputBufferSize];
    
    PriorityQueue<Pair> heap;
    int[] minPerSegment = loadMultiSegmentOfFirst();
    heapify(heap, minPerSegment);  // 小顶堆
    
    // load multi segment
    while (hasData()) {
        int currMinData = heap.poll();
        outputBuffer[outPos] = currMinData();
        outPos++;
        // 触发追加到最终的排序大文件     
        if (ouPos == outputBufferSize) {
            appendTo(outputBuffer);
        }
        
        int nextMinData = getMinDataFromSegments();
        heap.add(nextMinData);
    }
}
```

Note: \[1\]: [什么是外部排序算法？](http://c.biancheng.net/view/3452.html)\[2\]: 外部[排序图解](https://blog.csdn.net/ailunlee/article/details/84548950)

