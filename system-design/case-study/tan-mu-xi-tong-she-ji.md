---
description: 如何设计弹幕系统
---

# 弹幕系统设计

支撑百万连接的弹幕系统特点

1. 实时性高：发送出去的弹幕，延时要低，毫秒级差距
2. 并发量大：一个人发，万人以上能看到
3. 数据一直性的要求并不高：在弹幕非常多时，真的丢了几条数据也没关系



技术方案 一

Ajax + 弹幕后台服务集群 + Redis 缓存集群

技术方案二

WebSocket + 弹幕后台服务集群（Netty 实现网络层） + zk + Redis 缓存 + 弹幕数据库

TODO



