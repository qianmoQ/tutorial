package org.devlive.tutorial.stream.chapter04;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class IntStreamCreation
{
    public static void main(String[] args)
    {
        System.out.println("=== 创建IntStream的方式 ===");

        // 1. 从范围创建
        System.out.println("range(1, 5): ");
        IntStream.range(1, 5)  // 不包括5
                .forEach(num -> System.out.print(num + " "));

        System.out.println("\nrangeClosed(1, 5): ");
        IntStream.rangeClosed(1, 5)  // 包括5
                .forEach(num -> System.out.print(num + " "));

        // 2. 从数组创建
        System.out.println("\n\n从数组创建:");
        int[] arr = {10, 20, 30, 40};
        Arrays.stream(arr)
                .forEach(num -> System.out.print(num + " "));

        // 3. 从可变参数创建
        System.out.println("\n\n从可变参数创建:");
        IntStream.of(100, 200, 300)
                .forEach(num -> System.out.print(num + " "));

        // 4. 生成随机数流
        System.out.println("\n\n生成5个随机数(0-99):");
        new Random().ints(5, 0, 100)  // 生成5个[0,100)的随机数
                .forEach(num -> System.out.print(num + " "));

        // 5. 无限流
        System.out.println("\n\n无限流(前6个偶数):");
        IntStream.iterate(2, n -> n + 2)
                .limit(6)
                .forEach(num -> System.out.print(num + " "));
    }
}
