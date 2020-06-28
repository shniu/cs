# RestTemplate 使用中遇到的问题

### Request Aborted 问题描述

最近在工作中使用 RestTemplate, 有时候会出现 Request Aborted

![Request Aborted &#x5F02;&#x5E38;](../.gitbook/assets/image%20%2812%29.png)

注：使用 RestTemplate 的场景是，有多个定时任务需要执行，使用了 xxl-job 做分布式任务的调度，在初期被调度的机器只有一台。

在使用 RestTemplate 时，对它做了定制，定制的代码如下：

```java
@Configuration
public class TemplateConfig {

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        // ...
    }
    
    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        // ...
    }
    
    @Bean
    public CloseableHttpClient httpClient() {
        // ...
    }
    
    @Bean
    public RestTemplate restTemplate(final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory) {
        // ...
    }
    
    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory(final CloseableHttpClient httpClient) {
        // ...
    }

}
```





