# 频繁 GC 导致的 Java 服务不响应

### 现象

项目中的某个服务进程是正常的，但是访问这个服务一直返回 Socket Read timeout，最终导致其他服务也受影响。打开服务的日志发现最下面有：

```java
Exception: java.lang.OutOfMemoryError thrown from the UncaughtExceptionHandler in thread "SimplePauseDetectorThread_0" 
 
Exception: java.lang.OutOfMemoryError thrown from the UncaughtExceptionHandler in thread "metrics-logger-reporter-1-thread-1" 
 
Exception: java.lang.OutOfMemoryError thrown from the UncaughtExceptionHandler in thread "RequestHouseKeepingService" 
 
Exception: java.lang.OutOfMemoryError thrown from the UncaughtExceptionHandler in thread "http-nio-8000-ClientPoller" 
```

通过日志大致猜测是内存方面的问题，可能是堆内存被用完了，一直不停的做 FGC，触发 Stop the world，同时由于内存不足，其他的线程无法分配到内存，也就无法进行任何工作。

### 问题排查与分析

一般碰到问题，如果有监控的情况下，最好先去看一下监控指标，比如 Java 进程所在的那台机器的整体情况，JVM 的内存情况、Java 进程的 CPU 使用情况等，没有可视化监控也没关系，自己去搜集信息分析就可以了，有了可视化监控会更加方便，一般容器实例数量比较多时还是要借助可视化监控的。

首先去看一下 Java 进程异常的那台机器的整体情况，我们的服务是部署在 Kubernetes + Docker 中的

```bash
# 进入 Docker 容器执行 top
# 一般 jps、ps -ef | grep java、top
$ top
top - 10:26:07 up 1 day,  4:13,  0 users,  load average: 1.34, 1.37, 1.43
Tasks:  37 total,   1 running,  34 sleeping,   2 stopped,   0 zombie
%Cpu(s):  8.4 us,  0.6 sy,  0.0 ni, 90.8 id,  0.0 wa,  0.0 hi,  0.1 si,  0.0 st
KiB Mem : 32946036 total, 11481888 free, 12608160 used,  8855988 buff/cache
KiB Swap:        0 total,        0 free,        0 used. 19854660 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND                                                                                                                                              
    1 root      20   0 13.716g 4.563g  16320 S 100.0 14.5   1297:10 java                                                                                                                                                 
 1741 root      20   0   42812   3460   2960 S   0.3  0.0   0:00.03 top                                                                                                                                                  
 1297 root      20   0    4296    728    640 S   0.0  0.0   0:00.00 sh                                                                                                                                                   
 1302 root      20   0    4296    124      0 S   0.0  0.0   0:00.00 sh                                                                                                                                                   
 1303 root      20   0   20696   2216   2020 S   0.0  0.0   0:00.00 script                                                                                                                                               
 1304 root      20   0    4296    764    680 S   0.0  0.0   0:00.00 sh                                                                                                                                                   
 1305 root      20   0   20144   3744   2992 S   0.0  0.0   0:00.00 bash    
 ...
```

可以看到 java 进程 CPU 占到 100%，可能是某个 Java 的线程比较活跃，而且平均负载并不高，问题不大。接着，查看 Java 进程中的线程使用内存和 CPU 的情况

```bash
# 查看进程中每个线程的情况
$ top -Hp 1
Threads:  89 total,   1 running,  88 sleeping,   0 stopped,   0 zombie
%Cpu(s):  8.6 us,  0.6 sy,  0.0 ni, 90.7 id,  0.1 wa,  0.0 hi,  0.1 si,  0.0 st
KiB Mem : 32946036 total, 11551216 free, 12556776 used,  8838044 buff/cache
KiB Swap:        0 total,        0 free,        0 used. 19906292 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S %CPU %MEM     TIME+ COMMAND                                                                                                                                               
   12 root      20   0 13.731g 4.562g  16320 R 99.7 14.5 689:33.70 java                                                                                                                                                  
    1 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.01 java                                                                                                                                                  
    6 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:13.83 java                                                                                                                                                  
    7 root      20   0 13.731g 4.562g  16320 S  0.0 14.5 473:08.60 java                                                                                                                                                  
    8 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:01.26 java                                                                                                                                                  
    9 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   1:54.50 java                                                                                                                                                  
   10 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:01.65 java                                                                                                                                                  
   11 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   4:46.74 java                                                                                                                                                  
   13 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.36 java                                                                                                                                                  
   14 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.46 java                                                                                                                                                  
   15 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.00 java                                                                                                                                                  
   16 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.00 java                                                                                                                                                  
   17 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   1:28.35 java                                                                                                                                                  
   18 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:17.89 java                                                                                                                                                  
   19 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:03.46 java                                                                                                                                                  
   20 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:39.76 java                                                                                                                                                  
   32 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.00 java                                                                                                                                                  
   33 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:02.84 java                                                                                                                                                  
   34 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:03.11 java                                                                                                                                                  
   35 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:03.28 java                                                                                                                                                  
   36 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.09 java                                                                                                                                                  
   37 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.96 java                                                                                                                                                  
   41 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.05 java                                                                                                                                                  
   42 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:01.51 java                                                                                                                                                  
   43 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:03.23 java                                                                                                                                                  
   44 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.34 java                                                                                                                                                  
   45 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.66 java                                                                                                                                                  
   47 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:01.55 java                                                                                                                                                  
   48 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.00 java                                                                                                                                                  
   49 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.14 java                                                                                                                                                  
   50 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.69 java                                                                                                                                                  
   51 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.38 java                                                                                                                                                  
   52 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.62 java                                                                                                                                                  
   53 root      20   0 13.731g 4.562g  16320 S  0.0 14.5   0:00.97 java
```

可以看到 PID 是 12 的线程有嫌疑，以及 PID 是 7 的线程也有嫌疑，因为它的 TIME+ 列的值比较大，说明占用的 CPU 时间很长，`473:08.60` 表示多少呢？473分钟8秒600毫秒 \(从右到左分别是百分之一秒，十分之一秒，秒，十秒，分钟\)；继续来看这两个线程是什么？

```bash
# 把 PID 换成 16 进制
$ printf "%x\n" 12
c
$ printf "%x\n" 7
7

# 查看线程信息
$ jstack 1 | grep -20 0xc
"Reference Handler" #2 daemon prio=10 os_prio=0 tid=0x00007fc32036bb60 nid=0xd waiting for monitor entry [0x00007fc3101dd000] 
   java.lang.Thread.State: BLOCKED (on object monitor) 
	at java.lang.ref.Reference.tryHandlePending(Reference.java:178) 
	- waiting to lock <0x00000006c00b8f40> (a java.lang.ref.Reference$Lock) 
	at java.lang.ref.Reference$ReferenceHandler.run(Reference.java:153) 
	
"VM Thread" os_prio=0 tid=0x00007fc3203629f0 nid=0xc runnable 
 
"Gang worker#0 (Parallel GC Threads)" os_prio=0 tid=0x00007fc3200208a0 nid=0x7 runnable 
 
"G1 Main Concurrent Mark GC Thread" os_prio=0 tid=0x00007fc320049ab0 nid=0xa runnable 
 
"Gang worker#0 (G1 Parallel Marking Threads)" os_prio=0 tid=0x00007fc32004af50 nid=0xb runnable 
 
"G1 Concurrent Refinement Thread#0" os_prio=0 tid=0x00007fc3200239d0 nid=0x9 runnable 
```

看 nid=0xc 的那里是 VM Thread, "VM Thread" 是 JVM 自身启动的一个线程, 它主要用来协调其它线程达到安全点\(Safepoint\). 需要达到安全点的线程主要有: Stop the world 的 GC, 做 thread dump, 线程挂起以及偏向锁的撤销. 到这里我们基本可以猜测是和 GC 是有关的，我们再看 nid=0x7 Parallel GC Threads 和 nid=0xb G1 Parallel Marking Threads

接下来验证是 GC 问题的想法，查看 gc 情况

```bash
# 执行命令查看 GC
$ jstat -gc 1 5000
S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT   
 0.0    0.0    0.0    0.0   16384.0    0.0    4177920.0  4173598.4  113152.0 107152.9 13312.0 12162.1  10192  726.350 4863  68977.295 69703.645
 0.0    0.0    0.0    0.0   16384.0    0.0    4177920.0  4173598.3  113152.0 107152.9 13312.0 12162.1  10192  726.350 4864  68991.644 69717.994
 0.0    0.0    0.0    0.0   16384.0    0.0    4177920.0  4173598.3  113152.0 107152.9 13312.0 12162.1  10192  726.350 4864  68991.644 69717.994
 0.0    0.0    0.0    0.0   16384.0    0.0    4177920.0  4173598.3  113152.0 107152.9 13312.0 12162.1  10192  726.350 4864  68991.644 69717.994

```

从执行结果可以看出 FGC 做了 4864 次，两次 FGC 之间隔了 14s，FGC 总共花了 68991s，也就是平均每次 FGC 花了 14s 左右，说明一次 FGC 做完，紧接着就继续做 FGC

```bash
# 继续观测的结果
$ jstat -gcutil 1 1000 10
  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT   
  0.00   0.00   0.00  99.90  94.70  91.36  10232  726.649  4898 69478.825 70205.474
  0.00   0.00   0.00  99.90  94.70  91.36  10232  726.649  4898 69478.825 70205.474
  0.00   0.00   0.00  99.90  94.70  91.36  10232  726.649  4898 69478.825 70205.474
  0.00   0.00   0.00  99.90  94.70  91.36  10232  726.649  4898 69478.825 70205.474
  0.00   0.00   0.00  99.90  94.70  91.36  10232  726.649  4898 69478.825 70205.474
  0.00   0.00   0.00  99.90  94.70  91.36  10232  726.649  4898 69478.825 70205.474
  0.00   0.00   0.00  99.90  94.70  91.36  10232  726.649  4898 69478.825 70205.474
  0.00   0.00   0.00  99.90  94.70  91.36  10232  726.649  4898 69478.825 70205.474
  0.00   0.00   0.00  99.90  94.70  91.36  10232  726.649  4898 69478.825 70205.474
  0.00   0.00   0.00  99.90  94.70  91.36  10232  726.649  4898 69478.825 70205.474
  ... 
  0.00   0.00   0.00  99.90  94.70  91.36  10244  726.733  4909 69633.181 70359.913
  0.00   0.00   0.00  99.90  94.70  91.36  10244  726.733  4909 69633.181 70359.913
  0.00   0.00   0.00  99.90  94.70  91.36  10244  726.733  4909 69633.181 70359.913
  0.00   0.00   0.00  99.90  94.70  91.36  10244  726.733  4909 69633.181 70359.913
  0.00   0.00   0.00  99.90  94.70  91.36  10244  726.733  4909 69633.181 70359.913
  0.00   0.00   0.00  99.90  94.70  91.36  10244  726.733  4909 69633.181 70359.913
  0.00   0.00   0.00  99.90  94.70  91.36  10244  726.733  4909 69633.181 70359.913
  0.00   0.00   0.00  99.90  94.70  91.36  10244  726.733  4909 69633.181 70359.913
  0.00   0.00   0.00  99.90  94.70  91.36  10244  726.733  4909 69633.181 70359.913
  0.00   0.00   0.00  99.90  94.70  91.36  10244  726.733  4909 69633.181 70359.913
```

也就是 FGC 远远超过 YGC，而且 FGC 的耗时再增加。极有可能是有大对象需要清除，或者内存泄漏等。我们还需要定位到具体占用内存的对象是哪个

```bash
# 查询 JVM 的堆内存使用情况，比如实例数以及占用内存大小
$ jmap -histo 1 > jmap_1_histo.log

num     #instances         #bytes  class name
----------------------------------------------
   1:       8893206      861105800  [C
   2:      23862699      572704776  java.util.ArrayList
   3:      10177038      529487176  [Ljava.lang.Object;
   4:      11898656      475946240  com.itextpdf.styledxmlparser.jsoup.nodes.TextNode
   5:      11898656      380756992  com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupTextNode
   6:       6834912      328075776  com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode
   7:       6834912      273396480  com.itextpdf.styledxmlparser.jsoup.nodes.Element
   8:       8890099      213362376  java.lang.String
   9:       6834917      109358672  com.itextpdf.styledxmlparser.jsoup.nodes.Attributes
  10:       6834913      109358608  com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupAttributes
  11:       1327091       42466912  java.util.HashMap$Node
  12:       1053255       42130200  java.util.LinkedHashMap$Entry
  13:        523312       29305472  java.util.LinkedHashMap
  14:        643867       28031016  [Ljava.util.HashMap$Node;
  15:       1012831       24307944  com.itextpdf.styledxmlparser.jsoup.nodes.Attribute
  16:        252087       10083480  java.math.BigDecimal
  17:        112203        7358200  [I
  18:         10996        7182696  [B
  19:        149413        7171824  java.util.HashMap
  20:        102200        4088000  java.math.BigInteger
  21:        111474        3567168  java.util.concurrent.ConcurrentHashMap$Node
  22:         35890        3158320  java.lang.reflect.Method
  23:        111948        2686752  com.itextpdf.layout.property.UnitValue
  24:        101622        2438928  java.lang.Long
  25:         17662        1965152  java.lang.Class
  26:         36538        1753824  org.aspectj.weaver.reflect.ShadowMatchImpl
  27:         36538        1169216  org.aspectj.weaver.patterns.ExposedState
  28:           418        1071008  [Ljava.util.concurrent.ConcurrentHashMap$Node;
  29:         25644        1025760  com.itextpdf.layout.element.Text
  
## 可以看出占用内存较大的对象有哪些 

# 查询 JVM 的堆信息
$ jmap -heap 1
Attaching to process ID 1, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.212-b04

using thread-local object allocation.
Garbage-First (G1) GC with 1 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 4294967296 (4096.0MB)
   NewSize                  = 1363144 (1.2999954223632812MB)
   MaxNewSize               = 2566914048 (2448.0MB)
   OldSize                  = 5452592 (5.1999969482421875MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 16777216 (16.0MB)

Heap Usage:
G1 Heap:
   regions  = 256
   capacity = 4294967296 (4096.0MB)
   used     = 4275729784 (4077.653678894043MB)
   free     = 19237512 (18.34632110595703MB)
   99.5520917698741% used
G1 Young Generation:
Eden Space:
   regions  = 0
   capacity = 16777216 (16.0MB)
   used     = 0 (0.0MB)
   free     = 16777216 (16.0MB)
   0.0% used
Survivor Space:
   regions  = 0
   capacity = 0 (0.0MB)
   used     = 0 (0.0MB)
   free     = 0 (0.0MB)
   0.0% used
G1 Old Generation:
   regions  = 25
   capacity = 4278190080 (4080.0MB)
   used     = 4275729784 (4077.653678894043MB)
   free     = 2460296 (2.3463211059570312MB)
   99.94249212975596% used

39891 interned Strings occupying 3978792 bytes.
# dump 堆内存
$ jmap -dump:format=b,file=test.hprof 1

JNI global references: 2230 
 
Heap 
 garbage-first heap   total 4194304K, used 4173596K [0x00000006c0000000, 0x00000006c1000800, 0x00000007c0000000) 
  region size 16384K, 0 young (0K), 0 survivors (0K) 
 Metaspace       used 107153K, capacity 112762K, committed 113152K, reserved 1148928K 
  class space    used 12162K, capacity 13171K, committed 13312K, reserved 1048576K 
```

通过堆信息我们可以分析出哪些对象占用了大量内存，进而关联到相关的代码，然后转到对应的代码去分析可能出现问题的地方。

通过 jvisualvm 来分析 dump 文件

![&#x603B;&#x4F53;&#x89C6;&#x56FE;](../../.gitbook/assets/image%20%28105%29.png)

![&#x7C7B;&#x548C;&#x5B9E;&#x4F8B;&#x6570;&#x7684;&#x89C6;&#x56FE;](../../.gitbook/assets/image%20%28113%29.png)

可以看到差不多 60% 左右的内存都被 ArrayList, JsoupTextNode, TextNode, JsoupAttributs 等占用了，我们可以根据这些信息进一步定位到代码，然后去分析代码做了什么事情。

### 总结

综合以上获得的信息，可以看出来垃圾收集器使用的 G1，配置的内存大小是 4G，被使用的对空间很接近 4G，所以 JVM 的堆中已经没有多余的内存分配给其他线程了，导致 JVM 的 0xc 和 0x7 两个线程在努力做 GC 的工作，而且这些被回收的对象都是大对象，没有办法直接释放，因为还在用，最终导致一直做 FGC，但是内存又没办法释放；此外，外部的请求到达服务时，服务由于没有内存可供分配，导致接口请求无法及时响应，外部服务调用后会报 Read Timeout。

从分析这个问题，也发现了自己在某些知识点上的不足，接下来要啃一下：

1. G1 垃圾器不是很熟悉，如何应对这种场景下的问题，确实需要内存做计算，怎么合理选择垃圾收集器和配置参数
2. 一开始解决这类问题有点蒙，还需要多积累经验，找讨论，边收集信息，边分析，边猜测，边决策下一步该怎么定位问题

### 扩展

* 查看 JVM 的启动参数等

```bash
$ jinfo -flags 1
Attaching to process ID 1, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.212-b04
Non-default VM flags: -XX:+AlwaysPreTouch -XX:CICompilerCount=2 -XX:ConcGCThreads=1 -XX:G1HeapRegionSize=16777216 -XX:G1ReservePercent=25 -XX:GCLogFileSize=31457280 -XX:InitialHeapSize=4294967296 -XX:InitiatingHeapOccupancyPercent=30 -XX:MarkStackSize=4194304 -XX:MaxDirectMemorySize=4026531840 -XX:MaxHeapSize=4294967296 -XX:MaxNewSize=2566914048 -XX:MinHeapDeltaBytes=16777216 -XX:NumberOfGCLogFiles=5 -XX:-OmitStackTraceInFastThrow -XX:+PrintAdaptiveSizePolicy -XX:+PrintGC -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:SoftRefLRUPolicyMSPerMB=0 -XX:SurvivorRatio=8 -XX:-UseBiasedLocking -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseG1GC -XX:+UseGCLogFileRotation -XX:-UseLargePages 
Command line:  -Xmx4g -Xms4g -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercent=25 -XX:InitiatingHeapOccupancyPercent=30 -XX:SoftRefLRUPolicyMSPerMB=0 -XX:SurvivorRatio=8 -verbose:gc -Xloggc:/dev/shm/mq_gc_%p.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime -XX:+PrintAdaptiveSizePolicy -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=30m -XX:-OmitStackTraceInFastThrow -XX:+AlwaysPreTouch -XX:MaxDirectMemorySize=4026531840 -XX:-UseLargePages -XX:-UseBiasedLocking

$ jinfo -sysprops 1
Attaching to process ID 1, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.212-b04
java.runtime.name = OpenJDK Runtime Environment
java.vm.version = 25.212-b04
sun.boot.library.path = /usr/local/openjdk-8/jre/lib/amd64
java.protocol.handler.pkgs = org.springframework.boot.loader
rocketmq.remoting.version = 355
java.vendor.url = http://java.oracle.com/
java.vm.vendor = Oracle Corporation
path.separator = :
file.encoding.pkg = sun.io
java.vm.name = OpenJDK 64-Bit Server VM
rocketmq.client.logUseSlf4j = true
sun.os.patch.level = unknown
sun.java.launcher = SUN_STANDARD
user.dir = /
java.vm.specification.name = Java Virtual Machine Specification
PID = 1
java.runtime.version = 1.8.0_212-b04
java.awt.graphicsenv = sun.awt.X11GraphicsEnvironment
os.arch = amd64
java.endorsed.dirs = /usr/local/openjdk-8/jre/lib/endorsed
line.separator = 

java.io.tmpdir = /tmp
java.vm.specification.vendor = Oracle Corporation
os.name = Linux
sun.jnu.encoding = UTF-8
java.library.path = /usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
spring.beaninfo.ignore = true
sun.nio.ch.bugLevel = 
java.specification.name = Java Platform API Specification
java.class.version = 52.0
sun.management.compiler = HotSpot 64-Bit Tiered Compilers
os.version = 4.4.0-62-generic
user.home = /root
user.timezone = Etc/UTC
catalina.useNaming = false
java.awt.printerjob = sun.print.PSPrinterJob
file.encoding = UTF-8
@appId = ui
java.specification.version = 1.8
catalina.home = /tmp/tomcat.7077067909867259166.8000
user.name = root
java.class.path = matrix-ui.jar
java.vm.specification.version = 1.8
sun.arch.data.model = 64
sun.java.command = test.jar --spring.profiles.active=test
java.home = /usr/local/openjdk-8/jre
user.language = en
java.specification.vendor = Oracle Corporation
awt.toolkit = sun.awt.X11.XToolkit
java.vm.info = mixed mode
java.version = 1.8.0_212
java.ext.dirs = /usr/local/openjdk-8/jre/lib/ext:/usr/java/packages/lib/ext
sun.boot.class.path = /usr/local/openjdk-8/jre/lib/resources.jar:/usr/local/openjdk-8/jre/lib/rt.jar:/usr/local/openjdk-8/jre/lib/sunrsasign.jar:/usr/local/openjdk-8/jre/lib/jsse.jar:/usr/local/openjdk-8/jre/lib/jce.jar:/usr/local/openjdk-8/jre/lib/charsets.jar:/usr/local/openjdk-8/jre/lib/jfr.jar:/usr/local/openjdk-8/jre/classes
java.awt.headless = true
java.vendor = Oracle Corporation
catalina.base = /tmp/tomcat.7077067909867259166.8000
com.zaxxer.hikari.pool_number = 2
file.separator = /
sdk.project.version = 4.4.6
java.vendor.url.bug = http://bugreport.sun.com/bugreport/
sun.io.unicode.encoding = UnicodeLittle
sun.cpu.endian = little
sun.cpu.isalist = 
```

* Java 相关命令的参数含义
* 堆内存统计

```bash
$ jstat -gccapacity 1
NGCMN    NGCMX     NGC     S0C   S1C       EC      OGCMN      OGCMX       OGC         OC       MCMN     MCMX      MC     CCSMN    CCSMX     CCSC    YGC    FGC 
     0.0 4194304.0  16384.0    0.0    0.0  16384.0        0.0  4194304.0  4177920.0  4177920.0      0.0 1130496.0  93440.0      0.0 1048576.0  11520.0    268   105

```

* 在排查问题时出现 Can't attach to the process: ptrace\(PTRACE\_ATTACH, ..\) failed for 1: Operation not permitted 的错误

我们使用了 Docker，jmap 等命令是在 Docker 容器内执行的，这是由于 Docker 自 1.10 版本开始加入的安全特性，类似于 jmap 这些 JDK 工具依赖于 Linux 的 PTRACE\_ATTACH，而是 Docker 自 1.10 在默认的 seccomp 配置文件中禁用了 ptrace。解决办法是：

```text
# 1. run 的时候加上参数 SYS_PTRACE
docker run --cap-add=SYS_PTRACE ...

# 2. docker-compose.yml 
version: '2'

services:
  mysql:
   ...
  api:
   ...
   cap_add:

    - SYS_PTRACE
    
# 3. kubernetes 的 pod 模版上增加对应的 docker 启动参数
```

