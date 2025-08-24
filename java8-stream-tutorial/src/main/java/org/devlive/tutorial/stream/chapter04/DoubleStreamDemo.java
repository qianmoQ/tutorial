package org.devlive.tutorial.stream.chapter04;

import java.util.DoubleSummaryStatistics;
import java.util.stream.DoubleStream;

public class DoubleStreamDemo
{
    public static void main(String[] args)
    {
        System.out.println("=== DoubleStream处理浮点数 ===");

        // 1. 从数组创建
        double[] prices = {19.99, 29.99, 39.99, 49.99, 59.99};
        System.out.println("商品价格统计:");
        DoubleSummaryStatistics stats = DoubleStream.of(prices).summaryStatistics();
        System.out.printf("平均价格: %.2f, 最高价格: %.2f, 最低价格: %.2f\n",
                stats.getAverage(), stats.getMax(), stats.getMin());

        // 2. 数学计算
        System.out.println("\n计算税后价格（税率8%）:");
        DoubleStream.of(prices)
                .map(price -> price * 1.08)  // 加税
                .forEach(taxedPrice -> System.out.printf("%.2f ", taxedPrice));

        // 3. 生成随机浮点数
        System.out.println("\n\n生成5个随机浮点数(0.0-1.0):");
        DoubleStream.generate(Math::random)
                .limit(5)
                .forEach(num -> System.out.printf("%.3f ", num));
    }
}
