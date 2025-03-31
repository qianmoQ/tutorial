package org.devlive.tutorial.multithreading.chapter03;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程文件搜索工具
 */
public class ConcurrentFileSearcher
{
    // 搜索结果
    private final ConcurrentLinkedQueue<File> results = new ConcurrentLinkedQueue<>();
    // 搜索线程数量
    private final int threadCount;

    // 构造函数
    public ConcurrentFileSearcher(int threadCount)
    {
        this.threadCount = threadCount;
    }

    // 主程序示例
    public static void main(String[] args)
    {
        // 创建文件搜索器，使用4个线程
        ConcurrentFileSearcher searcher = new ConcurrentFileSearcher(4);
        // 定义搜索起始目录
//        File startDir = new File("C:/"); // Windows系统
        File startDir = new File("/"); // Linux/Mac系统
        System.out.println("开始在 " + startDir + " 中搜索...");
        // 定义搜索条件：查找大于10MB的Java源代码文件
        SearchCriteria criteria = file -> {
            // 检查文件扩展名
            if (file.getName().endsWith(".java")) {
                try {
                    // 检查文件大小（字节）
                    return Files.size(file.toPath()) > 10 * 1024; // > 10KB
                }
                catch (IOException e) {
                    return false;
                }
            }
            return false;
        };
        // 执行搜索
        List<File> searchResults = searcher.search(startDir, criteria);
        // 显示搜索结果
        System.out.println("\n搜索结果:");
        if (searchResults.isEmpty()) {
            System.out.println("未找到匹配的文件");
        }
        else {
            for (int i = 0; i < searchResults.size(); i++) {
                File file = searchResults.get(i);
                try {
                    long size = Files.size(file.toPath());
                    String sizeStr = size < 1024 ? size + " B" :
                            size < 1024 * 1024 ? String.format("%.2f KB", size / 1024.0) :
                                    String.format("%.2f MB", size / (1024.0 * 1024.0));
                    System.out.printf("%d. %s (大小: %s)\n", i + 1, file.getAbsolutePath(), sizeStr);
                }
                catch (IOException e) {
                    System.out.printf("%d. %s (无法获取文件大小)\n", i + 1, file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 搜索文件
     *
     * @param startDir 起始目录
     * @param criteria 搜索条件
     * @return 匹配的文件列表
     */
    public List<File> search(File startDir, SearchCriteria criteria)
    {
        if (!startDir.exists() || !startDir.isDirectory()) {
            throw new IllegalArgumentException("起始目录不存在或不是一个目录: " + startDir);
        }
        // 清空上次搜索结果
        results.clear();
        // 计数器，用于跟踪处理的文件数和目录数
        AtomicInteger processedFiles = new AtomicInteger(0);
        AtomicInteger processedDirs = new AtomicInteger(0);
        // 创建目录队列
        ConcurrentLinkedQueue<File> directoryQueue = new ConcurrentLinkedQueue<>();
        directoryQueue.add(startDir);
        // 创建并启动工作线程
        Thread[] searchThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            searchThreads[i] = new Thread(() -> {
                while (true) {
                    // 从队列中获取下一个要处理的目录
                    File currentDir = directoryQueue.poll();
                    // 如果队列为空，检查是否所有线程都空闲
                    if (currentDir == null) {
                        // 等待一会儿，看看其他线程是否会添加新的目录
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        }
                        catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                        // 再次检查队列
                        if (directoryQueue.isEmpty()) {
                            break; // 如果仍然为空，则结束线程
                        }
                        else {
                            continue; // 如果有新目录，继续处理
                        }
                    }
                    // 处理当前目录中的文件和子目录
                    File[] items = currentDir.listFiles();
                    if (items != null) {
                        for (File item : items) {
                            if (item.isDirectory()) {
                                // 将子目录添加到队列中
                                directoryQueue.add(item);
                                processedDirs.incrementAndGet();
                            }
                            else {
                                // 检查文件是否匹配搜索条件
                                if (criteria.matches(item)) {
                                    results.add(item);
                                }
                                processedFiles.incrementAndGet();
                            }
                        }
                    }
                }
            });
            // 设置线程名称并启动
            searchThreads[i].setName("SearchThread-" + i);
            searchThreads[i].start();
        }
        // 创建并启动一个守护线程来显示搜索进度
        Thread progressThread = new Thread(() -> {
            try {
                while (true) {
                    int files = processedFiles.get();
                    int dirs = processedDirs.get();
                    int found = results.size();
                    System.out.printf("\r处理中: %d 个文件, %d 个目录, 找到 %d 个匹配文件",
                            files, dirs, found);
                    TimeUnit.SECONDS.sleep(1);
                }
            }
            catch (InterruptedException e) {
                // 忽略中断
            }
        });
        progressThread.setDaemon(true);
        progressThread.start();
        // 等待所有搜索线程完成
        try {
            for (Thread thread : searchThreads) {
                thread.join();
            }
        }
        catch (InterruptedException e) {
            // 如果主线程被中断，中断所有搜索线程
            for (Thread thread : searchThreads) {
                thread.interrupt();
            }
            Thread.currentThread().interrupt();
        }
        // 打印最终结果
        System.out.println("\n搜索完成: 处理了 " + processedFiles.get() + " 个文件, "
                + processedDirs.get() + " 个目录, 找到 " + results.size() + " 个匹配文件");
        // 将结果转换为List并返回
        return new ArrayList<>(results);
    }

    // 搜索条件接口
    public interface SearchCriteria
    {
        boolean matches(File file);
    }
}