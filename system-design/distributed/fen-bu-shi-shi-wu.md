# 分布式事务

本地事务主要限制在单个会话内，不涉及多个数据库资源。但是在基于 SOA（Service-Oriented Architecture，面向服务架构）的分布式应用环境下，越来越多的应用要求对多个数据库资源，多个服务的访问都能纳入到同一个事务当中，分布式事务应运而生。

本质上来说，分布式事务就是为了保证不同数据库的数据一致性。

### TCC

TCC 事务，即 Try-Confirm-Cancel，它由 3 部分组成：

* Try 阶段，这个阶段是一个初步的阶段，应用服务尝试执行数据库操作，TCC 的 try 阶段会将本次执行的数据库事务提交，所以需要根据实际的业务场景来决定哪些操作在 try 阶段执行
* Confirm 阶段，全局的事务管理器会收到 try 阶段的反馈，如果最终决定提交，commit 阶段会去执行应用服务的 confirm 操作，这部分执行的提交操作由可能会失败，这个需要做事务补偿
* Cancel 阶段，当TCC事务管理器决定 rollback 全局事务时，就会逐个执行 **Try** 操作指定的 **Cancel** 操作，将 **Try** 操作已完成的事项全部撤回。

### SAGAS

* [sagas paper](https://www.cs.cornell.edu/andru/cs711/2002fa/reading/sagas.pdf)
* [Microservice Pattern: saga](https://microservices.io/patterns/data/saga.html)
* [https://dimosr.github.io/saga-transactions/](https://dimosr.github.io/saga-transactions/)
* [https://blog.couchbase.com/saga-pattern-implement-business-transactions-using-microservices-part/](https://blog.couchbase.com/saga-pattern-implement-business-transactions-using-microservices-part/)
* [https://blog.couchbase.com/saga-pattern-implement-business-transactions-using-microservices-part-2/](https://blog.couchbase.com/saga-pattern-implement-business-transactions-using-microservices-part-2/)

### Reference

* [分布式事务，这一篇就够了](https://xiaomi-info.github.io/2020/01/02/distributed-transaction/) - 小米信息部
* [Seata 分布式事务开源详解](https://www.sofastack.tech/blog/seata-distributed-transaction-deep-dive/)
* [蚂蚁金服的 dts](https://tech.antfin.com/docs/2/46885)
* [https://www.infoq.cn/article/g1avp9fua6cdoyralv4r](https://www.infoq.cn/article/g1avp9fua6cdoyralv4r)

#### 开源实现

* [https://github.com/changmingxie/tcc-transaction](https://github.com/changmingxie/tcc-transaction)
* 
