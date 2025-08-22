package org.devlive.tutorial.stream.chapter02;

import java.util.Arrays;
import java.util.stream.Stream;

public class StreamFromArrays
{
    public static void main(String[] args)
    {
        System.out.println("=== 从数组创建Stream ===");

        // 方式1: Arrays.stream() - 推荐方式
        String[] cities = {"北京", "上海", "广州", "深圳"};
        System.out.println("方式1: Arrays.stream()");
        Arrays.stream(cities)
                .forEach(city -> System.out.print(city + " "));

        // 方式2: Stream.of()
        System.out.println("\n\n方式2: Stream.of()");
        Stream.of(cities)
                .forEach(city -> System.out.print(city + " "));

        // 数值数组的特殊处理
        int[] ages = {25, 30, 28, 35, 23};
        System.out.println("\n\n处理int数组:");
        Arrays.stream(ages)              // 返回IntStream
                .filter(age -> age > 26)   // 筛选年龄大于26的
                .forEach(age -> System.out.print(age + " "));

        // 指定数组范围创建Stream
        System.out.println("\n\n从数组的部分元素创建Stream:");
        String[] allCities = {"北京", "上海", "广州", "深圳", "杭州", "南京"};
        Arrays.stream(allCities, 1, 4)  // 从索引1到3（不包括4）
                .forEach(city -> System.out.print(city + " "));
    }
}
