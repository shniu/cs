# MySQL - Analysis Tool



### InnoDB 文件分析

我们可以使用 innodb\_ruby 工具来分析 InnoDB 的相关文件

```bash
# innodb options
-f 加载表空间，如ibd文件
-s 加载系统表空间，如ibd
-T 指定表名
-I 指定索引名

# 列出所有物理对象的数量
$ innodb_space -s ibdata1 system-spaces

#
innodb_space -f t.ibd space-page-type-regions
```



### 参考资源

* [innodb diagrams](https://github.com/jeremycole/innodb_diagrams) Diagrams for InnoDB data structures and behaviors
* [innodb\_ruby](https://github.com/jeremycole/innodb_ruby) A parser for InnoDB file formats
* [innodb\_ruby 使用分析](https://juejin.im/post/6844903844107780103)
* [Introduction innodb\_ruby](https://blog.jcole.us/2013/01/03/a-quick-introduction-to-innodb-ruby/)
* [关于 Innodb 内部设计](https://blog.jcole.us/innodb/)，不懂就可以看这个



