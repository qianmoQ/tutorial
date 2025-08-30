package org.devlive.tutorial.stream.chapter07;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapPerformance {
    public static void main(String[] args) {
        System.out.println("=== flatMap()性能优化 ===");

        List<String> sentences = Arrays.asList(
                "Hello World Java",
                "Stream API Programming",
                "Functional Style Coding"
        );

        // ❌ 性能较差：创建了中间数组
        System.out.println("方式1: 创建中间数组");
        List<String> words1 = sentences.stream()
                .map(s -> s.split(" "))              // 创建String[]数组
                .flatMap(Arrays::stream)             // 再拍扁
                .collect(Collectors.toList());
        System.out.println("结果: " + words1);

        // ✅ 性能更好：直接使用流
        System.out.println("\n方式2: 直接流式处理");
        List<String> words2 = sentences.stream()
                .flatMap(s -> Arrays.stream(s.split(" ")))  // 直接拍扁
                .collect(Collectors.toList());
        System.out.println("结果: " + words2);

        // 复杂场景下的性能对比
        System.out.println("\n性能对比场景:");
        long startTime = System.nanoTime();

        sentences.stream()
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .filter(word -> word.length() > 4)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        long endTime = System.nanoTime();
        System.out.println("优化后耗时: " + (endTime - startTime) + "ns");
    }
}
