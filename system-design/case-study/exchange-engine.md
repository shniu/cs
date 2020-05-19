---
description: 关于交易引擎
---

# 交易引擎

证券交易系统通过买卖双方各自的报价，按照价格优先、时间优先的顺序，对买卖双方进行撮合，实现每秒成千上万的交易量，可以为市场提供高度的流动性和价格发现机制。

### 撮合引擎

撮合引擎是所有撮合交易系统的核心组件，不管是股票交易系统——包括现货交易、期货交易、期权交易等，还是数字货币交易系统——包括币币交易、合约交易、杠杆交易等，以及各种不同的贵金属交易系统、大宗商品交易系统等，虽然各种不同交易系统的交易标的不同，但只要都是采用撮合交易模式，都离不开撮合引擎。

撮合引擎的设计目标是通用性，设计要遵循 SRP 原则（也就是说保证撮合引擎的通用，应当采取 SRP，撮合引擎应该只负责撮合订单）



术语

1. [Order Book](https://www.investopedia.com/terms/o/order-book.asp) \(可以理解为交易委托账本\)

参考

* [证券交易系统设计与开发](https://www.liaoxuefeng.com/article/1185272483766752)
* [高性能交易系统设计原理](https://www.liaoxuefeng.com/article/1341133393231906)
* [撮合系统设计](https://mp.weixin.qq.com/s/sU7C2Bs-tqezdMHPD8xSjw)
* [撮合引擎系统设计升级版](https://mp.weixin.qq.com/s/PFIQYbVoSdtkJXZItL_rsw)
* [撮合引擎系列 - 开篇](https://mp.weixin.qq.com/s/y_gcu-pIZFOMZ4QDz9P2Pg)
* [撮合引擎系列 - MVP](https://mp.weixin.qq.com/s/D_p-eMSwx-oXlSLTWwgumw)
* [撮合引擎系列 - 核心数据结构](https://mp.weixin.qq.com/s/MxiGXK7WjbsuK6Le-eNTDg)
* [撮合引擎系列 - 接口](https://mp.weixin.qq.com/s/LVgN1nCVZPYWWEuLKF31fw)
* [撮合引擎系列 - 核心流程设计](https://mp.weixin.qq.com/s/BqLuYkK1WpVdcN4vahGe_g)
* [撮合引擎系列 - 部分代码实现](https://mp.weixin.qq.com/s/09tjjnAYBwVsrLeqMqVdsA)
* [撮合引擎系列 - 缓存和MQ](https://mp.weixin.qq.com/s/-6agdLkVzQwez5IW2jGWVA)
* [撮合引擎系列 - 多种订单类型的处理流程](https://mp.weixin.qq.com/s/vB8TYK12hWpXNYjcxCH5YQ)
* [数字货币交易所开源项目1](https://gitee.com/cexchange/CoinExchange)
* [Cryptocurrency Exchange Architecture with Akka Microservices - Part 1](https://www.linkedin.com/pulse/cryptocurrency-exchange-architecture-akka-part-1-jim-yang?articleId=6453307976605323264#comments-6453307976605323264&trk=public_profile_article_view)
* [Cryptocurrency Exchange Architecture with Akka Microservices - Part 2](https://www.linkedin.com/pulse/cryptocurrency-exchange-architecture-akka-part-2-jim-yang?articleId=6453460455888289792#comments-6453460455888289792&trk=public_profile_article_view)
* [Cryptocurrency Exchange Architecture with Akka Microservices - Part 3](https://www.linkedin.com/pulse/cryptocurrency-exchange-architecture-akka-part-3-jim-yang-1c?articleId=6456366788652392448#comments-6456366788652392448&trk=public_profile_article_view)



