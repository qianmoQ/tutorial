[TOC]

比如我们正在优化一个数据统计模块，需要处理大量的数值计算：求和、平均值、最大值等。用普通的Stream处理时，你发现了一个问题：

```java
// 这样写会有装箱拆箱的性能开销
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
int sum = numbers.stream()
    .mapToInt(Integer::intValue)  // 需要手动转换
    .sum();
```

正在为频繁的装箱拆箱操作担心性能时，忽然想到 Stream 中可以使用用IntStream、LongStream这些专门的数字流，性能更好，API也更简洁！

今天我们就来探索Java 8的数字流专题，看看IntStream、LongStream、DoubleStream如何让数学计算变得既高效又优雅！

## 数字流的优势：告别装箱拆箱

### 为什么需要数字流？

普通的`Stream<Integer>`在处理数字时存在装箱拆箱的性能开销，而`IntStream`直接处理基本类型，效率更高：

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class NumberStreamAdvantage {
    public static void main(String[] args) {
        System.out.println("=== 数字流的优势 ===");
        
        // 方式1: 普通Stream（有装箱拆箱开销）
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        long startTime = System.nanoTime();
        int sum1 = numbers.stream()
                          .mapToInt(Integer::intValue)  // 需要转换
                          .sum();
        long endTime = System.nanoTime();
        System.out.println("普通Stream求和: " + sum1 + ", 耗时: " + (endTime - startTime) + "ns");
        
        // 方式2: IntStream（直接处理基本类型）
        startTime = System.nanoTime();
        int sum2 = IntStream.rangeClosed(1, 10)  // 直接生成int流
                           .sum();
        endTime = System.nanoTime();
        System.out.println("IntStream求和: " + sum2 + ", 耗时: " + (endTime - startTime) + "ns");
        
        // 展示IntStream的简洁API
        System.out.println("\nIntStream的简洁API:");
        IntStream.rangeClosed(1, 10)
                .forEach(num -> System.out.print(num + " "));
    }
}
```

**输出结果：**

```
=== 数字流的优势 ===
普通Stream求和: 55, 耗时: 115598625ns
IntStream求和: 55, 耗时: 303500ns

IntStream的简洁API:
1 2 3 4 5 6 7 8 9 10 
```

💡 **性能提升关键**：IntStream避免了Integer对象的创建和拆箱操作，在大数据量处理时性能提升显著！

## IntStream：整数流的强大功能

### 创建IntStream的多种方式

```java
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class IntStreamCreation {
    public static void main(String[] args) {
        System.out.println("=== 创建IntStream的方式 ===");
        
        // 1. 从范围创建
        System.out.println("range(1, 5): ");
        IntStream.range(1, 5)  // 不包括5
                .forEach(num -> System.out.print(num + " "));
        
        System.out.println("\nrangeClosed(1, 5): ");
        IntStream.rangeClosed(1, 5)  // 包括5
                .forEach(num -> System.out.print(num + " "));
        
        // 2. 从数组创建
        System.out.println("\n\n从数组创建:");
        int[] arr = {10, 20, 30, 40};
        Arrays.stream(arr)
              .forEach(num -> System.out.print(num + " "));
        
        // 3. 从可变参数创建
        System.out.println("\n\n从可变参数创建:");
        IntStream.of(100, 200, 300)
                .forEach(num -> System.out.print(num + " "));
        
        // 4. 生成随机数流
        System.out.println("\n\n生成5个随机数(0-99):");
        new Random().ints(5, 0, 100)  // 生成5个[0,100)的随机数
                    .forEach(num -> System.out.print(num + " "));
        
        // 5. 无限流
        System.out.println("\n\n无限流(前6个偶数):");
        IntStream.iterate(2, n -> n + 2)
                .limit(6)
                .forEach(num -> System.out.print(num + " "));
    }
}
```

**输出结果：**

```
=== 创建IntStream的方式 ===
range(1, 5): 
1 2 3 4 
rangeClosed(1, 5): 
1 2 3 4 5 

从数组创建:
10 20 30 40 

从可变参数创建:
100 200 300 

生成5个随机数(0-99):
38 62 33 52 2 

无限流(前6个偶数):
2 4 6 8 10 12 
```

### IntStream的统计功能

IntStream提供了丰富的数学统计方法，让数值计算变得非常简单：

```java
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.IntSummaryStatistics;

public class IntStreamStatistics {
    public static void main(String[] args) {
        System.out.println("=== IntStream统计功能 ===");
        
        IntStream numbers = IntStream.of(12, 45, 23, 67, 34, 89, 56);
        
        // 基础统计
        System.out.println("数据: 12, 45, 23, 67, 34, 89, 56");
        System.out.println("总和: " + IntStream.of(12, 45, 23, 67, 34, 89, 56).sum());
        System.out.println("个数: " + IntStream.of(12, 45, 23, 67, 34, 89, 56).count());
        
        // Optional类型的统计（处理空流的情况）
        OptionalInt max = IntStream.of(12, 45, 23, 67, 34, 89, 56).max();
        OptionalInt min = IntStream.of(12, 45, 23, 67, 34, 89, 56).min();
        OptionalDouble avg = IntStream.of(12, 45, 23, 67, 34, 89, 56).average();
        
        System.out.println("最大值: " + (max.isPresent() ? max.getAsInt() : "无数据"));
        System.out.println("最小值: " + (min.isPresent() ? min.getAsInt() : "无数据"));
        System.out.println("平均值: " + (avg.isPresent() ? String.format("%.2f", avg.getAsDouble()) : "无数据"));
        
        // 一次性获取所有统计信息
        System.out.println("\n使用summaryStatistics()一次获取所有统计:");
        IntSummaryStatistics stats = IntStream.of(12, 45, 23, 67, 34, 89, 56).summaryStatistics();
        System.out.println("统计摘要: " + stats);
        System.out.printf("详细信息 - 个数:%d, 总和:%d, 平均:%.2f, 最小:%d, 最大:%d\n",
            stats.getCount(), stats.getSum(), stats.getAverage(), 
            stats.getMin(), stats.getMax());
    }
}
```

**输出结果：**

```
=== IntStream统计功能 ===
数据: 12, 45, 23, 67, 34, 89, 56
总和: 326
个数: 7
最大值: 89
最小值: 12
平均值: 46.57

使用summaryStatistics()一次获取所有统计:
统计摘要: IntSummaryStatistics{count=7, sum=326, min=12, average=46.571429, max=89}
详细信息 - 个数:7, 总和:326, 平均:46.57, 最小:12, 最大:89
```

### 数学运算和转换

IntStream还支持各种数学运算和类型转换：

```java
import java.util.stream.IntStream;

public class IntStreamMath {
    public static void main(String[] args) {
        System.out.println("=== IntStream数学运算 ===");
        
        // 1. 数学运算
        System.out.println("1到10的平方:");
        IntStream.rangeClosed(1, 10)
                .map(n -> n * n)  // 计算平方
                .forEach(square -> System.out.print(square + " "));
        
        System.out.println("\n\n筛选偶数并求和:");
        int evenSum = IntStream.rangeClosed(1, 20)
                              .filter(n -> n % 2 == 0)  // 筛选偶数
                              .sum();
        System.out.println("1到20中偶数之和: " + evenSum);
        
        // 2. 类型转换
        System.out.println("\n转换为double类型:");
        IntStream.of(1, 2, 3, 4, 5)
                .asDoubleStream()  // 转为DoubleStream
                .map(d -> d / 2.0)  // 除以2.0
                .forEach(result -> System.out.printf("%.1f ", result));
        
        // 3. 转换为对象流
        System.out.println("\n\n转换为String对象流:");
        IntStream.rangeClosed(1, 5)
                .mapToObj(n -> "数字" + n)  // 转为Stream<String>
                .forEach(str -> System.out.print(str + " "));
        
        // 4. 复合运算
        System.out.println("\n\n1到10中奇数的平方和:");
        int oddSquareSum = IntStream.rangeClosed(1, 10)
                                  .filter(n -> n % 2 == 1)  // 筛选奇数
                                  .map(n -> n * n)          // 计算平方
                                  .sum();                   // 求和
        System.out.println("结果: " + oddSquareSum);
    }
}
```

**输出结果：**

```
=== IntStream数学运算 ===
1到10的平方:
1 4 9 16 25 36 49 64 81 100 

筛选偶数并求和:
1到20中偶数之和: 110

转换为double类型:
0.5 1.0 1.5 2.0 2.5 

转换为String对象流:
数字1 数字2 数字3 数字4 数字5 

1到10中奇数的平方和:
结果: 165
```

## LongStream和DoubleStream：处理大数和浮点数

### LongStream：处理大整数

```java
import java.util.stream.LongStream;

public class LongStreamDemo {
    public static void main(String[] args) {
        System.out.println("=== LongStream处理大整数 ===");
        
        // 1. 大数范围
        System.out.println("生成10个大数:");
        LongStream.range(1000000000L, 1000000010L)
                 .forEach(num -> System.out.print(num + " "));
        
        // 2. 阶乘计算（使用reduce）
        System.out.println("\n\n计算10的阶乘:");
        long factorial = LongStream.rangeClosed(1, 10)
                                  .reduce(1, (a, b) -> a * b);
        System.out.println("10! = " + factorial);
        
        // 3. 大数求和
        System.out.println("\n1到1000000的和:");
        long bigSum = LongStream.rangeClosed(1, 1000000).sum();
        System.out.println("结果: " + bigSum);
    }
}
```

**输出结果：**

```
=== LongStream处理大整数 ===
生成10个大数:
1000000000 1000000001 1000000002 1000000003 1000000004 1000000005 1000000006 1000000007 1000000008 1000000009 

计算10的阶乘:
10! = 3628800

1到1000000的和:
结果: 500000500000
```

### DoubleStream：处理浮点数

```java
import java.util.stream.DoubleStream;
import java.util.DoubleSummaryStatistics;

public class DoubleStreamDemo {
    public static void main(String[] args) {
        System.out.println("=== DoubleStream处理浮点数 ===");
        
        // 1. 从数组创建
        double[] prices = {19.99, 29.99, 39.99, 49.99, 59.99};
        System.out.println("商品价格统计:");
        DoubleSummaryStatistics stats = DoubleStream.of(prices).summaryStatistics();
        System.out.printf("平均价格: %.2f, 最高价格: %.2f, 最低价格: %.2f\n",
            stats.getAverage(), stats.getMax(), stats.getMin());
        
        // 2. 数学计算
        System.out.println("\n计算税后价格（税率8%）:");
        DoubleStream.of(prices)
                   .map(price -> price * 1.08)  // 加税
                   .forEach(taxedPrice -> System.out.printf("%.2f ", taxedPrice));
        
        // 3. 生成随机浮点数
        System.out.println("\n\n生成5个随机浮点数(0.0-1.0):");
        DoubleStream.generate(Math::random)
                   .limit(5)
                   .forEach(num -> System.out.printf("%.3f ", num));
    }
}
```

**输出结果：**

```
=== DoubleStream处理浮点数 ===
商品价格统计:
平均价格: 39.99, 最高价格: 59.99, 最低价格: 19.99

计算税后价格（税率8%）:
21.59 32.39 43.19 53.99 64.79 

生成5个随机浮点数(0.0-1.0):
0.623 0.421 0.051 0.919 0.131 
```

## 数字流之间的转换

不同类型的数字流可以相互转换：

```java
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class NumberStreamConversion {
    public static void main(String[] args) {
        System.out.println("=== 数字流类型转换 ===");
        
        // IntStream -> LongStream -> DoubleStream
        System.out.println("IntStream转换链:");
        IntStream.rangeClosed(1, 5)
                .peek(i -> System.out.print("int:" + i + " "))
                .asLongStream()  // 转为LongStream
                .peek(l -> System.out.print("long:" + l + " "))
                .asDoubleStream()  // 转为DoubleStream
                .forEach(d -> System.out.print("double:" + d + " "));
        
        // 精度处理
        System.out.println("\n\n除法运算精度对比:");
        System.out.println("IntStream整数除法: ");
        IntStream.of(10, 15, 20)
                .map(n -> n / 3)  // 整数除法，会丢失小数部分
                .forEach(result -> System.out.print(result + " "));
        
        System.out.println("\nDoubleStream浮点除法: ");
        IntStream.of(10, 15, 20)
                .asDoubleStream()
                .map(n -> n / 3.0)  // 浮点除法，保留小数
                .forEach(result -> System.out.printf("%.2f ", result));
    }
}
```

**输出结果：**

```
=== 数字流类型转换 ===
IntStream转换链:
int:1 long:1 double:1.0 int:2 long:2 double:2.0 int:3 long:3 double:3.0 int:4 long:4 double:4.0 int:5 long:5 double:5.0 

除法运算精度对比:
IntStream整数除法: 
3 5 6 
DoubleStream浮点除法: 
3.33 5.00 6.67 
```

## 实战案例：成绩统计系统

让我们用数字流来构建一个学生成绩统计系统：

```java
import java.util.IntSummaryStatistics;
import java.util.stream.IntStream;

public class GradeStatisticsSystem {
    public static void main(String[] args) {
        System.out.println("=== 学生成绩统计系统 ===");

        // 模拟一个班级的成绩数据
        int[] mathScores = {85, 92, 78, 96, 88, 73, 91, 82, 87, 95, 79, 89, 84, 90, 76};
        int[] englishScores = {88, 85, 92, 89, 91, 82, 87, 90, 85, 93, 81, 86, 88, 92, 79};

        GradeAnalyzer analyzer = new GradeAnalyzer();

        // 数学成绩分析
        System.out.println("数学成绩分析:");
        analyzer.analyzeScores("数学", mathScores);

        System.out.println("\n英语成绩分析:");
        analyzer.analyzeScores("英语", englishScores);

        // 对比分析
        System.out.println("\n学科对比分析:");
        analyzer.compareSubjects(mathScores, englishScores);
    }
}

class GradeAnalyzer {

    public void analyzeScores(String subject, int[] scores) {
        IntSummaryStatistics stats = IntStream.of(scores).summaryStatistics();

        System.out.printf("%s科目统计:\n", subject);
        System.out.printf("  学生人数: %d人\n", stats.getCount());
        System.out.printf("  平均分: %.1f分\n", stats.getAverage());
        System.out.printf("  最高分: %d分\n", stats.getMax());
        System.out.printf("  最低分: %d分\n", stats.getMin());

        // 等级分布统计
        long excellent = IntStream.of(scores).filter(score -> score >= 90).count();
        long good = IntStream.of(scores).filter(score -> score >= 80 && score < 90).count();
        long average = IntStream.of(scores).filter(score -> score >= 70 && score < 80).count();
        long poor = IntStream.of(scores).filter(score -> score < 70).count();

        System.out.println("  成绩分布:");
        System.out.printf("    优秀(90+): %d人 (%.1f%%)\n", excellent, excellent * 100.0 / stats.getCount());
        System.out.printf("    良好(80-89): %d人 (%.1f%%)\n", good, good * 100.0 / stats.getCount());
        System.out.printf("    中等(70-79): %d人 (%.1f%%)\n", average, average * 100.0 / stats.getCount());
        System.out.printf("    待改进(<70): %d人 (%.1f%%)\n", poor, poor * 100.0 / stats.getCount());
    }

    public void compareSubjects(int[] mathScores, int[] englishScores) {
        double mathAvg = IntStream.of(mathScores).average().orElse(0);
        double englishAvg = IntStream.of(englishScores).average().orElse(0);

        System.out.printf("数学平均分: %.1f, 英语平均分: %.1f\n", mathAvg, englishAvg);

        if (mathAvg > englishAvg) {
            System.out.printf("数学成绩比英语高 %.1f分\n", mathAvg - englishAvg);
        } else if (englishAvg > mathAvg) {
            System.out.printf("英语成绩比数学高 %.1f分\n", englishAvg - mathAvg);
        } else {
            System.out.println("两科成绩相当");
        }

        // 计算总分统计
        System.out.println("\n总分统计:");
        IntStream.range(0, mathScores.length)
                .map(i -> mathScores[i] + englishScores[i])  // 计算每个学生的总分
                .summaryStatistics();

        IntSummaryStatistics totalStats = IntStream.range(0, mathScores.length)
                .map(i -> mathScores[i] + englishScores[i])
                .summaryStatistics();

        System.out.printf("总分平均: %.1f, 最高总分: %d, 最低总分: %d\n",
                totalStats.getAverage(), totalStats.getMax(), totalStats.getMin());
    }
}
```

**输出结果：**

```
=== 学生成绩统计系统 ===
数学成绩分析:
数学科目统计:
  学生人数: 15人
  平均分: 85.7分
  最高分: 96分
  最低分: 73分
  成绩分布:
    优秀(90+): 5人 (33.3%)
    良好(80-89): 6人 (40.0%)
    中等(70-79): 4人 (26.7%)
    待改进(<70): 0人 (0.0%)

英语成绩分析:
英语科目统计:
  学生人数: 15人
  平均分: 87.2分
  最高分: 93分
  最低分: 79分
  成绩分布:
    优秀(90+): 5人 (33.3%)
    良好(80-89): 9人 (60.0%)
    中等(70-79): 1人 (6.7%)
    待改进(<70): 0人 (0.0%)

学科对比分析:
数学平均分: 85.7, 英语平均分: 87.2
英语成绩比数学高 1.5分

总分统计:
总分平均: 172.9, 最高总分: 188, 最低总分: 155
```

## 性能优化建议

### 选择合适的数字流类型

```java
// ✅ 处理整数用IntStream
IntStream.rangeClosed(1, 1000).sum();

// ✅ 处理大整数用LongStream  
LongStream.rangeClosed(1, 1000000000L).sum();

// ✅ 处理浮点数用DoubleStream
DoubleStream.of(1.1, 2.2, 3.3).average();

// ❌ 避免不必要的装箱拆箱
Stream.of(1, 2, 3).mapToInt(Integer::intValue).sum();  // 性能较差
```

### 并行处理大数据集

```java
// 大数据量时使用并行流
long parallelSum = IntStream.rangeClosed(1, 10000000)
                           .parallel()
                           .sum();
```

## 本章小结

今天我们深入学习了Java 8的数字流专题：

**数字流的优势：**
- **性能更好**：避免装箱拆箱，直接处理基本类型
- **API更简洁**：提供专门的数学统计方法
- **类型安全**：编译时确定数据类型

**三种数字流：**
- **IntStream**：处理int类型，适合一般整数计算
- **LongStream**：处理long类型，适合大整数和大范围计算
- **DoubleStream**：处理double类型，适合浮点数计算

**核心功能：**
- 便捷的创建方式（range、of、generate等）
- 丰富的统计方法（sum、max、min、average、summaryStatistics）
- 灵活的类型转换（asLongStream、asDoubleStream、mapToObj）

**实际应用：**
- 数学计算和统计分析
- 性能要求较高的数值处理
- 大数据量的聚合操作

下一章我们将学习《filter()：数据过滤器，想要什么筛什么》，探索如何优雅地从海量数据中筛选出我们需要的内容！

---

**源代码地址：** https://github.com/qianmoQ/tutorial/tree/main/java8-stream-tutorial/src/main/java/org/devlive/tutorial/stream/chapter04