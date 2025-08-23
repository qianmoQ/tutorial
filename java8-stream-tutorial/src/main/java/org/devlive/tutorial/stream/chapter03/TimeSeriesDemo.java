package org.devlive.tutorial.stream.chapter03;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class TimeSeriesDemo
{
    public static void main(String[] args)
    {
        System.out.println("=== 时间序列应用 ===");

        // 1. 生成连续日期序列
        System.out.println("未来一周日期:");
        LocalDate today = LocalDate.now();
        Stream.iterate(today, date -> date.plusDays(1))
                .limit(7)
                .forEach(date -> System.out.println("日期: " + date));

        // 2. 生成每4小时的时间点
        System.out.println("\n每4小时的时间点:");
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0);
        Stream.iterate(now, time -> time.plusHours(4))
                .limit(6)
                .forEach(time -> System.out.println("时间: " +
                        time.format(DateTimeFormatter.ofPattern("HH:mm"))));

        // 3. 模拟股价波动
        System.out.println("\n模拟股价走势（基准100元）:");
        Stream.iterate(100.0, price -> price + (Math.random() - 0.5) * 10)
                .limit(5)
                .forEach(price -> System.out.printf("股价: %.2f元\n", price));
    }
}
