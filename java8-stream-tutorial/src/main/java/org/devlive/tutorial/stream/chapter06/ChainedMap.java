package org.devlive.tutorial.stream.chapter06;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChainedMap
{
    public static void main(String[] args)
    {
        System.out.println("=== 链式map()转换 ===");

        List<String> sentences = Arrays.asList(
                "hello world",
                "java stream api",
                "functional programming"
        );

        // 多重转换：字符串 -> 大写 -> 替换空格 -> 添加前缀
        System.out.println("多重转换处理:");
        List<String> processed = sentences.stream()
                .map(String::toUpperCase)           // 第1步：转大写
                .map(s -> s.replace(" ", "_"))      // 第2步：替换空格
                .map(s -> "PREFIX_" + s)            // 第3步：添加前缀
                .collect(Collectors.toList());

        System.out.println("原始数据: " + sentences);
        System.out.println("处理结果: " + processed);

        // 数值处理链：原数 -> 平方 -> 加10 -> 转字符串
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println("\n数值处理链:");
        List<String> numberProcessed = numbers.stream()
                .map(n -> n * n)                    // 平方
                .map(n -> n + 10)                   // 加10
                .map(n -> "result_" + n)            // 转字符串并加前缀
                .collect(Collectors.toList());

        System.out.println("原始数字: " + numbers);
        System.out.println("处理结果: " + numberProcessed);

        // 使用peek()调试中间步骤
        System.out.println("\n调试中间步骤:");
        numbers.stream()
                .peek(n -> System.out.println("输入: " + n))
                .map(n -> n * n)
                .peek(n -> System.out.println("平方后: " + n))
                .map(n -> n + 10)
                .peek(n -> System.out.println("加10后: " + n))
                .map(n -> "result_" + n)
                .forEach(result -> System.out.println("最终结果: " + result));
    }
}
