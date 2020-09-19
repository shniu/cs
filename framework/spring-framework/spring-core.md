# Spring Core

Spring 生态是一个非常棒的生态，Spring 使得 Java 的开发简单高效：

{% hint style="info" %}
Spring makes programming Java quicker, easier, and safer for everybody. Spring’s focus on speed, simplicity, and productivity has made it the [world's most popular](https://snyk.io/blog/jvm-ecosystem-report-2018-platform-application/) Java framework.
{% endhint %}

而 Spring Framework 是整个 Spring 生态的核心框架，它提供了一个全面的编程和配置模型，Spring 的一个关键要素是应用层面的基础架构支持。Spring 专注于企业应用程序的 "管道"，因此团队可以专注于应用程序级的业务逻辑，而不必与特定的部署环境有不必要的联系。

Spring Framework 分为多个模块：

1. 核心组件：IoC 容器, 事件, 资源, 国际化, 验证, 数据绑定, 类型转换, SpEL, AOP
2. 测试：Mock objects, TestContext框架, Spring MVC 测试, WebTestClient
3. 数据访问：Transactions\(事务支持\), DAO support\(DAO 支持\), JDBC, ORM\(对象关系映射\), 编组 XML
4. Web Servlet：Spring MVC, WebSocket, SockJS, STOMP 消息
5. Web Reactive：Spring WebFlux, WebClient, WebSocket
6. 集成：Remoting\(远程调用\), JMS\(java消息服务\), JCA\(J2EE 连接器架构\), JMX\(Java管理扩展\), Email\(电子邮箱\), Tasks（任务执行）, Scheduling\(调度\), Cache\(缓存\)

学习 Spring 框架要抓住它的主线，IoC 容器就是它的主线，其他所有能力的扩展都是在 IoC 容器之上做的。 

{% hint style="info" %}
IoC 是一种框架层面的设计思想，框架提供了一个可扩展的代码骨架，用来组装对象、管理整个执行流程。程序员利用框架进行开发的时候，只需要往预留的扩展点上，添加跟自己业务相关的代码，就可以利用框架来驱动整个程序流程的执行。这里的“控制”指的是对程序执行流程的控制，而“反转”指的是在没有使用框架之前，程序员自己控制整个程序的执行。在使用框架之后，整个程序的执行流程可以通过框架来控制。流程的控制权从程序员“反转”到了框架。

DI 是一种 IoC 的具体实现，它通过依赖注入的方式将实例注入到其他实例中，我们只需要通过依赖注入框架提供的扩展点，简单配置一下所有需要创建的类对象、类与类之间的依赖关系，就可以实现由框架来自动创建对象、管理对象的生命周期、依赖注入等原本需要程序员来做的事情。
{% endhint %}

### IoC 及源码学习

整个 IoC 容器的设计思路是以 Bean 的定义加载、创建、生命周期管理为主线，在每个环节中加入扩展点增强了 IoC 容器的能力，ApplicationContext 是 IoC 容器的核心关键，可以说是它管理了 Spring 框架的生命周期。

