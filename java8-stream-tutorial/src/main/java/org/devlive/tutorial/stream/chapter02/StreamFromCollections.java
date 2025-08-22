package org.devlive.tutorial.stream.chapter02;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StreamFromCollections
{
    public static void main(String[] args)
    {
        System.out.println("=== 从各种集合创建Stream ===");

        // 1. 从List创建
        List<String> fruits = Arrays.asList("苹果", "香蕉", "橙子", "葡萄");
        System.out.println("从List创建Stream:");
        fruits.stream()
                .forEach(fruit -> System.out.print(fruit + " "));

        // 2. 从Set创建（自动去重）
        Set<Integer> numbers = new HashSet<>(Arrays.asList(1, 2, 2, 3, 3, 4));
        System.out.println("\n\n从Set创建Stream（注意去重效果）:");
        numbers.stream()
                .sorted()  // Set无序，这里排序一下方便观察
                .forEach(num -> System.out.print(num + " "));

        // 3. 从Map创建Stream（处理键值对）
        Map<String, Integer> scoreMap = new HashMap<>();
        scoreMap.put("张三", 85);
        scoreMap.put("李四", 92);
        scoreMap.put("王五", 78);

        System.out.println("\n\n从Map的entrySet创建Stream:");
        scoreMap.entrySet().stream()
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

        System.out.println("\n只处理Map的values:");
        scoreMap.values().stream()
                .filter(score -> score > 80)  // 找出80分以上的成绩
                .forEach(score -> System.out.print(score + " "));
    }
}
