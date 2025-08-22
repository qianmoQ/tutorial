package org.devlive.tutorial.stream.chapter02;

import java.util.Objects;
import java.util.stream.Stream;

public class DirectStreamCreation
{
    public static void main(String[] args)
    {
        System.out.println("=== 直接创建Stream ===");

        // 1. Stream.of() - 从零散数据创建
        System.out.println("从零散数据创建:");
        Stream.of("Java", "Python", "JavaScript", "Go")
                .forEach(lang -> System.out.print(lang + " "));

        // 2. Stream.empty() - 创建空流
        System.out.println("\n\n创建空Stream:");
        Stream<String> emptyStream = Stream.empty();
        System.out.println("空Stream的元素个数: " + emptyStream.count());

        // 3. Stream.builder() - 动态构建Stream
        System.out.println("\n使用Builder模式创建Stream:");
        Stream<String> builtStream = Stream.<String>builder()
                .add("第一个元素")
                .add("第二个元素")
                .add("第三个元素")
                .build();

        builtStream.forEach(System.out::println);

        // 4. 处理可能为null的情况
        System.out.println("\n处理可能为null的数据:");
        String nullableValue = null;
        String validValue = "有效数据";

        // Java 9+的方式（这里模拟实现）
        Stream.of(nullableValue, validValue)
                .filter(Objects::nonNull)  // 过滤掉null值
                .forEach(System.out::println);
    }
}
