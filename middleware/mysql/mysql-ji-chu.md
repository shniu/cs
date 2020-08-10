# MySQL 基础

* MySQL Install on MacOS 

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

* 主从同步配置

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

