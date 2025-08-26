package org.devlive.tutorial.stream.chapter05;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilterBasics
{
    public static void main(String[] args)
    {
        System.out.println("=== filter()基础用法 ===");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // 1. 筛选偶数
        System.out.println("筛选偶数:");
        List<Integer> evenNumbers = numbers.stream()
                .filter(n -> n % 2 == 0)  // 筛选条件：是偶数
                .collect(Collectors.toList());
        System.out.println("原数据: " + numbers);
        System.out.println("偶数: " + evenNumbers);

        // 2. 筛选大于5的数
        System.out.println("\n筛选大于5的数:");
        List<Integer> greaterThan5 = numbers.stream()
                .filter(n -> n > 5)
                .collect(Collectors.toList());
        System.out.println("大于5的数: " + greaterThan5);

        // 3. 多条件筛选
        System.out.println("\n多条件筛选（偶数且大于3）:");
        List<Integer> complexFilter = numbers.stream()
                .filter(n -> n % 2 == 0 && n > 3)  // 多个条件用&&连接
                .collect(Collectors.toList());
        System.out.println("偶数且大于3: " + complexFilter);
    }
}
