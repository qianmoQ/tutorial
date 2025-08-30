[TOC]

比如有一个新需求："我们需要统计所有班级中学生的总数，还要找出所有学生中成绩最高的。"数据结构是这样的：每个学校有多个班级，每个班级有多个学生。用传统方式处理这种嵌套结构：

```java
int totalStudents = 0;
for (School school : schools) {
    for (Class clazz : school.getClasses()) {
        totalStudents += clazz.getStudents().size();
    }
}
```

看着这层层嵌套的循环，感觉："这代码写得像俄罗斯套娃一样..."。这时候想起："用`flatMap()`啊！专门用来'拍扁'嵌套结构，一行代码就能搞定。"

```java
int totalStudents = schools.stream()
        .flatMap(school -> school.getClasses().stream())
        .flatMap(clazz -> clazz.getStudents().stream())
        .mapToInt(student -> 1)
        .sum();
```

今天我们就来学习`flatMap()`，这个处理嵌套数据的神器，看它如何把复杂的嵌套结构"拍扁"成简单的流！

## flatMap()基础：拍扁嵌套结构

### flatMap()与map()的区别

`map()`是一对一转换，而`flatMap()`是一对多转换，它会将每个元素转换为一个流，然后将所有流合并成一个流：

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapBasics {
    public static void main(String[] args) {
        System.out.println("=== flatMap()与map()的区别 ===");

        List<String> sentences = Arrays.asList(
                "Hello World",
                "Java Stream API",
                "Functional Programming"
        );

        // 使用map()：每个句子转换为单词数组
        System.out.println("使用map():");
        List<String[]> wordArrays = sentences.stream()
                .map(sentence -> sentence.split(" "))  // 每个句子变成String[]
                .collect(Collectors.toList());

        System.out.println("结果类型: List<String[]>");
        wordArrays.forEach(arr -> System.out.println("数组: " + Arrays.toString(arr)));

        // 使用flatMap()：将所有单词"拍扁"成一个流
        System.out.println("\n使用flatMap():");
        List<String> allWords = sentences.stream()
                .flatMap(sentence -> Arrays.stream(sentence.split(" ")))  // 拍扁成单词流
                .collect(Collectors.toList());

        System.out.println("结果类型: List<String>");
        System.out.println("所有单词: " + allWords);

        // 更直观的对比
        System.out.println("\n处理过程对比:");
        System.out.println("原始数据: " + sentences);
        System.out.println("map()结果: 3个数组 [每个句子一个数组]");
        System.out.println("flatMap()结果: " + allWords.size() + "个单词 [所有单词在一个列表中]");
    }
}
```

**输出结果：**

```
=== flatMap()与map()的区别 ===
使用map():
结果类型: List<String[]>
数组: [Hello, World]
数组: [Java, Stream, API]
数组: [Functional, Programming]

使用flatMap():
结果类型: List<String>
所有单词: [Hello, World, Java, Stream, API, Functional, Programming]

处理过程对比:
原始数据: [Hello World, Java Stream API, Functional Programming]
map()结果: 3个数组 [每个句子一个数组]
flatMap()结果: 7个单词 [所有单词在一个列表中]
```

💡 **关键理解**：
- `map()`：1个输入 → 1个输出
- `flatMap()`：1个输入 → 多个输出（然后拍扁成一个流）

### 数字集合的拍扁处理

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapNumbers {
    public static void main(String[] args) {
        System.out.println("=== 数字集合的拍扁处理 ===");

        List<List<Integer>> nestedNumbers = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5),
                Arrays.asList(6, 7, 8, 9)
        );

        System.out.println("嵌套数字列表: " + nestedNumbers);

        // 使用flatMap()拍扁
        List<Integer> flatNumbers = nestedNumbers.stream()
                .flatMap(List::stream)  // 将每个List<Integer>转换为流，然后合并
                .collect(Collectors.toList());

        System.out.println("拍扁后: " + flatNumbers);

        // 进一步处理：找出所有偶数
        List<Integer> evenNumbers = nestedNumbers.stream()
                .flatMap(List::stream)          // 先拍扁
                .filter(n -> n % 2 == 0)        // 再过滤偶数
                .collect(Collectors.toList());

        System.out.println("所有偶数: " + evenNumbers);

        // 计算所有数字的总和
        int sum = nestedNumbers.stream()
                .flatMap(List::stream)
                .mapToInt(Integer::intValue)
                .sum();

        System.out.println("数字总和: " + sum);
    }
}
```

**输出结果：**

```
=== 数字集合的拍扁处理 ===
嵌套数字列表: [[1, 2, 3], [4, 5], [6, 7, 8, 9]]
拍扁后: [1, 2, 3, 4, 5, 6, 7, 8, 9]
所有偶数: [2, 4, 6, 8]
数字总和: 45
```

## 处理复杂对象的嵌套结构

### 学校-班级-学生的层级处理

让我们用一个实际的业务场景来演示`flatMap()`的威力：

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NestedObjectProcessing {
    public static void main(String[] args) {
        System.out.println("=== 学校班级学生数据处理 ===");

        // 构造测试数据
        List<School> schools = createSchoolData();

        // 需求1: 统计所有学生总数
        long totalStudents = schools.stream()
                .flatMap(school -> school.getClasses().stream())    // 拍扁班级
                .flatMap(clazz -> clazz.getStudents().stream())     // 拍扁学生
                .count();

        System.out.println("所有学生总数: " + totalStudents);

        // 需求2: 找出所有学生姓名
        List<String> allStudentNames = schools.stream()
                .flatMap(school -> school.getClasses().stream())
                .flatMap(clazz -> clazz.getStudents().stream())
                .map(Student::getName)  // 提取姓名
                .collect(Collectors.toList());

        System.out.println("所有学生姓名: " + allStudentNames);

        // 需求3: 找出所有优秀学生(成绩>85)
        List<Student> excellentStudents = schools.stream()
                .flatMap(school -> school.getClasses().stream())
                .flatMap(clazz -> clazz.getStudents().stream())
                .filter(student -> student.getScore() > 85)     // 过滤优秀学生
                .collect(Collectors.toList());

        System.out.println("\n优秀学生(>85分):");
        excellentStudents.forEach(System.out::println);

        // 需求4: 按学校统计学生数量
        System.out.println("\n各学校学生数量:");
        schools.forEach(school -> {
            long count = school.getClasses().stream()
                    .flatMap(clazz -> clazz.getStudents().stream())
                    .count();
            System.out.println(school.getName() + ": " + count + "人");
        });

        // 需求5: 找出成绩最高的学生
        Student topStudent = schools.stream()
                .flatMap(school -> school.getClasses().stream())
                .flatMap(clazz -> clazz.getStudents().stream())
                .max((s1, s2) -> Integer.compare(s1.getScore(), s2.getScore()))
                .orElse(null);

        if (topStudent != null) {
            System.out.println("\n成绩最高的学生: " + topStudent);
        }
    }

    private static List<School> createSchoolData() {
        // 第一小学
        List<Student> class1A = Arrays.asList(
                new Student("张三", 88),
                new Student("李四", 92),
                new Student("王五", 79)
        );
        List<Student> class1B = Arrays.asList(
                new Student("赵六", 85),
                new Student("孙七", 90)
        );

        School school1 = new School("第一小学", Arrays.asList(
                new Class("一年级A班", class1A),
                new Class("一年级B班", class1B)
        ));

        // 第二小学
        List<Student> class2A = Arrays.asList(
                new Student("周八", 94),
                new Student("吴九", 87),
                new Student("郑十", 82)
        );

        School school2 = new School("第二小学", Arrays.asList(
                new Class("一年级A班", class2A)
        ));

        return Arrays.asList(school1, school2);
    }
}

class School {
    private String name;
    private List<Class> classes;

    public School(String name, List<Class> classes) {
        this.name = name;
        this.classes = classes;
    }

    public String getName() { return name; }
    public List<Class> getClasses() { return classes; }
}

class Class {
    private String name;
    private List<Student> students;

    public Class(String name, List<Student> students) {
        this.name = name;
        this.students = students;
    }

    public String getName() { return name; }
    public List<Student> getStudents() { return students; }
}

class Student {
    private String name;
    private int score;

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() { return name; }
    public int getScore() { return score; }

    @Override
    public String toString() {
        return String.format("%s(分数:%d)", name, score);
    }
}
```

**输出结果：**

```
=== 学校班级学生数据处理 ===
所有学生总数: 8
所有学生姓名: [张三, 李四, 王五, 赵六, 孙七, 周八, 吴九, 郑十]

优秀学生(>85分):
张三(分数:88)
李四(分数:92)
孙七(分数:90)
周八(分数:94)
吴九(分数:87)

各学校学生数量:
第一小学: 5人
第二小学: 3人

成绩最高的学生: 周八(分数:94)
```

## flatMap()的高级应用

### 处理Optional和空值

`flatMap()`在处理可能为空的集合时特别有用：

```java
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlatMapOptional {
    public static void main(String[] args) {
        System.out.println("=== flatMap()处理Optional和空值 ===");

        List<Department> departments = Arrays.asList(
                new Department("开发部", Arrays.asList("张三", "李四", "王五")),
                new Department("测试部", Collections.emptyList()),  // 空列表
                new Department("产品部", Arrays.asList("赵六", "孙七")),
                new Department("设计部", null)  // null列表
        );

        // 安全地获取所有员工姓名
        List<String> allEmployees = departments.stream()
                .flatMap(dept -> {
                    List<String> employees = dept.getEmployees();
                    // 安全处理null和空集合
                    return employees == null ?
                            java.util.stream.Stream.empty() :
                            employees.stream();
                })
                .collect(Collectors.toList());

        System.out.println("所有员工: " + allEmployees);

        // 使用Optional更优雅地处理
        List<String> allEmployeesOptional = departments.stream()
                .flatMap(dept -> Optional.ofNullable(dept.getEmployees())
                        .orElse(Collections.emptyList())
                        .stream())
                .collect(Collectors.toList());

        System.out.println("使用Optional处理: " + allEmployeesOptional);

        // 统计非空部门数量
        long nonEmptyDepts = departments.stream()
                .flatMap(dept -> {
                    List<String> employees = dept.getEmployees();
                    return employees != null && !employees.isEmpty() ?
                            java.util.stream.Stream.of(dept) :
                            java.util.stream.Stream.empty();
                })
                .count();

        System.out.println("有员工的部门数量: " + nonEmptyDepts);
    }
}

class Department {
    private String name;
    private List<String> employees;

    public Department(String name, List<String> employees) {
        this.name = name;
        this.employees = employees;
    }

    public String getName() { return name; }
    public List<String> getEmployees() { return employees; }
}
```

**输出结果：**

```
=== flatMap()处理Optional和空值 ===
所有员工: [张三, 李四, 王五, 赵六, 孙七]
使用Optional处理: [张三, 李四, 王五, 赵六, 孙七]
有员工的部门数量: 2
```

### 字符串处理的高级应用

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringFlatMapAdvanced {
    public static void main(String[] args) {
        System.out.println("=== 字符串处理的高级应用 ===");

        List<String> documents = Arrays.asList(
                "Java 8 Stream API",
                "Lambda Expressions",
                "Functional Programming"
        );

        // 1. 获取所有不重复的字符
        List<Character> uniqueChars = documents.stream()
                .flatMap(doc -> doc.chars()                    // 转为IntStream
                        .filter(c -> c != ' ')       // 过滤空格
                        .mapToObj(c -> (char) c))    // 转为Character流
                .distinct()                                    // 去重
                .sorted()                                      // 排序
                .collect(Collectors.toList());

        System.out.println("所有不重复字符: " + uniqueChars);

        // 2. 统计单词频率（简化版）
        List<String> allWords = documents.stream()
                .flatMap(doc -> Arrays.stream(doc.toLowerCase().split("\\s+")))
                .collect(Collectors.toList());

        System.out.println("所有单词: " + allWords);

        // 3. 查找包含特定字符的单词
        String targetChar = "a";
        List<String> wordsWithA = documents.stream()
                .flatMap(doc -> Arrays.stream(doc.split("\\s+")))
                .filter(word -> word.toLowerCase().contains(targetChar))
                .distinct()
                .collect(Collectors.toList());

        System.out.println("包含字母'" + targetChar + "'的单词: " + wordsWithA);

        // 4. 处理CSV数据
        List<String> csvLines = Arrays.asList(
                "张三,李四,王五",
                "赵六,孙七",
                "周八,吴九,郑十,钱一"
        );

        List<String> allNames = csvLines.stream()
                .flatMap(line -> Arrays.stream(line.split(",")))
                .map(String::trim)  // 清除可能的空格
                .collect(Collectors.toList());

        System.out.println("CSV中所有姓名: " + allNames);
    }
}
```

**输出结果：**

```
=== 字符串处理的高级应用 ===
所有不重复字符: [8, A, E, F, I, J, L, P, S, a, b, c, d, e, g, i, l, m, n, o, p, r, s, t, u, v, x]
所有单词: [java, 8, stream, api, lambda, expressions, functional, programming]
包含字母'a'的单词: [Java, Stream, API, Lambda, Functional, Programming]
CSV中所有姓名: [张三, 李四, 王五, 赵六, 孙七, 周八, 吴九, 郑十, 钱一]
```

## 性能考虑和最佳实践

### 避免不必要的中间集合创建

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapPerformance {
    public static void main(String[] args) {
        System.out.println("=== flatMap()性能优化 ===");

        List<String> sentences = Arrays.asList(
                "Hello World Java",
                "Stream API Programming",
                "Functional Style Coding"
        );

        // ❌ 性能较差：创建了中间数组
        System.out.println("方式1: 创建中间数组");
        List<String> words1 = sentences.stream()
                .map(s -> s.split(" "))              // 创建String[]数组
                .flatMap(Arrays::stream)             // 再拍扁
                .collect(Collectors.toList());
        System.out.println("结果: " + words1);

        // ✅ 性能更好：直接使用流
        System.out.println("\n方式2: 直接流式处理");
        List<String> words2 = sentences.stream()
                .flatMap(s -> Arrays.stream(s.split(" ")))  // 直接拍扁
                .collect(Collectors.toList());
        System.out.println("结果: " + words2);

        // 复杂场景下的性能对比
        System.out.println("\n性能对比场景:");
        long startTime = System.nanoTime();

        sentences.stream()
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .filter(word -> word.length() > 4)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        long endTime = System.nanoTime();
        System.out.println("优化后耗时: " + (endTime - startTime) + "ns");
    }
}
```

**输出结果：**

```
=== flatMap()性能优化 ===
方式1: 创建中间数组
结果: [Hello, World, Java, Stream, API, Programming, Functional, Style, Coding]

方式2: 直接流式处理
结果: [Hello, World, Java, Stream, API, Programming, Functional, Style, Coding]

性能对比场景:
优化后耗时: 1275625ns
```

### flatMap()与其他操作的最佳组合顺序

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapOptimization {
    public static void main(String[] args) {
        System.out.println("=== 操作顺序优化 ===");

        List<List<Integer>> nestedNumbers = Arrays.asList(
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20),
                Arrays.asList(21, 22, 23, 24, 25, 26, 27, 28, 29, 30)
        );

        // 需求：找出所有大于15的偶数

        // ❌ 效率较低：先拍扁所有数据，再过滤
        System.out.println("方式1: 先flatMap再filter");
        List<Integer> result1 = nestedNumbers.stream()
                .flatMap(List::stream)           // 拍扁所有30个数字
                .filter(n -> n > 15)             // 过滤大于15的
                .filter(n -> n % 2 == 0)         // 过滤偶数
                .collect(Collectors.toList());
        System.out.println("结果: " + result1);

        // ✅ 效率更高：在每个子列表内先过滤，减少拍扁后的数据量
        System.out.println("\n方式2: 先filter再flatMap");
        List<Integer> result2 = nestedNumbers.stream()
                .flatMap(subList -> subList.stream()
                        .filter(n -> n > 15)     // 在子列表内先过滤
                        .filter(n -> n % 2 == 0)) // 减少需要拍扁的数据
                .collect(Collectors.toList());
        System.out.println("结果: " + result2);

        System.out.println("\n💡 在flatMap内部先进行过滤可以减少数据处理量！");
    }
}
```

**输出结果：**

```
=== 操作顺序优化 ===
方式1: 先flatMap再filter
结果: [16, 18, 20, 22, 24, 26, 28, 30]

方式2: 先filter再flatMap
结果: [16, 18, 20, 22, 24, 26, 28, 30]

💡 在flatMap内部先进行过滤可以减少数据处理量！
```

## 实战案例：文档关键词分析系统

让我们构建一个文档分析系统，展示`flatMap()`在实际项目中的应用：

```java
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DocumentAnalysisSystem {
    public static void main(String[] args) {
        System.out.println("=== 文档关键词分析系统 ===");

        List<Document> documents = Arrays.asList(
                new Document("Java教程", Arrays.asList(
                        "Java是一门面向对象的编程语言",
                        "Java具有跨平台特性",
                        "Java广泛应用于企业开发"
                )),
                new Document("Stream API", Arrays.asList(
                        "Stream API是Java 8的重要特性",
                        "Stream提供了函数式编程能力",
                        "使用Stream可以简化集合操作"
                )),
                new Document("大数据处理", Arrays.asList(
                        "大数据处理需要高效的算法",
                        "Java在大数据领域有广泛应用",
                        "Stream API适合处理大量数据"
                ))
        );

        DocumentAnalyzer analyzer = new DocumentAnalyzer();

        // 1. 提取所有文档的所有单词
        List<String> allWords = analyzer.extractAllWords(documents);
        System.out.println("文档总词数: " + allWords.size());

        // 2. 统计词频
        Map<String, Long> wordFrequency = analyzer.analyzeWordFrequency(documents);
        System.out.println("\n高频词汇(出现2次以上):");
        wordFrequency.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + "次"));

        // 3. 查找包含特定关键词的文档
        String keyword = "Java";
        List<String> docsWithKeyword = analyzer.findDocumentsContaining(documents, keyword);
        System.out.println("\n包含'" + keyword + "'的文档:");
        docsWithKeyword.forEach(System.out::println);

        // 4. 生成文档摘要
        List<String> summaries = analyzer.generateDocumentSummaries(documents);
        System.out.println("\n文档摘要:");
        summaries.forEach(System.out::println);
    }
}

class DocumentAnalyzer {

    // 提取所有单词
    public List<String> extractAllWords(List<Document> documents) {
        return documents.stream()
                .flatMap(doc -> doc.getContents().stream())        // 拍扁所有段落
                .flatMap(paragraph -> Arrays.stream(paragraph.split("\\s+")))  // 拍扁所有单词
                .map(word -> word.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z]", ""))  // 清理标点
                .filter(word -> !word.isEmpty())                  // 过滤空字符串
                .collect(Collectors.toList());
    }

    // 分析词频
    public Map<String, Long> analyzeWordFrequency(List<Document> documents) {
        return extractAllWords(documents).stream()
                .collect(Collectors.groupingBy(
                        Function.identity(),              // 按单词分组
                        Collectors.counting()             // 计数
                ));
    }

    // 查找包含关键词的文档
    public List<String> findDocumentsContaining(List<Document> documents, String keyword) {
        return documents.stream()
                .filter(doc -> doc.getContents().stream()
                        .anyMatch(content -> content.contains(keyword)))
                .map(Document::getTitle)
                .collect(Collectors.toList());
    }

    // 生成文档摘要
    public List<String> generateDocumentSummaries(List<Document> documents) {
        return documents.stream()
                .map(doc -> {
                    long wordCount = doc.getContents().stream()
                            .flatMap(content -> Arrays.stream(content.split("\\s+")))
                            .count();

                    String firstSentence = doc.getContents().isEmpty() ?
                            "" : doc.getContents().get(0);

                    return String.format("%s [%d词] - %s",
                            doc.getTitle(), wordCount,
                            firstSentence.length() > 20 ?
                                    firstSentence.substring(0, 20) + "..." : firstSentence);
                })
                .collect(Collectors.toList());
    }
}

class Document {
    private String title;
    private List<String> contents;

    public Document(String title, List<String> contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() { return title; }
    public List<String> getContents() { return contents; }
}
```

**输出结果：**

```
=== 文档关键词分析系统 ===
文档总词数: 12

高频词汇(出现2次以上):
Stream: 2次

包含'Java'的文档:
Java教程
Stream API
大数据处理

文档摘要:
Java教程 [3词] - Java是一门面向对象的编程语言
Stream API [5词] - Stream API是Java 8的重要...
大数据处理 [4词] - 大数据处理需要高效的算法
```

## 本章小结

今天我们深入学习了`flatMap()`方法的强大功能：

**核心概念：**
- **拍扁操作**：将嵌套结构"拍扁"成单层流
- **一对多转换**：每个输入元素可以产生多个输出元素
- **流合并**：将多个流合并成一个流

**与map()的区别：**
- `map()`：1→1转换，保持流的结构
- `flatMap()`：1→多转换，改变流的结构

**主要应用场景：**
- 处理嵌套集合（List<List<T>>）
- 处理对象的嵌套关系（如学校-班级-学生）
- 字符串分割和处理
- 安全处理null和空集合

**性能优化要点：**
- 避免创建不必要的中间集合
- 在flatMap()内部先进行过滤操作
- 合理安排操作顺序

**实际应用：**
- 文档内容分析和关键词提取
- 多层级数据结构的统计分析
- CSV/JSON等格式数据的解析
- 复杂业务对象的数据提取

**最佳实践：**
- 使用方法引用简化代码（如`List::stream`）
- 结合Optional处理可能为null的集合
- 先过滤再拍扁，提升性能
- 合理使用peek()调试复杂的flatMap()操作

下一章我们将学习《排序和去重：sorted()和distinct()让数据更整齐》，探索如何让杂乱的数据变得井井有条！

---

**源代码地址：** https://github.com/qianmoQ/tutorial/tree/main/java8-stream-tutorial/src/main/java/org/devlive/tutorial/stream/chapter07