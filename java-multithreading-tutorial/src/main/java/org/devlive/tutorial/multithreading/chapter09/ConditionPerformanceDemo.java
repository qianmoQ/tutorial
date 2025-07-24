package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 条件变量的性能优化示例
 */
public class ConditionPerformanceDemo
{

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private volatile boolean ready = false;

    // 性能统计
    private volatile long signalCount = 0;
    private volatile long awaitCount = 0;

    /**
     * 优化的等待方法：减少不必要的锁获取
     */
    public void optimizedWait()
    {
        // 先进行无锁检查，减少锁竞争
        if (ready) {
            return;
        }

        lock.lock();
        try {
            // 双重检查模式
            while (!ready) {
                awaitCount++;
                condition.await();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 批量通知优化：减少signal调用次数
     */
    public void batchNotify(int count)
    {
        lock.lock();
        try {
            for (int i = 0; i < count; i++) {
                // 执行一些准备工作
                processItem(i);
            }

            // 批量设置条件并一次性通知
            ready = true;
            signalCount++;
            condition.signalAll(); // 一次通知所有等待者
        }
        finally {
            lock.unlock();
        }
    }

    private void processItem(int item)
    {
        // 模拟处理工作
        try {
            Thread.sleep(10);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 条件检查优化：使用更精确的条件
     */
    public void precisConditionWait(int expectedValue)
    {
        lock.lock();
        try {
            // 使用更精确的条件，减少虚假唤醒的影响
            while (getCurrentValue() != expectedValue) {
                condition.await();
            }

            System.out.println(Thread.currentThread().getName() +
                    " 精确条件满足，值为: " + expectedValue);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    private int getCurrentValue()
    {
        // 模拟获取当前值
        return ready ? 100 : 0;
    }

    /**
     * 获取性能统计信息
     */
    public void printPerformanceStats()
    {
        lock.lock();
        try {
            System.out.println("=== 性能统计 ===");
            System.out.println("信号发送次数: " + signalCount);
            System.out.println("等待次数: " + awaitCount);
            System.out.println("等待队列长度: " + lock.getWaitQueueLength(condition));
        }
        finally {
            lock.unlock();
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        ConditionPerformanceDemo demo = new ConditionPerformanceDemo();

        // 创建多个等待线程
        Thread[] waiters = new Thread[5];
        for (int i = 0; i < 5; i++) {
            waiters[i] = new Thread(() -> {
                demo.optimizedWait();
                System.out.println(Thread.currentThread().getName() + " 完成等待");
            }, "等待线程-" + (i + 1));
            waiters[i].start();
        }

        Thread.sleep(1000);
        demo.printPerformanceStats();

        // 批量通知
        Thread notifier = new Thread(() -> {
            demo.batchNotify(3);
            System.out.println("批量通知完成");
        }, "通知线程");

        notifier.start();

        // 等待所有线程完成
        for (Thread waiter : waiters) {
            waiter.join();
        }
        notifier.join();

        demo.printPerformanceStats();
    }
}
