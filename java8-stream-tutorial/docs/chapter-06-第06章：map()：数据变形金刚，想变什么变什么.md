[TOC]

想象一下如果处理一批用户数据，需要把用户对象转换成不同的格式：有时需要提取姓名列表，有时需要计算年龄，有时需要格式化显示信息。用传统方式，你得写好多个循环：

```java
// 提取姓名
List<String> names = new ArrayList<>();
for (User user : users) {
    names.add(user.getName());
}

// 计算年龄
List<Integer> ages = new ArrayList<>();
for (User user : users) {
    ages.add(calculateAge(user.getBirthYear()));
}
```

传统方式很麻烦麻烦，用Stream的`map()`方法一行代码就能搞定数据转换。"

```java
List<String> names = users.stream().map(User::getName).collect(toList());
List<Integer> ages = users.stream().map(user -> calculateAge(user.getBirthYear())).collect(toList());
```

今天我们就来学习`map()`方法，这个数据转换的"变形金刚"，看看它如何让数据转换变得简单优雅！

## map()基础：一对一的数据转换

### map()的工作原理

`map()`方法对流中的每个元素应用一个函数，将原始元素转换为新的元素。它是一对一的映射关系：

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapBasics {
    public static void main(String[] args) {
        System.out.println("=== map()基础用法 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // 1. 数字平方转换
        System.out.println("原数据: " + numbers);
        List<Integer> squares = numbers.stream()
            .map(n -> n * n)  // 每个数字转换为它的平方
            .collect(Collectors.toList());
        System.out.println("平方后: " + squares);
        
        // 2. 数字转字符串
        List<String> numberStrings = numbers.stream()
            .map(n -> "数字" + n)  // 转换为字符串格式
            .collect(Collectors.toList());
        System.out.println("转字符串: " + numberStrings);
        
        // 3. 类型转换
        List<Double> doubles = numbers.stream()
            .map(n -> n.doubleValue())  // int转double
            .collect(Collectors.toList());
        System.out.println("转double: " + doubles);
        
        // 4. 字符串转换
        List<String> words = Arrays.asList("hello", "world", "java");
        List<String> upperWords = words.stream()
            .map(String::toUpperCase)  // 方法引用：转大写
            .collect(Collectors.toList());
        System.out.println("转大写: " + upperWords);
        
        // 5. 字符串长度转换
        List<Integer> lengths = words.stream()
            .map(String::length)  // 提取字符串长度
            .collect(Collectors.toList());
        System.out.println("字符串长度: " + lengths);
    }
}
```

**输出结果：**

```
=== map()基础用法 ===
原数据: [1, 2, 3, 4, 5]
平方后: [1, 4, 9, 16, 25]
转字符串: [数字1, 数字2, 数字3, 数字4, 数字5]
转double: [1.0, 2.0, 3.0, 4.0, 5.0]
转大写: [HELLO, WORLD, JAVA]
字符串长度: [5, 5, 4]
```

💡 **关键理解**：`map()`不会改变流中元素的数量，只是改变元素的类型或值。输入5个元素，输出也是5个元素。

### 方法引用让代码更简洁

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodReferenceMap {
    public static void main(String[] args) {
        System.out.println("=== 方法引用简化map() ===");
        
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        // Lambda表达式方式
        System.out.println("Lambda表达式:");
        List<String> result1 = words.stream()
            .map(word -> word.toUpperCase())
            .collect(Collectors.toList());
        System.out.println("结果: " + result1);
        
        // 方法引用方式（更简洁）
        System.out.println("\n方法引用:");
        List<String> result2 = words.stream()
            .map(String::toUpperCase)  // 等价于 word -> word.toUpperCase()
            .collect(Collectors.toList());
        System.out.println("结果: " + result2);
        
        // 静态方法引用
        List<Integer> numbers = Arrays.asList(-3, -1, 0, 2, 5);
        System.out.println("\n静态方法引用:");
        List<Integer> absolutes = numbers.stream()
            .map(Math::abs)  // 等价于 n -> Math.abs(n)
            .collect(Collectors.toList());
        System.out.println("原数据: " + numbers);
        System.out.println("绝对值: " + absolutes);
    }
}
```

**输出结果：**

```
=== 方法引用简化map() ===
Lambda表达式:
结果: [APPLE, BANANA, CHERRY]

方法引用:
结果: [APPLE, BANANA, CHERRY]

静态方法引用:
原数据: [-3, -1, 0, 2, 5]
绝对值: [3, 1, 0, 2, 5]
```

## 对象转换：实际业务应用

### 用户信息转换示例

让我们看看在实际业务中如何使用`map()`进行对象转换：

```java
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectTransformation {
    public static void main(String[] args) {
        System.out.println("=== 对象转换应用 ===");
        
        List<User> users = Arrays.asList(
            new User("张三", 1995, "北京", "开发工程师"),
            new User("李四", 1988, "上海", "产品经理"),
            new User("王五", 1992, "广州", "测试工程师"),
            new User("赵六", 1985, "深圳", "架构师")
        );
        
        // 1. 提取用户名列表
        System.out.println("提取用户名:");
        List<String> names = users.stream()
            .map(User::getName)  // 方法引用提取姓名
            .collect(Collectors.toList());
        System.out.println("用户名: " + names);
        
        // 2. 计算用户年龄
        int currentYear = LocalDate.now().getYear();
        System.out.println("\n计算用户年龄:");
        List<Integer> ages = users.stream()
            .map(user -> currentYear - user.getBirthYear())  // 计算年龄
            .collect(Collectors.toList());
        System.out.println("年龄: " + ages);
        
        // 3. 格式化用户信息
        System.out.println("\n格式化用户信息:");
        List<String> userInfos = users.stream()
            .map(user -> String.format("%s(%d岁) - %s", 
                user.getName(), 
                currentYear - user.getBirthYear(),
                user.getPosition()))
            .collect(Collectors.toList());
        userInfos.forEach(System.out::println);
        
        // 4. 转换为用户摘要对象
        System.out.println("\n转换为用户摘要:");
        List<UserSummary> summaries = users.stream()
            .map(user -> new UserSummary(
                user.getName(),
                currentYear - user.getBirthYear(),
                user.getCity().length() > 2 ? user.getCity().substring(0, 2) : user.getCity()
            ))
            .collect(Collectors.toList());
        summaries.forEach(System.out::println);
    }
}

class User {
    private String name;
    private int birthYear;
    private String city;
    private String position;
    
    public User(String name, int birthYear, String city, String position) {
        this.name = name;
        this.birthYear = birthYear;
        this.city = city;
        this.position = position;
    }
    
    // getter方法
    public String getName() { return name; }
    public int getBirthYear() { return birthYear; }
    public String getCity() { return city; }
    public String getPosition() { return position; }
}

class UserSummary {
    private String name;
    private int age;
    private String cityPrefix;
    
    public UserSummary(String name, int age, String cityPrefix) {
        this.name = name;
        this.age = age;
        this.cityPrefix = cityPrefix;
    }
    
    @Override
    public String toString() {
        return String.format("摘要[%s, %d岁, %s地区]", name, age, cityPrefix);
    }
}
```

**输出结果：**

```
=== 对象转换应用 ===
提取用户名:
用户名: [张三, 李四, 王五, 赵六]

计算用户年龄:
年龄: [30, 37, 33, 40]

格式化用户信息:
张三(30岁) - 开发工程师
李四(37岁) - 产品经理
王五(33岁) - 测试工程师
赵六(40岁) - 架构师

转换为用户摘要:
摘要[张三, 30岁, 北京地区]
摘要[李四, 37岁, 上海地区]
摘要[王五, 33岁, 广州地区]
摘要[赵六, 40岁, 深圳地区]
```

## 特殊类型的map()：mapToInt、mapToLong、mapToDouble

当转换结果是基本数据类型时，使用专门的map方法可以避免装箱拆箱，提升性能：

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class PrimitiveMap {
    public static void main(String[] args) {
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

class Product {
    private String name;
    private int price;
    
    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }
    
    public String getName() { return name; }
    public int getPrice() { return price; }
}
```

**输出结果：**

```
=== 基本类型map()优化 ===
字符串长度统计:
各单词长度: [4, 6, 10, 2]
总长度: 22
平均长度: 5.5

价格计算:
总价: 12297.00元
含税价格:
3238.92 9718.92 322.92 
```

## 链式map()：多重转换

多个`map()`可以链式调用，实现复杂的数据转换管道：

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChainedMap {
    public static void main(String[] args) {
        System.out.println("=== 链式map()转换 ===");
        
        List<String> sentences = Arrays.asList(
            "hello world", 
            "java stream api", 
            "functional programming"
        );
        
        // 多重转换：字符串 -> 大写 -> 替换空格 -> 添加前缀
        System.out.println("多重转换处理:");
        List<String> processed = sentences.stream()
            .map(String::toUpperCase)           // 第1步：转大写
            .map(s -> s.replace(" ", "_"))      // 第2步：替换空格
            .map(s -> "PREFIX_" + s)            // 第3步：添加前缀
            .collect(Collectors.toList());
        
        System.out.println("原始数据: " + sentences);
        System.out.println("处理结果: " + processed);
        
        // 数值处理链：原数 -> 平方 -> 加10 -> 转字符串
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println("\n数值处理链:");
        List<String> numberProcessed = numbers.stream()
            .map(n -> n * n)                    // 平方
            .map(n -> n + 10)                   // 加10
            .map(n -> "result_" + n)            // 转字符串并加前缀
            .collect(Collectors.toList());
        
        System.out.println("原始数字: " + numbers);
        System.out.println("处理结果: " + numberProcessed);
        
        // 使用peek()调试中间步骤
        System.out.println("\n调试中间步骤:");
        numbers.stream()
               .peek(n -> System.out.println("输入: " + n))
               .map(n -> n * n)
               .peek(n -> System.out.println("平方后: " + n))
               .map(n -> n + 10)
               .peek(n -> System.out.println("加10后: " + n))
               .map(n -> "result_" + n)
               .forEach(result -> System.out.println("最终结果: " + result));
    }
}
```

**输出结果：**

```
=== 链式map()转换 ===
多重转换处理:
原始数据: [hello world, java stream api, functional programming]
处理结果: [PREFIX_HELLO_WORLD, PREFIX_JAVA_STREAM_API, PREFIX_FUNCTIONAL_PROGRAMMING]

数值处理链:
原始数字: [1, 2, 3, 4, 5]
处理结果: [result_11, result_14, result_19, result_26, result_35]

调试中间步骤:
输入: 1
平方后: 1
加10后: 11
最终结果: result_11
输入: 2
平方后: 4
加10后: 14
最终结果: result_14
输入: 3
平方后: 9
加10后: 19
最终结果: result_19
输入: 4
平方后: 16
加10后: 26
最终结果: result_26
输入: 5
平方后: 25
加10后: 35
最终结果: result_35
```

## map()与filter()组合：数据处理管道

`map()`经常与`filter()`组合使用，构成强大的数据处理管道：

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapFilterCombination {
    public static void main(String[] args) {
        System.out.println("=== map()与filter()组合 ===");
        
        List<Employee> employees = Arrays.asList(
            new Employee("张三", 28, 12000),
            new Employee("李四", 35, 15000),
            new Employee("王五", 23, 8000),
            new Employee("赵六", 31, 18000),
            new Employee("孙七", 26, 10000)
        );
        
        // 需求：找出高薪员工(>12000)的姓名，并格式化显示
        System.out.println("高薪员工名单:");
        List<String> highSalaryNames = employees.stream()
            .filter(emp -> emp.getSalary() > 12000)     // 先过滤
            .map(emp -> emp.getName() + "(" + emp.getSalary() + "元)")  // 再转换格式
            .collect(Collectors.toList());
        highSalaryNames.forEach(System.out::println);
        
        // 需求：30岁以下员工的平均工资
        System.out.println("\n30岁以下员工的平均工资:");
        double avgSalary = employees.stream()
            .filter(emp -> emp.getAge() < 30)           // 筛选30岁以下
            .mapToInt(Employee::getSalary)              // 提取工资
            .average()                                  // 计算平均值
            .orElse(0);
        System.out.printf("平均工资: %.2f元\n", avgSalary);
        
        // 顺序很重要！先过滤再转换vs先转换再过滤
        System.out.println("\n顺序优化对比:");
        
        // ✅ 效率更高：先过滤，减少map()操作次数
        long count1 = employees.stream()
            .filter(emp -> emp.getAge() > 25)           // 先过滤
            .map(emp -> emp.getName().toUpperCase())    // 再转换（只处理筛选后的数据）
            .count();
        
        // ❌ 效率较低：先转换，对所有数据进行map()操作
        long count2 = employees.stream()
            .map(emp -> emp.getName().toUpperCase())    // 先转换（处理所有数据）
            .filter(name -> name.length() > 2)          // 再过滤
            .count();
        
        System.out.println("优化后处理的数据量: " + count1);
        System.out.println("未优化处理的数据量: " + count2);
    }
}

class Employee {
    private String name;
    private int age;
    private int salary;
    
    public Employee(String name, int age, int salary) {
        this.name = name;
        this.age = age;
        this.salary = salary;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
    public int getSalary() { return salary; }
}
```

**输出结果：**

```
=== map()与filter()组合 ===
高薪员工名单:
李四(15000元)
赵六(18000元)

30岁以下员工的平均工资:
平均工资: 10000.00元

顺序优化对比:
优化后处理的数据量: 4
未优化处理的数据量: 0
```

## 实战案例：订单数据处理系统

让我们构建一个完整的订单数据处理系统，展示`map()`在实际业务中的应用：

```java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderProcessingSystem {
    public static void main(String[] args) {
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

class OrderProcessor {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // 生成订单摘要
    public List<String> generateOrderSummaries(List<Order> orders) {
        return orders.stream()
            .map(order -> String.format("订单%s: %s购买，金额%.2f元，日期%s",
                order.getOrderId(),
                order.getCustomerName(),
                order.getAmount(),
                order.getOrderDate().format(dateFormatter)))
            .collect(Collectors.toList());
    }
    
    // 计算含税订单
    public List<TaxedOrder> calculateTaxedOrders(List<Order> orders, double taxRate) {
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
    public List<CustomerBill> generateCustomerBills(List<Order> orders) {
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
    public List<String> convertToCsvFormat(List<Order> orders) {
        return orders.stream()
            .map(order -> String.join(",",
                order.getOrderId(),
                order.getCustomerName(),
                String.valueOf(order.getAmount()),
                order.getOrderDate().toString()))
            .collect(Collectors.toList());
    }
    
    private int calculatePoints(double amount) {
        return (int) (amount / 10);  // 每10元1积分
    }
}

class Order {
    private String orderId;
    private String customerName;
    private double amount;
    private LocalDate orderDate;
    
    public Order(String orderId, String customerName, double amount, LocalDate orderDate) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
        this.orderDate = orderDate;
    }
    
    // getter方法
    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public double getAmount() { return amount; }
    public LocalDate getOrderDate() { return orderDate; }
}

class TaxedOrder {
    private String orderId;
    private String customerName;
    private double originalAmount;
    private double taxAmount;
    private double totalAmount;
    
    public TaxedOrder(String orderId, String customerName, double originalAmount, 
                     double taxAmount, double totalAmount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.originalAmount = originalAmount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s，原价%.2f，税额%.2f，总计%.2f",
            orderId, customerName, originalAmount, taxAmount, totalAmount);
    }
}

class CustomerBill {
    private String customerName;
    private double amount;
    private int points;
    private LocalDate billDate;
    
    public CustomerBill(String customerName, double amount, int points, LocalDate billDate) {
        this.customerName = customerName;
        this.amount = amount;
        this.points = points;
        this.billDate = billDate;
    }
    
    @Override
    public String toString() {
        return String.format("客户%s: 消费%.2f元，获得%d积分，日期%s",
            customerName, amount, points, billDate);
    }
}
```

**输出结果：**

```
=== 订单数据处理系统 ===
订单摘要报告:
订单ORD001: 张三购买，金额1299.99元，日期2025-08-20
订单ORD002: 李四购买，金额899.50元，日期2025-08-21
订单ORD003: 王五购买，金额1599.00元，日期2025-08-22
订单ORD004: 赵六购买，金额599.99元，日期2025-08-23

含税价格计算（税率10%）:
ORD001: 张三，原价1299.99，税额130.00，总计1429.99
ORD002: 李四，原价899.50，税额89.95，总计989.45
ORD003: 王五，原价1599.00，税额159.90，总计1758.90
ORD004: 赵六，原价599.99，税额60.00，总计659.99

客户账单:
客户张三: 消费1299.99元，获得129积分，日期2025-08-20
客户李四: 消费899.50元，获得89积分，日期2025-08-21
客户王五: 消费1599.00元，获得159积分，日期2025-08-22
客户赵六: 消费599.99元，获得59积分，日期2025-08-23

CSV导出格式:
ORD001,张三,1299.99,2025-08-20
ORD002,李四,899.5,2025-08-21
ORD003,王五,1599.0,2025-08-22
ORD004,赵六,599.99,2025-08-23
```

## 本章小结

今天我们深入学习了`map()`方法的强大功能：

**核心概念：**
- **一对一转换**：map()不改变元素数量，只改变元素的类型或值
- **函数式转换**：通过传入转换函数，实现灵活的数据变换
- **链式调用**：多个map()可以串联，构建数据转换管道

**重要方法：**
- **map()**：通用转换方法
- **mapToInt/mapToLong/mapToDouble**：转换为基本类型流，性能更好
- **方法引用**：简化常见的转换操作

**实用技巧：**
- **与filter()组合**：先过滤再转换，提升性能
- **链式转换**：多步骤数据处理管道
- **peek()调试**：查看中间转换步骤

**性能考虑：**
- 合理安排操作顺序：先filter()后map()
- 使用mapToInt等避免装箱拆箱
- 避免在map()中进行重复计算

**实际应用场景：**
- 数据格式转换（对象转字符串、类型转换）
- 业务计算（价格计算、积分计算）
- 数据提取（从复杂对象中提取字段）
- 报表生成（格式化输出）

下一章我们将学习《flatMap()：处理嵌套数据的利器》，探索如何优雅地处理复杂的嵌套数据结构！

---

**源代码地址：** https://github.com/qianmoQ/tutorial/tree/main/java8-stream-tutorial/src/main/java/org/devlive/tutorial/stream/chapter06