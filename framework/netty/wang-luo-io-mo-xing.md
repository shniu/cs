# 网络 IO 模型

IO 是输入输出，对于计算机来讲，IO 是基本组成，通过 IO 来和外界进行交互，比如通过网卡收发数据，使用磁盘持久化数据，使用显示器观看视频，使用键盘输入信息，使用打印机打印东西，使用麦克风讲话等，都属于 IO 的范畴；在计算机中，CPU 是指挥者，它是一个高速运算的部件，和 IO 设备之间通过 IO 接口进行连接，CPU 向 IO 接口下达指令来控制 IO 外设，以磁盘为例，CPU 操作磁盘读写数据，在 Linux 系统中的大致做法是数据从磁盘加载到内核的高速缓冲区，然后内核高速缓冲区拷贝到用户空间，用户空间处理完数据后再拷贝回内核缓冲区，然后有 OS 将脏页刷回磁盘。

而对于网络，网络的 IO 过程还是比较复杂的，网络处理程序处理网络请求方式的不同可以分为几种 IO 模型，其中 IO 模型需要借助 OS 的能力。

* 阻塞 IO

![&#x963B;&#x585E; IO](../../.gitbook/assets/image%20%28107%29.png)

阻塞 IO 的工作模式是应用程序线程需要从网络中读取数据，会发起 read 系统调用，进入内核态，试图从内核缓冲区\(有可能是 Socket 缓冲区\)中读取数据，如果缓冲区空或者数据没有 Ready\(有可能还没有构成一个完整的数据包\)，这时应用程序线程就会进入等待状态，直到数据准备好后，由内核唤醒应用程序线程，应用程序线程会进入就绪队列竞争 CPU，获得 CPU 使用权之后，应用程序线程工作在内核态，然后 Copy 内核缓冲区的数据到用户空间的内存中，然后应用程序线程切换回用户态，继续执行后续的指令。

可见，整个读取数据的过程，线程一直被占用，不能去处理其他的事情，只有等待、等待...，可见阻塞 IO 模型只适用于简单、并发低等的场景中，高性能高并发的场景是无法应对的，因为高并发的场景需要处理大量的连接，使用阻塞 IO 就需要大量的线程去处理，不仅大量线程会占用大量资源，而且线程上下文切换也会非常频繁，最终导致 CPU 利用率下降，整体资源的利用率也下降。

阻塞 IO 在编码时，最常用的两种模式是：

1. 利用一个线程处理所有的事情，用户连接处理和连接的读写处理
2. 利用一个线程来接收连接，和一个线程池来处理连接的读写，一般这种是比较常用的

用 Java 程序来理解一下阻塞 IO

```java
public class SocketServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);
        
        while (true) {
            System.out.println("Waiting...");
            // accept 是一个阻塞方法
            Socket clientSocket = serverSocket.accept();
            System.out.println("Accept a connection.");
            
            handler(clientSocket);

            // new Thread 是使用另外一个线程来处理连接的读写
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        handler(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();*/
        }
    }

    private static void handler(Socket clientSocket) throws IOException {
        byte[] bytes = new byte[1024];
        System.out.println("Start to read");
        // 接收客户端的数据，阻塞方法，没有数据可读时就阻塞
        int read = clientSocket.getInputStream().read(bytes);
        System.out.println("Read completed");
        if (read != -1) {
            System.out.println("Data sent by client：" + new String(bytes, 0, read));
        }
        
        clientSocket.getOutputStream().write("HelloClient".getBytes());
        clientSocket.getOutputStream().flush();
    }
}
```

* 非阻塞 IO

![&#x975E;&#x963B;&#x585E; IO](../../.gitbook/assets/image%20%28100%29.png)

和阻塞 IO 相比，非阻塞 IO 在数据准备阶段，不需要一直等待，而是不断的去轮询数据的可用状态，但是在数据准备好之后（所谓的数据准备好可以理解为网络中的数据帧被网卡接收到后，经过网络协议栈的处理形成数据包放入 Socket 缓冲区或者内核缓冲区，等待被处理），由应用程序线程将数据从内核缓冲区中取走，放入自己的内存缓冲区中，Copy 完成后，线程切换回用户态，继续执行后续的指令。

非阻塞 IO 虽然不用一直阻塞，但是需要不断的去轮询数据状态，CPU 大部分时间可能在空转，一定程度上也降低了 CPU 的利用率，在数据 Copy 阶段同样是阻塞的。

想象一下，如果我们由成千上万的 Socket 要处理，我们要对每个 Socket 都去轮询一遍，还是很耗资源的，可能 CPU 啥事也做不了了，也有可能 Socket 中的数据得不到及时处理，造成延迟，还会白白的占用系统资源得不到释放。所以说这种方式还是无法处理大量连接的情况。

如果连接数太多的话，会有大量的无效遍历，假如有10000个连接，其中只有1000个连接有写数据，但是由于其他9000个连接并没有断开，我们还是要每次轮询遍历一万次，其中有十分之九的遍历都是无效的，这显然不是一个让人很满意的状态。

用 Java 程序来理解一下非阻塞 IO：

```java
public class NioServer {

    // Save client channels
    static List<SocketChannel> channelList = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        // Create NIO ServerSocketChannel, same as BIO serverSocket
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(9000));
        
        // 设置 ServerSocketChannel 为非阻塞, 非常关键
        serverSocket.configureBlocking(false);
        System.out.println("Server started.");

        while (true) {
            // 非阻塞模式 accept 方法不会阻塞，否则会阻塞
            // NIO 的非阻塞是由操作系统内部实现的，底层调用了 linux 内核的 accept 函数
            SocketChannel socketChannel = serverSocket.accept();
            
            // 如果有客户端进行连接
            if (socketChannel != null) { 
                System.out.println("Connection established.");
                // 设置 SocketChannel 为非阻塞
                socketChannel.configureBlocking(false);
                channelList.add(socketChannel);
            }
            
            // 遍历连接进行数据读取, 这里就是非阻塞 IO 的轮询遍历，一直检查是否有数据准备好
            Iterator<SocketChannel> iterator = channelList.iterator();
            while (iterator.hasNext()) {
                SocketChannel sc = iterator.next();
                ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                
                // 非阻塞模式 read 方法不会阻塞，否则会阻塞
                int len = sc.read(byteBuffer);
                // 如果有数据，把数据打印出来
                if (len > 0) {
                    System.out.println("Received：" + new String(byteBuffer.array()));
                } 
                // 如果客户端断开，把 socket 从集合中去掉
                else if (len == -1) { 
                    iterator.remove();
                    System.out.println("Client connection closed.");
                }
            }
        }
    }
}
```

* IO 复用
* 信号驱动 IO
* 异步 IO

