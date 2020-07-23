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

* 事务相关

```bash
# 登录MySQL后执行
### 事务通用

# 开启一个事务，在执行到第一个语句时才真正启动成功
#  一致性视图是在执行第一个快照读语句时创建的
mysql> start transaction;
mysql> begin;

# 开启一个事务
# 一致性视图是在执行 start transaction with consistent snapshot 时创建的
mysql> start transaction with consistent snapshot;

# 查看隔离级别
mysql> show variables like 'transaction_isolation';

mysql> select @@tx_isolation;
```

* 锁相关

```bash
### 锁
# 查询时加锁

# 对当前记录加 X 锁，任何其他语句都需要阻塞等待
mysql> select * from user where uid = 101 for update;

# 对当前记录加 S 锁
mysql> select * from user where uid = 101 lock in share mode;
```

