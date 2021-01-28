# Linux 最多支撑的 TCP 连接

Linux 能支撑的最大连接数受到多个因素限制：

* 表示一个 TCP 连接的四元组

四元组是 \(Source IP, Source Port, Target IP, Target Port\)，作为服务端 IP 和端口是确定的，所以最大是 2^32 \* 2^16，这个理论值是很大的

* Linux 系统可打开的文件数限制，fs.file-max 来控制
* Linux 指定用户可打开的文件数限制，/etc/security/limits.conf 来控制
* Linux 单个进程可打开的文件数限制，fs.nr\_open 来控制
* 内存大小，每个 TCP 连接大约占用 3KB 左右的内存，这还不包括发送数据和接收数据要额外分配的内存

```bash
# 查看需要额外为每个 TCP 连接还需要分配的接收缓冲区
$ sysctl -a | grep rmem
net.core.rmem_default = 212992
net.core.rmem_max = 212992
net.ipv4.tcp_rmem = 4096	131072	6291456
net.ipv4.udp_rmem_min = 4096
```

`net.ipv4.tcp_rmem = 4096 131072 6291456` 表示需要最少分配 4KB，最多分配 6MB

```bash
# 发送缓冲区需要分配的大小，最小也是 4KB
$ sysctl -a | grep wmem
net.core.wmem_default = 212992
net.core.wmem_max = 212992
net.ipv4.tcp_wmem = 4096	16384	4194304
net.ipv4.udp_wmem_min = 4096
vm.lowmem_reserve_ratio = 256	256	32	0

# 查看活动连接数
$ ss -n | grep ESTAB | wc -l

# 查看内存详细信息
$ cat /proc/meminfo

# 查看活动对象
$ slabtop
```



### 一个服务器或者客户端能创建多少连接？

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

### 总结

一个服务器最大能支撑多少连接受制于很多因素，在内存充足的情况下，一台服务器可以支撑千万级并发连接，这只是空连接，还不做很多的业务收发数据，支撑千万级连接至少需要 30 G内存在连接的建立上，最起码还需要 20 ～ 30 G 的内存来支撑业务，所以单机内存要 64G 才能相对正常的跑业务。

