package org.devlive.tutorial.stream.chapter06;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapBasics
{
    public static void main(String[] args)
    {
        System.out.println("=== map()基础用法 ===");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        // 1. 数字平方转换
        System.out.println("原数据: " + numbers);
        List<Integer> squares = numbers.stream()
                .map(n -> n * n)  // 每个数字转换为它的平方
                .collect(Collectors.toList());
        System.out.println("平方后: " + squares);

        // 2. 数字转字符串
        List<String> numberStrings = numbers.stream()
                .map(n -> "数字" + n)  // 转换为字符串格式
                .collect(Collectors.toList());
        System.out.println("转字符串: " + numberStrings);

        // 3. 类型转换
        List<Double> doubles = numbers.stream()
                .map(n -> n.doubleValue())  // int转double
                .collect(Collectors.toList());
        System.out.println("转double: " + doubles);

        // 4. 字符串转换
        List<String> words = Arrays.asList("hello", "world", "java");
        List<String> upperWords = words.stream()
                .map(String::toUpperCase)  // 方法引用：转大写
                .collect(Collectors.toList());
        System.out.println("转大写: " + upperWords);

        // 5. 字符串长度转换
        List<Integer> lengths = words.stream()
                .map(String::length)  // 提取字符串长度
                .collect(Collectors.toList());
        System.out.println("字符串长度: " + lengths);
    }
}
