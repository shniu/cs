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





### 参考资源

* [记一次 httpclient 死锁问题](http://blog.kail.xyz/post/2019-04-21/tools/httpclient-lock.html)
* [httpclient 连接池配置](https://www.jianshu.com/p/6a41c95855e3)
* [httpclient 连接管理](https://www.iteye.com/blog/study121007-2304274)
* [httpclient 的踩坑总结](https://www.cnblogs.com/nuccch/p/10611877.html)
* [https://tech.asimio.net/2016/12/27/Troubleshooting-Spring-RestTemplate-Requests-Timeout.html](https://tech.asimio.net/2016/12/27/Troubleshooting-Spring-RestTemplate-Requests-Timeout.html)
* [https://www.rectcircle.cn/posts/spring-resttemplate%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97/](https://www.rectcircle.cn/posts/spring-resttemplate%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97/)



