# 领域驱动设计

### 概念

1. 模型：模型是能够表达系统业务逻辑和状态的对象；**模型，用来反映事物某部分特征的物件，无论是实物还是虚拟的**
2. 领域：现实世界中的业务逻辑，在 IT 系统业务分析时，适合某个行业和领域相关的，所以又叫做领域；**指的特定行业或者场景下的业务逻辑**
3. 领域模型：**DDD 中的模型是指反应 IT 系统的业务逻辑和状态的对象，是从具体业务（领域）中提取出来的，因此又叫做领域模型**
4. 领域驱动设计：通过对实际业务出发，而非马上关注数据库、程序设计。通过识别出固定的模式，并将这些业务逻辑的承载者抽象到一个模型上。这个模型负责处理业务逻辑，并表达当前的系统状态。这个过程就是领域驱动设计

按照面向对象设计的话，我们的系统是一个电子餐厅。现实餐厅中的实体，应该对应到我们的系统中去，用于承载业务，例如收银员、顾客、厨师、餐桌、菜品，这些虚拟的实体表达了系统的状态，在某种程度上就能指代系统，这就是模型，如果找到了这些元素，就很容易设计出软件

一般方法：

分析业务，设计领域模型，编写代码，建立了领域模型后，我可以考虑使用领域模型指导开发工作，如

* 指导数据库设计
* 指导模块分包和代码设计
* 指导 RESTful API 设计
* 指导事务策略
* 指导权限
* 指导微服务划分（有必要的情况）

### 理解 DDD

参考了：[DDD、EventStorming与业务中台](https://zhuanlan.zhihu.com/p/120896743)

**DDD其实就是面向对象的一套“方言”，提供了一种基于业务领域的对象划分和分类方法，其最大的价值就在于对于软件开发过程全生命周期使用语言的统一！**

EventStorming: 

\*\*\*\*

### 文章

* [使用 DDD 指导业务设计的一点思考](https://insights.thoughtworks.cn/ddd-business-design/)

由浅入深的分析了模型和领域模型，生动的讲述了基于领域模型的驱动开发，和传统的基于数据驱动的开发截然不同，基于领域驱动的设计从业务中分析，提炼出领域模型，然后根据领域模型进行系统设计和开发。

* [DDD 开发落地实践](https://insights.thoughtworks.cn/backend-development-ddd/)
* [后端开发实践：简单可用的 CQRS 编码实践](https://insights.thoughtworks.cn/backend-development-cqrs/)
* [ ] [简单可用的 CQRS 编码实践](https://insights.thoughtworks.cn/backend-development-cqrs)
* [ ] [在微服务中使用领域事件](https://insights.thoughtworks.cn/use-domain-events-in-microservices/)
* [ ] [事件驱动架构EDA编码实践](https://zhuanlan.zhihu.com/p/79095599)
* [ ] [识别领域事件](https://zhuanlan.zhihu.com/p/43776403)
* [ ] DDD 实践案例：[e-commerce-sample](https://github.com/e-commerce-sample)
* [ ] [DDD 战术模型之聚合](https://gitbook.cn/books/5b481d2f3ba8652852051915/index.html)

参考读书笔记：[实现领域驱动设计](../other/reading/ddd-impl.md)

