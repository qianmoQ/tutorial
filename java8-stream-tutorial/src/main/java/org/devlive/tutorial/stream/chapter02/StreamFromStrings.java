package org.devlive.tutorial.stream.chapter02;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StreamFromStrings
{
    public static void main(String[] args)
    {
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
