[TOC]

比如我们需要从用户数据库中筛选出一些特定用户：年龄在25-35岁之间的、活跃度高的、居住在一线城市的用户，用于推送新产品。需要快速搞定。

如果用传统方式，你需要写好几层嵌套的if语句：

```java
List<User> result = new ArrayList<>();
for (User user : users) {
    if (user.getAge() >= 25 && user.getAge() <= 35) {
        if (user.isActive()) {
            if (isFirstTierCity(user.getCity())) {
                result.add(user);
            }
        }
    }
}
```

但如果用Stream的`filter()`方法，这个复杂的筛选逻辑瞬间变得优雅：

```java
List<User> result = users.stream()
    .filter(user -> user.getAge() >= 25 && user.getAge() <= 35)
    .filter(User::isActive)
    .filter(user -> isFirstTierCity(user.getCity()))
    .collect(toList());
```

今天我们就来深入学习`filter()`方法，看看这个强大的数据过滤器如何让复杂的筛选逻辑变得简单优雅！

## filter()基础：数据筛选的核心

### filter()的工作原理

`filter()`方法接受一个`Predicate`函数作为参数，对流中的每个元素进行判断，只保留返回`true`的元素：

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilterBasics {
    public static void main(String[] args) {
        System.out.println("=== filter()基础用法 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 1. 筛选偶数
        System.out.println("筛选偶数:");
        List<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)  // 筛选条件：是偶数
            .collect(Collectors.toList());
        System.out.println("原数据: " + numbers);
        System.out.println("偶数: " + evenNumbers);
        
        // 2. 筛选大于5的数
        System.out.println("\n筛选大于5的数:");
        List<Integer> greaterThan5 = numbers.stream()
            .filter(n -> n > 5)
            .collect(Collectors.toList());
        System.out.println("大于5的数: " + greaterThan5);
        
        // 3. 多条件筛选
        System.out.println("\n多条件筛选（偶数且大于3）:");
        List<Integer> complexFilter = numbers.stream()
            .filter(n -> n % 2 == 0 && n > 3)  // 多个条件用&&连接
            .collect(Collectors.toList());
        System.out.println("偶数且大于3: " + complexFilter);
    }
}
```

**输出结果：**

```
=== filter()基础用法 ===
筛选偶数:
原数据: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
偶数: [2, 4, 6, 8, 10]

筛选大于5的数:
大于5的数: [6, 7, 8, 9, 10]

多条件筛选（偶数且大于3）:
偶数且大于3: [4, 6, 8, 10]
```

### 链式filter()：清晰的逻辑分层

虽然可以在一个filter()中写复杂条件，但链式调用多个filter()往往更清晰：

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChainedFilters {
    public static void main(String[] args) {
        System.out.println("=== 链式filter()对比 ===");
        
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date", "elderberry", "fig");
        
        // 方式1: 单个filter()，复杂条件
        System.out.println("方式1: 复杂条件在一个filter()中");
        List<String> result1 = words.stream()
            .filter(word -> word.length() > 4 && word.contains("e") && !word.startsWith("e"))
            .collect(Collectors.toList());
        System.out.println("结果: " + result1);
        
        // 方式2: 链式filter()，逻辑清晰
        System.out.println("\n方式2: 链式filter()，逻辑分层");
        List<String> result2 = words.stream()
            .filter(word -> word.length() > 4)      // 第1层：长度过滤
            .filter(word -> word.contains("e"))     // 第2层：包含字母e
            .filter(word -> !word.startsWith("e"))  // 第3层：不以e开头
            .collect(Collectors.toList());
        System.out.println("结果: " + result2);
        
        // 展示每一步的筛选过程
        System.out.println("\n筛选过程演示:");
        words.stream()
            .peek(word -> System.out.println("原始: " + word))
            .filter(word -> word.length() > 4)
            .peek(word -> System.out.println("  长度>4: " + word))
            .filter(word -> word.contains("e"))
            .peek(word -> System.out.println("    包含e: " + word))
            .filter(word -> !word.startsWith("e"))
            .forEach(word -> System.out.println("      最终结果: " + word));
    }
}
```

**输出结果：**

```
=== 链式filter()对比 ===
方式1: 复杂条件在一个filter()中
结果: [apple, cherry]

方式2: 链式filter()，逻辑分层
结果: [apple, cherry]

筛选过程演示:
原始: apple
  长度>4: apple
    包含e: apple
      最终结果: apple
原始: banana
  长度>4: banana
原始: cherry
  长度>4: cherry
    包含e: cherry
      最终结果: cherry
原始: date
原始: elderberry
  长度>4: elderberry
    包含e: elderberry
原始: fig
```

💡 **最佳实践**：链式filter()让代码更易读、易维护，每个filter()职责单一，便于调试。

## 对象筛选：实际业务场景

### 员工信息筛选系统

让我们用一个更贴近实际的例子来演示filter()的强大功能：

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeFilter {
    public static void main(String[] args) {
        System.out.println("=== 员工信息筛选系统 ===");
        
        List<Employee> employees = Arrays.asList(
            new Employee("张三", 28, "开发部", 12000, true),
            new Employee("李四", 35, "产品部", 15000, true),
            new Employee("王五", 23, "测试部", 8000, false),
            new Employee("赵六", 31, "开发部", 18000, true),
            new Employee("孙七", 26, "设计部", 10000, true),
            new Employee("周八", 42, "管理部", 25000, true),
            new Employee("吴九", 29, "开发部", 13000, false)
        );
        
        // 需求1: 筛选开发部的员工
        System.out.println("需求1: 开发部员工");
        List<Employee> developers = employees.stream()
            .filter(emp -> "开发部".equals(emp.getDepartment()))
            .collect(Collectors.toList());
        developers.forEach(System.out::println);
        
        // 需求2: 筛选活跃且薪资大于10000的员工
        System.out.println("\n需求2: 活跃且高薪员工");
        List<Employee> activeHighPaid = employees.stream()
            .filter(Employee::isActive)                    // 方法引用
            .filter(emp -> emp.getSalary() > 10000)
            .collect(Collectors.toList());
        activeHighPaid.forEach(System.out::println);
        
        // 需求3: 筛选30岁以下的技术岗位员工
        System.out.println("\n需求3: 30岁以下的技术岗位员工");
        List<String> techDepts = Arrays.asList("开发部", "测试部");
        List<Employee> youngTechies = employees.stream()
            .filter(emp -> emp.getAge() < 30)
            .filter(emp -> techDepts.contains(emp.getDepartment()))
            .collect(Collectors.toList());
        youngTechies.forEach(System.out::println);
        
        // 需求4: 复杂筛选 - 管理层候选人
        System.out.println("\n需求4: 管理层候选人（年龄>30，薪资>15000，活跃）");
        List<Employee> managementCandidates = employees.stream()
            .filter(emp -> emp.getAge() > 30)           // 年龄筛选
            .filter(emp -> emp.getSalary() > 15000)     // 薪资筛选
            .filter(Employee::isActive)                 // 活跃度筛选
            .collect(Collectors.toList());
        managementCandidates.forEach(System.out::println);
    }
}

class Employee {
    private String name;
    private int age;
    private String department;
    private int salary;
    private boolean isActive;
    
    public Employee(String name, int age, String department, int salary, boolean isActive) {
        this.name = name;
        this.age = age;
        this.department = department;
        this.salary = salary;
        this.isActive = isActive;
    }
    
    // getter方法
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getDepartment() { return department; }
    public int getSalary() { return salary; }
    public boolean isActive() { return isActive; }
    
    @Override
    public String toString() {
        return String.format("%s(%d岁, %s, %d元, %s)", 
            name, age, department, salary, isActive ? "活跃" : "不活跃");
    }
}
```

**输出结果：**

```
=== 员工信息筛选系统 ===
需求1: 开发部员工
张三(28岁, 开发部, 12000元, 活跃)
赵六(31岁, 开发部, 18000元, 活跃)
吴九(29岁, 开发部, 13000元, 不活跃)

需求2: 活跃且高薪员工
张三(28岁, 开发部, 12000元, 活跃)
李四(35岁, 产品部, 15000元, 活跃)
赵六(31岁, 开发部, 18000元, 活跃)
周八(42岁, 管理部, 25000元, 活跃)

需求3: 30岁以下的技术岗位员工
张三(28岁, 开发部, 12000元, 活跃)
王五(23岁, 测试部, 8000元, 不活跃)
吴九(29岁, 开发部, 13000元, 不活跃)

需求4: 管理层候选人（年龄>30，薪资>15000，活跃）
赵六(31岁, 开发部, 18000元, 活跃)
周八(42岁, 管理部, 25000元, 活跃)
```

## 高级筛选技巧

### 使用方法引用简化代码

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodReferenceFilter {
    public static void main(String[] args) {
        System.out.println("=== 方法引用简化filter() ===");
        
        List<String> words = Arrays.asList("hello", "", "world", null, "java", "", "stream");
        
        // 1. Lambda表达式方式
        System.out.println("Lambda表达式方式:");
        List<String> result1 = words.stream()
            .filter(s -> s != null)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
        System.out.println("结果: " + result1);
        
        // 2. 方法引用方式（更简洁）
        System.out.println("\n方法引用方式:");
        List<String> result2 = words.stream()
            .filter(java.util.Objects::nonNull)         // 方法引用：非null
            .filter(s -> !s.isEmpty())                  // Lambda：非空字符串
            .collect(Collectors.toList());
        System.out.println("结果: " + result2);
        
        // 3. 自定义静态方法
        System.out.println("\n使用自定义工具方法:");
        List<String> result3 = words.stream()
            .filter(StringUtils::isValid)               // 自定义方法引用
            .collect(Collectors.toList());
        System.out.println("结果: " + result3);
    }
}

class StringUtils {
    public static boolean isValid(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
```

**输出结果：**

```
=== 方法引用简化filter() ===
Lambda表达式方式:
结果: [hello, world, java, stream]

方法引用方式:
结果: [hello, world, java, stream]

使用自定义工具方法:
结果: [hello, world, java, stream]
```

### 条件组合与复用

```java
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateComposition {
    public static void main(String[] args) {
        System.out.println("=== Predicate条件组合 ===");
        
        List<Integer> numbers = Arrays.asList(-5, -2, 0, 3, 7, 10, 15, 20);
        
        // 定义可复用的条件
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isGreaterThan10 = n -> n > 10;
        
        // 1. 单独使用条件
        System.out.println("正数: " + 
            numbers.stream().filter(isPositive).collect(Collectors.toList()));
        
        // 2. 组合条件 - AND
        System.out.println("正偶数: " + 
            numbers.stream().filter(isPositive.and(isEven)).collect(Collectors.toList()));
        
        // 3. 组合条件 - OR
        System.out.println("负数或大于10: " + 
            numbers.stream().filter(isPositive.negate().or(isGreaterThan10))
                   .collect(Collectors.toList()));
        
        // 4. 复杂组合
        System.out.println("正数且(偶数或大于10): " + 
            numbers.stream().filter(isPositive.and(isEven.or(isGreaterThan10)))
                   .collect(Collectors.toList()));
    }
}
```

**输出结果：**

```
=== Predicate条件组合 ===
正数: [3, 7, 10, 15, 20]
正偶数: [10, 20]
负数或大于10: [-5, -2, 0, 15, 20]
正数且(偶数或大于10): [10, 15, 20]
```

## 性能优化与注意事项

### filter()的位置很重要

```java
import java.util.stream.IntStream;

public class FilterPerformance {
    public static void main(String[] args) {
        System.out.println("=== filter()位置对性能的影响 ===");
        
        // ❌ 性能较差：先进行昂贵操作，再过滤
        System.out.println("方式1: 先计算再过滤");
        long count1 = IntStream.rangeClosed(1, 1000)
            .map(n -> expensiveOperation(n))    // 昂贵操作
            .filter(n -> n > 500)               // 过滤
            .count();
        System.out.println("结果: " + count1);
        
        // ✅ 性能更好：先过滤，再进行昂贵操作
        System.out.println("\n方式2: 先过滤再计算");
        long count2 = IntStream.rangeClosed(1, 1000)
            .filter(n -> n > 500)               // 先过滤
            .map(FilterPerformance::expensiveOperation)  // 昂贵操作
            .count();
        System.out.println("结果: " + count2);
        
        System.out.println("\n💡 将filter()放在前面可以减少后续操作的数据量，提升性能！");
    }
    
    private static int expensiveOperation(int n) {
        // 模拟耗时操作
        return n * n;
    }
}
```

**输出结果：**

```
=== filter()位置对性能的影响 ===
方式1: 先计算再过滤
结果: 978

方式2: 先过滤再计算
结果: 500

💡 将filter()放在前面可以减少后续操作的数据量，提升性能！
```

### 避免在filter()中进行副作用操作

```java
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FilterSideEffects {
    public static void main(String[] args) {
        System.out.println("=== 避免在filter()中的副作用 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        AtomicInteger counter = new AtomicInteger(0);
        
        // ❌ 不好的做法：在filter中进行副作用操作
        System.out.println("❌ 不好的做法:");
        List<Integer> result1 = numbers.stream()
            .filter(n -> {
                counter.incrementAndGet();  // 副作用！
                System.out.println("检查数字: " + n);
                return n > 2;
            })
            .collect(Collectors.toList());
        System.out.println("结果: " + result1 + ", 计数器: " + counter.get());
        
        // ✅ 好的做法：使用peek()进行调试，保持filter()纯净
        System.out.println("\n✅ 好的做法:");
        counter.set(0);
        List<Integer> result2 = numbers.stream()
            .peek(n -> {
                counter.incrementAndGet();
                System.out.println("处理数字: " + n);
            })
            .filter(n -> n > 2)  // 纯粹的过滤逻辑
            .collect(Collectors.toList());
        System.out.println("结果: " + result2 + ", 计数器: " + counter.get());
    }
}
```

**输出结果：**

```
=== 避免在filter()中的副作用 ===
❌ 不好的做法:
检查数字: 1
检查数字: 2
检查数字: 3
检查数字: 4
检查数字: 5
结果: [3, 4, 5], 计数器: 5

✅ 好的做法:
处理数字: 1
处理数字: 2
处理数字: 3
处理数字: 4
处理数字: 5
结果: [3, 4, 5], 计数器: 5
```

## 实战案例：商品筛选系统

让我们构建一个完整的商品筛选系统，模拟电商网站的筛选功能：

```java
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProductFilterSystem {
    public static void main(String[] args) {
        System.out.println("=== 商品筛选系统 ===");
        
        List<Product> products = Arrays.asList(
            new Product("iPhone14", "电子产品", 6999.0, 4.8, true),
            new Product("华为Mate50", "电子产品", 5999.0, 4.7, true),
            new Product("Nike跑鞋", "运动用品", 899.0, 4.5, true),
            new Product("Adidas篮球鞋", "运动用品", 1299.0, 4.6, false),
            new Product("《Java编程思想》", "图书", 89.0, 4.9, true),
            new Product("MacBook Pro", "电子产品", 18999.0, 4.9, true),
            new Product("小米电视", "电子产品", 2999.0, 4.3, true)
        );
        
        ProductFilter filter = new ProductFilter();
        
        // 场景1: 价格筛选
        System.out.println("场景1: 价格在1000-10000元的商品");
        List<Product> priceFiltered = filter.filterByPriceRange(products, 1000, 10000);
        priceFiltered.forEach(System.out::println);
        
        // 场景2: 多条件组合筛选
        System.out.println("\n场景2: 电子产品 + 有库存 + 评分>4.5");
        List<Product> complexFiltered = filter.filterByMultipleConditions(
            products, "电子产品", 4.5, true);
        complexFiltered.forEach(System.out::println);
        
        // 场景3: 动态条件筛选
        System.out.println("\n场景3: 用户自定义筛选");
        FilterCriteria criteria = new FilterCriteria();
        criteria.minPrice = 500.0;
        criteria.maxPrice = 8000.0;
        criteria.minRating = 4.6;
        criteria.inStock = true;
        
        List<Product> dynamicFiltered = filter.filterByCriteria(products, criteria);
        dynamicFiltered.forEach(System.out::println);
    }
}

class ProductFilter {
    
    // 价格区间筛选
    public List<Product> filterByPriceRange(List<Product> products, double minPrice, double maxPrice) {
        return products.stream()
            .filter(p -> p.getPrice() >= minPrice)
            .filter(p -> p.getPrice() <= maxPrice)
            .collect(Collectors.toList());
    }
    
    // 多条件组合筛选
    public List<Product> filterByMultipleConditions(List<Product> products, 
                                                   String category, double minRating, boolean inStock) {
        return products.stream()
            .filter(p -> category.equals(p.getCategory()))
            .filter(p -> p.getRating() > minRating)
            .filter(p -> p.isInStock() == inStock)
            .collect(Collectors.toList());
    }
    
    // 动态条件筛选
    public List<Product> filterByCriteria(List<Product> products, FilterCriteria criteria) {
        Predicate<Product> predicate = p -> true;  // 初始条件：全部通过
        
        if (criteria.category != null) {
            predicate = predicate.and(p -> criteria.category.equals(p.getCategory()));
        }
        if (criteria.minPrice != null) {
            predicate = predicate.and(p -> p.getPrice() >= criteria.minPrice);
        }
        if (criteria.maxPrice != null) {
            predicate = predicate.and(p -> p.getPrice() <= criteria.maxPrice);
        }
        if (criteria.minRating != null) {
            predicate = predicate.and(p -> p.getRating() >= criteria.minRating);
        }
        if (criteria.inStock != null) {
            predicate = predicate.and(p -> p.isInStock() == criteria.inStock);
        }
        
        return products.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
}

class Product {
    private String name;
    private String category;
    private double price;
    private double rating;
    private boolean inStock;
    
    public Product(String name, String category, double price, double rating, boolean inStock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.rating = rating;
        this.inStock = inStock;
    }
    
    // getter方法
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public double getRating() { return rating; }
    public boolean isInStock() { return inStock; }
    
    @Override
    public String toString() {
        return String.format("%s [%s] ¥%.0f 评分:%.1f %s", 
            name, category, price, rating, inStock ? "有库存" : "无库存");
    }
}

class FilterCriteria {
    String category;
    Double minPrice;
    Double maxPrice;
    Double minRating;
    Boolean inStock;
}
```

**输出结果：**

```
=== 商品筛选系统 ===
场景1: 价格在1000-10000元的商品
iPhone14 [电子产品] ¥6999 评分:4.8 有库存
华为Mate50 [电子产品] ¥5999 评分:4.7 有库存
Adidas篮球鞋 [运动用品] ¥1299 评分:4.6 无库存
小米电视 [电子产品] ¥2999 评分:4.3 有库存

场景2: 电子产品 + 有库存 + 评分>4.5
iPhone14 [电子产品] ¥6999 评分:4.8 有库存
华为Mate50 [电子产品] ¥5999 评分:4.7 有库存
MacBook Pro [电子产品] ¥18999 评分:4.9 有库存

场景3: 用户自定义筛选
iPhone14 [电子产品] ¥6999 评分:4.8 有库存
华为Mate50 [电子产品] ¥5999 评分:4.7 有库存
```

## 本章小结

今天我们深入学习了`filter()`方法的强大功能：

**核心概念：**
- **工作原理**：接受Predicate函数，只保留返回true的元素
- **链式调用**：多个filter()可以串联，逻辑更清晰
- **方法引用**：使用方法引用简化常见筛选操作

**实用技巧：**
- **条件组合**：使用Predicate的and()、or()、negate()组合复杂条件
- **性能优化**：将filter()放在流水线前面，减少后续操作的数据量
- **避免副作用**：保持filter()函数的纯净性

**实际应用：**
- 员工信息筛选
- 商品搜索过滤
- 数据清洗和验证
- 用户权限过滤

**最佳实践：**
- 链式filter()提升代码可读性
- 使用Predicate变量提升代码复用性
- 避免在filter()中进行副作用操作

下一章我们将学习《map()：数据变形金刚，想变什么变什么》，探索如何优雅地转换和处理数据！

---

**源代码地址：** https://github.com/qianmoQ/tutorial/tree/main/java8-stream-tutorial/src/main/java/org/devlive/tutorial/stream/chapter05