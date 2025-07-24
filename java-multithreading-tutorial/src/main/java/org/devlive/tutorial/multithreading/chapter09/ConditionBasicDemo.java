package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition基本概念演示
 */
public class ConditionBasicDemo
{

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean ready = false;

    /**
     * 等待条件满足的方法
     */
    public void waitForCondition()
    {
        lock.lock();
        try {
            // 当条件不满足时，线程进入等待状态
            while (!ready) {
                System.out.println(Thread.currentThread().getName() + " 条件未满足，开始等待...");
                condition.await(); // 等待条件变量
                System.out.println(Thread.currentThread().getName() + " 被唤醒，重新检查条件");
            }

            // 条件满足，执行相应的操作
            System.out.println(Thread.currentThread().getName() + " 条件已满足，开始执行任务");

            // 模拟任务执行
            Thread.sleep(100);
            System.out.println(Thread.currentThread().getName() + " 任务执行完毕");
        }
        catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 在等待时被中断");
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 设置条件并通知等待的线程
     */
    public void setConditionAndNotify()
    {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 准备设置条件");

            // 模拟准备工作
            Thread.sleep(2000);

            // 设置条件为满足
            ready = true;
            System.out.println(Thread.currentThread().getName() + " 条件已设置，通知等待的线程");

            // 通知一个等待的线程
            condition.signal();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        ConditionBasicDemo demo = new ConditionBasicDemo();

        // 创建等待线程
        Thread waiter1 = new Thread(() -> {
            demo.waitForCondition();
        }, "等待线程1");

        Thread waiter2 = new Thread(() -> {
            demo.waitForCondition();
        }, "等待线程2");

        // 创建通知线程
        Thread notifier = new Thread(() -> {
            demo.setConditionAndNotify();
        }, "通知线程");

        // 启动等待线程
        waiter1.start();
        waiter2.start();

        // 稍等一下，确保等待线程已经开始等待
        Thread.sleep(500);

        // 启动通知线程
        notifier.start();

        // 等待所有线程完成
        waiter1.join();
        waiter2.join();
        notifier.join();

        System.out.println("所有线程执行完毕");
    }
}
