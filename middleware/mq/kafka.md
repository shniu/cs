# Kafka



### Reference

* [日志：每个软件工程师都应该知道的有关实时数据的统一概念](https://www.kancloud.cn/kancloud/log-real-time-datas-unifying/58708) - 经典必读， [英文版](https://engineering.linkedin.com/distributed-systems/log-what-every-software-engineer-should-know-about-real-time-datas-unifying)

日志可能是一种最简单的不能再简单的存储抽象，只能追加、按照时间完全有序（`totally-ordered`）的记录序列。

日志解决了两个核心的问题：变更动作的顺序（排序）和数据分发（通过日志把变更序列传输到其他服务、Slave or Replica。

状态机复制原理：**如果两个相同的、确定性的进程从同一状态开始，并且以相同的顺序获得相同的输入，那么这两个进程将会生成相同的输出，并且结束在相同的状态。**

* \*\*\*\*[**The Log: an epic software engineering article**](http://bryanpendleton.blogspot.com/2014/01/the-log-epic-software-engineering.html)\*\*\*\*
* \*\*\*\*[**学习笔记：The Log**](https://www.cnblogs.com/foreach-break/p/notes_about_distributed_system_and_The_log.html)\*\*\*\*

