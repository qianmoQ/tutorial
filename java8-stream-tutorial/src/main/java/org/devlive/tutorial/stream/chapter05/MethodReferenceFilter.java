package org.devlive.tutorial.stream.chapter05;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodReferenceFilter
{
    public static void main(String[] args)
    {
        System.out.println("=== 方法引用简化filter() ===");

        List<String> words = Arrays.asList("hello", "", "world", null, "java", "", "stream");

        // 1. Lambda表达式方式
        System.out.println("Lambda表达式方式:");
        List<String> result1 = words.stream()
                .filter(s -> s != null)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        System.out.println("结果: " + result1);

        // 2. 方法引用方式（更简洁）
        System.out.println("\n方法引用方式:");
        List<String> result2 = words.stream()
                .filter(java.util.Objects::nonNull)         // 方法引用：非null
                .filter(s -> !s.isEmpty())                  // Lambda：非空字符串
                .collect(Collectors.toList());
        System.out.println("结果: " + result2);

        // 3. 自定义静态方法
        System.out.println("\n使用自定义工具方法:");
        List<String> result3 = words.stream()
                .filter(StringUtils::isValid)               // 自定义方法引用
                .collect(Collectors.toList());
        System.out.println("结果: " + result3);
    }
}

class StringUtils
{
    public static boolean isValid(String str)
    {
        return str != null && !str.trim().isEmpty();
    }
}
