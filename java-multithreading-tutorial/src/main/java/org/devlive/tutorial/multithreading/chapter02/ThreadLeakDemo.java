package org.devlive.tutorial.multithreading.chapter02;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程泄漏示例
 */
public class ThreadLeakDemo
{
    public static void main(String[] args)
    {
        // 错误的线程池使用方式，没有关闭线程池
        badThreadPoolUsage();
        // 正确的线程池使用方式
        goodThreadPoolUsage();
    }

    private static void badThreadPoolUsage()
    {
        System.out.println("=== 错误的线程池使用方式 ===");
        // 创建一个固定大小的线程池
        ExecutorService executor = Executors.newFixedThreadPool(5);
        // 提交任务
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("执行任务 " + taskId);
                return taskId;
            });
        }
        // 没有调用shutdown()方法，线程池中的线程会一直存在
        System.out.println("任务提交完毕，但线程池没有被关闭");
    }

    private static void goodThreadPoolUsage()
    {
        System.out.println("=== 正确的线程池使用方式 ===");
        // 创建一个固定大小的线程池
        ExecutorService executor = Executors.newFixedThreadPool(5);
        try {
            // 提交任务
            for (int i = 0; i < 10; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println("执行任务 " + taskId);
                    return taskId;
                });
            }
        }
        finally {
            // 确保线程池被关闭
            executor.shutdown();
            System.out.println("任务提交完毕，线程池已关闭");
        }
    }
}