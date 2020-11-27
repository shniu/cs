# 工具

### Git

* [Learn Git Branching](https://github.com/pcottle/learnGitBranching)，含有动画演示，非常生动
* [卧槽！小姐姐用动画图解 Git 命令，这也太秀了吧？！](http://mp.weixin.qq.com/s?__biz=MzAxOTcxNTIwNQ==&mid=2457919169&idx=2&sn=7514209811adbd09b6161093e8ae3eb4&chksm=8cb6bb2bbbc1323dc0cd1c9110fcc6a2a06774040586fc21a01db98129a03ece8ee4cdb73960&scene=21#wechat_redirect)
* [Linus 在 Google 分享了 Git 的设计思路，顺带怼了一大波人（视频）](https://mp.weixin.qq.com/s?__biz=MzAxOTcxNTIwNQ==&mid=2457915907&idx=1&sn=7f39b7943bf0e9ba4a2b12b47d4a70d7&scene=21#wechat_redirect)
* [用好这几个工具，能大幅提升你的 Git/GitHub 操作效率！](https://mp.weixin.qq.com/s?__biz=MzAxOTcxNTIwNQ==&mid=2457915558&idx=1&sn=de0cdcb9fb199162ffe565e371b3dbf4&scene=21#wechat_redirect)
* [强烈推荐下 GitHub 官方的这个教程](https://mp.weixin.qq.com/s?__biz=MzAxOTcxNTIwNQ==&mid=2457914680&idx=1&sn=0061f76dbd0e33468216a460c624c2b4&scene=21#wechat_redirect)
* [收好这份 Git 命令应急手册，关键时刻可保你一命](https://mp.weixin.qq.com/s?__biz=MzAxOTcxNTIwNQ==&mid=2457914802&idx=1&sn=a8d2cb9b626da84d94d8b2ebd9e85c24&scene=21#wechat_redirect)
* [Git Book](https://git-scm.com/book/zh/v2/%E8%B5%B7%E6%AD%A5-%E5%85%B3%E4%BA%8E%E7%89%88%E6%9C%AC%E6%8E%A7%E5%88%B6) 关于 Git 的书
* [Git 工作流，如何正确的合并自己的代码到某个分支](https://blog.wj2015.com/2019/07/23/git%E5%B7%A5%E4%BD%9C%E6%B5%81%EF%BC%8C%E5%A6%82%E4%BD%95%E6%AD%A3%E7%A1%AE%E7%9A%84%E5%90%88%E5%B9%B6%E8%87%AA%E5%B7%B1%E7%9A%84%E4%BB%A3%E7%A0%81%E5%88%B0%E6%9F%90%E4%B8%AA%E5%88%86%E6%94%AF/)
* [git-scm: git 分支](https://git-scm.com/book/zh/v2/Git-%E5%88%86%E6%94%AF-%E5%88%86%E6%94%AF%E7%AE%80%E4%BB%8B)
* [git pull 和 git fetch](https://juejin.im/post/6844903921794859021)
* [git checkout tags, very detail](https://devconnected.com/how-to-checkout-git-tags/)
* [https://www.educative.io/edpresso/how-to-delete-remote-branches-in-git](https://www.educative.io/edpresso/how-to-delete-remote-branches-in-git)
* [https://devconnected.com/how-to-delete-local-and-remote-tags-on-git/](https://devconnected.com/how-to-delete-local-and-remote-tags-on-git/)
* [github fork 后如何 sync](https://www.jianshu.com/p/199733864fe7)

```bash
// --- 回退 merge
// 查看 操作记录，找到要回退的版本号
$ git reflog
3973c178 (HEAD -> test, origin/test) HEAD@{0}: merge feature-34: Merge made by the 'recursive' strategy.
58ed7d9b HEAD@{1}: checkout: moving from feature-34 to test
09140632 (origin/feature-34, feature-34) HEAD@{2}: commit:
....

// 执行 git reset
$ git reset --hard HEAD^
// or
$ git reset --hard HEAD@{2}
// or
$ git reset --hard 09140632

//// 另外还可以使用 git revert

// ---
git merge
git rebase

// The error is resolved by toggling the allow-unrelated-histories switch.
// https://www.educative.io/edpresso/the-fatal-refusing-to-merge-unrelated-histories-git-error
git pull origin master --allow-unrelated-histories
```

### Gradle 

使用 Gradle 构建 Spring 的项目，关于 Spring 的配置依赖

1. [Spring Dependency Management Plugin](https://docs.spring.io/dependency-management-plugin/docs/current-SNAPSHOT/reference/html/)
2. [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/)
3. [Spring Dependency Gradle Plugin](https://plugins.gradle.org/plugin/io.spring.dependency-management)

```text
// https://www.jianshu.com/p/01588c396a29
// buildscript
buildscript {
    repositories {
        maven {
            // url 'https://repo.spring.io/libs-milestone'
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath("io.spring.gradle:dependency-management-plugin:1.0.9.RELEASE")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
        classpath("gradle.plugin.com.gorylenko.gradle-git-properties:gradle-git-properties:1.4.17")
        classpath("gradle.plugin.com.palantir.gradle.docker:gradle-docker:0.13.0")
    }
}
apply plugin: 'org.springframework.boot'
// 会提供 bootJar task
apply plugin: 'java'
apply plugin: 'groovy'
// 当使用了该插件，Spring Boot的插件会自动地从你使用的Spring Boot
// 版本里导入spring-boot-dependencies bom
// 在声明依赖时，不需要带版本号
apply plugin: 'io.spring.dependency-management'

// ---
// ext 可以用来扩展属性
ext['slf4j.version'] = '1.7.20'
// or
ext {
    slf4jVersion = '1.7.20'
}

// bootJar
// springBoot
// maven 插件发布
// maven-publish 发布

```

* 更新 gradle 的版本

```bash
$ ./gradlew wrapper --gradle-version=6.2.1 --distribution-type=all
```

### OpenAPI Site

* [Public APIs](https://public-apis.xyz/)
* [Github Public APIs](https://github.com/public-apis/public-apis)
* Open API [any-api](https://any-api.com/)
* [API List ](https://apilist.fun/)

### Maven

1. [发布自己的 Java 类库到 Maven 中央仓库](https://segmentfault.com/a/1190000018026290)
2. [https://oss.sonatype.org/](https://oss.sonatype.org/)
3. [https://issues.sonatype.org/browse/OSSRH-61341](https://issues.sonatype.org/browse/OSSRH-61341)

### Shell

* [https://linuxcommand.org/lc3\_wss0120.php](https://linuxcommand.org/lc3_wss0120.php)
* [如何编写 shell 脚本](https://linuxcommand.org/lc3_writing_shell_scripts.php#contents)

### Java Libs

#### Lombok

* [Lombok Log Annotations](http://www.javabyexamples.com/lombok-log4j-slf4j-and-other-log-annotations)

#### 开发工具

[阿里巴巴程序员常用的开发工具](https://mp.weixin.qq.com/s/D7TpMYgcpZh5FA2qzv-vTA)

* Arthas: Java 线上诊断工具
* Java 代码规约，[alibaba/p3c](https://github.com/alibaba/p3c)
* 应用实时监控工具 ARMS，提供前端、应用、自定义监控，可快速构建实时的应用性能和业务监控能力；[应用端监控接入](https://help.aliyun.com/documentdetail/63796.html)
* [Docsite ](https://github.com/txd-team/docsite), [中文版文档](https://docsite.js.org/zh-cn/docs/installation.html)，一款集官网、文档、博客和社区为一体的静态开源站点的解决方案，具有简单易上手、上手不撒手的特质，同时支持 react 和静态渲染、PC端和移动端、支持中英文国际化、SEO、markdown文档、全局站点搜索、站点风格自定义、页面自定义等功能。
* 性能测试工具 [PTS](https://www.aliyun.com/product/pts)
* 云效开发者工具[ KT](https://yq.aliyun.com/download/3393)，简化在 Kubernetes 下进行联调测试的复杂度，提高基于Kubernetes的研发效率；[教程](https://yq.aliyun.com/articles/690519)，这是一款好工具
* 架构可视化 [AHAS](https://www.aliyun.com/product/ahas)，为 K8s 等容器环境提供了架构可视化的功能，同时，具有故障注入式高可用能力评测和一键流控降级等功能，可以快速低成本的提升应用可用性
* 数据处理工具 [EasyExcel](https://github.com/alibaba/easyexcel)
* 数据库连接池 [Druid](https://github.com/alibaba/druid/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98)，Java 语言下的数据库连接池，它能够提供强大的监控和扩展功能



#### 项目模版

关于项目模版，有一个 [cookiecutter](https://github.com/cookiecutter/cookiecutter) 的项目。

比较有用的项目模版： 1. [cookiecutter golang](https://github.com/lacion/cookiecutter-golang) 2. [cookiecutter-data-science](https://github.com/drivendata/cookiecutter-data-science)

#### 实践

* [后端开发实践系列——开发者的第0个迭代](https://mp.weixin.qq.com/s/uMB0nYc_c_lA0CHSqy3q4w)

### UML

* [UML 各种图总结](https://zhuanlan.zhihu.com/p/44518805)

### Vagrant

HashiCorp Vagrant provides the same, easy workflow regardless of your role as a developer, operator, or designer. It leverages a declarative configuration file which describes all your software requirements, packages, operating system configuration, users, and more.

Vagrant 是一个非常好用的工具，仅仅声明一个定义文件就可以获得一个相同的环境；在开发阶段利用 Vagrant 可以获得相同的开发环境，屏蔽不同环境带来的差异；Vagrant 底层是依赖于 VM 虚拟机的能力

* [https://www.vagrantup.com/](https://www.vagrantup.com/)
* Box: [https://app.vagrantup.com/boxes/search](https://app.vagrantup.com/boxes/search)
* Introduction: [https://www.vagrantup.com/intro](https://www.vagrantup.com/intro)
* Doc: [https://www.vagrantup.com/docs](https://www.vagrantup.com/docs)
* Tutorial: [https://learn.hashicorp.com/vagrant](https://learn.hashicorp.com/vagrant)

#### 使用 Vagrant 时，如何自动映射以及同步宿主机和虚拟机之间的目录和文件？

在 Vagrantfile 中声明 `config.vm.synced_folder ".", "/vagrant", type: "rsync", rsync__exclude: ".git/"` ，rsync 默认开启了 `rsync__auto=true` ，但是如果需要自动双向同步修改后的内容，需要在宿主机上运行 `vagrant rsync-auto` 



