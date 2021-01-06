# Python

* [是的，Python比较慢，但我不在乎：牺牲性能以提升工作效率](http://www.infoq.com/cn/articles/sacrifice-performance-to-improve-work-efficiency)

大家对Python最大的抱怨就是它的速度慢。有些人甚至因为Python的速度不如某个语言而拒绝使用它。本文中我将阐述，即便Python这么慢，为什么还值得你对它进行尝试。

> 完成项目比让项目跑得更快更重要。
>
> 在一个高吞吐量的环境中使用一个解释型语言看似矛盾，但是我们发现CPU时间几乎不是瓶颈因素，表达性强的语言意味着大部分代码是短小的，大多数时间花费在了I/O以及原生代码运行时上。此外，解释型的实现所具备的灵活性十分有用，它方便了我们在语言层面上的试验，也方便了我们探索将计算分布到多台机器上的方法。
>
> 因为某个语言速度快而选择其为开发你应用的语言是不成熟优化的一种体现。 优化你最昂贵的资源。也就是你自己，而不是电脑。 选择可以有助于快速开发的语言、框架、架构，例如Python。不要只因为运行速度快而选择某个技术。 当你的应用有性能问题时，找出你应用中性能的瓶颈。 你的瓶颈通常不是CPU或Python本身。 如果你已经优化了算法或其他方面，确定Python的确是你项目的瓶颈，那么可以将这个热点移到Cython/C中进行改写。

* [Interpreting the Data: Parallel Analysis with Sawzall](https://static.googleusercontent.com/media/research.google.com/en//archive/sawzall-sciprog.pdf)

Google关于`语言的速度（也就是CPU时间）几乎不会导致问题`的研究，涉及谈论了设计高吞吐量的系统。

