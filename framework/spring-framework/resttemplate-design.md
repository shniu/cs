---
description: RestTemplate 的设计思想、设计原则和设计模式
---

# RestTemplate 设计

### RestTemplate 的设计目标（背景、用途等）

> Synchronous client to perform HTTP requests, exposing a simple, template method API over underlying HTTP client libraries such as the JDK HttpURLConnection, Apache HttpComponents, and others. The RestTemplate offers templates for common scenarios by HTTP method, in addition to the generalized exchange and execute methods that support of less frequent cases.

可以看到，`RestTemplate` 的主要作用是作为 http 调用的客户端来简化客户端调用，它采用同步方式执行 HTTP 请求，具体执行请求时的 Connection 和 Request 等是封装了 JDK 的 HttpURLConnection，或者 Apache HttpComponents，或者其他的 http client 类库；此外，`RestTemplate` 提供了模版化的方法让开发者非常容易使用 。

Note: 在 Spring Framework 5.0 以后，推荐使用 `org.springframework.web.reactive.client.WebClient` 来代替 `RestTemplate` .

### RestTemplate 的总体设计（模型）

#### RestTemplate 类和接口的结构

![RestTemplate](../../.gitbook/assets/image%20%2819%29.png)

RestTemplate 的模型：在需要发起 Http 调用的场景，让使用者可以使用统一的编程模型，而无须花费过多的精力在处理请求准备、序列化反序列化、连接池管理等事情上，屏蔽更多的 http 调用的编程细节；同时，RestTemplate 支持动态切换底层的 http client 类库。RestTemplate 提供了一些模版方法，方便我们使用它进行 Http 调用，并处理调用过程中的请求头、请求体、序列化、响应体、响应结果等

![RestTemplate &#x7684;&#x6A21;&#x578B;](../../.gitbook/assets/image%20%2815%29.png)

### RestTemplate 的编程接口

```java
RestTemplate restTemplate = new RestTemplate();

// ClientHttpRequestFactory & ClientHttpRequest & ClientHttpResponse
public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
}

// ClientHttpRequestInterceptor
public void setInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
}

// HttpMessageConverter
public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
}

// ResponseErrorHandler
public void setErrorHandler(ResponseErrorHandler errorHandler) {
}

// UriTemplateHandler
public void setUriTemplateHandler(UriTemplateHandler handler) {
}

// RestOperations
// see https://docs.spring.io/spring/docs/5.2.7.RELEASE/spring-framework-reference/integration.html#rest-resttemplate
// org.springframework.web.client.RestOperations
// e.g.
<T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws RestClientException;
<T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType)
			throws RestClientException;
			
// RestClientException

```

从 RestTemplate 的编程接口来看，分为两部分：

* 构造 RestTemplate 对象

RestTemplate 提供了定制化的能力，比如可以定制 `ClientHttpRequestFactory` , `HttpMessageConverter` , `ResponseErrorHandler` , `UriTemplateHandler` , `RequestInterceptor` 等

其中，`ClientHttpRequestFactory` 是定制化具体请求的地方，比如连接池的管理、超时时间的设置（获取连接的超时时间、读取超时时间、连接主机超时时间等）、证书配置、SSL等

* 使用 RestTemplate 处理 http 请求和响应

`RestTemplate` 提供了众多的 Rest 风格的调用方法，比如 `getForObject` , `getForEntity` , `postForObject` , `put` , `delete` 等；同时还提供了通用的 `exchange` , `execute` 等

### RestTemplate 如何实现的

可以把 RestTemplate 看成分为两层，第一层是面向应用程序的，第二层是 RestTemplate 提供一些抽象来屏蔽底层 Http Client 的差异的。第一层是使用层面的，主要还是看编程接口，我们主要来看第二层是如何实现的（从设计原则、设计思想、设计模式等方面来分析）

* Http Request 的抽象

![Client Http Request &#x62BD;&#x8C61;](../../.gitbook/assets/image%20%2821%29.png)

```java
public interface ClientHttpRequest extends HttpRequest, HttpOutputMessage {
    // 核心的抽象：执行 request 后返回 ClientHttpResponse
    ClientHttpResponse execute() throws IOException;
}

public interface HttpRequest extends HttpMessage {
    // 定义请求的 url
    URI getURI();
    // 定义请求的 method, e.g. get, post, put ...
    default HttpMethod getMethod() {}
    String getMethodValue();
}

public interface HttpOutputMessage extends HttpMessage {
	/**
	 * Return the body of the message as an output stream.
	 */
	OutputStream getBody() throws IOException;
}

public interface HttpMessage {
	/**
	 * Return the headers of this message.
	 */
	HttpHeaders getHeaders();
}
```

`ClientHttpRequest` 抽象出了一个 http 请求最关键的四个部分：URL, Method, Header, Body；这几部分正是每一个 http 请求关注的部分，`execute` 方法提供了调用的抽象，具体使用什么库，如何执行，交给具体的实现。

`ClientHttpRequest` 的创建使用了工厂模式: `ClientHttpRequestFactory` , 在这里就非常适合，因为每个http请求都是相互隔离的，需要实时创建。

```java
public interface ClientHttpRequestFactory {

	/**
	 * Create a new {@link ClientHttpRequest} for the specified URI and HTTP method.
	 * <p>The returned request can be written to, and then executed by calling
	 * {@link ClientHttpRequest#execute()}.
	 * @param uri the URI to create a request for
	 * @param httpMethod the HTTP method to execute
	 * @return the created request
	 * @throws IOException in case of I/O errors
	 */
	ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException;
}
```



### 总结

从 resttemplate 可以吸取到哪些设计优点：

1. 针对定制化需求预留好扩展点，并提供一个适用于大部分场景的默认配置
2. 把对做的事情设计出一个好的抽象，识别哪些该做，哪些不该做，比如resttemplate 就不该做具体的 http client 实现，而是使用现有的被验证过的流行的http client，做好集成不同 client 的抽象
3.  做一个公共类库，基础框架的代码，要考虑屏蔽更多的具体实现细节，屏蔽不同底层库自身的差异，提供扩展点进行定制化
4. 擅用设计原则，设计模式，如抽象，面向接口编程，策略模式，拦截器模式，构造器模式，模版方法模式等，多用组合的思想

### HTTP 客户端

#### OkHttp

{% embed url="https://square.github.io/okhttp/" %}

#### HttpClient

{% embed url="https://hc.apache.org/httpcomponents-client-ga/" %}

