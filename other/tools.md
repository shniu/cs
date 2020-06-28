# 工具

### Git

* [Learn Git Branching](https://github.com/pcottle/learnGitBranching)，含有动画演示，非常生动
* [卧槽！小姐姐用动画图解 Git 命令，这也太秀了吧？！](http://mp.weixin.qq.com/s?__biz=MzAxOTcxNTIwNQ==&mid=2457919169&idx=2&sn=7514209811adbd09b6161093e8ae3eb4&chksm=8cb6bb2bbbc1323dc0cd1c9110fcc6a2a06774040586fc21a01db98129a03ece8ee4cdb73960&scene=21#wechat_redirect)
* [Linus 在 Google 分享了 Git 的设计思路，顺带怼了一大波人（视频）](https://mp.weixin.qq.com/s?__biz=MzAxOTcxNTIwNQ==&mid=2457915907&idx=1&sn=7f39b7943bf0e9ba4a2b12b47d4a70d7&scene=21#wechat_redirect)
* [用好这几个工具，能大幅提升你的 Git/GitHub 操作效率！](https://mp.weixin.qq.com/s?__biz=MzAxOTcxNTIwNQ==&mid=2457915558&idx=1&sn=de0cdcb9fb199162ffe565e371b3dbf4&scene=21#wechat_redirect)
* [强烈推荐下 GitHub 官方的这个教程](https://mp.weixin.qq.com/s?__biz=MzAxOTcxNTIwNQ==&mid=2457914680&idx=1&sn=0061f76dbd0e33468216a460c624c2b4&scene=21#wechat_redirect)
* [收好这份 Git 命令应急手册，关键时刻可保你一命](https://mp.weixin.qq.com/s?__biz=MzAxOTcxNTIwNQ==&mid=2457914802&idx=1&sn=a8d2cb9b626da84d94d8b2ebd9e85c24&scene=21#wechat_redirect)
* [Git Book](https://git-scm.com/book/zh/v2/%E8%B5%B7%E6%AD%A5-%E5%85%B3%E4%BA%8E%E7%89%88%E6%9C%AC%E6%8E%A7%E5%88%B6) 关于 Git 的书

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



### OpenAPI Site

* [Public APIs](https://public-apis.xyz/)
* [Github Public APIs](https://github.com/public-apis/public-apis)
* Open API [any-api](https://any-api.com/)
* [API List ](https://apilist.fun/)

### Maven



#### 项目模版

关于项目模版，有一个 [cookiecutter](https://github.com/cookiecutter/cookiecutter) 的项目。

比较有用的项目模版： 1. [cookiecutter golang](https://github.com/lacion/cookiecutter-golang) 2. [cookiecutter-data-science](https://github.com/drivendata/cookiecutter-data-science)

#### 实践

* [后端开发实践系列——开发者的第0个迭代](https://mp.weixin.qq.com/s/uMB0nYc_c_lA0CHSqy3q4w)





