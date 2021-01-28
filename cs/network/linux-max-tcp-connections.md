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



