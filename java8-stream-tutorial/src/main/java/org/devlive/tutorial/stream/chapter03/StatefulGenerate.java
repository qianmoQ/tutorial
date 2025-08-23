package org.devlive.tutorial.stream.chapter03;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StatefulGenerate
{
    public static void main(String[] args)
    {
        System.out.println("=== 带状态的generate()用法 ===");

        // 1. 使用匿名内部类实现计数器
        System.out.println("递增计数器:");
        Stream.generate(new Supplier<Integer>()
                {
                    private int counter = 0;

                    @Override
                    public Integer get()
                    {
                        return ++counter;  // 每次调用返回递增的数
                    }
                })
                .limit(8)
                .forEach(num -> System.out.print(num + " "));

        // 2. 使用AtomicInteger实现线程安全计数器
        System.out.println("\n\n线程安全计数器:");
        AtomicInteger atomicCounter = new AtomicInteger(0);
        Stream.generate(() -> atomicCounter.incrementAndGet())
                .limit(6)
                .forEach(num -> System.out.print(num + " "));

        // 3. 生成带编号的消息
        System.out.println("\n\n带编号的消息:");
        Stream.generate(new Supplier<String>()
                {
                    private int messageNo = 1;

                    @Override
                    public String get()
                    {
                        return "消息编号: " + (messageNo++);
                    }
                })
                .limit(4)
                .forEach(System.out::println);
    }
}
