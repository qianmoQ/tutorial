package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition与Object.wait/notify的对比演示
 */
public class ConditionVsWaitNotifyDemo
{

    // 使用Object的wait/notify机制
    private final Object monitor = new Object();
    private boolean objectCondition = false;

    // 使用Condition机制
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean conditionFlag = false;

    /**
     * 使用Object.wait()的等待方法
     */
    public void waitWithObjectMonitor()
    {
        synchronized (monitor) {
            try {
                while (!objectCondition) {
                    System.out.println(Thread.currentThread().getName() + " 使用Object.wait()等待");
                    monitor.wait();
                }
                System.out.println(Thread.currentThread().getName() + " Object.wait()被唤醒，继续执行");
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 使用Object.notify()的通知方法
     */
    public void notifyWithObjectMonitor()
    {
        synchronized (monitor) {
            try {
                Thread.sleep(1000);
                objectCondition = true;
                System.out.println(Thread.currentThread().getName() + " 使用Object.notify()通知");
                monitor.notify();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 使用Condition.await()的等待方法
     */
    public void waitWithCondition()
    {
        lock.lock();
        try {
            while (!conditionFlag) {
                System.out.println(Thread.currentThread().getName() + " 使用Condition.await()等待");
                condition.await();
            }
            System.out.println(Thread.currentThread().getName() + " Condition.await()被唤醒，继续执行");
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 使用Condition.signal()的通知方法
     */
    public void notifyWithCondition()
    {
        lock.lock();
        try {
            Thread.sleep(1000);
            conditionFlag = true;
            System.out.println(Thread.currentThread().getName() + " 使用Condition.signal()通知");
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
        ConditionVsWaitNotifyDemo demo = new ConditionVsWaitNotifyDemo();

        System.out.println("=== Object.wait/notify机制演示 ===");

        Thread objectWaiter = new Thread(() -> {
            demo.waitWithObjectMonitor();
        }, "Object等待线程");

        Thread objectNotifier = new Thread(() -> {
            demo.notifyWithObjectMonitor();
        }, "Object通知线程");

        objectWaiter.start();
        Thread.sleep(100);
        objectNotifier.start();

        objectWaiter.join();
        objectNotifier.join();

        System.out.println("\n=== Condition机制演示 ===");

        Thread conditionWaiter = new Thread(() -> {
            demo.waitWithCondition();
        }, "Condition等待线程");

        Thread conditionNotifier = new Thread(() -> {
            demo.notifyWithCondition();
        }, "Condition通知线程");

        conditionWaiter.start();
        Thread.sleep(100);
        conditionNotifier.start();

        conditionWaiter.join();
        conditionNotifier.join();
    }
}
