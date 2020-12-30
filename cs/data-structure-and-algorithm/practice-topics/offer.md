---
description: 剑指 Offer 是一本关于程序员面试的书，从编程语言、数据结构及算法三方面总结了面试的知识点，认真阅读本书将受益匪浅。
---

# 剑指 Offer 题解

> 思路比结论更重要。

### 数据结构及算法题解

* 03 - [数组中重复的数字](https://leetcode-cn.com/problems/shu-zu-zhong-zhong-fu-de-shu-zi-lcof)

> 在一个长度为 n 的数组 nums 里的所有数字都在 0～n-1 的范围内。数组中某些数字是重复的，但不知道有几个数字重复了，也不知道每个数字重复了几次。请找出数组中任意一个重复的数字。

思路：

1. 第一直觉是使用排序来解决，基本思路是排序后，遍历数组, 然后判断相邻的两个数是否是相等的，相等则返回，直到最后一个元素。时间复杂度就是排序的时间复杂度，最好是 `O(nlogn)`  , 还有没有更好的思路？
2. 利用 hashmap 是另外一种思路，遍历数组，构建 hashmap，遍历的过程中，判断 hashmap 中是否存在当前元素，在就返回，否则一直到结束，这种方式的不足是，空间复杂度会高一些；我想，在日常开发中，这种方式应该是使用的最多的，它具有通用性，但是综合时间和空间复杂度来看，还有提升的空间
3. 利用置换法，这种思路不好想到，因为需要充分利用问题中的 `所有数字都在0~n-1范围内` 这个特性，数字 i 置换到 nums 数组中 i 的位置上，也就是`一个萝卜一个坑，都找准自己的位置` ，当同一个位置出现多个值时，就找到了需要的结果；时间复杂度 `O(n)` , 空间复杂度 `O(1)` 

参考代码：[数组中的重复数字](https://github.com/shniu/java-eco/blob/master/eco-algorithm/src/main/java/io/github/shniu/algorithm/offer/FindRepeatNumber.java)

* 05 - [替换空格](https://leetcode-cn.com/problems/ti-huan-kong-ge-lcof/)

> 请实现一个函数，把字符串 `s` 中的每个空格替换成"%20"。

思路：

1. 暴力法，一般是不可取的，不浪费时间了
2. 空格替换成 %20，也就是 1 个字符变成了 3 个，需要声明一个新的字符数组存放替换后的字符串；有两个问题需要解决：a、声明多大的新数组合适？b、怎么把老数组中的字符复制到新数组，并把空格做替换？新数组的大小，好解决，就是遍历数组找到所有的空格为 `zeroCount` ，那么新数组的大小为 `s.length + 2 * zeroCount` ；然后怎么复制字符数组呢？两个指针分别指向两个数组，遍历老数组，非空格就直接复制，碰见空格就替换为 %20 , 然后 新数组的指针后移 3

参考代码：[替换空格](https://github.com/shniu/java-eco/blob/master/eco-algorithm/src/main/java/io/github/shniu/algorithm/offer/ReplaceSpace.java)

* 06 - [从尾到头打印链表](https://leetcode-cn.com/problems/cong-wei-dao-tou-da-yin-lian-biao-lcof/)

> 输入一个链表的头节点，从尾到头反过来返回每个节点的值（用数组返回）。

思路：

1. 遍历，现在直到链表的头节点，而且要求从后向前输出，符合先进后出的特性，可以联想到栈，自然我们就可以借助栈来实现；基本思路：先创建一个栈，遍历链表，把元素放入栈中，链表遍历完成后，栈里的数据依次出栈，生成数组，并返回；时间复杂度 O\(n\), 空间复杂度 O\(n\)
2. 递归，思路1是使用了显式栈的方式，当然我们也可以使用隐式栈的方式，那就是利用递归的特性

参考代码：[Reverse Print](https://github.com/shniu/java-eco/blob/master/eco-algorithm/src/main/java/io/github/shniu/algorithm/offer/ReversePrint.java)

```java
// 递归实现
public int[] reversePrint(ListNode head) {
    List<Integer> print = new ArrayList<>();
    reverse(head, print);
    
    int[] res = new int[print.size()];
    for (int i = 0; i < print.size(); i++) {
        res[i] = print.get(i);
    }
    
    return res;
}

private void reverse(ListNode head, List<Integer> print) {
    if (head == null) {
        return;
    }
    
    reverse(head.next, print);
    
    print.add(head.val);
}
```

### 关于编程语言

### 基本的面试流程

### 面试中的解题思路



面试中的各项能力



