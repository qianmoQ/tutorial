package org.devlive.tutorial.stream.chapter04;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class NumberStreamAdvantage
{
    public static void main(String[] args)
    {
        System.out.println("=== 数字流的优势 ===");

        // 方式1: 普通Stream（有装箱拆箱开销）
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        long startTime = System.nanoTime();
        int sum1 = numbers.stream()
                .mapToInt(Integer::intValue)  // 需要转换
                .sum();
        long endTime = System.nanoTime();
        System.out.println("普通Stream求和: " + sum1 + ", 耗时: " + (endTime - startTime) + "ns");

        // 方式2: IntStream（直接处理基本类型）
        startTime = System.nanoTime();
        int sum2 = IntStream.rangeClosed(1, 10)  // 直接生成int流
                .sum();
        endTime = System.nanoTime();
        System.out.println("IntStream求和: " + sum2 + ", 耗时: " + (endTime - startTime) + "ns");

        // 展示IntStream的简洁API
        System.out.println("\nIntStream的简洁API:");
        IntStream.rangeClosed(1, 10)
                .forEach(num -> System.out.print(num + " "));
    }
}
