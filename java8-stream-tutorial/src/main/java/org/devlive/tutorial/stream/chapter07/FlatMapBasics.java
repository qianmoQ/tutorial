package org.devlive.tutorial.stream.chapter07;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapBasics {
    public static void main(String[] args) {
        System.out.println("=== flatMap()与map()的区别 ===");

        List<String> sentences = Arrays.asList(
                "Hello World",
                "Java Stream API",
                "Functional Programming"
        );

        // 使用map()：每个句子转换为单词数组
        System.out.println("使用map():");
        List<String[]> wordArrays = sentences.stream()
                .map(sentence -> sentence.split(" "))  // 每个句子变成String[]
                .collect(Collectors.toList());

        System.out.println("结果类型: List<String[]>");
        wordArrays.forEach(arr -> System.out.println("数组: " + Arrays.toString(arr)));

        // 使用flatMap()：将所有单词"拍扁"成一个流
        System.out.println("\n使用flatMap():");
        List<String> allWords = sentences.stream()
                .flatMap(sentence -> Arrays.stream(sentence.split(" ")))  // 拍扁成单词流
                .collect(Collectors.toList());

        System.out.println("结果类型: List<String>");
        System.out.println("所有单词: " + allWords);

        // 更直观的对比
        System.out.println("\n处理过程对比:");
        System.out.println("原始数据: " + sentences);
        System.out.println("map()结果: 3个数组 [每个句子一个数组]");
        System.out.println("flatMap()结果: " + allWords.size() + "个单词 [所有单词在一个列表中]");
    }
}
