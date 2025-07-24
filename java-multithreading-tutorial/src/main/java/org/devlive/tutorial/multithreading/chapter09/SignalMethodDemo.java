package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * signal()和signalAll()方法的区别演示
 */
public class SignalMethodDemo
{

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private int value = 0;

    /**
     * 等待值变为正数的方法
     */
    public void waitForPositive()
    {
        lock.lock();
        try {
            while (value <= 0) {
                System.out.println(Thread.currentThread().getName() + " 等待值变为正数，当前值: " + value);
                condition.await();
            }
            System.out.println(Thread.currentThread().getName() + " 检测到正数值: " + value + "，开始处理");

            // 模拟处理时间
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + " 处理完毕");
        }
        catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 被中断");
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 使用signal()通知一个等待的线程
     */
    public void incrementAndSignal()
    {
        lock.lock();
        try {
            value++;
            System.out.println(Thread.currentThread().getName() + " 将值增加到: " + value + "，使用signal()通知一个线程");
            condition.signal(); // 只通知一个等待的线程
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 使用signalAll()通知所有等待的线程
     */
    public void incrementAndSignalAll()
    {
        lock.lock();
        try {
            value++;
            System.out.println(Thread.currentThread().getName() + " 将值增加到: " + value + "，使用signalAll()通知所有线程");
            condition.signalAll(); // 通知所有等待的线程
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 重置值为0
     */
    public void resetValue()
    {
        lock.lock();
        try {
            value = 0;
            System.out.println(Thread.currentThread().getName() + " 重置值为: " + value);
        }
        finally {
            lock.unlock();
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        SignalMethodDemo demo = new SignalMethodDemo();

        System.out.println("=== 演示signal()方法 - 只唤醒一个线程 ===");

        // 创建多个等待线程
        Thread[] waiters = new Thread[3];
        for (int i = 0; i < 3; i++) {
            waiters[i] = new Thread(() -> {
                demo.waitForPositive();
            }, "等待线程-" + (i + 1));
            waiters[i].start();
        }

        Thread.sleep(1000); // 确保所有等待线程都开始等待

        // 使用signal()通知
        Thread signalThread = new Thread(() -> {
            demo.incrementAndSignal();
        }, "Signal通知线程");
        signalThread.start();
        signalThread.join();

        // 等待被唤醒的线程完成
        Thread.sleep(2000);

        // 重置并演示signalAll()
        demo.resetValue();

        System.out.println("\n=== 演示signalAll()方法 - 唤醒所有线程 ===");

        // 创建新的等待线程
        Thread[] waiters2 = new Thread[3];
        for (int i = 0; i < 3; i++) {
            waiters2[i] = new Thread(() -> {
                demo.waitForPositive();
            }, "等待线程2-" + (i + 1));
            waiters2[i].start();
        }

        Thread.sleep(1000); // 确保所有等待线程都开始等待

        // 使用signalAll()通知
        Thread signalAllThread = new Thread(() -> {
            demo.incrementAndSignalAll();
        }, "SignalAll通知线程");
        signalAllThread.start();
        signalAllThread.join();

        // 等待所有线程完成
        for (Thread waiter : waiters2) {
            waiter.join();
        }

        // 清理剩余的等待线程（如果有的话）
        for (Thread waiter : waiters) {
            if (waiter.isAlive()) {
                waiter.interrupt();
                waiter.join();
            }
        }

        System.out.println("所有线程执行完毕");
    }
}
