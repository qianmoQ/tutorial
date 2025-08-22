package org.devlive.tutorial.stream.chapter02;

import java.time.LocalDate;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DataGeneratorFactory
{
    public static void main(String[] args)
    {
        System.out.println("=== 综合数据生成器 ===");

        DataGenerator generator = new DataGenerator();

        // 生成测试用户数据
        System.out.println("生成5个测试用户:");
        generator.generateUsers(5)
                .forEach(System.out::println);

        // 生成日期范围
        System.out.println("\n生成本周日期:");
        generator.generateDateRange(LocalDate.now(), 7)
                .forEach(date -> System.out.println("日期: " + date));

        // 生成随机订单数据
        System.out.println("\n生成3个随机订单:");
        generator.generateOrders(3)
                .forEach(System.out::println);
    }
}

class DataGenerator
{
    private final Random random = new Random();
    private final String[] names = {"张三", "李四", "王五", "赵六", "孙七", "周八", "吴九", "郑十"};
    private final String[] cities = {"北京", "上海", "广州", "深圳", "杭州", "南京", "成都", "武汉"};

    // 生成随机用户数据
    public Stream<User> generateUsers(int count)
    {
        return Stream.generate(new Supplier<User>()
        {
            @Override
            public User get()
            {
                return new User(
                        names[random.nextInt(names.length)],
                        18 + random.nextInt(50),  // 年龄18-67
                        cities[random.nextInt(cities.length)]
                );
            }
        }).limit(count);
    }

    // 生成日期范围
    public Stream<LocalDate> generateDateRange(LocalDate start, int days)
    {
        return Stream.iterate(start, date -> date.plusDays(1))
                .limit(days);
    }

    // 生成随机订单
    public Stream<Order> generateOrders(int count)
    {
        return Stream.generate(() -> new Order(
                "ORD" + String.format("%06d", random.nextInt(1000000)),
                100.0 + random.nextDouble() * 1900.0,  // 金额100-2000
                names[random.nextInt(names.length)]
        )).limit(count);
    }
}

// 用户类
class User
{
    private String name;
    private int age;
    private String city;

    public User(String name, int age, String city)
    {
        this.name = name;
        this.age = age;
        this.city = city;
    }

    @Override
    public String toString()
    {
        return String.format("用户[姓名:%s, 年龄:%d, 城市:%s]", name, age, city);
    }
}

// 订单类
class Order
{
    private String orderId;
    private double amount;
    private String customerName;

    public Order(String orderId, double amount, String customerName)
    {
        this.orderId = orderId;
        this.amount = amount;
        this.customerName = customerName;
    }

    @Override
    public String toString()
    {
        return String.format("订单[ID:%s, 金额:%.2f, 客户:%s]", orderId, amount, customerName);
    }
}
