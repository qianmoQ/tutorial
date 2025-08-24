package org.devlive.tutorial.stream.chapter04;

import java.util.IntSummaryStatistics;
import java.util.stream.IntStream;

public class GradeStatisticsSystem
{
    public static void main(String[] args)
    {
        System.out.println("=== 学生成绩统计系统 ===");

        // 模拟一个班级的成绩数据
        int[] mathScores = {85, 92, 78, 96, 88, 73, 91, 82, 87, 95, 79, 89, 84, 90, 76};
        int[] englishScores = {88, 85, 92, 89, 91, 82, 87, 90, 85, 93, 81, 86, 88, 92, 79};

        GradeAnalyzer analyzer = new GradeAnalyzer();

        // 数学成绩分析
        System.out.println("数学成绩分析:");
        analyzer.analyzeScores("数学", mathScores);

        System.out.println("\n英语成绩分析:");
        analyzer.analyzeScores("英语", englishScores);

        // 对比分析
        System.out.println("\n学科对比分析:");
        analyzer.compareSubjects(mathScores, englishScores);
    }
}

class GradeAnalyzer
{

    public void analyzeScores(String subject, int[] scores)
    {
        IntSummaryStatistics stats = IntStream.of(scores).summaryStatistics();

        System.out.printf("%s科目统计:\n", subject);
        System.out.printf("  学生人数: %d人\n", stats.getCount());
        System.out.printf("  平均分: %.1f分\n", stats.getAverage());
        System.out.printf("  最高分: %d分\n", stats.getMax());
        System.out.printf("  最低分: %d分\n", stats.getMin());

        // 等级分布统计
        long excellent = IntStream.of(scores).filter(score -> score >= 90).count();
        long good = IntStream.of(scores).filter(score -> score >= 80 && score < 90).count();
        long average = IntStream.of(scores).filter(score -> score >= 70 && score < 80).count();
        long poor = IntStream.of(scores).filter(score -> score < 70).count();

        System.out.println("  成绩分布:");
        System.out.printf("    优秀(90+): %d人 (%.1f%%)\n", excellent, excellent * 100.0 / stats.getCount());
        System.out.printf("    良好(80-89): %d人 (%.1f%%)\n", good, good * 100.0 / stats.getCount());
        System.out.printf("    中等(70-79): %d人 (%.1f%%)\n", average, average * 100.0 / stats.getCount());
        System.out.printf("    待改进(<70): %d人 (%.1f%%)\n", poor, poor * 100.0 / stats.getCount());
    }

    public void compareSubjects(int[] mathScores, int[] englishScores)
    {
        double mathAvg = IntStream.of(mathScores).average().orElse(0);
        double englishAvg = IntStream.of(englishScores).average().orElse(0);

        System.out.printf("数学平均分: %.1f, 英语平均分: %.1f\n", mathAvg, englishAvg);

        if (mathAvg > englishAvg) {
            System.out.printf("数学成绩比英语高 %.1f分\n", mathAvg - englishAvg);
        }
        else if (englishAvg > mathAvg) {
            System.out.printf("英语成绩比数学高 %.1f分\n", englishAvg - mathAvg);
        }
        else {
            System.out.println("两科成绩相当");
        }

        // 计算总分统计
        System.out.println("\n总分统计:");
        IntStream.range(0, mathScores.length)
                .map(i -> mathScores[i] + englishScores[i])  // 计算每个学生的总分
                .summaryStatistics();

        IntSummaryStatistics totalStats = IntStream.range(0, mathScores.length)
                .map(i -> mathScores[i] + englishScores[i])
                .summaryStatistics();

        System.out.printf("总分平均: %.1f, 最高总分: %d, 最低总分: %d\n",
                totalStats.getAverage(), totalStats.getMax(), totalStats.getMin());
    }
}
