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

