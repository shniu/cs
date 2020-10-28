# 1- 通信

大纲

1. 通信之前要先协商好协议，看看协议的一般设计
2. 定义协议要考虑诸多因素，如编码/解码、通信效率等
3. 常见的通信模型
4. 通信的客户端网络模型
5. 通信的服务端网络模型
6. 案例解析：MySQL 的通信协议设计
7. 案例解析：Redis 的通信协议设计
8. 案例解析：Spring Cloud Feign 的设计
9. 案例解析：RPC 框架的设计，e.g. Dubbo / gRPC / [rpcx](https://doc.rpcx.io/)\([rpcx协议详解](https://doc.rpcx.io/part5/protocol.html)\), [参考1](https://developer.51cto.com/art/201906/597963.htm)， [参考2](https://colobu.com/2020/01/21/benchmark-2019-spring-of-popular-rpc-frameworks/)
10. 案例解析：MQ 设计, e.g. RocketMQ / BigQueue / kibill-commons/queue / Kafka ...
11. 涉及到哪些计算机底层知识

分布式系统之间的通信是基于计算机网络进行的，也就是说我们自定义的应用协议是跑在 TCP/IP 协议之上的，利用计算机网络协议来设计自己的应用层协议。

TCP/IP 协议

{% hint style="info" %}
不同的协议层对数据包有不同的称谓，在传输层叫做段\(segment\)，在网络层叫做数据报\(datagram\)，在链路层叫做帧\(frame\)。
{% endhint %}

