---
description: RestTemplate 的设计思想、设计原则和设计模式
---

# RestTemplate 设计

### RestTemplate 的设计目标（背景、用途等）

> Synchronous client to perform HTTP requests, exposing a simple, template method API over underlying HTTP client libraries such as the JDK HttpURLConnection, Apache HttpComponents, and others. The RestTemplate offers templates for common scenarios by HTTP method, in addition to the generalized exchange and execute methods that support of less frequent cases.

可以看到，`RestTemplate` 的主要作用是作为 http 调用的客户端来简化客户端调用，它采用同步方式执行 HTTP 请求，具体执行请求时的 Connection 和 Request 等是封装了 JDK 的 HttpURLConnection，或者 Apache HttpComponents，或者其他的 http client 类库；此外，`RestTemplate` 提供了模版化的方法让开发者非常容易使用 

### RestTemplate 的总体设计（模型）

### RestTemplate 的编程接口

### RestTemplate 如何实现的



