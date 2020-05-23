# 开发实践

不要乱用 domain

domain 是一个领域对象，往往我们再做传统 Java 软件 Web 开发中，这些 domain 都是贫血模型，是没有行为的，或是没有足够的领域模型的行为的，所以，以这个理论来讲，这些 domain 都应该是一个普通的 entity 对象，并非领域对象，所以请把包名改为:com.xxx.entity；领域和实体之间的区别，以及贫血模型和充血模型，可以看看领域驱动设计方面的书和资料

DTO

只要是用于网络传输的对象，我们都认为他们可以当做是 DTO 对象，比如电商平台中，用户进行下单，下单后的数据，订单会发到 OMS 或者 ERP 系统，这些对接的返回值以及入参也叫 DTO 对象；DTO 为系统与外界交互的模型对象，那么肯定会有一个步骤是将 DTO 对象转化为 BO 对象或者是普通的 entity 对象，让 service 层去处理。

Bean 之间的转化

使用set可以做属性转化，但是太麻烦，可以考虑使用工具，如modelmapper、BeanUtils.copyProperties。在写代码时，我们应该尽量把语义层次差不多的放到一个方法中。

Bean 验证

lombok

Accesstors

参考

* [如何更好的做开发：项目中的开发实践](https://mp.weixin.qq.com/s/seVOch3w8GdDKyu0FDOw3g)

