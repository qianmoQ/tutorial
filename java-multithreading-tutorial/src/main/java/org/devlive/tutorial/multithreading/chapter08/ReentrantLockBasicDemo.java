package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock基本使用示例
 */
public class ReentrantLockBasicDemo
{

    private final ReentrantLock lock = new ReentrantLock();
    private int count = 0;

    /**
     * 使用ReentrantLock保护共享资源
     */
    public void increment()
    {
        // 获取锁
        lock.lock();
        try {
            // 临界区：修改共享资源
            count++;
            System.out.println(Thread.currentThread().getName() + " 执行increment，当前值：" + count);

            // 模拟一些处理时间
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            // 在finally块中释放锁，确保锁一定会被释放
            lock.unlock();
        }
    }

    /**
     * 获取当前计数值
     */
    public int getCount()
    {
        lock.lock();
        try {
            return count;
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 显示锁的状态信息
     */
    public void showLockInfo()
    {
        System.out.println("锁是否被当前线程持有：" + lock.isHeldByCurrentThread());
        System.out.println("锁的持有次数：" + lock.getHoldCount());
        System.out.println("等待获取锁的线程数：" + lock.getQueueLength());
        System.out.println("是否是公平锁：" + lock.isFair());
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        ReentrantLockBasicDemo demo = new ReentrantLockBasicDemo();

        // 在主线程中显示锁的初始状态
        System.out.println("=== 锁的初始状态 ===");
        demo.showLockInfo();

        // 创建多个线程并发执行increment操作
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    demo.increment();
                }
            }, "工作线程-" + i);
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("\n=== 最终结果 ===");
        System.out.println("最终计数值：" + demo.getCount());
        demo.showLockInfo();
    }
}
