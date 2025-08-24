package org.devlive.tutorial.stream.chapter04;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class NumberStreamConversion
{
    public static void main(String[] args)
    {
        System.out.println("=== 数字流类型转换 ===");

        // IntStream -> LongStream -> DoubleStream
        System.out.println("IntStream转换链:");
        IntStream.rangeClosed(1, 5)
                .peek(i -> System.out.print("int:" + i + " "))
                .asLongStream()  // 转为LongStream
                .peek(l -> System.out.print("long:" + l + " "))
                .asDoubleStream()  // 转为DoubleStream
                .forEach(d -> System.out.print("double:" + d + " "));

        // 精度处理
        System.out.println("\n\n除法运算精度对比:");
        System.out.println("IntStream整数除法: ");
        IntStream.of(10, 15, 20)
                .map(n -> n / 3)  // 整数除法，会丢失小数部分
                .forEach(result -> System.out.print(result + " "));

        System.out.println("\nDoubleStream浮点除法: ");
        IntStream.of(10, 15, 20)
                .asDoubleStream()
                .map(n -> n / 3.0)  // 浮点除法，保留小数
                .forEach(result -> System.out.printf("%.2f ", result));
    }
}
