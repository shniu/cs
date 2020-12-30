# Topic - 滑动窗口



#### [无重复字符的最长子串](https://leetcode-cn.com/problems/longest-substring-without-repeating-characters)

> 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度。 如 abcabcaabb， 最大长度是 abc，也就是3

可以使用滑动窗口解决。假设字符串长度为 n，假设 i...j 是不含重复字符的子串，j 往后移动一位，会有两种情况：

1. 第 j+1 位在 i...j 中出现过，则在 i...j 中找到出现的位置 k，让 i = k+1（移动左边界）
2. 第 j+1 位在 i...j 中没有出现过，就继续往后移动（移动右边界）

直到 j 到达字符串的最后，代码如下

```java
class Solution {
    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) return 0;
        
        // max 记录最大长度，left 记录左边界
        int max = 0, left = 0;
        char[] chars = s.toCharArray();
        // i...j, j 每次移动一个
        for (int j = 0; j < s.length(); j++) {
            // 找不重复的左边界，直到 j
            for (int i = left; i < j; i++) {
                // left ... j-1 中有和 j 相同的字符，记录最大值，然后滑动左边界
                if (chars[i] == chars[j]) {
                    max = Math.max(max, j - left);
                    left = i + 1;
                    break;
                }
            }
        }
        
        // 返回最大值
        return Math.max(s.length() - left, max);
    }
}
```

滑动窗口是一种解决问题经常用到的方法，是一种高级的双指针技巧。针对双指针（快慢指针、左右指针）的参考文章，总结的很到位

1. [双指针技巧总结](https://labuladong.gitbook.io/algo/di-ling-zhang-bi-du-xi-lie/shuang-zhi-zhen-ji-qiao)
2. [滑动窗口总结](https://labuladong.gitbook.io/algo/di-ling-zhang-bi-du-xi-lie/hua-dong-chuang-kou-ji-qiao)

