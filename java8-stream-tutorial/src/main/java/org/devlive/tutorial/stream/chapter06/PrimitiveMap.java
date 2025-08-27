package org.devlive.tutorial.stream.chapter06;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class PrimitiveMap
{
    public static void main(String[] args)
    {
        System.out.println("=== 基本类型map()优化 ===");

        List<String> words = Arrays.asList("Java", "Python", "JavaScript", "Go");

        // 1. mapToInt - 转换为IntStream
        System.out.println("字符串长度统计:");
        IntStream lengths = words.stream()
                .mapToInt(String::length);  // 直接返回IntStream，避免装箱

        System.out.println("各单词长度: " + Arrays.toString(lengths.toArray()));

        // IntStream提供更多数学操作
        int totalLength = words.stream()
                .mapToInt(String::length)
                .sum();  // IntStream提供的sum()方法
        System.out.println("总长度: " + totalLength);

        double avgLength = words.stream()
                .mapToInt(String::length)
                .average()  // 计算平均值
                .orElse(0);
        System.out.printf("平均长度: %.1f\n", avgLength);

        // 2. mapToDouble - 处理价格计算
        List<Product> products = Arrays.asList(
                new Product("手机", 2999),
                new Product("电脑", 8999),
                new Product("耳机", 299)
        );

        System.out.println("\n价格计算:");
        double totalPrice = products.stream()
                .mapToDouble(Product::getPrice)  // 转为DoubleStream
                .sum();
        System.out.printf("总价: %.2f元\n", totalPrice);

        // 计算含税价格（税率8%）
        System.out.println("含税价格:");
        products.stream()
                .mapToDouble(p -> p.getPrice() * 1.08)
                .forEach(price -> System.out.printf("%.2f ", price));
    }
}

class Product
{
    private String name;
    private int price;

    public Product(String name, int price)
    {
        this.name = name;
        this.price = price;
    }

    public String getName() {return name;}

    public int getPrice() {return price;}
}
