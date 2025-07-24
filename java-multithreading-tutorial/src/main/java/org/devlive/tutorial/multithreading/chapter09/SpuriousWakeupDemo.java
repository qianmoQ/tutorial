package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示虚假唤醒问题及解决方案
 */
public class SpuriousWakeupDemo
{

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean ready = false;
    private int waitCount = 0;

    /**
     * 错误的做法：使用if检查条件（可能受虚假唤醒影响）
     */
    public void wrongWayToWait()
    {
        lock.lock();
        try {
            waitCount++;
            System.out.println(Thread.currentThread().getName() + " 开始等待（错误方式 - 使用if）");

            if (!ready) {  // 错误：使用if而不是while
                condition.await();
            }

            // 这里可能在条件未满足时执行
            System.out.println(Thread.currentThread().getName() + " 停止等待，ready状态: " + ready);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 正确的做法：使用while循环检查条件（防止虚假唤醒）
     */
    public void correctWayToWait()
    {
        lock.lock();
        try {
            waitCount++;
            System.out.println(Thread.currentThread().getName() + " 开始等待（正确方式 - 使用while）");

            while (!ready) {  // 正确：使用while循环
                condition.await();
                System.out.println(Thread.currentThread().getName() + " 被唤醒，重新检查条件，ready: " + ready);
            }

            // 只有在条件满足时才会执行到这里
            System.out.println(Thread.currentThread().getName() + " 条件满足，继续执行");
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 设置条件并通知
     */
    public void notifyWaiters()
    {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 当前等待线程数: " + waitCount);

            // 模拟一些准备工作
            Thread.sleep(1000);

            ready = true;
            System.out.println(Thread.currentThread().getName() + " 设置ready为true，发出通知");
            condition.signalAll();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 模拟虚假唤醒的场景
     */
    public void simulateSpuriousWakeup()
    {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 模拟虚假唤醒 - 在条件未满足时发出信号");
            condition.signalAll(); // 在ready仍为false时发出信号，模拟虚假唤醒
        }
        finally {
            lock.unlock();
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        SpuriousWakeupDemo demo = new SpuriousWakeupDemo();

        System.out.println("=== 演示错误的等待方式（可能受虚假唤醒影响） ===");

        // 创建使用错误方式等待的线程
        Thread wrongWaiter = new Thread(() -> {
            demo.wrongWayToWait();
        }, "错误等待线程");

        wrongWaiter.start();
        Thread.sleep(500);

        // 模拟虚假唤醒
        Thread spuriousWaker = new Thread(() -> {
            demo.simulateSpuriousWakeup();
        }, "虚假唤醒线程");

        spuriousWaker.start();
        spuriousWaker.join();
        wrongWaiter.join();

        // 重置状态
        demo.ready = false;
        demo.waitCount = 0;

        System.out.println("\n=== 演示正确的等待方式（防止虚假唤醒） ===");

        // 创建使用正确方式等待的线程
        Thread correctWaiter = new Thread(() -> {
            demo.correctWayToWait();
        }, "正确等待线程");

        correctWaiter.start();
        Thread.sleep(500);

        // 再次模拟虚假唤醒
        Thread spuriousWaker2 = new Thread(() -> {
            demo.simulateSpuriousWakeup();
        }, "虚假唤醒线程2");

        spuriousWaker2.start();
        spuriousWaker2.join();

        // 等待一下，然后发出真正的通知
        Thread.sleep(1000);

        Thread realNotifier = new Thread(() -> {
            demo.notifyWaiters();
        }, "真正通知线程");

        realNotifier.start();
        realNotifier.join();
        correctWaiter.join();

        System.out.println("演示完成");
    }
}
