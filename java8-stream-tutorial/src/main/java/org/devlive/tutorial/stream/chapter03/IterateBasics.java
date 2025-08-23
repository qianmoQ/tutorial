package org.devlive.tutorial.stream.chapter03;

import java.util.stream.Stream;

public class IterateBasics
{
    public static void main(String[] args)
    {
        System.out.println("=== iterate()基础用法 ===");

        // 1. 等差数列
        System.out.println("等差数列（首项1，公差2）:");
        Stream.iterate(1, n -> n + 2)  // 1, 3, 5, 7, 9...
                .limit(8)
                .forEach(num -> System.out.print(num + " "));

        // 2. 等比数列
        System.out.println("\n\n等比数列（首项2，公比3）:");
        Stream.iterate(2, n -> n * 3)  // 2, 6, 18, 54...
                .limit(6)
                .forEach(num -> System.out.print(num + " "));

        // 3. 2的幂次数列
        System.out.println("\n\n2的幂次数列:");
        Stream.iterate(1, n -> n * 2)  // 1, 2, 4, 8, 16...
                .limit(8)
                .forEach(num -> System.out.print(num + " "));
    }
}
