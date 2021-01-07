# Request Aborted

### Request Aborted 问题描述

最近项目中使用 RestTemplate, 有时候会出现 Request Aborted，场景如下：

业务中有调度任务需要跑，使用的是 XXL-Job 作为分布式调度中心，在运行中经常出现 Request Aborted 异常，虽然还未影响业务的正常运行，但还是要搞清楚的，截图如下：

![Request Aborted &#x5F02;&#x5E38;](../../.gitbook/assets/image%20%2812%29.png)

![&#x8BE6;&#x7EC6;&#x7684;&#x9519;&#x8BEF;&#x65E5;&#x5FD7;](../../.gitbook/assets/image%20%2890%29.png)

注：需要说明的是在被调度的任务中，有需要调用外部服务的情况。

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

使用 `PoolingHttpClientConnectionManager` 对连接进行了池化管理，通过追踪错误信息，对 XXL-Job 的调度代码做分析，对 RestTemplate / HttpClients 的源码进行分析，得出如下结论：

1. XXL-Job 进行任务调度时，会配置任务超时时间，如果任务超时后会被主动 Kill 调，XXL-Job 中每个任务是由一个 JobThread 执行的，被 Kill 意味着执行 JobThread.interrupt\(\), 所以任务在超时后的某个时刻会收到中断请求，见 XXL-Job 中关于任务超时的说明
2. 再来说任务本身，XXL-Job 调度的任务需要请求外部接口，有时由于业务需要调用接口的次数会很多，短时间内无法结束，可能需要跑几个小时甚至更长，从而会触发任务超时
3. 为什么是 Request Aborted？看下图

![](../../.gitbook/assets/image%20%2889%29.png)

#### 优化

没有办法解决任务耗时长的问题，但通过分析 RestTemplate + HttpClient 这个组合，是可以让整个任务的耗时尽可能短一点。

从上面的现象和问题分析，可以推断出当多个任务并发执行时，由于 CPool 的 maxPerRoute 的限制，即使 max\_total\_connnections 设置成 200 也得不到太大的帮助，所以可以适当提高 maxPerRoute 的设置，比如设置成 20，假设访问外部服务的 RT 200ms，那么最大 QPS 理论值就是：20 \* （1000 / 200）= 100，那也就是说无形中会增加外部请求的并发度，让任务更快的完成。由于该任务是一个 IO 密集型的，也可以考虑将调用异步化，这样也可以提升性能，缩短任务运行事件。

提升了 maxPerRoute 后，是可以保证每个任务都可以非常快速的获取到连接，而不需要花费过长的时间等待获取连接。

此外，我们也可以使用压力测试来预估 maxPerRoute 设置在多少是一个更加合理的值，经过测试发现，在 10 ～ 20 是比较合理的值；当然需要根据机器的配置和运行环境来决定。



### 参考资源

* [记一次 httpclient 死锁问题](http://blog.kail.xyz/post/2019-04-21/tools/httpclient-lock.html)
* [httpclient 连接池配置](https://www.jianshu.com/p/6a41c95855e3)
* [httpclient 连接管理](https://www.iteye.com/blog/study121007-2304274)
* [httpclient 的踩坑总结](https://www.cnblogs.com/nuccch/p/10611877.html)
* [https://tech.asimio.net/2016/12/27/Troubleshooting-Spring-RestTemplate-Requests-Timeout.html](https://tech.asimio.net/2016/12/27/Troubleshooting-Spring-RestTemplate-Requests-Timeout.html)
* [https://www.rectcircle.cn/posts/spring-resttemplate%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97/](https://www.rectcircle.cn/posts/spring-resttemplate%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97/)



