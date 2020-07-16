---
description: DDD
---

# 领域驱动设计

领域驱动设计是目前为止比较完善的软件设计方法，目前比较流行的做法是结合事件风暴\(Event Storming\)和领域驱动设计在一起，运用事件风暴的工作坊方式将相关人员（业务领域专家、项目负责人、开发、测试、架构师等）聚集在一起，通过类似头脑风暴的形式将系统涉及到的关键事件、动作、业务约束、业务边界等识别出来，并建立统一语言，然后再结合 DDD 的战略设计和战术设计做更近一步的落地；从 OO 的角度来看，DDD 是 OO 的一种延伸，可以指导设计和实现，所以 OO 的思想、设计模式、设计原则同样适用在 DDD 中，系统开发是不断运用设计（更加深刻的理解业务）、不断权衡各种设计方案、不断权衡实现方案等的过程。

NOTE: 可见事件风暴和领域驱动设计是从团队协作出发的，对整个团队的要求其实是挺高的，应该有一个在这方面的专家，带着所有人不断去尝试，有可能前几次会失败，但是一定得坚持下来。

### 基本概念

1. **模型**：模型是能够表达系统业务逻辑和状态的对象；**模型，用来反映事物某部分特征的物件，无论是实物还是虚拟的**
2. **领域**：现实世界中的业务逻辑，在 IT 系统业务分析时，适合某个行业和领域相关的，所以又叫做领域；**指的特定行业或者场景下的业务逻辑**
3. **领域模型**：**DDD 中的模型是指反应 IT 系统的业务逻辑和状态的对象，是从具体业务（领域）中提取出来的，因此又叫做领域模型**
4. **领域驱动设计**：通过对实际业务出发，而非马上关注数据库、程序设计。通过识别出固定的模式，并将这些业务逻辑的承载者抽象到一个模型上。这个模型负责处理业务逻辑，并表达当前的系统状态。这个过程就是领域驱动设计

按照面向对象设计的话，我们的系统是一个电子餐厅。现实餐厅中的实体，应该对应到我们的系统中去，用于承载业务，例如收银员、顾客、厨师、餐桌、菜品，这些虚拟的实体表达了系统的状态，在某种程度上就能指代系统，这就是模型，如果找到了这些元素，就很容易设计出软件

一般方法：

分析业务，设计领域模型，编写代码，建立了领域模型后，我可以考虑使用领域模型指导开发工作，如

* 指导数据库设计
* 指导模块分包和代码设计
* 指导 RESTful API 设计
* 指导事务策略
* 指导权限
* 指导微服务划分（有必要的情况）

### Event Storming & DDD

DDD 拥有一套完整的方法论，最开始接触 DDD 被它的各种概念搞得晕头转向；后来接触到事件风暴，更是不知所云，但是在这条道路上要继续走下去。

* EventStorming

EventStorming 是一种新型的工作坊模式，它可以帮助我们协作探索复杂的业务领域。

> **EventStorming** is a flexible workshop format for collaborative exploration of complex business domains.

* DDD

**DDD其实就是面向对象的一套“方言”，提供了一种基于业务领域的对象划分和分类方法，其最大的价值就在于对于软件开发过程全生命周期使用语言的统一！**

#### DDD

**值对象**：如果这些数据没有ID和生命周期，在修改时是整体替换，也不需要基于它做统计分析，你就可以将它设计为值对象。

如果一个业务行为由多个实体对象参与完成，我们就将这部分业务逻辑放在领域服务中实现。领域服务与实体方法的区别是：实体方法完成单一实体自身的业务逻辑，是相对简单的原子业务逻辑，而领域服务则是多个实体组合出的相对复杂的业务逻辑。两者都在领域层，实现领域模型的核心业务能力。

### 参考

#### Event Storming

1. [EventStorming resource](https://www.eventstorming.com/resources/)
2. [https://blog.avanscoperta.it/it/tag/event-storming-it/](https://blog.avanscoperta.it/it/tag/event-storming-it/)
3. [http://exploreddd.com/](http://exploreddd.com/)
4. [https://github.com/mariuszgil/awesome-eventstorming](https://github.com/mariuszgil/awesome-eventstorming)
5. [ddd-by-examples](https://github.com/ddd-by-examples)/[**library**](https://github.com/ddd-by-examples/library) ****公共图书馆的例子，A comprehensive Domain-Driven Design example with problem space strategic analysis and various tactical patterns. 非常全面的DDD实践
6. [https://github.com/ddd-by-examples](https://github.com/ddd-by-examples)
7. [x] [EventStorming and Spring with a Splash of DDD](https://spring.io/blog/2018/04/11/event-storming-and-spring-with-a-splash-of-ddd) 介绍 DDD 和 Spring 的结合，principles for building Spring applications with DDD

#### DDD

* [DDD、EventStorming与业务中台](https://zhuanlan.zhihu.com/p/120896743)
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
* [ ] [https://zhuanlan.zhihu.com/c\_137428247](https://zhuanlan.zhihu.com/c_137428247)
* [ ] [https://tech.meituan.com/2017/12/22/ddd-in-practice.html](https://tech.meituan.com/2017/12/22/ddd-in-practice.html) DDD 在美团的实践
* [ ] [https://insights.thoughtworks.cn/backend-development-ddd/\#comment-50693](https://insights.thoughtworks.cn/backend-development-ddd/#comment-50693)

#### Book

1. [实现领域驱动设计](../../other/reading/ddd-impl.md)

