# JVM Bytecode

字节码是 JVM 虚拟机规范的一部分，它是 Java 实现一次编译到处运行的关键，它连接了 Java 源码和机器码，所以掌握字节码对于理解 JVM 虚拟机的运行机制非常有帮助。

在 JVM 的虚拟机规范中有关于字节码类文件的定义，参看 [Java Virtual Machine Specification / The class file format](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html). 字节码文件结构是一组以 8 字节为基础的二进制流，各数据严格按照顺序紧凑的排列在文件中，中间并没有特殊的分隔符，如何识别不同的组（或者说不同的部分）？使用的是长度标识的方法。下面是字节码文件结构的大致组成部分：

![&#x5B57;&#x8282;&#x7801;&#x6587;&#x4EF6;&#x7EC4;&#x6210;&#x90E8;&#x5206;](../../../.gitbook/assets/image%20%28128%29.png)

通过一段程序来分析:

```java
// Java 代码如下
package io.github.shniu.toolbox;

public class BytecodeTest {
    private static final int MAX_COUNT = 8;
    private Reader reader;

    public BytecodeTest() {
        this.reader = new BytecodeReader();
    }

    public void startup() {
        byte[] bytes = new byte[MAX_COUNT];
        reader.read(bytes);
        System.out.println(new String(bytes));
    }

    public static void main(String[] args) {
        BytecodeTest bytecodeTest = new BytecodeTest();
        bytecodeTest.startup();
        System.out.println("Bytecode finished.");
    }
}

class BytecodeReader implements Reader {
    @Override
    public void read(byte[] bytes) {
        if (bytes == null) {
            return;
        }

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (i + 1);
        }
    }
}

interface Reader {
    void read(byte[] bytes);
}
```

将上面这段代码编译后，会产生 3 个 `.class` 文件，分别是：`BytecodeTest.class`, `BytecodeReader.class`, `Reader.class`，当我们运行 `BytecodeTest.java` 中的 main 方法时，JVM 的类加载系统会把这三个字节码文件都加载到内存的方法区（这个区域在 1.8 及以后被叫做元空间 Metaspace），类加载的过程也是读取字节码文件，然后在内存中构建 Java 可使用的对象的过程。

来详细分析一下这些字节码文件，分析最复杂的 `BytecodeTest.class` 文件，用 16 进制查看器查看这个文件如下：

![16 &#x8FDB;&#x5236;&#x4E0B;&#x663E;&#x793A;](../../../.gitbook/assets/image%20%28129%29.png)

上面有一张字节码文件的大致组成部分的图，依次来分析

* 最开始的前 4 个字节，是魔数，用来快速判断这是不是一个 Java 的字节码文件，如果不是直接退出或者报错；`CAFEBABE` 这 4 个字节是 Java 的魔数
* 紧接着的 4 个字节 `0000 0034` , 前两个字节是此版本号，后两个字节是主版本号，`0034` 表示是 JDK 1.8
* 接着就是常量池的部分，由常量池个数和常量池表组成
  * 第 9～10 这 2 字节表示常量池中常量的个数，这里是 `0036` ，10 进制是 54，那么常量池表中的数据项就是 53 个，为啥需要 -1，因为常量池中的第0个位置被我们的jvm占用了表示为null 所以我们通过编译出来的常量池索引是从1开始的
  * 紧接着后面的就是常量池表了，逐项分析，在这个常量池表中的每一项都以1个字节长度的 tag 来标记常量的类型
  * 常量池表中的第 1 项，`0A` 表示 `CONSTANT_Methodref_info`, 这种类型的常量后面有 2 字节的类索引 \(`CONSTANT_Class_info`\)和 2 字节的名称及类型描述符\(`CONSTANT_NameAndType_info` \); 所以 `00 0E` 是 14，表示指向常量池中位置是 14 的常量； `00 1E` 是 30, 表示指向常量池中位置是 30 的常量，而且这个常量的类型是 `NameAndType_info` 
  * 第二个常量 07 00 1F, 07 表示 `CONSTANT_Class_info` , 接着的 2 个字节指向权限定类名，指向索引 31，索引 31 处其实是 `31 = Utf8               io/github/shniu/toolbox/BytecodeReader` , 它是一个 Utf8 的字面量
  * 第三个常量 0A 00 02 00 1E，表示是一个 `CONSTANT_Methodref_info` , 分别指向索引 2 和 30，也就是指向 `BytecodeReader` 的无参构造方法

