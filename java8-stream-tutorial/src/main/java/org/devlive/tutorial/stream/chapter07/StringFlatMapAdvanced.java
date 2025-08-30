package org.devlive.tutorial.stream.chapter07;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringFlatMapAdvanced {
    public static void main(String[] args) {
        System.out.println("=== 字符串处理的高级应用 ===");

        List<String> documents = Arrays.asList(
                "Java 8 Stream API",
                "Lambda Expressions",
                "Functional Programming"
        );

        // 1. 获取所有不重复的字符
        List<Character> uniqueChars = documents.stream()
                .flatMap(doc -> doc.chars()                    // 转为IntStream
                        .filter(c -> c != ' ')       // 过滤空格
                        .mapToObj(c -> (char) c))    // 转为Character流
                .distinct()                                    // 去重
                .sorted()                                      // 排序
                .collect(Collectors.toList());

        System.out.println("所有不重复字符: " + uniqueChars);

        // 2. 统计单词频率（简化版）
        List<String> allWords = documents.stream()
                .flatMap(doc -> Arrays.stream(doc.toLowerCase().split("\\s+")))
                .collect(Collectors.toList());

        System.out.println("所有单词: " + allWords);

        // 3. 查找包含特定字符的单词
        String targetChar = "a";
        List<String> wordsWithA = documents.stream()
                .flatMap(doc -> Arrays.stream(doc.split("\\s+")))
                .filter(word -> word.toLowerCase().contains(targetChar))
                .distinct()
                .collect(Collectors.toList());

        System.out.println("包含字母'" + targetChar + "'的单词: " + wordsWithA);

        // 4. 处理CSV数据
        List<String> csvLines = Arrays.asList(
                "张三,李四,王五",
                "赵六,孙七",
                "周八,吴九,郑十,钱一"
        );

        List<String> allNames = csvLines.stream()
                .flatMap(line -> Arrays.stream(line.split(",")))
                .map(String::trim)  // 清除可能的空格
                .collect(Collectors.toList());

        System.out.println("CSV中所有姓名: " + allNames);
    }
}
