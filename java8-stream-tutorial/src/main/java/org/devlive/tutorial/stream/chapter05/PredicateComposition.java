package org.devlive.tutorial.stream.chapter05;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateComposition
{
    public static void main(String[] args)
    {
        System.out.println("=== Predicate条件组合 ===");

        List<Integer> numbers = Arrays.asList(-5, -2, 0, 3, 7, 10, 15, 20);

        // 定义可复用的条件
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isGreaterThan10 = n -> n > 10;

        // 1. 单独使用条件
        System.out.println("正数: " +
                numbers.stream().filter(isPositive).collect(Collectors.toList()));

        // 2. 组合条件 - AND
        System.out.println("正偶数: " +
                numbers.stream().filter(isPositive.and(isEven)).collect(Collectors.toList()));

        // 3. 组合条件 - OR
        System.out.println("负数或大于10: " +
                numbers.stream().filter(isPositive.negate().or(isGreaterThan10))
                        .collect(Collectors.toList()));

        // 4. 复杂组合
        System.out.println("正数且(偶数或大于10): " +
                numbers.stream().filter(isPositive.and(isEven.or(isGreaterThan10)))
                        .collect(Collectors.toList()));
    }
}
