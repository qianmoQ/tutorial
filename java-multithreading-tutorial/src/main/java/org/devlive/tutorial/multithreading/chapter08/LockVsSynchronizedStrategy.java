package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock与synchronized的选择策略示例
 */
public class LockVsSynchronizedStrategy
{

    private final ReentrantLock lock = new ReentrantLock();
    private final Object syncLock = new Object();
    private int count = 0;

    /**
     * 使用synchronized的简单场景
     */
    public void simpleTaskWithSynchronized()
    {
        synchronized (syncLock) {
            count++;
            System.out.println(Thread.currentThread().getName() + " synchronized: " + count);
        }
    }

    /**
     * 使用Lock的需要高级功能的场景
     */
    public void advancedTaskWithLock()
    {
        try {
            // 尝试在1秒内获取锁
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    count++;
                    System.out.println(Thread.currentThread().getName() + " Lock: " + count);

                    // 模拟长时间处理
                    Thread.sleep(500);
                }
                finally {
                    lock.unlock();
                }
            }
            else {
                System.out.println(Thread.currentThread().getName() + " 获取锁超时，执行替代逻辑");
                // 执行不需要锁的替代逻辑
            }
        }
        catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 被中断");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 需要可中断的锁获取
     */
    public void interruptibleTask()
    {
        try {
            lock.lockInterruptibly();
            try {
                count++;
                System.out.println(Thread.currentThread().getName() + " 可中断Lock: " + count);

                // 模拟长时间处理
                Thread.sleep(2000);
            }
            finally {
                lock.unlock();
            }
        }
        catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 在等待锁时被中断");
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        LockVsSynchronizedStrategy demo = new LockVsSynchronizedStrategy();

        System.out.println("=== 选择策略指南 ===");
        System.out.println("使用synchronized的场景：");
        System.out.println("1. 简单的互斥访问");
        System.out.println("2. 代码简洁性更重要");
        System.out.println("3. 不需要特殊的锁功能");

        System.out.println("\n使用Lock的场景：");
        System.out.println("1. 需要尝试获取锁（tryLock）");
        System.out.println("2. 需要可中断的锁获取");
        System.out.println("3. 需要公平锁");
        System.out.println("4. 需要超时的锁获取");
        System.out.println("5. 需要条件变量（Condition）");

        System.out.println("\n=== 实际演示 ===");

        // 演示synchronized的简单使用
        Thread[] syncThreads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            syncThreads[i] = new Thread(demo::simpleTaskWithSynchronized, "Sync线程-" + (i + 1));
            syncThreads[i].start();
        }

        for (Thread thread : syncThreads) {
            thread.join();
        }

        // 演示Lock的高级功能
        Thread[] lockThreads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            lockThreads[i] = new Thread(demo::advancedTaskWithLock, "Lock线程-" + (i + 1));
            lockThreads[i].start();
        }

        for (Thread thread : lockThreads) {
            thread.join();
        }

        // 演示可中断的锁
        Thread interruptibleThread = new Thread(demo::interruptibleTask, "可中断线程");

        interruptibleThread.start();
        Thread.sleep(1000);

        System.out.println("主线程中断可中断线程...");
        interruptibleThread.interrupt();
        interruptibleThread.join();
    }
}
