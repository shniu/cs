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

* [Limit Order](https://www.investopedia.com/terms/l/limitorder.asp)

限价单是指以特定价格或更好的价格买入或卖出股票的订单。 买入限价单只能以限价或更低的价格执行，而卖出限价单只能以限价或更高的价格执行。 限价单并不保证会被执行。 只有当股票的市场价格达到限价时，限价单才能被执行。 虽然限价单不保证执行，但它们有助于确保投资者为股票支付的价格不会超过预先确定的价格。

* [Market Order](https://www.investopedia.com/terms/m/marketorder.asp) 市价单：是指以现价或市价尽快执行的交易

市价单是投资者提出的要求--通常是通过经纪人或经纪服务提出的--以当前市场的最佳价格买入或卖出证券。它被广泛认为是进入或退出交易的最快和最可靠的方式，并提供了最有可能快速进入或退出交易的方法。对于许多大盘流通股来说，市场订单几乎是瞬间完成的

市价订单非常适合交易量非常大的证券，如大盘股、期货或ETF。例如，E-迷你标准普尔指数或微软等股票的市价订单往往能迅速完成，不会出现问题。

对于浮动量较低和/或日均成交量极少的股票来说，情况就不同了。由于这些股票的交易量很小，买卖价差往往很大。因此，这些证券的市场订单有时会被缓慢地执行，而且往往以意想不到的价格执行，从而导致有意义的交易成本。

* Stop Order 止损单：是指当股票的交易价格达到或超过指定价格（"止损价"）时，按市价买入或卖出股票的订单。如果股票达到止损价，该订单就成为市价订单，并在下一个可用市价成交。如果股票未能达到止损价，则该订单不会被执行。
* [3 order types: limit order, marker order and stop order](https://www.schwab.com/resource-center/insights/content/3-order-types-market-limit-and-stop-orders)

三种类型的订单适用场景是不一样的。

* [Order Book](https://www.investopedia.com/terms/o/order-book.asp) \(交易委托账本\)
* [Spot Trade](https://investinganswers.com/dictionary/s/spot-trade) \(现货交易，A **spot trade** is an [asset](https://investinganswers.com/dictionary/a/asset) or [commodity](https://investinganswers.com/dictionary/c/commodity) transacted and delivered immediately\)
* [Tick ](https://www.investopedia.com/terms/t/tick.asp)
* LTP is Last Traded Price, and [what is LTP in share market](https://www.angelbroking.com/knowledge-center/share-market/what-is-ltp-in-share-market), [LTP in stock market](https://pocketsense.com/ltp-stock-market-6687864.html)
* [Bid Ask Spread](https://www.investopedia.com/terms/b/bid-askspread.asp) 买卖差价
* Security  证券
* Market maker 做市商，Price taker 价格接受者 （trader）
* Ask price 卖价 （也就是 Sell）；Bid price 买价 （也就是 Buy）
* [Trailing Stop](https://www.investopedia.com/terms/t/trailingstop.asp)
* [市场深度](https://zhuanlan.zhihu.com/p/31870077)
* [现货交易 spot trading](https://wiki.mbalib.com/wiki/%E7%8E%B0%E8%B4%A7%E4%BA%A4%E6%98%93)
* [Order Book Level 1 and Level 2 Market Data](https://www.thebalance.com/order-book-level-2-market-data-and-depth-of-market-1031118)
* [流动性 market liquidity](https://www.investopedia.com/terms/l/liquidity.asp)
* [Stop Limit](https://www.investopedia.com/terms/s/stop-limitorder.asp)  & [Trailing Stop 追踪止损](https://www.interactivebrokers.com/cn/index.php?f=5302)

Stop Limit 和 Trailing Stop 比较有意思，他们是有效的风险控制工具。追踪止损单比止损单更灵活，一旦市场以规定的价格（称为价格距离）对您不利，就会执行。在进行保证金交易时，可以使用尾随止损单来保护利润。 举例说明。如果交易者处于多头仓位，当前市场价格从225快速上涨后为250，交易者可以设置一个价格距离为5的尾随止损单。这将在245点建立一个卖出止损单。与一般的止损单不同，如果市场价格继续上涨到275，那么追踪止损单也会相应上涨，始终保持在市场价格的5后面，在本例中上涨到270。

止损价格落后于市场价格的金额指定为价格距离，如果市场向有利可图的方向移动，止损单可以根据市场进行调整。如果触发了止损，则会下达市场订单。

[止损限价单](https://www.interactivebrokers.com/cn/index.php?f=5122)

限价止损单是在设定的时间范围内进行的一种有条件的交易，它结合了止损单和限价单的特点，用于降低风险。它与其他订单类型有关，包括限价订单（以给定价格或更好的价格买入或卖出指定数量的股票的订单）和报价止损订单（在证券价格超过指定点后买入或卖出证券的订单）

* 爆仓： 所谓**爆仓**，是指在某些特殊条件下，投资者保证金账户中的客户权益为负值的情形。 在市场行情发生较大变化时，如果投资者保证金账户中资金的绝大部分都被交易保证金占用，而且交易方向又与市场走势相反时，由于保证金交易的杠杆效应，就很容易出现**爆仓**。 如果**爆仓**导致了亏空且由投资者的原因引起，投资者需要将亏空补足，否则会面临法律追索。
* **做多**：就是你看好大米会涨价，买入或借入大米囤起来，等大米价格上涨了就卖出去赚差价；**做空**：就是你看好大米会降价，跟大米老板借大米，然后卖出去换成钱，等大米降价再用钱买大米还给大米老板，赚取差价

都是为了赢利，具体的说，有的是为了投机，有的是套期保值，有的是为了交割现货。

* [OkEx 委托类型说明](https://www.okex.com/support/hc/zh-cn/articles/360003100091)
* [Currency futures](https://www.investopedia.com/terms/c/currencyfuture.asp) 货币期货
* [Margin Trading](https://academy.binance.com/en/articles/what-is-margin-trading)  保证金交易
* [Margin Definition](https://www.investopedia.com/terms/m/margin.asp) 保证金的定义
* [Option](https://www.investopedia.com/terms/o/option.asp)  期权
* [Limit Move ](https://www.investopedia.com/terms/l/limit-move.asp) 
* [L2 Market](https://www.investopedia.com/terms/l/level2.asp) 和 [L2 Market Data](https://www.exegy.com/2019/03/level-2-market-data-what-level-supports-your-trading-strategy/)
* [FOK](https://www.investopedia.com/terms/f/fok.asp) - The purpose of a fill or kill \(FOK\) order is to ensure that an entire position is executed at prevailing prices in a timely manner.
* [Post only Order](https://www.delta.exchange/zh/blog/support/what-are-post-only-orders) - 只挂单的委托单。挂单是增加交易所的流动性的，而吃单是降低交易所流动性的，一般交易所会激励挂单，所以如果一个单子在下单时标记为 post only，就会只以挂单的方式执行，如果这个单子能被部分填充或者全部填充，会被立即取消掉

1. Post only orders always receive maker fee on execution
2. Only limit orders can be post-only. Since market orders execute immediately, they can’t be made post only
3. If a post only order will partially or fully match against an existing order in the order book, then the post only order is cancelled.

* [GTC](https://www.investopedia.com/terms/g/gtc.asp)

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
* [~~https://github.com/jammy928/CoinExchange\_CryptoExchange\_Java~~](https://github.com/jammy928/CoinExchange_CryptoExchange_Java)~~~~
* [https://github.com/sengeiou/ZTuoExchange\_framework](https://github.com/sengeiou/ZTuoExchange_framework)
* [https://github.com/bmoscon/cryptostore](https://github.com/bmoscon/cryptostore) A storage engine for cryptocurrency data

其他资源

* [The Adaptive Radix Tree](https://db.in.tum.de/~leis/papers/ART.pdf)， [解析1](https://blog.csdn.net/matrixyy/article/details/70182527)
* [Exchange core collections](https://github.com/shniu/collections)

### 撮合引擎

要设计撮合引擎，需要知道撮合引擎是如何工作的。

* 交易系统设计：[https://www.youtube.com/watch?v=dUMWMZmMsVE](https://www.youtube.com/watch?v=dUMWMZmMsVE)
* Risk Management 如何工作：[https://www1.nseindia.com/products/content/derivatives/equities/risk\_management.htm](https://www1.nseindia.com/products/content/derivatives/equities/risk_management.htm)

### LMAX-Exchange

* [Low latency Trading Architecture at LMAX Exchange](https://www.infoq.com/presentations/lmax-trading-architecture/)
* [LMAX Architecture by Martin Fowler](https://martinfowler.com/articles/lmax.html)
* Disruptor - A High Performance Inter-Thread Messaging Library

### 火币、币安等交易所架构

* [火币网交易所架构演进](https://mp.weixin.qq.com/s/mZaMhaN-56j-jBykovTJMQ)
* [交易所架构解析](https://mp.weixin.qq.com/s/iObYqbCZDIJH9ruz6316ow)
* [数字货币交易所架构初探](https://mp.weixin.qq.com/s/RCND9QEiVtKxHHzBAGqB7A)
* [交易风险控制](https://mp.weixin.qq.com/s/KMyTsfwMXnZcjNXeQLS00w)
* \*\*\*\*[**高并发、低 RT 的风控系统架构及技术架构的实现**](https://gitbook.cn/gitchat/activity/5cd91b637b22ef4d1f70c332)\*\*\*\*
* \*\*\*\*[**https://cloud.tencent.com/developer/article/1476492**](https://cloud.tencent.com/developer/article/1476492)\*\*\*\*
* [交易所架构开发学习](https://www.jianshu.com/p/4daba3bb413e) - [https://gitee.com/cexchange/CoinExchange](https://gitee.com/cexchange/CoinExchange)
* [https://www.itbaizhan.cn/stages/id/33/phase/385](https://www.itbaizhan.cn/stages/id/33/phase/385)

 

