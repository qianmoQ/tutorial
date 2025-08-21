[TOC]

想象一下这个场景：你正在开发一个电商系统，产品经理急匆匆跑到你面前说："小张，我们需要从订单列表中筛选出金额大于1000元的订单，然后按照订单金额降序排列，最后只取前5条记录用于VIP客户推荐。明天上午就要！"

听到这个需求，你的第一反应可能是："又是嵌套循环的活儿..."。确实，在Java 8之前，我们通常需要写好几层嵌套循环，创建临时集合，还要手写排序逻辑。代码写出来又长又乱，维护起来就像在迷宫里找出口。

但是，如果我告诉你，用Java 8的Stream，这个需求只需要一行链式调用就能搞定，你信不信？今天我们就来揭开Stream的神秘面纱，让你的循环代码从此变得优雅起来。

## 什么是Stream？用流水线思维处理数据

### Stream的本质：数据的流水线

Stream就像是工厂里的流水线，数据从一端进入，经过一系列加工处理，最终从另一端输出我们想要的结果。想象一下苹果汁的生产过程：

1. **原材料**：一堆苹果（原始数据集合）
2. **清洗工序**：去掉坏苹果（filter过滤）
3. **切片工序**：把苹果切成小块（map转换）
4. **榨汁工序**：提取果汁（collect收集结果）

Stream的处理过程和这个流水线一模一样！

### 第一个Stream例子：告别传统循环

让我们先来看看传统方式和Stream方式的对比：

```java
import java.util.*;
import java.util.stream.Collectors;

public class StreamIntroduction {
    public static void main(String[] args) {
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
    public static void traditionalWay(List<Order> orders) {
        // 1. 创建临时集合存储筛选结果
        List<Order> filteredOrders = new ArrayList<>();
        
        // 2. 循环筛选金额大于1000的订单
        for (Order order : orders) {
            if (order.getAmount() > 1000) {
                filteredOrders.add(order);
            }
        }
        
        // 3. 手动排序（降序）
        Collections.sort(filteredOrders, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
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
    public static void streamWay(List<Order> orders) {
        orders.stream()                                    // 创建流
              .filter(order -> order.getAmount() > 1000)  // 筛选金额>1000的订单
              .sorted((o1, o2) -> Double.compare(o2.getAmount(), o1.getAmount())) // 降序排序
              .limit(5)                                    // 取前5条
              .forEach(System.out::println);               // 输出每个订单
    }
}

// 订单类
class Order {
    private String orderId;
    private Double amount;
    
    public Order(String orderId, Double amount) {
        this.orderId = orderId;
        this.amount = amount;
    }
    
    public String getOrderId() { return orderId; }
    public Double getAmount() { return amount; }
    
    @Override
    public String toString() {
        return String.format("订单号: %s, 金额: %.2f元", orderId, amount);
    }
}
```

**输出结果：**

```
=== 传统方式 ===
订单号: ORD006, 金额: 2000.00元
订单号: ORD004, 金额: 1500.00元
订单号: ORD002, 金额: 1200.00元
订单号: ORD007, 金额: 1100.00元

=== Stream方式 ===
订单号: ORD006, 金额: 2000.00元
订单号: ORD004, 金额: 1500.00元
订单号: ORD002, 金额: 1200.00元
订单号: ORD007, 金额: 1100.00元
```

看到区别了吗？传统方式需要20多行代码，而Stream方式只需要5行！

💡 **小贴士**：Stream的链式调用读起来就像说话一样自然："从订单流中，筛选金额大于1000的，然后按金额降序排列，取前5条，最后打印出来"。

### Stream的核心概念：中间操作 vs 终端操作

Stream的操作分为两类，就像我们组装乐高一样：

1. **中间操作（Intermediate Operations）**：负责加工数据，可以串联多个
    - `filter()`、`map()`、`sorted()`、`distinct()`等
    - 特点：返回新的Stream，支持链式调用
    - 懒加载：不会立即执行，等到遇到终端操作才开始处理

2. **终端操作（Terminal Operations）**：负责触发处理并产生最终结果
    - `collect()`、`forEach()`、`reduce()`、`count()`等
    - 特点：返回具体结果，结束Stream链

让我们用一个简单的例子来理解：

```java
import java.util.*;
import java.util.stream.Collectors;

public class StreamOperationTypes {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("张三", "李四", "王五", "赵六", "孙七");
        
        System.out.println("=== 演示中间操作和终端操作 ===");
        
        // 这里只有中间操作，不会执行任何处理
        System.out.println("只创建中间操作...");
        names.stream()
             .filter(name -> {
                 System.out.println("过滤: " + name); // 这行不会打印！
                 return name.length() == 2;
             })
             .map(name -> {
                 System.out.println("转换: " + name); // 这行也不会打印！
                 return name + "先生";
             });
        
        System.out.println("没有任何输出，因为缺少终端操作！\n");
        
        // 添加终端操作，整个链才会执行
        System.out.println("添加终端操作后：");
        List<String> result = names.stream()
             .filter(name -> {
                 System.out.println("过滤: " + name); // 现在会打印了
                 return name.length() == 2;
             })
             .map(name -> {
                 System.out.println("转换: " + name); // 现在会打印了
                 return name + "先生";
             })
             .collect(Collectors.toList()); // 终端操作：收集到List
        
        System.out.println("最终结果: " + result);
    }
}
```

**输出结果：**

```
=== 演示中间操作和终端操作 ===
只创建中间操作...
没有任何输出，因为缺少终端操作！

添加终端操作后：
过滤: 张三
转换: 张三
过滤: 李四
转换: 李四
过滤: 王五
转换: 王五
过滤: 赵六
转换: 赵六
过滤: 孙七
转换: 孙七
最终结果: [张三先生, 李四先生, 王五先生, 赵六先生, 孙七先生]
```

⚠️ **重要提醒**：中间操作是懒加载的，只有遇到终端操作才会真正开始处理数据。这就像是你在流水线上设置了各种工序，但是直到有人按下"开始"按钮，流水线才会运转。

### Stream的优势：为什么要用它？

让我们再通过一个实际例子来看看Stream的优势：

```java
import java.util.*;
import java.util.stream.Collectors;

public class StreamAdvantages {
    public static void main(String[] args) {
        // 模拟员工数据
        List<Employee> employees = Arrays.asList(
            new Employee("张三", 25, 8000),
            new Employee("李四", 30, 12000),
            new Employee("王五", 28, 9000),
            new Employee("赵六", 35, 15000),
            new Employee("孙七", 23, 7000)
        );
        
        System.out.println("需求：找出年龄大于25岁且薪资大于8000的员工姓名");
        
        // Stream方式：简洁明了
        List<String> result = employees.stream()
            .filter(emp -> emp.getAge() > 25)           // 年龄筛选
            .filter(emp -> emp.getSalary() > 8000)      // 薪资筛选  
            .map(Employee::getName)                     // 提取姓名
            .collect(Collectors.toList());              // 收集结果
        
        System.out.println("符合条件的员工: " + result);
        
        // 另一个需求：按薪资分组统计
        System.out.println("\n按薪资等级分组：");
        Map<String, List<String>> salaryGroups = employees.stream()
            .collect(Collectors.groupingBy(
                emp -> emp.getSalary() >= 10000 ? "高薪" : "普通",
                Collectors.mapping(Employee::getName, Collectors.toList())
            ));
        
        salaryGroups.forEach((group, names) -> 
            System.out.println(group + "组: " + names));
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
    
    @Override
    public String toString() {
        return String.format("%s(年龄:%d, 薪资:%d)", name, age, salary);
    }
}
```

**输出结果：**

```
需求：找出年龄大于25岁且薪资大于8000的员工姓名
符合条件的员工: [李四, 王五, 赵六]

按薪资等级分组：
普通组: [张三, 王五, 孙七]
高薪组: [李四, 赵六]
```

**Stream的主要优势：**

1. **代码简洁**：告别嵌套循环，链式调用更直观
2. **可读性强**：代码读起来像自然语言
3. **函数式编程**：减少可变状态，降低bug概率
4. **并行处理**：轻松实现多线程处理（后续章节详解）
5. **懒加载**：只有需要时才处理，性能更好

## 常见问题和解决方案

### 问题1：Stream只能使用一次

```java
// ❌ 错误用法：Stream使用后就被消费了
Stream<String> stream = Arrays.asList("a", "b", "c").stream();
stream.forEach(System.out::println); // 第一次使用
stream.forEach(System.out::println); // 💥 报错：stream has already been operated upon or closed

// ✅ 正确用法：重新创建Stream
List<String> list = Arrays.asList("a", "b", "c");
list.stream().forEach(System.out::println); // 第一次
list.stream().forEach(System.out::println); // 第二次
```

💡 **解决方案**：把Stream想象成一次性的传送带，用完就没了。如果需要多次处理，就从原数据源重新创建Stream。

### 问题2：忘记终端操作

```java
// ❌ 忘记终端操作，什么都不会执行
list.stream().filter(s -> s.length() > 2); 

// ✅ 添加终端操作
list.stream().filter(s -> s.length() > 2).collect(Collectors.toList());
```

## 实战案例：用户行为数据分析

假设你在一家互联网公司工作，需要分析用户行为数据。现在要统计：活跃用户中，按照年龄段分组，计算每个年龄段用户的平均消费金额。

```java
import java.util.*;
import java.util.stream.Collectors;

public class UserBehaviorAnalysis {
    public static void main(String[] args) {
        // 模拟用户行为数据
        List<UserBehavior> behaviors = Arrays.asList(
            new UserBehavior("user001", 25, 1200.0, true),
            new UserBehavior("user002", 35, 800.0, false),
            new UserBehavior("user003", 28, 1500.0, true),
            new UserBehavior("user004", 42, 2000.0, true),
            new UserBehavior("user005", 31, 900.0, false),
            new UserBehavior("user006", 26, 1100.0, true),
            new UserBehavior("user007", 38, 1800.0, true),
            new UserBehavior("user008", 29, 1300.0, true)
        );
        
        System.out.println("=== 活跃用户年龄段消费分析 ===");
        
        // 使用Stream进行复杂数据分析
        Map<String, Double> ageGroupAvgConsumption = behaviors.stream()
            .filter(UserBehavior::isActive)                    // 只要活跃用户
            .collect(Collectors.groupingBy(                    // 按年龄段分组
                behavior -> {
                    int age = behavior.getAge();
                    if (age < 30) return "青年组(20-29岁)";
                    else if (age < 40) return "中年组(30-39岁)";
                    else return "成熟组(40+岁)";
                },
                Collectors.averagingDouble(UserBehavior::getConsumption) // 计算平均消费
            ));
        
        // 输出分析结果
        ageGroupAvgConsumption.forEach((ageGroup, avgConsumption) ->
            System.out.printf("%s: 平均消费 %.2f元\n", ageGroup, avgConsumption));
        
        // 额外分析：找出消费最高的活跃用户
        System.out.println("\n=== 活跃用户中的消费冠军 ===");
        behaviors.stream()
            .filter(UserBehavior::isActive)
            .max(Comparator.comparing(UserBehavior::getConsumption))
            .ifPresent(champion -> 
                System.out.printf("消费冠军: %s，年龄: %d岁，消费: %.2f元\n", 
                    champion.getUserId(), champion.getAge(), champion.getConsumption()));
    }
}

class UserBehavior {
    private String userId;
    private int age;
    private double consumption;
    private boolean isActive;
    
    public UserBehavior(String userId, int age, double consumption, boolean isActive) {
        this.userId = userId;
        this.age = age;
        this.consumption = consumption;
        this.isActive = isActive;
    }
    
    // getter methods
    public String getUserId() { return userId; }
    public int getAge() { return age; }
    public double getConsumption() { return consumption; }
    public boolean isActive() { return isActive; }
}
```

**输出结果：**

```
=== 活跃用户年龄段消费分析 ===
中年组(30-39岁): 平均消费 1800.00元
成熟组(40+岁): 平均消费 2000.00元
青年组(20-29岁): 平均消费 1275.00元

=== 活跃用户中的消费冠军 ===
消费冠军: user004，年龄: 42岁，消费: 2000.00元
```

这个例子展示了Stream在实际业务中的强大能力：仅仅几行代码就完成了复杂的数据分析任务！

## 本章小结

今天我们初步认识了Java 8 Stream这个强大的数据处理工具。关键要记住：

- **Stream是数据处理的流水线**，让复杂的集合操作变得简单优雅
- **中间操作 + 终端操作**的组合模式，记住中间操作是懒加载的
- **一次使用原则**：Stream用过就消费了，需要重复处理就重新创建
- **链式调用**让代码读起来像自然语言，提升可读性和维护性

下一章我们将深入学习《创建Stream的N种方法：让数据流起来》，探索各种创建Stream的方式，为后续的数据处理打好基础。准备好了吗？让我们继续这趟Stream的奇妙之旅！

---

**源代码地址：** https://github.com/qianmoQ/tutorial/tree/main/java8-stream-tutorial/src/main/java/org/devlive/tutorial/stream/chapter01
