package org.devlive.tutorial.stream.chapter03;

import java.util.stream.Stream;

public class AdvancedIterate
{
    public static void main(String[] args)
    {
        System.out.println("=== 复杂序列生成 ===");

        // 1. 斐波那契数列
        System.out.println("斐波那契数列:");
        Stream.iterate(new long[] {0, 1}, arr -> new long[] {arr[1], arr[0] + arr[1]})
                .limit(10)
                .mapToLong(arr -> arr[0])  // 提取第一个数
                .forEach(num -> System.out.print(num + " "));

        // 2. 三角数序列（1+2+3+...+n）
        System.out.println("\n\n三角数序列:");
        Stream.iterate(new int[] {1, 1}, arr -> new int[] {arr[0] + 1, arr[1] + arr[0] + 1})
                .limit(8)
                .mapToInt(arr -> arr[1])  // 提取三角数
                .forEach(num -> System.out.print(num + " "));

        // 3. 质数序列
        System.out.println("\n\n质数序列:");
        Stream.iterate(2, n -> n + 1)
                .filter(AdvancedIterate::isPrime)  // 过滤出质数
                .limit(8)
                .forEach(num -> System.out.print(num + " "));
    }

    // 简单的质数判断
    private static boolean isPrime(int n)
    {
        if (n < 2) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
