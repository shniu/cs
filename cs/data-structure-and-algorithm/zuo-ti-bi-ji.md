# 做题笔记

算法和数据结构 - 必知必会必手写

* 树
  * 树的前中后遍历，递归和非递归两种实现
* 搜索算法
  * 图的广度优先搜索和深度优先搜索
  * 二分查找
* 排序
  * 常见排序算法：冒泡、插入、选择、快速、归并、堆等



#### LeetCode 114 题

给定一个二叉树的根节点，请你将它展开为一个单链表。展开后的单链表应该同样使用 TreeNode ，其中 right 子指针指向链表中下一个结点，而左子指针始终为 null；展开后的单链表应该与二叉树 先序遍历 顺序相同。

问题分析，要把一个二叉树展开为一个只有右子树的单链表形式，更改了树的结构，而且展开后的顺序和前序遍历一致，也就是说这个问题的思路需要从前序遍历开始解决，在此基础上得到最终结果

```java
// 对于二叉树的前序遍历
// 递归
void preorder(TreeNode root) {
    if (root == null) return;
    // visit root
    preorder(root.left);
    preorder(root.right);
}

// 非递归
void preorder(TreeNode root) {
    if (root == null) return;
    
    LinkedList<TreeNode> stack = new LinkedList<>();
    TreeNode curr = root;
    
    while (curr != null || !stack.isEmpty()) {
        if (curr == null) {
            curr = stack.removeFirst();
        }
        // visit curr
        if (curr.right != null) {
            stack.addFirst(curr.right);
        }
        curr = curr.left;
    }
}
```

思路1：把前序遍历的结果放在一个列表中，然后对这个列表转化成树的单链表形式；这种思路实现起来也不复杂，比较中规中矩的做法

思路2: 在前序遍历的同时，变换树的结构，可以考虑引入两个节点指针，一个指向当前节点，一个指向前一个节点

```java
// 思路2 的实现：在前序遍历的基础上，增加一个指向前一个节点的指针，移动指针和指针的左右树
public void flatten(TreeNode root) {
    if (root == null) return;
    
    TreeNode curr = root;
    TreeNode prev = null;     //// 这里是新增的
    LinkedList<TreeNode> stack = new LinkedList<>();
    
    while (curr != null || !stack.isEmpty()) {
        if (curr == null) {
            curr = stack.removeFirst();
        }
        
        // visit curr
        if (prev != null) {     ///// 这里是新增的
            prev.right = curr;
            prev.left = null;
        }
        
        if (curr.right != null) {
            stack.addFirst(curr.right);
        }
        
        prev = curr;        //// 这里是新增的
        curr = curr.left;
    }
}
```

从中可以明白一个道理：**问题之间是有关联性的，往往一个复杂点的问题是在一个或者几个简单的问题有机的组合之后可以得到解决；至少能学到如下几点：1、掌握一些基础知识、原理、运行规则等是很有帮助的，往往从这里开始往外发散去找到要解决的问题的办法，也可以认为这是扎马步，基本功、基本范式、基本套路，既要理解也要强化记忆；2、知识的关联性是很强的，并不存在一个独立的知识点和任何东西都没有联系，所以找到联系，掌握住基本原理和思想就可以解决很多同类问题；**



