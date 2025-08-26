package org.devlive.tutorial.stream.chapter05;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChainedFilters
{
    public static void main(String[] args)
    {
        System.out.println("=== 链式filter()对比 ===");

        List<String> words = Arrays.asList("apple", "banana", "cherry", "date", "elderberry", "fig");

        // 方式1: 单个filter()，复杂条件
        System.out.println("方式1: 复杂条件在一个filter()中");
        List<String> result1 = words.stream()
                .filter(word -> word.length() > 4 && word.contains("e") && !word.startsWith("e"))
                .collect(Collectors.toList());
        System.out.println("结果: " + result1);

        // 方式2: 链式filter()，逻辑清晰
        System.out.println("\n方式2: 链式filter()，逻辑分层");
        List<String> result2 = words.stream()
                .filter(word -> word.length() > 4)      // 第1层：长度过滤
                .filter(word -> word.contains("e"))     // 第2层：包含字母e
                .filter(word -> !word.startsWith("e"))  // 第3层：不以e开头
                .collect(Collectors.toList());
        System.out.println("结果: " + result2);

        // 展示每一步的筛选过程
        System.out.println("\n筛选过程演示:");
        words.stream()
                .peek(word -> System.out.println("原始: " + word))
                .filter(word -> word.length() > 4)
                .peek(word -> System.out.println("  长度>4: " + word))
                .filter(word -> word.contains("e"))
                .peek(word -> System.out.println("    包含e: " + word))
                .filter(word -> !word.startsWith("e"))
                .forEach(word -> System.out.println("      最终结果: " + word));
    }
}
