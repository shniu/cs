# Kubernetes - Mapping External Services

在使用 Kubernetes 集群时，我们有访问外部服务的需要，比如一些公共的 API 服务，或者是自己自建的一些数据存储服务等。

如果在不同环境中的应用程序连接到相同的外部 Endpoint，并且没有计划将外部服务引入 Kubernetes 集群，那么直接在代码中使用外部服务 Endpoint 是完全可以的。但是，有些情况是例外的，比如某些 Cloud Native 的数据库（例如 Cloud Firestore 或 Cloud Spanner）使用单个端点进行所有访问，但大多数数据库对于不同实例具有单独的端点。

一种解决方法是：使用 ConfigMap。只需将端点地址存储在 ConfigMap 中，然后在代码中将其用作环境变量。这个解决方案有效，但存在一些缺点：需要修改 ConfigMap 并编写其他代码以从环境变量中读取；但最重要的是，如果端点地址发生更改，则可能需要重新启动所有正在运行的容器以获取更新的端点地址。而 Kubernetes 为我们提供了内置的服务发现机制来更好的解决这个问题。

### Kubernetes 如何做外部服务映射

Kubernetes 的内置服务发现机制为集群外运行的服务提供服务，就像为集群内的服务一样！这使得跨开发环境和产品环境提供了方便，并且如果最终将服务移至群集内，则完全不必更改代码。

#### 在 Kubernetes 群集外具有 IP 地址的数据库做服务映射

这种场景比较常见：在 Kubernetes 集群外有一个独立的数据库服务，需要让集群内的服务可以访问到这个带有 IP 地址的外部服务。

* 首先创建一个 Service

```yaml
kind: Service
apiVersion: v1
metadata:
 name: external-mysql-svc
Spec:
 type: ClusterIP
 ports:
 - port: 3306
   targetPort: 13306
```

这个服务没有 Pod 选择器，在创建 Service 后，并不知道将流量发送到哪里，接下来需要创建一个 Endpoint，该对象将接收来自该服务的流量。

* 再创建一个 Endpoint

```yaml
kind: Endpoints
apiVersion: v1
metadata:
 name: external-mysql-svc
subsets:
 - addresses:
     - ip: 10.240.0.4
   ports:
     - port: 13306
```

Endpoint 手动定义了数据库的 IP 地址，并且使用了与 Service 相同的名称。 Kubernetes 使用 Endpoint 中定义的所有 IP 地址，就像它们是常规的 Kubernetes Pod 一样。

然后就可以使用下面的方式在应用服务中访问数据库：

```text
jdbc:mysql://external-mysql-svc/test?useSSL=false&characterEncoding=utf8
```

这种方式完全不需要在代码中使用 IP 地址！如果将来 IP 地址发生变化，则可以使用新的 IP 地址更新 Endpoint，并且应用程序无需进行任何更改.

#### 在 Kubernetes 群集外具有 URI 地址的数据库做服务映射

如果使用的是来自第三方的托管数据库服务，则很可能会提供了可用于连接的统一资源标识符（URI）。三方托管的服务可能为不同的环境提供了不同的 URI 地址：

```text
// dev 环境
mongodb://<dbuser>:<dbpassword>@ds149763.mlab.com:49763/dev

// prod 环境
mongodb://<dbuser>:<dbpassword>@ds145868.mlab.com:45868/prod
```

这个时候可以创建 ExternalName Service，这提供了静态 Kubernetes 服务，可将流量重定向到外部服务。此服务在内核级别执行简单的 CNAME 重定向，因此对性能的影响很小。如下：

```yaml
kind: Service
apiVersion: v1
metadata:
  name: mongo
spec:
  type: ExternalName
  externalName: ds149763.mlab.com
```

由于 ExternalName 使用 CNAME 重定向，因此无法进行端口映射。对于具有静态端口的服务，这种方式会更加适用。

### 总结

通过将外部服务映射到内部服务，可以灵活地将来将这些服务引入群集中，同时最大程度地减少重构工作。此外，它使管理和了解正在使用哪些外部服务变得更加容易。

如果外部服务具有有效的域名，并且不需要重新映射端口，则使用 ExternalName 服务类型是将外部服务映射到内部服务的简便快捷方式。如果没有域名或需要进行端口重新映射，只需将 IP 地址添加到 Endpoint 并使用它即可。

via: [https://cloud.google.com/blog/products/gcp/kubernetes-best-practices-mapping-external-services](https://cloud.google.com/blog/products/gcp/kubernetes-best-practices-mapping-external-services)

via: [https://blog.opskumu.com/kubernetes-ext-service.html](https://blog.opskumu.com/kubernetes-ext-service.html)

