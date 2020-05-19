# Java 8 新特性

Java 8 带来的改变是非常深远的，Java8 的新特性：

1. Stream 与并行处理
2. 行为参数化
3. Lambda 表达式
4. 函数式编程
5. 更好的异步编程方式
6. Optional
7. 默认方法
8. 日期和时间API

### 行为参数化

行为参数化是一种能够帮助你处理频繁变更的需求的一种软件开发模式。我们可以准备好一块代码而不立即执行它，而是在其他程序调用到这些代码时才被执行，这块代码就代表了需要完成的某种特定行为，这个行为可以作为参数传递给另外一个方法，也就是说这个行为基于那块代码被参数化了。

一个方法接收多个不同的行为作为参数，并在内部使用他们，完成不同行为的能力。

```java
// 行为参数化的一个直观的例子
// 需求：现在有一堆苹果，能根据不同的筛选条件来筛选苹果，比如根据颜色、大小、重量、品种、产地等
//      进行筛选，也可以根据他们的不同组合新型筛选
// 如何做？
public interface Predicate {
    boolean test(Apple apple);
}

public class WeightPredicate implements Predicate {
    public boolean test(Apple apple) {
        return apple.getWeight() > 100;
    }
}

public class ColorPredicate implements Predicate {
    public boolean test(Apple apple) {
        return "red".equals(apple.getColor());
    }
}

List<Apple> filter(List<Apple> inventory, Predicate p) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
        if (p.test(apple) {
            result.add(apple);
        }
    }
    return result;
}

inventory = filter(inventory, new WeightPredicate());
inventory = filter(inventory, new ColorPredicate());

// 这里的 Predicate 就是对行为的抽象，比如过滤红色的、重量大于100g的等
// 通过将 Predicate 当作参数传给 filter 来实现筛选的能力
// 同时根据需要可以实现不同的 Predicate，来满足不同的功能
// 实际上是把筛选的逻辑的逻辑通过行为参数化了，这就是行为参数化
```

行为参数化的最大好处是可以使用相同的代码模版应对多变的需求

### Lambda

Lambda 的一个核心能力是简化代码，同时他可以作为参数传递给方法和变量。

```java
// Lambda 表达式
Comparator<Apple> byWeight = (Apple a1, Apple a2) -> 
    a1.getWeight().compareTo(a2.getWeight); 
```

Lambda 一般配合行为参数化、函数式接口一起使用，函数式接口的抽象方法的签名基本就是 Lambda 的签名，成为函数描述符。

Java 8 引入的几个函数式接口，主要作用是对常用的场景做的抽象

```java
// Predicate
public interface Predicate<T> {
    boolean test(T t);
}

// Consumer
public interface Consumer<T> {
    void accept(T t);
}

// Function
public interface Function<T, R> {
    R apply(T t);
}

// Supplier
public interface Supplier<T> {
    T get();
}

// IntPredicate, 针对基本数据类型做了特殊处理，避免了自动装箱、拆箱的过程
// 一定程度上提升了性能，基本数据类型都提供了特殊的函数式处理接口
public interface IntPredicate {
    boolean test(int t);
}
```

### 流

流允许以声明的方式处理数据集合，同时还支持透明的并行处理。（声明性，可复合，可并行）

流可以理解为从支持数据处理操作的源生成的元素序列，流偏重于计算，集合偏重于数据，所以流的核心能力是数据处理操作，如 filter, map, reduce, sort, match, find, limit, distinct 等，流处理完成后可以返回一个流，作为下一个处理的输入，这样就构建了一个流水线；流的迭代是在背后默默进行的

