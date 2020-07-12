---
description: 关于排序算法
---

# 排序算法

### 内部排序

todo

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

![](http://note.youdao.com/yws/res/11033/1818FCF740184F64B93628E01DE9A529)

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



