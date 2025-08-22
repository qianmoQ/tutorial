package org.devlive.tutorial.stream.chapter02;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StreamGenerate
{
    public static void main(String[] args)
    {
        System.out.println("=== 使用generate()创建无限流 ===");

        // 1. 生成固定值的无限流
        System.out.println("生成10个相同的问候语:");
        Stream.generate(() -> "Hello Stream!")
                .limit(10)  // 限制数量，否则会无限执行下去！
                .forEach(System.out::println);

        // 2. 生成随机数流
        System.out.println("\n生成5个随机数:");
        Random random = new Random();
        Stream.generate(() -> random.nextInt(100))  // 生成0-99的随机数
                .limit(5)
                .forEach(num -> System.out.print(num + " "));

        // 3. 生成带状态的数据（模拟计数器）
        System.out.println("\n\n生成递增序列（使用状态对象）:");
        Stream.generate(new Supplier<Integer>()
                {
                    private int current = 0;

                    @Override
                    public Integer get()
                    {
                        return current++;
                    }
                })
                .limit(8)
                .forEach(num -> System.out.print(num + " "));

        // 4. 实际应用：生成测试数据
        System.out.println("\n\n生成测试用户数据:");
        String[] names = {"张三", "李四", "王五", "赵六", "孙七"};
        Stream.generate(() -> {
                    Random r = new Random();
                    return new TestUser(
                            names[r.nextInt(names.length)],
                            18 + r.nextInt(40),  // 年龄18-57
                            r.nextBoolean()      // 随机性别
                    );
                })
                .limit(3)
                .forEach(System.out::println);
    }
}

// 测试用户类
class TestUser
{
    private String name;
    private int age;
    private boolean isMale;

    public TestUser(String name, int age, boolean isMale)
    {
        this.name = name;
        this.age = age;
        this.isMale = isMale;
    }

    @Override
    public String toString()
    {
        return String.format("用户[姓名:%s, 年龄:%d, 性别:%s]",
                name, age, isMale ? "男" : "女");
    }
}
