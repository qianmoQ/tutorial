package org.devlive.tutorial.stream.chapter04;

import java.util.IntSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class IntStreamStatistics
{
    public static void main(String[] args)
    {
        System.out.println("=== IntStream统计功能 ===");

        IntStream numbers = IntStream.of(12, 45, 23, 67, 34, 89, 56);

        // 基础统计
        System.out.println("数据: 12, 45, 23, 67, 34, 89, 56");
        System.out.println("总和: " + IntStream.of(12, 45, 23, 67, 34, 89, 56).sum());
        System.out.println("个数: " + IntStream.of(12, 45, 23, 67, 34, 89, 56).count());

        // Optional类型的统计（处理空流的情况）
        OptionalInt max = IntStream.of(12, 45, 23, 67, 34, 89, 56).max();
        OptionalInt min = IntStream.of(12, 45, 23, 67, 34, 89, 56).min();
        OptionalDouble avg = IntStream.of(12, 45, 23, 67, 34, 89, 56).average();

        System.out.println("最大值: " + (max.isPresent() ? max.getAsInt() : "无数据"));
        System.out.println("最小值: " + (min.isPresent() ? min.getAsInt() : "无数据"));
        System.out.println("平均值: " + (avg.isPresent() ? String.format("%.2f", avg.getAsDouble()) : "无数据"));

        // 一次性获取所有统计信息
        System.out.println("\n使用summaryStatistics()一次获取所有统计:");
        IntSummaryStatistics stats = IntStream.of(12, 45, 23, 67, 34, 89, 56).summaryStatistics();
        System.out.println("统计摘要: " + stats);
        System.out.printf("详细信息 - 个数:%d, 总和:%d, 平均:%.2f, 最小:%d, 最大:%d\n",
                stats.getCount(), stats.getSum(), stats.getAverage(),
                stats.getMin(), stats.getMax());
    }
}
