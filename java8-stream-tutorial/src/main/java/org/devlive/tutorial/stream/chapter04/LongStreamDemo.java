package org.devlive.tutorial.stream.chapter04;

import java.util.stream.LongStream;

public class LongStreamDemo
{
    public static void main(String[] args)
    {
        System.out.println("=== LongStream处理大整数 ===");

        // 1. 大数范围
        System.out.println("生成10个大数:");
        LongStream.range(1000000000L, 1000000010L)
                .forEach(num -> System.out.print(num + " "));

        // 2. 阶乘计算（使用reduce）
        System.out.println("\n\n计算10的阶乘:");
        long factorial = LongStream.rangeClosed(1, 10)
                .reduce(1, (a, b) -> a * b);
        System.out.println("10! = " + factorial);

        // 3. 大数求和
        System.out.println("\n1到1000000的和:");
        long bigSum = LongStream.rangeClosed(1, 1000000).sum();
        System.out.println("结果: " + bigSum);
    }
}
