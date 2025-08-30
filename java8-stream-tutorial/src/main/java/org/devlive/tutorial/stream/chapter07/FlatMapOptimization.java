package org.devlive.tutorial.stream.chapter07;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapOptimization {
    public static void main(String[] args) {
        System.out.println("=== 操作顺序优化 ===");

        List<List<Integer>> nestedNumbers = Arrays.asList(
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20),
                Arrays.asList(21, 22, 23, 24, 25, 26, 27, 28, 29, 30)
        );

        // 需求：找出所有大于15的偶数

        // ❌ 效率较低：先拍扁所有数据，再过滤
        System.out.println("方式1: 先flatMap再filter");
        List<Integer> result1 = nestedNumbers.stream()
                .flatMap(List::stream)           // 拍扁所有30个数字
                .filter(n -> n > 15)             // 过滤大于15的
                .filter(n -> n % 2 == 0)         // 过滤偶数
                .collect(Collectors.toList());
        System.out.println("结果: " + result1);

        // ✅ 效率更高：在每个子列表内先过滤，减少拍扁后的数据量
        System.out.println("\n方式2: 先filter再flatMap");
        List<Integer> result2 = nestedNumbers.stream()
                .flatMap(subList -> subList.stream()
                        .filter(n -> n > 15)     // 在子列表内先过滤
                        .filter(n -> n % 2 == 0)) // 减少需要拍扁的数据
                .collect(Collectors.toList());
        System.out.println("结果: " + result2);

        System.out.println("\n💡 在flatMap内部先进行过滤可以减少数据处理量！");
    }
}
