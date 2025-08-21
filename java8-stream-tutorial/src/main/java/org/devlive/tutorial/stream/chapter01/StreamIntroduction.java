package org.devlive.tutorial.stream.chapter01;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StreamIntroduction
{
    public static void main(String[] args)
    {
        // 模拟订单数据
        List<Order> orders = Arrays.asList(
                new Order("ORD001", 800.0),
                new Order("ORD002", 1200.0),
                new Order("ORD003", 500.0),
                new Order("ORD004", 1500.0),
                new Order("ORD005", 900.0),
                new Order("ORD006", 2000.0),
                new Order("ORD007", 1100.0)
        );

        System.out.println("=== 传统方式 ===");
        traditionalWay(orders);

        System.out.println("\n=== Stream方式 ===");
        streamWay(orders);
    }

    // 传统方式：需要写很多代码
    public static void traditionalWay(List<Order> orders)
    {
        // 1. 创建临时集合存储筛选结果
        List<Order> filteredOrders = new ArrayList<>();

        // 2. 循环筛选金额大于1000的订单
        for (Order order : orders) {
            if (order.getAmount() > 1000) {
                filteredOrders.add(order);
            }
        }

        // 3. 手动排序（降序）
        Collections.sort(filteredOrders, new Comparator<Order>()
        {
            @Override
            public int compare(Order o1, Order o2)
            {
                return Double.compare(o2.getAmount(), o1.getAmount());
            }
        });

        // 4. 取前5条记录
        List<Order> result = new ArrayList<>();
        for (int i = 0; i < Math.min(5, filteredOrders.size()); i++) {
            result.add(filteredOrders.get(i));
        }

        // 5. 输出结果
        for (Order order : result) {
            System.out.println(order);
        }
    }

    // Stream方式：一行搞定！
    public static void streamWay(List<Order> orders)
    {
        orders.stream()                                    // 创建流
                .filter(order -> order.getAmount() > 1000)  // 筛选金额>1000的订单
                .sorted((o1, o2) -> Double.compare(o2.getAmount(), o1.getAmount())) // 降序排序
                .limit(5)                                    // 取前5条
                .forEach(System.out::println);               // 输出每个订单
    }
}

// 订单类
class Order
{
    private String orderId;
    private Double amount;

    public Order(String orderId, Double amount)
    {
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getOrderId() {return orderId;}

    public Double getAmount() {return amount;}

    @Override
    public String toString()
    {
        return String.format("订单号: %s, 金额: %.2f元", orderId, amount);
    }
}
