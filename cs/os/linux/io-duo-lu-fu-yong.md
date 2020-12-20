# IO 多路复用

epoll

* [epoll\(7\) - Linux manual page](https://man7.org/linux/man-pages/man7/epoll.7.html)
* [epoll\_create\(2\) - Linux manual page](https://man7.org/linux/man-pages/man2/epoll_create.2.html)
* [epoll\_wait\(\) - Linux manual page](https://man7.org/linux/man-pages/man2/epoll_wait.2.html)



* [ ] [https://www.zhihu.com/question/20122137/answer/14049112](https://www.zhihu.com/question/20122137/answer/14049112)
* [ ] [https://segmentfault.com/a/1190000003063859](https://segmentfault.com/a/1190000003063859)
* [ ] [https://github.com/eliben/code-for-blog/blob/master/2017/async-socket-server/epoll-server.c](https://github.com/eliben/code-for-blog/blob/master/2017/async-socket-server/epoll-server.c) epoll server example
* [ ] [https://cloud.tencent.com/developer/article/1694517](https://cloud.tencent.com/developer/article/1694517)
* [ ] [https://cloud.tencent.com/developer/article/1109615](https://cloud.tencent.com/developer/article/1109615)
* [ ] [https://medium.com/@chongye225/networking-with-c-cf15426cc270](https://medium.com/@chongye225/networking-with-c-cf15426cc270)
* [ ] [https://xie.infoq.cn/article/628ae27da9ccb37d2900e8ef4](https://xie.infoq.cn/article/628ae27da9ccb37d2900e8ef4)
* [ ] [http://swingseagull.github.io/2016/11/08/epoll-sample/](http://swingseagull.github.io/2016/11/08/epoll-sample/)

**在 IO 过程中，发送和接收数据的过程分为两步**：

1. 对于发送数据流程：

   1. 第一阶段是用户态的应用程序准备好数据，执行系统调用，将用户态的数据 copy 到内核中的缓冲区，每个 Socket 有自己的发送 buffer  \(**Waiting for the data to be ready**\)
   2. 第二个阶段是数据 copy 到内核完成后，由内核进行调度，交给网卡，将数据发送出去 \(**Copying the data from the process to the kernel**\)

2. 对于接收数据流程：
   1. 第一阶段内核态下准备数据，网卡收到数据，复制到内核 buffer 中，每个 Socket 都有自己的接收 buffer  \(**Waiting for the data to be ready**\)
   2. 第二阶段是数据到达或者准备好后，将数据从内核态 copy 到用户态，供应用程序使用 \(**Copying the data from the kernel to the process**\)



### QA

#### 1. 在使用 epoll IO 多路复用时，一个服务器或者客户端能创建多少连接？

以 Linux 为例，一个连接是由：客户端【IP + PORT】+ 服务器 【IP（固定的） + PORT（固定的）】 四个元素决定的，所以支持多少，要从两个角度分析：

* 对于客户端

单个客户端连接到一个服务器最多连接数取决于本地可用端口数 \(因为其他3个元素固定了\) 65535（报文中端口占用字节数是 16，所以最大端口数 65535）- 1024\(保留端口，不给用\) 约 64K \(这个是理论值\)

1. 理论值大约是 65535 - 1024 = 64511
2. 但是实际是系统其他方面的限制的，取决于以下三个方面：
   1. TCP 层：ip\_local\_port\_range \(参考/proc/sys/net/ipv4/ip\_local\_port\_range\)，可调整，最大65535-1024
   2. 系统限制：最大文件句柄数（参考/etc/security/limits.conf），可调整，最大 21 亿
   3. 资源限制：内存等资源有限，例如连接本身占用资源，Netty本身的socket相关的对象也占用jvm，需要根据机器做测试。

* 服务端

1. 理论值：最大连接数 = 客户端数量（IP地址数量） \* 单个客户端的最多连接数（约64K），不考虑资源限制，最多 21 亿，实际以资源限制为准, **100 万连接就要占用 3G** 以上了
2. 实际值
   1. TCP层： IPv4使用32位（4字节）地址，因此地址空间中只有4,294,967,296（约43亿），所以乘以单个客户端最大64K, 数量惊人
   2. 系统限制：同上，最大 21 亿
   3. 资源限制：同上

总结： 

1. 对于客户端，6 万多点，对于服务器，100 万到 1000 万，再多，内存就要 30G 以上了，所以网上经常说百万连接。当然，如果你机器内存1G不到的话，那也搞不了了。 
2. 单纯看连接多少意义不是很大，因为连接是为了做事情，光能连上很多，但是占用资源过大导致基本已经不能动弹的话，意义就不大了，不过这个问题本身有趣

#### 延伸知识

```bash
### 1. ip_local_port_range
# 查看系统默认的 ip_local_port_range
#  系统中的程序会选择这个范围内的端口来连接到目的端口
vagrant@ubuntu-bionic:~$ cat /proc/sys/net/ipv4/ip_local_port_range
32768	60999

# 这个值也是可以修改的，具体需要使用到 sysctl, sysctl 是一个用来在系统运作中查看及调整系统参数的工具。
# 有的 sysctl 参数只是用来查看目前的系统状况，
# 例如查看目前已开机时间、所使用的操作系统版本、核心名称等等；而有的可以让我们修改参数以调整系统运作的行为，
# 例如网络暂存内存的大小、最大的上线人数等等。
# /etc/sysctl.conf就是sysctl的配置文件,而这些可以调整的参数中必须在一开机系统执行其它程序前就设定好，
# 有的可以在开机完后任意调整。同大多数配置文件一样,我们可以对sysctl.conf进行配置来优化系统的性能．

# 可以查看当前系统
sysctl -a

# 修改 ip_local_port_range
vim /etc/sysctl.conf
# 添加下面的行
net.ipv4.ip_local_port_range = 32768 59000

# 让配置生效
sysctl -p /etc/sysctl.conf
```

1. \*\*\*\*[**net.ipv4.ip\_local\_port\_range 的值究竟影响了啥**](https://mozillazg.com/2019/05/linux-what-net.ipv4.ip_local_port_range-effect-or-mean.html)\*\*\*\*
2. \*\*\*\*[**https://www.kernel.org/doc/Documentation/networking/ip-sysctl.txt**](https://www.kernel.org/doc/Documentation/networking/ip-sysctl.txt)\*\*\*\*

* 服务器支持1200万连接的案例 \([12 Million Concurrent Connections with MigratoryData WebSocket Server](https://migratorydata.com/2013/06/20/12-million-concurrent-connections-with-migratorydata-websocket-server/)\)

对 1200 万的并发连接在单机服务器上做了基准测试，给出了资源利用率和单机服务器的配置；1200 万连接大概使用了 60% CPU，54G 内存（这个内存是JVM占用的，实际连接占用的内存在36G左右）

* 最大文件句柄数的最大值受另外一个参数控制

```bash
# 系统限制的最大文件句柄数
vagrant@ubuntu-bionic:~$ cat /proc/sys/fs/nr_open
1048576

# 如果超过，就会报错
vagrant@ubuntu-bionic:~$ ulimit -Hn 9000000
-bash: ulimit: open files: cannot modify limit: Operation not permitted

# 把值修改的更大一些
root@ubuntu-bionic:~# sysctl -w fs.nr_open=100000000
fs.nr_open = 100000000
```

那这参数最大值，可以到多少呢？2147483584 （即7FFFFFC0，也就是在MAXINT（2147483647）基础上按64字节对齐）

* **linux系统下，一个socket连接一般占用 3K, 所以 100 万连接至少需要 3G，而 1000 万就要 30G 了**

