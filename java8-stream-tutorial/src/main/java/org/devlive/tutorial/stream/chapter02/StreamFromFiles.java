package org.devlive.tutorial.stream.chapter02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StreamFromFiles
{
    public static void main(String[] args)
    {
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
        }
        catch (IOException e) {
            System.err.println("文件读取错误: " + e.getMessage());
        }
    }

    // 创建示例文件的方法
    private static void createSampleFile()
    {
        try {
            String content = "Java是一门优秀的编程语言\n" +
                    "                    Python也很受欢迎\n" +
                    "                    \n" +
                    "                    JavaScript用于前端开发\n" +
                    "                    Java Stream让数据处理变得简单\n" +
                    "                    \n" +
                    "                    学好编程，走遍天下都不怕！";
            Files.write(Paths.get("sample.txt"), content.getBytes());
        }
        catch (IOException e) {
            System.err.println("创建示例文件失败: " + e.getMessage());
        }
    }
}
