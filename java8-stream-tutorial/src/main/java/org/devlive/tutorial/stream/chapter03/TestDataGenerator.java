package org.devlive.tutorial.stream.chapter03;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TestDataGenerator
{
    public static void main(String[] args)
    {
        System.out.println("=== 测试数据生成器 ===");

        DataGenerator generator = new DataGenerator();

        // 生成随机用户数据
        System.out.println("随机用户数据:");
        generator.generateUsers(3)
                .forEach(System.out::println);

        // 生成递增订单序列
        System.out.println("\n递增订单序列:");
        generator.generateOrders(4)
                .forEach(System.out::println);

        // 生成性能测试TPS数据
        System.out.println("\n性能测试TPS数据:");
        generator.generateTpsData(5)
                .forEach(System.out::println);
    }
}

class DataGenerator
{
    private final Random random = new Random();
    private final AtomicLong orderIdGenerator = new AtomicLong(1000000L);
    private final List<String> names = Arrays.asList("张三", "李四", "王五", "赵六");
    private final List<String> cities = Arrays.asList("北京", "上海", "广州", "深圳");

    // 使用generate()生成随机用户数据
    public Stream<String> generateUsers(int count)
    {
        return Stream.generate(() -> {
            String name = names.get(random.nextInt(names.size()));
            int age = 18 + random.nextInt(50);
            String city = cities.get(random.nextInt(cities.size()));
            return String.format("用户[%s, %d岁, %s]", name, age, city);
        }).limit(count);
    }

    // 使用iterate()生成递增订单序列
    public Stream<String> generateOrders(int count)
    {
        return Stream.iterate(100.0, amount -> amount + 50.0 + random.nextDouble() * 100.0)
                .limit(count)
                .map(amount -> String.format("订单[ID:%d, 金额:%.2f元]",
                        orderIdGenerator.incrementAndGet(), amount));
    }

    // 使用iterate()生成TPS性能数据
    public Stream<String> generateTpsData(int count)
    {
        return Stream.iterate(1000, tps -> (int) (tps * (0.9 + random.nextDouble() * 0.2)))
                .limit(count)
                .map(tps -> String.format("TPS: %d, 响应时间: %.2fms",
                        tps, 30.0 + random.nextDouble() * 40.0));
    }
}
