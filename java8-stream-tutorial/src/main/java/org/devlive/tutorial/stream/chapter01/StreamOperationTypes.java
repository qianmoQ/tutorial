package org.devlive.tutorial.stream.chapter01;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamOperationTypes
{
    public static void main(String[] args)
    {
        List<String> names = Arrays.asList("张三", "李四", "王五", "赵六", "孙七");

        System.out.println("=== 演示中间操作和终端操作 ===");

        // 这里只有中间操作，不会执行任何处理
        System.out.println("只创建中间操作...");
        names.stream()
                .filter(name -> {
                    System.out.println("过滤: " + name); // 这行不会打印！
                    return name.length() == 2;
                })
                .map(name -> {
                    System.out.println("转换: " + name); // 这行也不会打印！
                    return name + "先生";
                });

        System.out.println("没有任何输出，因为缺少终端操作！\n");

        // 添加终端操作，整个链才会执行
        System.out.println("添加终端操作后：");
        List<String> result = names.stream()
                .filter(name -> {
                    System.out.println("过滤: " + name); // 现在会打印了
                    return name.length() == 2;
                })
                .map(name -> {
                    System.out.println("转换: " + name); // 现在会打印了
                    return name + "先生";
                })
                .collect(Collectors.toList()); // 终端操作：收集到List

        System.out.println("最终结果: " + result);
    }
}
