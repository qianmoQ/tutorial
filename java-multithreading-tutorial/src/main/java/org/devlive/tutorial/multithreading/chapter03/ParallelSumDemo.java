package org.devlive.tutorial.multithreading.chapter03;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 并行计算示例：计算从1到n的总和
 */
public class ParallelSumDemo
{
    public static void main(String[] args)
    {
        long n = 1_000_000_000L; // 计算1到10亿的和
        int numThreads = 4; // 使用4个线程并行计算
        // 存储最终结果
        AtomicLong totalSum = new AtomicLong(0);
        // 创建并启动工作线程
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final long start = i * (n / numThreads) + 1;
            final long end = (i == numThreads - 1) ? n : (i + 1) * (n / numThreads);
            threads[i] = new Thread(() -> {
                System.out.println("计算从 " + start + " 到 " + end + " 的和");
                long partialSum = 0;
                for (long j = start; j <= end; j++) {
                    partialSum += j;
                }
                totalSum.addAndGet(partialSum);
                System.out.println("部分和 (" + start + "-" + end + "): " + partialSum);
            });
            threads[i].start();
        }
        // 等待所有线程完成
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 输出最终结果
        System.out.println("并行计算结果: " + totalSum.get());
        // 验证结果
        long expectedSum = n * (n + 1) / 2;
        System.out.println("正确结果: " + expectedSum);
        System.out.println("结果正确: " + (totalSum.get() == expectedSum));
    }
}