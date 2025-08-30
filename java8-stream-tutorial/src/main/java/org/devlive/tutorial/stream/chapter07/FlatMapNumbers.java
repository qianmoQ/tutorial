package org.devlive.tutorial.stream.chapter07;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapNumbers {
    public static void main(String[] args) {
        System.out.println("=== 数字集合的拍扁处理 ===");

        List<List<Integer>> nestedNumbers = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5),
                Arrays.asList(6, 7, 8, 9)
        );

        System.out.println("嵌套数字列表: " + nestedNumbers);

        // 使用flatMap()拍扁
        List<Integer> flatNumbers = nestedNumbers.stream()
                .flatMap(List::stream)  // 将每个List<Integer>转换为流，然后合并
                .collect(Collectors.toList());

        System.out.println("拍扁后: " + flatNumbers);

        // 进一步处理：找出所有偶数
        List<Integer> evenNumbers = nestedNumbers.stream()
                .flatMap(List::stream)          // 先拍扁
                .filter(n -> n % 2 == 0)        // 再过滤偶数
                .collect(Collectors.toList());

        System.out.println("所有偶数: " + evenNumbers);

        // 计算所有数字的总和
        int sum = nestedNumbers.stream()
                .flatMap(List::stream)
                .mapToInt(Integer::intValue)
                .sum();

        System.out.println("数字总和: " + sum);
    }
}
