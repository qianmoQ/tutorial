package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 公平锁与非公平锁的性能对比
 */
public class LockPerformanceComparison
{

    private static final int THREAD_COUNT = 10;
    private static final int OPERATIONS_PER_THREAD = 1000;

    private final ReentrantLock fairLock = new ReentrantLock(true);
    private final ReentrantLock unfairLock = new ReentrantLock(false);

    private volatile int fairCounter = 0;
    private volatile int unfairCounter = 0;

    /**
     * 使用公平锁进行计数
     */
    public void fairIncrement()
    {
        fairLock.lock();
        try {
            fairCounter++;
        }
        finally {
            fairLock.unlock();
        }
    }

    /**
     * 使用非公平锁进行计数
     */
    public void unfairIncrement()
    {
        unfairLock.lock();
        try {
            unfairCounter++;
        }
        finally {
            unfairLock.unlock();
        }
    }

    /**
     * 测试公平锁性能
     */
    public long testFairLock()
            throws InterruptedException
    {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);

        // 创建线程
        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> {
                try {
                    startLatch.await(); // 等待统一开始信号

                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        fairIncrement();
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                finally {
                    endLatch.countDown();
                }
            }, "公平锁线程-" + i).start();
        }

        long startTime = System.nanoTime();
        startLatch.countDown(); // 发出开始信号
        endLatch.await(); // 等待所有线程完成
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    /**
     * 测试非公平锁性能
     */
    public long testUnfairLock()
            throws InterruptedException
    {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);

        // 创建线程
        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> {
                try {
                    startLatch.await(); // 等待统一开始信号

                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        unfairIncrement();
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                finally {
                    endLatch.countDown();
                }
            }, "非公平锁线程-" + i).start();
        }

        long startTime = System.nanoTime();
        startLatch.countDown(); // 发出开始信号
        endLatch.await(); // 等待所有线程完成
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        LockPerformanceComparison comparison = new LockPerformanceComparison();

        System.out.println("开始性能测试...");
        System.out.println("线程数：" + THREAD_COUNT);
        System.out.println("每线程操作次数：" + OPERATIONS_PER_THREAD);
        System.out.println("总操作次数：" + (THREAD_COUNT * OPERATIONS_PER_THREAD));

        // 测试公平锁
        System.out.println("\n=== 公平锁性能测试 ===");
        long fairLockTime = comparison.testFairLock();
        System.out.println("公平锁执行时间：" + (fairLockTime / 1_000_000) + " 毫秒");
        System.out.println("公平锁计数结果：" + comparison.fairCounter);

        // 稍等一下，让系统稳定
        Thread.sleep(1000);

        // 测试非公平锁
        System.out.println("\n=== 非公平锁性能测试 ===");
        long unfairLockTime = comparison.testUnfairLock();
        System.out.println("非公平锁执行时间：" + (unfairLockTime / 1_000_000) + " 毫秒");
        System.out.println("非公平锁计数结果：" + comparison.unfairCounter);

        // 性能对比
        System.out.println("\n=== 性能对比 ===");
        double performanceRatio = (double) fairLockTime / unfairLockTime;
        System.out.printf("公平锁/非公平锁性能比：%.2f\n", performanceRatio);
        if (performanceRatio > 1) {
            System.out.printf("非公平锁比公平锁快 %.1f%%\n", (performanceRatio - 1) * 100);
        }
        else {
            System.out.printf("公平锁比非公平锁快 %.1f%%\n", (1 / performanceRatio - 1) * 100);
        }
    }
}
