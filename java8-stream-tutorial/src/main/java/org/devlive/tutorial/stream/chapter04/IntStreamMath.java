package org.devlive.tutorial.stream.chapter04;

import java.util.stream.IntStream;

public class IntStreamMath
{
    public static void main(String[] args)
    {
        System.out.println("=== IntStream数学运算 ===");

        // 1. 数学运算
        System.out.println("1到10的平方:");
        IntStream.rangeClosed(1, 10)
                .map(n -> n * n)  // 计算平方
                .forEach(square -> System.out.print(square + " "));

        System.out.println("\n\n筛选偶数并求和:");
        int evenSum = IntStream.rangeClosed(1, 20)
                .filter(n -> n % 2 == 0)  // 筛选偶数
                .sum();
        System.out.println("1到20中偶数之和: " + evenSum);

        // 2. 类型转换
        System.out.println("\n转换为double类型:");
        IntStream.of(1, 2, 3, 4, 5)
                .asDoubleStream()  // 转为DoubleStream
                .map(d -> d / 2.0)  // 除以2.0
                .forEach(result -> System.out.printf("%.1f ", result));

        // 3. 转换为对象流
        System.out.println("\n\n转换为String对象流:");
        IntStream.rangeClosed(1, 5)
                .mapToObj(n -> "数字" + n)  // 转为Stream<String>
                .forEach(str -> System.out.print(str + " "));

        // 4. 复合运算
        System.out.println("\n\n1到10中奇数的平方和:");
        int oddSquareSum = IntStream.rangeClosed(1, 10)
                .filter(n -> n % 2 == 1)  // 筛选奇数
                .map(n -> n * n)          // 计算平方
                .sum();                   // 求和
        System.out.println("结果: " + oddSquareSum);
    }
}
