[TOC]

在实际项目中，我们需要做一个测试数据生成器，要能持续生成各种测试数据，比如用户行为序列、订单流水，还有一些数学序列用于算法验证。关键是这些数据要能无限生成，但又要可控制。

听到这个需求，你第一反应是："无限生成数据？那不得写个while(true)循环？"，但是 Java Stream 有 `generate()`和`iterate()`方法。这两个方法就像数据的"永动机"，可以源源不断地产生数据，而且还能优雅地控制！

## generate()：万能数据工厂

### 基础用法：简单数据生成

`generate()`方法就像一个数据工厂，你给它一个"生产配方"（Supplier函数），它就能按照这个配方无限生产数据：

```java
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class GenerateBasics {
    public static void main(String[] args) {
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
```

**输出结果：**

```
=== generate()基础用法 ===
生成5个问候语:
Hello Stream!
Hello Stream!
Hello Stream!
Hello Stream!
Hello Stream!

生成8个随机数:
53 54 41 88 1 48 54 15 

生成6个随机大写字母:
W I Z K R K 
```

⚠️ **重要提醒**：`generate()`产生的是无限流，必须使用`limit()`限制数量，否则程序会无限运行下去！

### 高级用法：带状态的数据生成

有时候我们需要生成带状态的数据，比如计数器、累加器等：

```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StatefulGenerate {
    public static void main(String[] args) {
        System.out.println("=== 带状态的generate()用法 ===");
        
        // 1. 使用匿名内部类实现计数器
        System.out.println("递增计数器:");
        Stream.generate(new Supplier<Integer>() {
            private int counter = 0;
            
            @Override
            public Integer get() {
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
        Stream.generate(new Supplier<String>() {
            private int messageNo = 1;
            
            @Override
            public String get() {
                return "消息编号: " + (messageNo++);
            }
        })
        .limit(4)
        .forEach(System.out::println);
    }
}
```

**输出结果：**

```
=== 带状态的generate()用法 ===
递增计数器:
1 2 3 4 5 6 7 8 

线程安全计数器:
1 2 3 4 5 6 

带编号的消息:
消息编号: 1
消息编号: 2
消息编号: 3
消息编号: 4
```

💡 **小贴士**：匿名内部类适合简单状态管理，`AtomicInteger`适合并发环境。

## iterate()：基于规律的序列生成器

### 基础序列生成

`iterate()`就像数学中的递推数列，给定初值和递推规则，就能生成无限序列：

```java
import java.util.stream.Stream;

public class IterateBasics {
    public static void main(String[] args) {
        System.out.println("=== iterate()基础用法 ===");
        
        // 1. 等差数列
        System.out.println("等差数列（首项1，公差2）:");
        Stream.iterate(1, n -> n + 2)  // 1, 3, 5, 7, 9...
              .limit(8)
              .forEach(num -> System.out.print(num + " "));
        
        // 2. 等比数列
        System.out.println("\n\n等比数列（首项2，公比3）:");
        Stream.iterate(2, n -> n * 3)  // 2, 6, 18, 54...
              .limit(6)
              .forEach(num -> System.out.print(num + " "));
        
        // 3. 2的幂次数列
        System.out.println("\n\n2的幂次数列:");
        Stream.iterate(1, n -> n * 2)  // 1, 2, 4, 8, 16...
              .limit(8)
              .forEach(num -> System.out.print(num + " "));
    }
}
```

**输出结果：**

```
=== iterate()基础用法 ===
等差数列（首项1，公差2）:
1 3 5 7 9 11 13 15 

等比数列（首项2，公比3）:
2 6 18 54 162 486 

2的幂次数列:
1 2 4 8 16 32 64 128
```

### 复杂序列生成

让我们看看如何生成一些经典的数学序列：

```java
import java.util.stream.Stream;

public class AdvancedIterate {
    public static void main(String[] args) {
        System.out.println("=== 复杂序列生成 ===");
        
        // 1. 斐波那契数列
        System.out.println("斐波那契数列:");
        Stream.iterate(new long[]{0, 1}, arr -> new long[]{arr[1], arr[0] + arr[1]})
              .limit(10)
              .mapToLong(arr -> arr[0])  // 提取第一个数
              .forEach(num -> System.out.print(num + " "));
        
        // 2. 三角数序列（1+2+3+...+n）
        System.out.println("\n\n三角数序列:");
        Stream.iterate(new int[]{1, 1}, arr -> new int[]{arr[0] + 1, arr[1] + arr[0] + 1})
              .limit(8)
              .mapToInt(arr -> arr[1])  // 提取三角数
              .forEach(num -> System.out.print(num + " "));
        
        // 3. 质数序列
        System.out.println("\n\n质数序列:");
        Stream.iterate(2, n -> n + 1)
              .filter(AdvancedIterate::isPrime)  // 过滤出质数
              .limit(8)
              .forEach(num -> System.out.print(num + " "));
    }
    
    // 简单的质数判断
    private static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
```

**输出结果：**

```
=== 复杂序列生成 ===
斐波那契数列:
0 1 1 2 3 5 8 13 21 34 

三角数序列:
1 3 6 10 15 21 28 36 

质数序列:
2 3 5 7 11 13 17 19
```

### 实际应用：时间序列和数据模拟

`iterate()`在实际业务中特别适合生成时间序列：

```java
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class TimeSeriesDemo {
    public static void main(String[] args) {
        System.out.println("=== 时间序列应用 ===");
        
        // 1. 生成连续日期序列
        System.out.println("未来一周日期:");
        LocalDate today = LocalDate.now();
        Stream.iterate(today, date -> date.plusDays(1))
              .limit(7)
              .forEach(date -> System.out.println("日期: " + date));
        
        // 2. 生成每4小时的时间点
        System.out.println("\n每4小时的时间点:");
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0);
        Stream.iterate(now, time -> time.plusHours(4))
              .limit(6)
              .forEach(time -> System.out.println("时间: " + 
                  time.format(DateTimeFormatter.ofPattern("HH:mm"))));
        
        // 3. 模拟股价波动
        System.out.println("\n模拟股价走势（基准100元）:");
        Stream.iterate(100.0, price -> price + (Math.random() - 0.5) * 10)
              .limit(5)
              .forEach(price -> System.out.printf("股价: %.2f元\n", price));
    }
}
```

**输出结果：**

```
=== 时间序列应用 ===
未来一周日期:
日期: 2025-08-23
日期: 2025-08-24
日期: 2025-08-25
日期: 2025-08-26
日期: 2025-08-27
日期: 2025-08-28
日期: 2025-08-29

每4小时的时间点:
时间: 10:00
时间: 14:00
时间: 18:00
时间: 22:00
时间: 02:00
时间: 06:00

模拟股价走势（基准100元）:
股价: 100.00元
股价: 104.54元
股价: 105.13元
股价: 102.95元
股价: 105.55元
```

## generate() vs iterate()：如何选择？

| 场景 | 推荐方法 | 原因 |
|------|----------|------|
| 随机数生成 | `generate()` | 每次调用独立，无状态依赖 |
| 数学序列 | `iterate()` | 基于前值计算，天然递推 |
| 固定值重复 | `generate()` | 简单的Supplier即可 |
| 时间序列 | `iterate()` | 基于时间递推规律 |
| 复杂状态管理 | `generate()` | 可以封装复杂逻辑 |

## 常见问题和解决方案

### 问题1：忘记限制导致无限循环

```java
// ❌ 危险！会无限运行
Stream.iterate(1, n -> n + 1).forEach(System.out::println);

// ✅ 正确：添加限制
Stream.iterate(1, n -> n + 1).limit(10).forEach(System.out::println);
```

### 问题2：并发环境下的状态安全

```java
// ❌ 在并发环境下可能出问题
int[] counter = {0};
Stream.generate(() -> ++counter[0])
      .parallel()  // 并行处理
      .limit(1000)
      .forEach(System.out::println);

// ✅ 使用线程安全的计数器
AtomicInteger safeCounter = new AtomicInteger(0);
Stream.generate(() -> safeCounter.incrementAndGet())
      .parallel()
      .limit(1000)
      .forEach(System.out::println);
```

## 实战案例：测试数据生成器

让我们用两种方法解决开篇提到的测试数据生成需求：

```java
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TestDataGenerator {
    public static void main(String[] args) {
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

class DataGenerator {
    private final Random random = new Random();
    private final AtomicLong orderIdGenerator = new AtomicLong(1000000L);
    private final List<String> names = Arrays.asList("张三", "李四", "王五", "赵六");
    private final List<String> cities = Arrays.asList("北京", "上海", "广州", "深圳");
    
    // 使用generate()生成随机用户数据
    public Stream<String> generateUsers(int count) {
        return Stream.generate(() -> {
            String name = names.get(random.nextInt(names.size()));
            int age = 18 + random.nextInt(50);
            String city = cities.get(random.nextInt(cities.size()));
            return String.format("用户[%s, %d岁, %s]", name, age, city);
        }).limit(count);
    }
    
    // 使用iterate()生成递增订单序列
    public Stream<String> generateOrders(int count) {
        return Stream.iterate(100.0, amount -> amount + 50.0 + random.nextDouble() * 100.0)
                     .limit(count)
                     .map(amount -> String.format("订单[ID:%d, 金额:%.2f元]", 
                         orderIdGenerator.incrementAndGet(), amount));
    }
    
    // 使用iterate()生成TPS性能数据
    public Stream<String> generateTpsData(int count) {
        return Stream.iterate(1000, tps -> (int)(tps * (0.9 + random.nextDouble() * 0.2)))
                     .limit(count)
                     .map(tps -> String.format("TPS: %d, 响应时间: %.2fms", 
                         tps, 30.0 + random.nextDouble() * 40.0));
    }
}
```

**输出结果：**

```
=== 测试数据生成器 ===
随机用户数据:
用户[赵六, 21岁, 广州]
用户[王五, 66岁, 深圳]
用户[李四, 49岁, 广州]

递增订单序列:
订单[ID:1000001, 金额:100.00元]
订单[ID:1000002, 金额:195.39元]
订单[ID:1000003, 金额:338.82元]
订单[ID:1000004, 金额:469.57元]

性能测试TPS数据:
TPS: 1000, 响应时间: 63.14ms
TPS: 1017, 响应时间: 62.11ms
TPS: 1091, 响应时间: 60.15ms
TPS: 1109, 响应时间: 54.05ms
TPS: 1170, 响应时间: 57.35ms
```

这个案例完美展示了两种方法的结合使用：
- **generate()** 生成随机的用户数据
- **iterate()** 生成递增的订单序列和有波动的TPS数据

## 本章小结

今天我们深入学习了Stream的两大无限流生成方法：

**generate()的特点：**
- 适合生成随机数据和无状态依赖的数据
- 需要提供Supplier函数
- 状态管理需要考虑线程安全

**iterate()的特点：**
- 适合生成基于规律的序列数据
- 天然支持递推逻辑
- 特别适合数学序列和时间序列

**核心要点：**
- 🚨 **必须使用limit()限制数量**，避免无限循环
- 选择合适的方法：随机数据用generate()，序列数据用iterate()
- 并发环境下注意线程安全

下一章我们将学习《数字流专题：IntStream让数学计算更简单》，探索专门针对数字处理优化的Stream类型！

---

**源代码地址：** https://github.com/qianmoQ/tutorial/tree/main/java8-stream-tutorial/src/main/java/org/devlive/tutorial/stream/chapter03