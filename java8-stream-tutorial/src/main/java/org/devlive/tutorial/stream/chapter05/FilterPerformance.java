package org.devlive.tutorial.stream.chapter05;

import java.util.stream.IntStream;

public class FilterPerformance
{
    public static void main(String[] args)
    {
        System.out.println("=== filter()位置对性能的影响 ===");

        // ❌ 性能较差：先进行昂贵操作，再过滤
        System.out.println("方式1: 先计算再过滤");
        long count1 = IntStream.rangeClosed(1, 1000)
                .map(n -> expensiveOperation(n))    // 昂贵操作
                .filter(n -> n > 500)               // 过滤
                .count();
        System.out.println("结果: " + count1);

        // ✅ 性能更好：先过滤，再进行昂贵操作
        System.out.println("\n方式2: 先过滤再计算");
        long count2 = IntStream.rangeClosed(1, 1000)
                .filter(n -> n > 500)               // 先过滤
                .map(FilterPerformance::expensiveOperation)  // 昂贵操作
                .count();
        System.out.println("结果: " + count2);

        System.out.println("\n💡 将filter()放在前面可以减少后续操作的数据量，提升性能！");
    }

    private static int expensiveOperation(int n)
    {
        // 模拟耗时操作
        return n * n;
    }
}
