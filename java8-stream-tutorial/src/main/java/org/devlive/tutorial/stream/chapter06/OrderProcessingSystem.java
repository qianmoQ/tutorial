package org.devlive.tutorial.stream.chapter06;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderProcessingSystem
{
    public static void main(String[] args)
    {
        System.out.println("=== 订单数据处理系统 ===");

        List<Order> orders = Arrays.asList(
                new Order("ORD001", "张三", 1299.99, LocalDate.of(2025, 8, 20)),
                new Order("ORD002", "李四", 899.50, LocalDate.of(2025, 8, 21)),
                new Order("ORD003", "王五", 1599.00, LocalDate.of(2025, 8, 22)),
                new Order("ORD004", "赵六", 599.99, LocalDate.of(2025, 8, 23))
        );

        OrderProcessor processor = new OrderProcessor();

        // 场景1：生成订单摘要报告
        System.out.println("订单摘要报告:");
        List<String> summaries = processor.generateOrderSummaries(orders);
        summaries.forEach(System.out::println);

        // 场景2：计算含税总价
        System.out.println("\n含税价格计算（税率10%）:");
        List<TaxedOrder> taxedOrders = processor.calculateTaxedOrders(orders, 0.10);
        taxedOrders.forEach(System.out::println);

        // 场景3：生成客户账单
        System.out.println("\n客户账单:");
        List<CustomerBill> bills = processor.generateCustomerBills(orders);
        bills.forEach(System.out::println);

        // 场景4：数据导出格式转换
        System.out.println("\nCSV导出格式:");
        List<String> csvLines = processor.convertToCsvFormat(orders);
        csvLines.forEach(System.out::println);
    }
}

class OrderProcessor
{
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 生成订单摘要
    public List<String> generateOrderSummaries(List<Order> orders)
    {
        return orders.stream()
                .map(order -> String.format("订单%s: %s购买，金额%.2f元，日期%s",
                        order.getOrderId(),
                        order.getCustomerName(),
                        order.getAmount(),
                        order.getOrderDate().format(dateFormatter)))
                .collect(Collectors.toList());
    }

    // 计算含税订单
    public List<TaxedOrder> calculateTaxedOrders(List<Order> orders, double taxRate)
    {
        return orders.stream()
                .map(order -> new TaxedOrder(
                        order.getOrderId(),
                        order.getCustomerName(),
                        order.getAmount(),
                        order.getAmount() * taxRate,  // 税额
                        order.getAmount() * (1 + taxRate)  // 含税总额
                ))
                .collect(Collectors.toList());
    }

    // 生成客户账单
    public List<CustomerBill> generateCustomerBills(List<Order> orders)
    {
        return orders.stream()
                .map(order -> new CustomerBill(
                        order.getCustomerName(),
                        order.getAmount(),
                        calculatePoints(order.getAmount()),  // 积分计算
                        order.getOrderDate()
                ))
                .collect(Collectors.toList());
    }

    // 转换为CSV格式
    public List<String> convertToCsvFormat(List<Order> orders)
    {
        return orders.stream()
                .map(order -> String.join(",",
                        order.getOrderId(),
                        order.getCustomerName(),
                        String.valueOf(order.getAmount()),
                        order.getOrderDate().toString()))
                .collect(Collectors.toList());
    }

    private int calculatePoints(double amount)
    {
        return (int) (amount / 10);  // 每10元1积分
    }
}

class Order
{
    private String orderId;
    private String customerName;
    private double amount;
    private LocalDate orderDate;

    public Order(String orderId, String customerName, double amount, LocalDate orderDate)
    {
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
        this.orderDate = orderDate;
    }

    // getter方法
    public String getOrderId() {return orderId;}

    public String getCustomerName() {return customerName;}

    public double getAmount() {return amount;}

    public LocalDate getOrderDate() {return orderDate;}
}

class TaxedOrder
{
    private String orderId;
    private String customerName;
    private double originalAmount;
    private double taxAmount;
    private double totalAmount;

    public TaxedOrder(String orderId, String customerName, double originalAmount,
            double taxAmount, double totalAmount)
    {
        this.orderId = orderId;
        this.customerName = customerName;
        this.originalAmount = originalAmount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString()
    {
        return String.format("%s: %s，原价%.2f，税额%.2f，总计%.2f",
                orderId, customerName, originalAmount, taxAmount, totalAmount);
    }
}

class CustomerBill
{
    private String customerName;
    private double amount;
    private int points;
    private LocalDate billDate;

    public CustomerBill(String customerName, double amount, int points, LocalDate billDate)
    {
        this.customerName = customerName;
        this.amount = amount;
        this.points = points;
        this.billDate = billDate;
    }

    @Override
    public String toString()
    {
        return String.format("客户%s: 消费%.2f元，获得%d积分，日期%s",
                customerName, amount, points, billDate);
    }
}
