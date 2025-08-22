package org.devlive.tutorial.stream.chapter02;

import java.util.stream.Stream;

public class StreamIterate
{
    public static void main(String[] args)
    {
        System.out.println("=== 使用iterate()创建序列流 ===");

        // 1. 生成等差数列
        System.out.println("生成等差数列（首项为2，公差为3）:");
        Stream.iterate(2, n -> n + 3)  // 从2开始，每次加3
                .limit(8)
                .forEach(num -> System.out.print(num + " "));

        // 2. 生成等比数列
        System.out.println("\n\n生成等比数列（首项为1，公比为2）:");
        Stream.iterate(1, n -> n * 2)  // 从1开始，每次乘以2
                .limit(10)
                .forEach(num -> System.out.print(num + " "));

        // 3. 生成斐波那契数列
        System.out.println("\n\n生成斐波那契数列:");
        Stream.iterate(new int[] {0, 1}, arr -> new int[] {arr[1], arr[0] + arr[1]})
                .limit(10)
                .mapToInt(arr -> arr[0])  // 提取数组的第一个元素
                .forEach(num -> System.out.print(num + " "));

        // 4. 生成日期序列
        System.out.println("\n\n生成连续日期序列:");
        java.time.LocalDate today = java.time.LocalDate.now();
        Stream.iterate(today, date -> date.plusDays(1))  // 每次加1天
                .limit(5)
                .forEach(date -> System.out.println("日期: " + date));

        System.out.println("\n生成有条件的序列（小于50的平方数）:");
        Stream.iterate(1, n -> n + 1)
                .limit(49)  // 直接限制数量（1到49）
                .map(n -> n * n)
                .forEach(square -> System.out.print(square + " "));
    }
}
