---
description: 关于交易引擎
---

# 交易系统

证券交易系统是金融市场上能够提供的最有流动性，效率最高的交易场所。和传统的商品交易不同的是，证券交易系统提供的买卖标的物是标准的数字化资产，如USD、股票、BTC等，它们的特点是数字计价，**可分割买卖**。

交易系统通过买卖双方各自的报价，按照价格优先、时间优先的顺序，对买卖双方进行撮合，实现每秒成千上万的交易量，可以为市场提供高度的流动性和价格发现机制。

### 核心服务

一个交易系统包含的最核心的几个组件：

#### 用户服务 \(user service\)

基本的用户鉴权、KYC、安全设置等

#### 账户服务 \(account service\)

用户的数字资产账户的管理（应该指在交易所的虚拟资产账户），比如充值、提币、冻结、转帐、解冻等

#### 订单服务 \(order service\)

Place an Order，用户可以下各种类型各种交易对的委托单（Order），委托单管理

#### 定序服务 \(sequence service\)

交易系统的所有订单是一个有序队列。不同的用户在同一时刻下单，也必须由定序系统确定先后顺序。

#### 撮合引擎 \(match engine service\)

撮合引擎是所有撮合交易系统的核心组件，不管是股票交易系统——包括现货交易、期货交易、期权交易等，还是数字货币交易系统——包括币币交易、合约交易、杠杆交易等，以及各种不同的贵金属交易系统、大宗商品交易系统等，虽然各种不同交易系统的交易标的不同，但只要都是采用撮合交易模式，都离不开撮合引擎。

撮合引擎的设计目标是通用性，设计要遵循 SRP 原则（也就是说保证撮合引擎的通用，应当采取 SRP，撮合引擎应该只负责撮合订单）。

撮合引擎可以说是交易所的最核心的东西，撮合引擎可以根据交易对的不同分别处理；撮合引擎包含两个主要的队列：卖单队列和买单队列，通过定序服务后，对每个委托单执行撮合

数字资产交易所一般**采用连续竞价的方式，采用高性能的内存撮合技术**。

#### 清算服务 \(clearing service\)

把撮合后的结果进行清算，包括 taker 和 maker 的资产账户对应的币的增减、根据费率收取手续费、更新订单状态，通知用户

#### 行情服务 \(quotation service\)

撮合结果同步给行情服务，为用户提供市场的成交价、成交量等信息，并输出实时价格、K线图等技术数据，提供查询

#### 通知服务 \(notification service\)

通知订单状态，市场行情数据等

#### 钱包服务 \(wallet service\)

可以做成一个抽象的钱包服务网关，针对不同的区块链主网有不同的具体实现

#### 做市商服务（Market Making service）

做市商（Market maker）为交易所增加了流动性，缩小了买卖价差，同时也为交易委托账本增加了深度，这些因素同时也会更加吸引交易人的加入。

一个基本的做市策略包括同时挂买单和卖单，这样当两方的订单都被市场吃掉后就挣到了买卖价差。由于加密货币的价格剧烈不稳定性，当市场价格向一个方向持续运动时，使用这种策略的做市商有可 能损失惨重。例如，做市商挂买单以300 USDT的价格买1 ETH，同时挂另一个卖单以301 USDT的价格卖1 ETH。如果这两个订单都成交了，那么做市商就赚了1 USDT。如果卖单成交，价格继续上涨至310USDT，那么做市商的买单就没有机会在短期内成交，这导致做市商面临一个潜在的9 USDT的亏损。

#### 对账服务

财务对账

#### 其他服务

分布式调度、监控系统，风控系统等

#### 设计与实现

设计并实现一个功能完整的交易系统，项目名称：exchange

### 术语

1. [Order Book](https://www.investopedia.com/terms/o/order-book.asp) \(交易委托账本\)
2. [Spot Trade](https://investinganswers.com/dictionary/s/spot-trade) \(现货交易，A **spot trade** is an [asset](https://investinganswers.com/dictionary/a/asset) or [commodity](https://investinganswers.com/dictionary/c/commodity) transacted and delivered immediately\)
3. [Tick ](https://www.investopedia.com/terms/t/tick.asp)
4. LTP is Last Traded Price, and [what is LTP in share market](https://www.angelbroking.com/knowledge-center/share-market/what-is-ltp-in-share-market), [LTP in stock market](https://pocketsense.com/ltp-stock-market-6687864.html)

### 参考

* [证券交易系统设计与开发](https://www.liaoxuefeng.com/article/1185272483766752)
* [高性能交易系统设计原理](https://www.liaoxuefeng.com/article/1341133393231906)
* [交易系统如何确保账簿 100% 正确](https://www.liaoxuefeng.com/article/1256044879924224)
* [交易系统的消息服务如何保证 100% 可靠](https://www.liaoxuefeng.com/article/1255966635024608)
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
* [Cryptocurrency Exchange Architecture with Akka Microservices - Part 3](https://www.linkedin.com/pulse/cryptocurrency-exchange-architecture-akka-part-3-jim-yang-1c?articleId=6456366788652392448#comments-6456366788652392448&trk=public_profile_article_view)  [中文版](https://blog.csdn.net/chimigaipangsh8139/article/details/101064387)
* 开源撮合引擎：[mzheravin/exchange-core](https://github.com/mzheravin/exchange-core) 这个可以研究一下
* 开源撮合引擎：[enewhuis/liquibook](https://github.com/enewhuis/liquibook)
* [https://github.com/gitbitex/gitbitex-spot](https://github.com/gitbitex/gitbitex-spot) An Open Source Cryptocurrency Exchange

![gitbitex spot](../../.gitbook/assets/image%20%2849%29.png)

* [https://github.com/saniales/golang-crypto-trading-bot](https://github.com/saniales/golang-crypto-trading-bot)
* [https://github.com/jammy928/CoinExchange\_CryptoExchange\_Java](https://github.com/jammy928/CoinExchange_CryptoExchange_Java)
* [https://github.com/bmoscon/cryptostore](https://github.com/bmoscon/cryptostore) A storage engine for cryptocurrency data



