package org.devlive.tutorial.multithreading.chapter03;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 使用守护线程实现自动保存功能
 */
public class DaemonThreadAutoSaveDemo
{
    // 模拟文档内容
    private static final StringBuilder documentContent = new StringBuilder();
    // 文档是否被修改
    private static volatile boolean documentModified = false;
    // 记录上次保存时间
    private static LocalDateTime lastSaveTime;

    public static void main(String[] args)
    {
        // 启动自动保存守护线程
        startAutoSaveThread();
        // 模拟文本编辑器
        System.out.println("简易文本编辑器 (输入'exit'退出)");
        System.out.println("----------------------");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if ("exit".equalsIgnoreCase(line)) {
                System.out.println("正在退出编辑器...");
                // 如果有未保存的内容，强制保存
                if (documentModified) {
                    saveDocument();
                }
                break;
            }
            else {
                // 添加用户输入到文档
                documentContent.append(line).append("\n");
                documentModified = true;
                System.out.println("文本已添加 (当前字符数: " + documentContent.length() + ")");
            }
        }
        System.out.println("编辑器已关闭，程序退出");
    }

    private static void startAutoSaveThread()
    {
        Thread autoSaveThread = new Thread(() -> {
            System.out.println("自动保存线程已启动 (每10秒检查一次)");
            while (true) {
                try {
                    // 休眠10秒
                    TimeUnit.SECONDS.sleep(10);
                    // 检查文档是否被修改
                    if (documentModified) {
                        saveDocument();
                    }
                }
                catch (InterruptedException e) {
                    System.out.println("自动保存线程被中断");
                    break;
                }
            }
        });
        // 设置为守护线程
        autoSaveThread.setDaemon(true);
        autoSaveThread.setName("AutoSaveThread");
        autoSaveThread.start();
    }

    private static void saveDocument()
    {
        File file = new File("autosave_document.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(documentContent.toString());
            // 更新状态
            documentModified = false;
            lastSaveTime = LocalDateTime.now();
            System.out.println("\n[自动保存] 文档已保存到: " + file.getAbsolutePath() +
                    " (" + lastSaveTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ")");
        }
        catch (IOException e) {
            System.out.println("\n[自动保存] 保存文档时出错: " + e.getMessage());
        }
    }
}