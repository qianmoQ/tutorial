[TOC]

举个例子：上周五下午，你正准备愉快地下班，突然测试小姐姐跑过来："哎呀，这个功能怎么测试啊？我需要各种各样的测试数据，有集合的、数组的，还要能生成序列数据，你能帮我写个数据生成器吗？"

听到这个需求，你心想："这不就是需要从各种数据源创建Stream吗？"但转念一想，除了从List创建Stream，其他方式你还真没深入研究过。数组怎么转Stream？如何生成无限序列？文件内容能直接变成Stream吗？

别担心！今天我们就来系统学习Stream的各种创建方法。掌握了这些"数据流的源头"，以后无论遇到什么样的数据源，你都能轻松让它们"流起来"。从最常见的集合创建，到高级的自定义生成器，这一章将让你成为Stream创建的专家！

## 基础创建方式：从现有数据到Stream

### 方式一：从集合创建Stream

这是我们最常用的方式，就像把水果放到传送带上一样简单：

```java
import java.util.*;
import java.util.stream.Stream;

public class StreamFromCollections {
    public static void main(String[] args) {
        System.out.println("=== 从各种集合创建Stream ===");
        
        // 1. 从List创建
        List<String> fruits = Arrays.asList("苹果", "香蕉", "橙子", "葡萄");
        System.out.println("从List创建Stream:");
        fruits.stream()
              .forEach(fruit -> System.out.print(fruit + " "));
        
        // 2. 从Set创建（自动去重）
        Set<Integer> numbers = new HashSet<>(Arrays.asList(1, 2, 2, 3, 3, 4));
        System.out.println("\n\n从Set创建Stream（注意去重效果）:");
        numbers.stream()
               .sorted()  // Set无序，这里排序一下方便观察
               .forEach(num -> System.out.print(num + " "));
        
        // 3. 从Map创建Stream（处理键值对）
        Map<String, Integer> scoreMap = new HashMap<>();
        scoreMap.put("张三", 85);
        scoreMap.put("李四", 92);
        scoreMap.put("王五", 78);
        
        System.out.println("\n\n从Map的entrySet创建Stream:");
        scoreMap.entrySet().stream()
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
        
        System.out.println("\n只处理Map的values:");
        scoreMap.values().stream()
                .filter(score -> score > 80)  // 找出80分以上的成绩
                .forEach(score -> System.out.print(score + " "));
    }
}
```

**输出结果：**

```
=== 从各种集合创建Stream ===
从List创建Stream:
苹果 香蕉 橙子 葡萄 

从Set创建Stream（注意去重效果）:
1 2 3 4 

从Map的entrySet创建Stream:
李四: 92
张三: 85
王五: 78

只处理Map的values:
92 85 
```

💡 **小贴士**：集合的`stream()`方法是最直接的方式，就像给数据装上了"流水线启动器"。

### 方式二：从数组创建Stream

数组转Stream有两种常用方法，就像有两条路都能到达目的地：

```java
import java.util.Arrays;
import java.util.stream.Stream;

public class StreamFromArrays {
    public static void main(String[] args) {
        System.out.println("=== 从数组创建Stream ===");
        
        // 方式1: Arrays.stream() - 推荐方式
        String[] cities = {"北京", "上海", "广州", "深圳"};
        System.out.println("方式1: Arrays.stream()");
        Arrays.stream(cities)
              .forEach(city -> System.out.print(city + " "));
        
        // 方式2: Stream.of() 
        System.out.println("\n\n方式2: Stream.of()");
        Stream.of(cities)
              .forEach(city -> System.out.print(city + " "));
        
        // 数值数组的特殊处理
        int[] ages = {25, 30, 28, 35, 23};
        System.out.println("\n\n处理int数组:");
        Arrays.stream(ages)              // 返回IntStream
              .filter(age -> age > 26)   // 筛选年龄大于26的
              .forEach(age -> System.out.print(age + " "));
        
        // 指定数组范围创建Stream
        System.out.println("\n\n从数组的部分元素创建Stream:");
        String[] allCities = {"北京", "上海", "广州", "深圳", "杭州", "南京"};
        Arrays.stream(allCities, 1, 4)  // 从索引1到3（不包括4）
              .forEach(city -> System.out.print(city + " "));
    }
}
```

**输出结果：**

```
=== 从数组创建Stream ===
方式1: Arrays.stream()
北京 上海 广州 深圳 

方式2: Stream.of()
北京 上海 广州 深圳 

处理int数组:
30 28 35 

从数组的部分元素创建Stream:
上海 广州 深圳 
```

⚠️ **注意事项**：
- `Arrays.stream()`更适合数组，性能稍好
- `Stream.of()`更灵活，可以处理可变参数
- 基本类型数组（如int[]）会返回对应的特殊Stream（如IntStream）

### 方式三：直接创建Stream

有时候我们需要临时创建一些数据流，就像现场搭建一个小型流水线：

```java
import java.util.stream.Stream;

public class DirectStreamCreation {
    public static void main(String[] args) {
        System.out.println("=== 直接创建Stream ===");
        
        // 1. Stream.of() - 从零散数据创建
        System.out.println("从零散数据创建:");
        Stream.of("Java", "Python", "JavaScript", "Go")
              .forEach(lang -> System.out.print(lang + " "));
        
        // 2. Stream.empty() - 创建空流
        System.out.println("\n\n创建空Stream:");
        Stream<String> emptyStream = Stream.empty();
        System.out.println("空Stream的元素个数: " + emptyStream.count());
        
        // 3. Stream.builder() - 动态构建Stream
        System.out.println("\n使用Builder模式创建Stream:");
        Stream<String> builtStream = Stream.<String>builder()
                .add("第一个元素")
                .add("第二个元素")
                .add("第三个元素")
                .build();
        
        builtStream.forEach(System.out::println);
        
        // 4. 处理可能为null的情况
        System.out.println("\n处理可能为null的数据:");
        String nullableValue = null;
        String validValue = "有效数据";
        
        // Java 9+的方式（这里模拟实现）
        Stream.of(nullableValue, validValue)
              .filter(Objects::nonNull)  // 过滤掉null值
              .forEach(System.out::println);
    }
}
```

**输出结果：**

```
=== 直接创建Stream ===
从零散数据创建:
Java Python JavaScript Go 

创建空Stream:
空Stream的元素个数: 0

使用Builder模式创建Stream:
第一个元素
第二个元素
第三个元素

处理可能为null的数据:
有效数据
```

## 高级创建方式：无限流和自定义生成

### 方式四：generate()生成无限流

`generate()`就像一个永动机，可以无限生成数据。想象一下一个永不停歇的数据工厂：

```java
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StreamGenerate {
    public static void main(String[] args) {
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
        Stream.generate(new Supplier<Integer>() {
            private int current = 0;
            
            @Override
            public Integer get() {
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
class TestUser {
    private String name;
    private int age;
    private boolean isMale;
    
    public TestUser(String name, int age, boolean isMale) {
        this.name = name;
        this.age = age;
        this.isMale = isMale;
    }
    
    @Override
    public String toString() {
        return String.format("用户[姓名:%s, 年龄:%d, 性别:%s]", 
            name, age, isMale ? "男" : "女");
    }
}
```

**输出结果：**

```
=== 使用generate()创建无限流 ===
生成10个相同的问候语:
Hello Stream!
Hello Stream!
Hello Stream!
Hello Stream!
Hello Stream!
Hello Stream!
Hello Stream!
Hello Stream!
Hello Stream!
Hello Stream!

生成5个随机数:
9 74 84 45 7 

生成递增序列（使用状态对象）:
0 1 2 3 4 5 6 7 

生成测试用户数据:
用户[姓名:赵六, 年龄:45, 性别:女]
用户[姓名:赵六, 年龄:46, 性别:女]
用户[姓名:张三, 年龄:50, 性别:男]
```

⚠️ **重要警告**：`generate()`创建的是无限流，务必使用`limit()`限制数量，否则程序会一直运行下去！

### 方式五：iterate()生成序列流

`iterate()`就像一个数学递推公式，根据前一个值生成下一个值：

```java
import java.util.stream.Stream;

public class StreamIterate {
    public static void main(String[] args) {
        System.out.println("=== 使用iterate()创建序列流 ===");
        
        // 1. 生成等差数列
        System.out.println("生成等差数列（首项为2，公差为3）:");
        Stream.iterate(2, n -> n + 3)  // 从2开始，每次加3
              .limit(8)
              .forEach(num -> System.out.print(num + " "));
        
        // 2. 生成等比数列
        System.out.println("\n\n生成等比数列（首项为1，公比为2）:");
        Stream.iterate(1, n -> n * 2)  // 从1开始，每次乘以2
              .limit(10)
              .forEach(num -> System.out.print(num + " "));
        
        // 3. 生成斐波那契数列
        System.out.println("\n\n生成斐波那契数列:");
        Stream.iterate(new int[]{0, 1}, arr -> new int[]{arr[1], arr[0] + arr[1]})
              .limit(10)
              .mapToInt(arr -> arr[0])  // 提取数组的第一个元素
              .forEach(num -> System.out.print(num + " "));
        
        // 4. 生成日期序列
        System.out.println("\n\n生成连续日期序列:");
        java.time.LocalDate today = java.time.LocalDate.now();
        Stream.iterate(today, date -> date.plusDays(1))  // 每次加1天
              .limit(5)
              .forEach(date -> System.out.println("日期: " + date));

        Stream.iterate(1, n -> n + 1)
                .limit(49)  // 直接限制数量（1到49）
                .map(n -> n * n)
                .forEach(square -> System.out.print(square + " "));
    }
}
```

**输出结果：**

```
=== 使用iterate()创建序列流 ===
生成等差数列（首项为2，公差为3）:
2 5 8 11 14 17 20 23 

生成等比数列（首项为1，公比为2）:
1 2 4 8 16 32 64 128 256 512 

生成斐波那契数列:
0 1 1 2 3 5 8 13 21 34 

生成连续日期序列:
日期: 2025-08-22
日期: 2025-08-23
日期: 2025-08-24
日期: 2025-08-25
日期: 2025-08-26

生成有条件的序列（小于50的平方数）:
1 4 9 16 25 36 49 64 81 100 121 144 169 196 225 256 289 324 361 400 441 484 529 576 625 676 729 784 841 900 961 1024 1089 1156 1225 1296 1369 1444 1521 1600 1681 1764 1849 1936 2025 2116 2209 2304 2401 
```

💡 **小贴士**：`iterate()`特别适合生成有规律的数据序列，比generate()更适合需要基于前值计算后值的场景。

## 特殊数据源的Stream创建

### 从文件创建Stream

处理文件内容时，Stream可以让我们逐行处理，而不用一次性加载整个文件到内存：

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class StreamFromFiles {
    public static void main(String[] args) {
        System.out.println("=== 从文件创建Stream ===");
        
        // 创建一个示例文件内容（模拟）
        createSampleFile();
        
        try {
            // 1. 读取文件所有行
            System.out.println("读取文件所有行:");
            Files.lines(Paths.get("sample.txt"))
                 .forEach(System.out::println);
            
            // 2. 处理文件内容（统计非空行数）
            System.out.println("\n统计非空行数:");
            long nonEmptyLines = Files.lines(Paths.get("sample.txt"))
                                    .filter(line -> !line.trim().isEmpty())
                                    .count();
            System.out.println("非空行数: " + nonEmptyLines);
            
            // 3. 查找包含特定关键字的行
            System.out.println("\n查找包含'Java'的行:");
            Files.lines(Paths.get("sample.txt"))
                 .filter(line -> line.contains("Java"))
                 .forEach(line -> System.out.println("找到: " + line));
                 
        } catch (IOException e) {
            System.err.println("文件读取错误: " + e.getMessage());
        }
    }
    
    // 创建示例文件的方法
    private static void createSampleFile() {
        try {
            String content = "Java是一门优秀的编程语言\n" +
                    "                    Python也很受欢迎\n" +
                    "                    \n" +
                    "                    JavaScript用于前端开发\n" +
                    "                    Java Stream让数据处理变得简单\n" +
                    "                    \n" +
                    "                    学好编程，走遍天下都不怕！";
            Files.write(Paths.get("sample.txt"), content.getBytes());
        } catch (IOException e) {
            System.err.println("创建示例文件失败: " + e.getMessage());
        }
    }
}
```

**输出结果：**

```
=== 从文件创建Stream ===
读取文件所有行:
Java是一门优秀的编程语言
                    Python也很受欢迎
                    
                    JavaScript用于前端开发
                    Java Stream让数据处理变得简单
                    
                    学好编程，走遍天下都不怕！

统计非空行数:
非空行数: 5

查找包含'Java'的行:
找到: Java是一门优秀的编程语言
找到:                     JavaScript用于前端开发
找到:                     Java Stream让数据处理变得简单
```

### 从字符串创建Stream

有时候我们需要处理字符串中的字符或者分割后的部分：

```java
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StreamFromStrings {
    public static void main(String[] args) {
        System.out.println("=== 从字符串创建Stream ===");
        
        // 1. 处理字符串中的字符
        String text = "Hello Stream";
        System.out.println("字符串中的每个字符:");
        text.chars()  // 返回IntStream
            .mapToObj(c -> (char) c)  // 转换为Character
            .forEach(ch -> System.out.print(ch + " "));
        
        // 2. 按分隔符分割字符串
        String csv = "苹果,香蕉,橙子,葡萄,西瓜";
        System.out.println("\n\n分割CSV字符串:");
        Pattern.compile(",")
               .splitAsStream(csv)
               .forEach(fruit -> System.out.println("水果: " + fruit));
        
        // 3. 处理多行文本
        String multiLineText = "第一行内容\n第二行内容\n第三行内容\n";
        System.out.println("\n处理多行文本:");
        Arrays.stream(multiLineText.split("\n"))
                .filter(line -> !line.trim().isEmpty())
                .forEach(line -> System.out.println("处理: " + line));
        
        // 4. 实际应用：解析配置文件格式的字符串
        String config = "name=张三,age=25,city=北京,job=程序员";
        System.out.println("\n解析配置字符串:");
        Pattern.compile(",")
               .splitAsStream(config)
               .map(pair -> pair.split("="))
               .filter(parts -> parts.length == 2)
               .forEach(parts -> System.out.printf("%s: %s\n", parts[0], parts[1]));
    }
}
```

**输出结果：**

```
=== 从字符串创建Stream ===
字符串中的每个字符:
H e l l o   S t r e a m 

分割CSV字符串:
水果: 苹果
水果: 香蕉
水果: 橙子
水果: 葡萄
水果: 西瓜

处理多行文本:
处理: 第一行内容
处理: 第二行内容
处理: 第三行内容

解析配置字符串:
name: 张三
age: 25
city: 北京
job: 程序员
```

## 常见问题和解决方案

### 问题1：无限流忘记限制导致程序卡死

```java
// ❌ 危险！会无限执行
Stream.generate(() -> "无限循环").forEach(System.out::println);

// ✅ 正确：添加限制
Stream.generate(() -> "安全执行").limit(10).forEach(System.out::println);
```

### 问题2：基本类型数组的处理

```java
int[] numbers = {1, 2, 3, 4, 5};

// ❌ 这样会把整个数组当作一个元素
Stream.of(numbers).forEach(System.out::println);  // 打印数组地址

// ✅ 正确处理基本类型数组
Arrays.stream(numbers).forEach(System.out::println);  // 打印每个数字
```

## 实战案例：数据生成器工厂

让我们综合运用各种Stream创建方法，构建一个实用的数据生成器：

```java
import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DataGeneratorFactory {
    public static void main(String[] args) {
        System.out.println("=== 综合数据生成器 ===");
        
        DataGenerator generator = new DataGenerator();
        
        // 生成测试用户数据
        System.out.println("生成5个测试用户:");
        generator.generateUsers(5)
                 .forEach(System.out::println);
        
        // 生成日期范围
        System.out.println("\n生成本周日期:");
        generator.generateDateRange(LocalDate.now(), 7)
                 .forEach(date -> System.out.println("日期: " + date));
        
        // 生成随机订单数据
        System.out.println("\n生成3个随机订单:");
        generator.generateOrders(3)
                 .forEach(System.out::println);
    }
}

class DataGenerator {
    private final Random random = new Random();
    private final String[] names = {"张三", "李四", "王五", "赵六", "孙七", "周八", "吴九", "郑十"};
    private final String[] cities = {"北京", "上海", "广州", "深圳", "杭州", "南京", "成都", "武汉"};
    
    // 生成随机用户数据
    public Stream<User> generateUsers(int count) {
        return Stream.generate(new Supplier<User>() {
            @Override
            public User get() {
                return new User(
                    names[random.nextInt(names.length)],
                    18 + random.nextInt(50),  // 年龄18-67
                    cities[random.nextInt(cities.length)]
                );
            }
        }).limit(count);
    }
    
    // 生成日期范围
    public Stream<LocalDate> generateDateRange(LocalDate start, int days) {
        return Stream.iterate(start, date -> date.plusDays(1))
                     .limit(days);
    }
    
    // 生成随机订单
    public Stream<Order> generateOrders(int count) {
        return Stream.generate(() -> new Order(
            "ORD" + String.format("%06d", random.nextInt(1000000)),
            100.0 + random.nextDouble() * 1900.0,  // 金额100-2000
            names[random.nextInt(names.length)]
        )).limit(count);
    }
}

// 用户类
class User {
    private String name;
    private int age;
    private String city;
    
    public User(String name, int age, String city) {
        this.name = name;
        this.age = age;
        this.city = city;
    }
    
    @Override
    public String toString() {
        return String.format("用户[姓名:%s, 年龄:%d, 城市:%s]", name, age, city);
    }
}

// 订单类
class Order {
    private String orderId;
    private double amount;
    private String customerName;
    
    public Order(String orderId, double amount, String customerName) {
        this.orderId = orderId;
        this.amount = amount;
        this.customerName = customerName;
    }
    
    @Override
    public String toString() {
        return String.format("订单[ID:%s, 金额:%.2f, 客户:%s]", orderId, amount, customerName);
    }
}
```

**输出结果：**

```
=== 综合数据生成器 ===
生成5个测试用户:
用户[姓名:孙七, 年龄:27, 城市:上海]
用户[姓名:李四, 年龄:34, 城市:杭州]
用户[姓名:赵六, 年龄:63, 城市:成都]
用户[姓名:吴九, 年龄:43, 城市:北京]
用户[姓名:赵六, 年龄:35, 城市:成都]

生成本周日期:
日期: 2025-08-22
日期: 2025-08-23
日期: 2025-08-24
日期: 2025-08-25
日期: 2025-08-26
日期: 2025-08-27
日期: 2025-08-28

生成3个随机订单:
订单[ID:ORD135445, 金额:1440.59, 客户:李四]
订单[ID:ORD790311, 金额:1909.92, 客户:赵六]
订单[ID:ORD714136, 金额:955.24, 客户:吴九]
```

这个数据生成器展示了如何将不同的Stream创建方法结合起来，解决实际的测试数据生成需求。

## 本章小结

今天我们深入学习了Stream的各种创建方法，现在你已经掌握了：

- **基础方式**：从集合、数组、零散数据创建Stream
- **高级方式**：使用`generate()`和`iterate()`创建无限流和序列流
- **特殊数据源**：从文件、字符串等创建Stream
- **实战应用**：构建数据生成器解决实际问题

**关键要点回顾：**
- 集合用`.stream()`，数组用`Arrays.stream()`
- `generate()`适合生成随机或固定数据，`iterate()`适合生成序列
- 无限流必须用`limit()`限制，避免程序卡死
- 基本类型数组需要特殊处理，会返回对应的专门Stream类型

下一章我们将学习《无限流：generate()和iterate()的神奇用法》，深入探讨这两个强大方法的高级应用技巧。数据的源头我们已经掌握，接下来让我们看看如何让这些"流"发挥更大的威力！

---

**源代码地址：** https://github.com/qianmoQ/tutorial/tree/main/java8-stream-tutorial/src/main/java/org/devlive/tutorial/stream/chapter02