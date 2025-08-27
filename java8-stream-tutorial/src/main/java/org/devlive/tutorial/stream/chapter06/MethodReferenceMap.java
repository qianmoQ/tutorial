package org.devlive.tutorial.stream.chapter06;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodReferenceMap
{
    public static void main(String[] args)
    {
        System.out.println("=== 方法引用简化map() ===");

        List<String> words = Arrays.asList("apple", "banana", "cherry");

        // Lambda表达式方式
        System.out.println("Lambda表达式:");
        List<String> result1 = words.stream()
                .map(word -> word.toUpperCase())
                .collect(Collectors.toList());
        System.out.println("结果: " + result1);

        // 方法引用方式（更简洁）
        System.out.println("\n方法引用:");
        List<String> result2 = words.stream()
                .map(String::toUpperCase)  // 等价于 word -> word.toUpperCase()
                .collect(Collectors.toList());
        System.out.println("结果: " + result2);

        // 静态方法引用
        List<Integer> numbers = Arrays.asList(-3, -1, 0, 2, 5);
        System.out.println("\n静态方法引用:");
        List<Integer> absolutes = numbers.stream()
                .map(Math::abs)  // 等价于 n -> Math.abs(n)
                .collect(Collectors.toList());
        System.out.println("原数据: " + numbers);
        System.out.println("绝对值: " + absolutes);
    }
}
