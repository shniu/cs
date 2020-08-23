# MySQL 基础

### MySQL 基础

#### MySQL Install on MacOS 

使用可解压的二进制安装包，安装 MySQL \([Installing MySQL](https://dev.mysql.com/doc/refman/8.0/en/binary-installation.html#binary-installation-createsysuser)\)，第一次启动需要初始化数据目录，参考这里：[Init the data directory ](https://dev.mysql.com/doc/refman/8.0/en/data-directory-initialization.html)on MySQL Document

```bash
$ cd mysql-master

# Init the data dir first
# bin/mysqld --defaults-file=./my.cnf --initialize --user=mysql
$ bin/mysqld --initialize --user=mysql
A temporary password is generated for root@localhost: r_/(wD0p3;Im

# bin/mysqld --defaults-file=/etc/my.cnf &
$ bin/mysqld --defaults-file=/etc/my.cnf

# test Server
$ bin/mysqladmin -S /var/run/mysqld/mysqld.sock -u root -p version

$ bin/mysqladmin -S /var/run/mysqld/mysqld.sock -u root -p shutdown

$ bin/mysqlshow -S /var/run/mysqld/mysqld.sock -u root -p

$ bin/mysqlshow -S /var/run/mysqld/mysqld.sock -u root -p mysql

$ bin/mysql --defaults-file=/etc/my.cnf -u root -p -e "select host, user from user" mysql


# Another terminal
$ bin/mysql -S /var/run/mysqld/mysqld.sock -P 3308 -u root -p

# alter user root@'127.0.0.1' IDENTIFIED WITH caching_sha2_password BY '123456';
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY '123456';
mysql> CREATE USER 'root'@'127.0.0.1' IDENTIFIED BY 'root-password';
mysql> CREATE USER 'root'@'::1' IDENTIFIED BY 'root-password';
```

#### 主从同步配置

```bash
### Master
# edit my.cnf, add

[mysqld]
server-id = 1
log-bin = mysql-bin

# Restart MySQL server
# Create a slave user
mysql> create user 'slave'@'%' identified by '123456';
mysql> grant REPLICATION SLAVE, REPLICATION CLIENT on *.* to 'slave'@'%';

# Get File and Position
mysql> show master status; 

# 备份数据
$ bin/mysqldump -h 127.0.0.1 -P 3306 -u root -p --databases test > test.dump

# 恢复数据
$ bin/mysql -h 127.0.0.1 -P 3308 -u root -p < test.dump

### Slave1
# edit my.cnf
[mysqld]
server-id = 10
log-bin = mysql-slave-bin
relay-log = mysql-relay-bin

# Restart MySQL server
# bind Master-Slave
# --get-server-public-key
mysql> change master to master_host='127.0.0.1', master_user='slave', master_password='123456', master_port=3308, master_log_file='mysql-bin.000001', master_log_pos=712, master_connect_retry=30;

```

**Note:** MySQL 8.0 配置主从同步和之前的版本有些区别，需要特别注意。 

### MySQL 特性

#### 字符集与比较规则

字符集：ASCII / ISO 8859-1\(latin1\) / GB2312 / GBK / utf8

```text
// 查看支持的字符集
show charset;
show CHARACTER SET;

// 查看比较规则
show collation [like 匹配模式];
show collation like 'utf8\_%';
```

使用的这个`MySQL`版本一共支持`41`种字符集，其中的`Default collation`列表示这种字符集中一种默认的`比较规则`。

* 比较规则名称以与其关联的字符集的名称开头。如上图的查询结果的比较规则名称都是以`utf8`开头的。
* 后边紧跟着该比较规则主要作用于哪种语言，比如`utf8_polish_ci`表示以波兰语的规则比较，`utf8_spanish_ci`是以西班牙语的规则比较，`utf8_general_ci`是一种通用的比较规则。
* 名称后缀意味着该比较规则是否区分语言中的重音、大小写; 

| 后缀 | 英文释义 | 描述 |
| :--- | :--- | :--- |


| `_ai` | `accent insensitive` | 不区分重音 |
| :--- | :--- | :--- |


| `_as` | `accent sensitive` | 区分重音 |
| :--- | :--- | :--- |


| `_ci` | `case insensitive` | 不区分大小写 |
| :--- | :--- | :--- |


| `_cs` | `case sensitive` | 区分大小写 |
| :--- | :--- | :--- |


| `_bin` | `binary` | 以二进制方式比较 |
| :--- | :--- | :--- |


每种字符集对应若干种比较规则，每种字符集都有一种默认的比较规则，`SHOW COLLATION`的返回结果中的`Default`列的值为`YES`的就是该字符集的默认比较规则，比方说`utf8`字符集默认的比较规则就是`utf8_general_ci`。

```text
// 查看默认字符集
show variables like 'character_set_server';

// 查看默认排序规则
show variables like 'collation_server';


```

字符集支持各种级别的配置：服务器级别 -&gt; 数据库级别 -&gt; 表级别 -&gt; 列级别

