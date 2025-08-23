package org.devlive.tutorial.stream.chapter03;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class GenerateBasics
{
    public static void main(String[] args)
    {
        System.out.println("=== generate()基础用法 ===");

        // 1. 生成固定值
        System.out.println("生成5个问候语:");
        Stream.generate(() -> "Hello Stream!")
                .limit(5)  // 🚨 必须限制数量！
                .forEach(System.out::println);

        // 2. 生成随机数
        System.out.println("\n生成8个随机数:");
        Random random = new Random();
        Stream.generate(() -> random.nextInt(100))
                .limit(8)
                .forEach(num -> System.out.print(num + " "));

        // 3. 生成随机字符
        System.out.println("\n\n生成6个随机大写字母:");
        Stream.generate(() -> (char) ('A' + random.nextInt(26)))
                .limit(6)
                .forEach(ch -> System.out.print(ch + " "));
    }
}
