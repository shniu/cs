# Guava EventBus

Guava EventBus 允许组件之间的发布-订阅式通信，而无需组件之间显式注册。

适用场景：进程内通信，不支持进程间，不是一个通用的发布订阅系统，使用显式注册代替传统的 Java 进程内事件分发，传统的事件分发需要定义 Listener、注册每个事件监听到列表中等

### 使用 EventBus

感受一下 EventBus 的简单与强悍

```java
public class EventBus01 {
    public static void main(String[] args) {
        EventBus eventBus = new EventBus("OrderEventBus");
        eventBus.register(new OrderEventSubscribe());

        eventBus.post(new OrderCreatedEvent("O187312", "120"));
        eventBus.post(new Object());
        eventBus.post(new OrderCancelledEvent("O9999", System.currentTimeMillis()));

        EventBus eventBus1 = new EventBus((exception, context) -> {
            System.out.println(exception);
            System.out.println(context);
        });

        eventBus1.register(new NoopEventSubscribe());
        eventBus1.post(new OrderCreatedEvent("123", "13.0"));
    }

    static class OrderEventSubscribe {
        @Subscribe
        public void handleOrderCreated(OrderCreatedEvent event) {
            System.out.println("Handle 1: " + event);
        }

        @Subscribe
        public void handleOrder2(OrderCreatedEvent event) {
            System.out.println("Handle 2: " + event);
        }

        @Subscribe
        public void handleOrder3(Object event) {
            System.out.println("Handle 3: " + event);
        }
    }

    static class NoopEventSubscribe {
        @Subscribe
        public void handleOrderCreated(DeadEvent event) {
            System.out.println("Dead Handle 1: " + event);
            throw new RuntimeException("Mock");
        }
    }

    @Data
    @ToString
    @AllArgsConstructor
    static class OrderCreatedEvent {
        private String orderId;
        private String amount;
        // ...
    }

    @Data
    @ToString
    @AllArgsConstructor
    static class OrderCancelledEvent {
        private String orderId;
        private long cancelledAt;
        // ...
    }
}
```

事件的订阅者通过 @Subscribe 来标识，是方法级别的。

为什么使用注解标记处理，而不是要求侦听器实现接口？因为注解可以表达接口想要表达的意图，此外注解可以允许你将事件处理程序放在任意想放置的地方，灵活性更好。传统的 Java 事件使用一个监听器接口，有几个缺点：

1. 任何一个类只能对给定事件实现单个响应
2. 侦听器接口方法可能会冲突
3. 该方法必须以事件（例如handleChangeEvent）命名，而不是其用途（例如recordChangeInJournal）命名
4. 每个事件通常都有其自己的接口，而没有用于一系列事件（例如，所有UI事件）的公共父接口

使用传统的 Java 的方式由于难以干净利落地实现，因此产生了一种模式，特别是在 Swing 应用程序中常见的模式，即使用微小的匿名类来实现事件监听器接口。如下：

```java
class ChangeRecorder {
  void setCustomer(Customer cust) {
      // 使用匿名类实现特定的 Listener 接口
    cust.addChangeListener(new ChangeListener() {
      public void customerChanged(ChangeEvent e) {
        recordChange(e.getChange());
      }
    };
  }
}
```

如果使用了 EventBus, 就是如下这样的，对比明显：

```java
// Class is typically registered by the container.
class EventBusChangeRecorder {
    // 使用 EventBus 的注解来实现
  @Subscribe 
  public void recordCustomerChange(ChangeEvent e) {
    recordChange(e.getChange());
  }
}
```

### 理解 EventBus 的设计

EventBus 内部有 4 个重要的组件：executor, exceptionHandler, subscribers, dispatcher

1. subscribers 用来存放所有已经注册到当前 EventBus 的订阅者, 它的实现是一个订阅者注册表 SubscriberRegistry
2. dispatcher 分发事件给订阅者的处理器，提供了不同场景下的不同事件分发顺序的保证，比如 perThreadDispatchQueue 确保每个线程内部发布的事件是有序的；immediate 表示立即消息，当消息发布后会被立即执行；legacyAsync 使用了一个全局队列来存储当前 EventBus 所有已发布的事件，然后分发给 executor 处理
3. executor 任务执行器的抽象，在 EventBus 中承担调用执行订阅者的职责，依赖该抽象既可以实现同步调用订阅者，也可以实现异步调用，满足了不同场景的灵活度，AsyncEventBus 就是一个可以灵活定制 Executor 的可实现异步执行调用者的类
4. exceptionHandler EventBus 提供了一个默认的异常处理器，但自定义的异常处理器可以满足对异常处理的特殊需求

EventBus 发布事件的流程

1. 根据 event 查询事件订阅者列表
2. 如果该事件有订阅者，事件分发器将事件分发给订阅者，其中 Subscriber 表示一个订阅者，包含了目标对象和需要执行的方法，订阅者在执行时会将方法的调用执行委托给 executor，在执行调用订阅者时使用了反射机制；如果在执行过程中出现异常，交给 exceptionHandler 去处理，整个处理结束
3. 如果事件没有订阅者，事件就被当作 DeadedEvent 进行处理，如果注册了 DeadedEvent 的订阅者，那么就会处理 DeadedEvent，否则会丢弃这个事件

#### EventBus 实现

EventBus 的两个核心是 register subscribe 和 post event

* register subscribe

SubscriberRegistry 是注册 Subscriber 的地方，为了实现一个线程安全的 EventBus，SubscriberRegistry 内部使用了 ConcurrentMap 作为存放 Subscriber 的容器，并且在需要更新 Subscriber 时，利用了具有写时复制能力的 CopyOnWriteArraySet，这样在并发注册 Subscriber 时也可以保证线程安全和正确性，之所以使用 CopyOnWriteArraySet 是因为注册订阅者是一个小概率发生的事情，一般情况下都是在启动时注册，运行时使用，一个典型的读多写少的场景。

> Note: 在平常编写程序时，应该多思考具体的应用场景来选择合适的数据结构和算法，这样能最大化程序的性能，同时也让设计和实现变得优雅，这是一个工程师的内核能力。

```java
  /** Registers all subscriber methods on the given listener object. */
void register(Object listener) {
    // 使用 MultiMap 存储 Subscriber, e.g.: 
    //  io.github.demo.OrderCreatedEvent -> [Subscriber1, Subscriber2]
    Multimap<Class<?>, Subscriber> listenerMethods = findAllSubscribers(listener);

    for (Entry<Class<?>, Collection<Subscriber>> entry : listenerMethods.asMap().entrySet()) {
      Class<?> eventType = entry.getKey();
      Collection<Subscriber> eventMethodsInListener = entry.getValue();

      // CopyOnWriteArraySet 存储 Subscriber 的列表，这个列表里的订阅者按顺序处理事件
      CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);

      if (eventSubscribers == null) {
        CopyOnWriteArraySet<Subscriber> newSet = new CopyOnWriteArraySet<>();
        eventSubscribers =
            MoreObjects.firstNonNull(subscribers.putIfAbsent(eventType, newSet), newSet);
      }

      // 添加最新注册的订阅者，使用写时复制机制
      eventSubscribers.addAll(eventMethodsInListener);
    }
}

// 发送事件时，就从 eventSubscribers 中查询订阅者列表，它返回的是一个快照
/**
 * Gets an iterator representing an immutable snapshot of all subscribers to the given event at
 * the time this method is called.
 */
Iterator<Subscriber> getSubscribers(Object event) {
    ImmutableSet<Class<?>> eventTypes = flattenHierarchy(event.getClass());

    List<Iterator<Subscriber>> subscriberIterators =
        Lists.newArrayListWithCapacity(eventTypes.size());

    for (Class<?> eventType : eventTypes) {
        CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);
        if (eventSubscribers != null) {
        // eager no-copy snapshot
        subscriberIterators.add(eventSubscribers.iterator());
        }
    }

    return Iterators.concat(subscriberIterators.iterator());
}
```

* post event

```java
// 发布事件
public void post(Object event) {
    // 获取订阅者列表
    Iterator<Subscriber> eventSubscribers = subscribers.getSubscribers(event);
    if (eventSubscribers.hasNext()) {
        // 分发事件，订阅者按顺序执行
        // dispatcher 的默认实现有三个实现策略：
        //  1. PerThreadQueuedDispatcher  每个线程使用一个 Queue 存储 event，使用了 ThreadLocal 并发模式
        //  2. LegacyAsyncDispatcher  同一个 EventBus 内部使用一个全局的队列存放 event
        //  3. ImmediateDispatcher  立即执行，不使用队列存储
      dispatcher.dispatch(event, eventSubscribers);
    } else if (!(event instanceof DeadEvent)) {
      // the event had no subscribers and was not itself a DeadEvent
      // 如果找不到订阅者，就生成一个 DeadedEvent 发布出去
      post(new DeadEvent(this, event));
    }
```

### 总结

EventBus 虽然是实现了一个小功能，但他内部的设计与实现是非常值得学习的。有几个设计上的点值得思考：

1. 封装复杂性，提供简单、易用、清晰的接口，在使用 EventBus 时，我们只需要定义事件和实现事件的订阅者，然后注册到 EventBus 中，就可以轻松实现进程内的事件通信，支持一个事件被多个订阅者消费；
2. 使用更加能表达意图的 @Subscribe 注解来声明订阅者，而不是实现特定接口，使得编程时代码的灵活度更好，因为可以在任何应该放置订阅者的地方放置这个订阅者，而不是被强制实现接口和使用一个固定的方法名\(因为接口是事先定义好的\)；
3. 在设计与实现时，尽量使用 OO 设计原则和设计思想, 将 EventBus 的功能划分为功能相对清晰单一的子功能，然后进行组合，比如抽象出普适的 Object event；抽象出 Subscriber, 并使用 @Subscribe 和反射机制来自动扫描、自动发现 Subscriber; 抽象出 Dispatcher 进行事件分发，进而实现满足不同需求的分发策略；抽象出 Executor，将 Subscriber 的执行委托给 Executor，进而可以灵活的控制是异步执行还是同步执行等；抽象出 exceptionHandler 来处理异常，并且可以将异常传播给自定义的异常处理组件，由开发者控制
4. 根据应用场景灵活选择并发模式\(ThreadLocal, 并发写控制等\)、数据结构与算法\(CopyOnWriteArraySet, MultiMap, Queue etc.\)等

### Reference

* [EventBus Explained](https://github.com/google/guava/wiki/EventBusExplained)
* [Guava-EventBus 使用详解](https://www.tianqun2019.com/topic/145)

