# Topic - Graph



* 针对图的存储以及基本表示，看书+Google

  
了解图的基本特性和在Java中的基本实现 [https://www.baeldung.com/java-graphs](https://www.baeldung.com/java-graphs)，Graphs in Java，Java 里的实现有 JGraphT，Google Guava，Apache Commons，JUNG  
图的表示是后续所有操作的前提：邻接矩阵（Adjacency Matrix）和邻接表（Adjacency List），看两张图  
[https://www.geeksforgeeks.org/graph-data-structure-and-algorithms/](https://www.geeksforgeeks.org/graph-data-structure-and-algorithms/)  对 Graph 做了一个去方位的总结  


1. [https://www.geeksforgeeks.org/graph-and-its-representations/](https://www.geeksforgeeks.org/graph-and-its-representations/)  主要介绍了图和它的表示，主流的方式有邻接矩阵和邻接表。邻接矩阵使用一个二维数组，好处是查询、删除、更新等操作的复杂度是O\(1\)，缺点是需要占用过多的空间，即使是一个稀疏矩阵，也需要占用 O\(V^2\) 的空间; 邻接表中的每个顶点存在数组中，且对应一个链表，存储与其相连的其他顶点。
2. 
* 针对bfs dfs 双向bfs 的一般实现

  
数据结构与算法之美-深度和广度优先搜索，简要介绍了BFS和DFS，以及他们的一般实现  
相关的题目  
高级搜索算法都有哪些，看书+Google应用场景怎么用  
自己在工作中常用的：解决业务问题的利益分配（有向权重图），有依赖关系的任务调度问题（有向无环图）把spark和airflow  
todo[https://www.cnblogs.com/v-July-v/archive/2011/02/14/1983678.html](https://www.cnblogs.com/v-July-v/archive/2011/02/14/1983678.html)[https://d3gt.com/unit.html?graphic-sequence](https://d3gt.com/unit.html?graphic-sequence)  
  
最短路径问题，如编辑距离可以考虑由浅入深引出高级算法，一步步优化和演变，如为了解决最短路径问题，bfs 到最短路径算法[https://www.baeldung.com/java-dijkstra](https://www.baeldung.com/java-dijkstra)  [https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-in-java-using-priorityqueue/](https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-in-java-using-priorityqueue/)[https://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html](https://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html)[https://time.geekbang.org/column/article/76468](https://time.geekbang.org/column/article/76468)[https://www.baeldung.com/java-dijkstra](https://www.baeldung.com/java-dijkstra)[https://github.com/billryan/algorithm-exercise/blob/master/zh-hans/graph/topological\_sorting.md](https://github.com/billryan/algorithm-exercise/blob/master/zh-hans/graph/topological_sorting.md)

