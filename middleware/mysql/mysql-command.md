# MySQL 命令

* MySQL 命令行

```bash
### mysql
# 连接 MySQL Server
$ bin/mysql -h 127.0.0.1 -P 3306 -u root -p

# 导入数据
$ bin/mysql -h 127.0.0.1 -P 3306 -u root -p < test.dump

### mysqladmin

### mysqldump
```

* 事务隔离级别

```bash
# 登录MySQL后执行
# 查看隔离级别
mysql> show variables like 'transaction_isolation';

mysql> select @@tx_isolation;
```



