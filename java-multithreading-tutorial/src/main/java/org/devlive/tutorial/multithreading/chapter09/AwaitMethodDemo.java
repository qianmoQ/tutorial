package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * await()方法的各种使用方式
 */
public class AwaitMethodDemo
{

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean ready = false;

    /**
     * 基本的await()方法使用
     */
    public void basicAwait()
    {
        lock.lock();
        try {
            while (!ready) {
                System.out.println(Thread.currentThread().getName() + " 使用基本await()等待");
                condition.await(); // 无限期等待
            }
            System.out.println(Thread.currentThread().getName() + " 基本await()被唤醒");
        }
        catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 基本await()被中断");
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 带超时的await()方法使用
     */
    public void awaitWithTimeout()
    {
        lock.lock();
        try {
            while (!ready) {
                System.out.println(Thread.currentThread().getName() + " 使用带超时的await()等待，最多等待2秒");
                boolean signaled = condition.await(2, TimeUnit.SECONDS);

                if (signaled) {
                    System.out.println(Thread.currentThread().getName() + " 带超时的await()被信号唤醒");
                }
                else {
                    System.out.println(Thread.currentThread().getName() + " 带超时的await()超时返回");
                    return; // 超时退出
                }
            }
            System.out.println(Thread.currentThread().getName() + " 带超时的await()条件满足");
        }
        catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 带超时的await()被中断");
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 不可中断的await()方法使用
     */
    public void awaitUninterruptibly()
    {
        lock.lock();
        try {
            while (!ready) {
                System.out.println(Thread.currentThread().getName() + " 使用不可中断的awaitUninterruptibly()等待");
                condition.awaitUninterruptibly(); // 不会抛出InterruptedException
            }
            System.out.println(Thread.currentThread().getName() + " 不可中断的awaitUninterruptibly()被唤醒");
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 等到指定时间的await()方法使用
     */
    public void awaitUntil()
    {
        lock.lock();
        try {
            // 设置等待到的具体时间点（当前时间后3秒）
            long deadlineTime = System.currentTimeMillis() + 3000;
            java.util.Date deadline = new java.util.Date(deadlineTime);

            while (!ready) {
                System.out.println(Thread.currentThread().getName() + " 使用awaitUntil()等待到: " + deadline);
                boolean signaled = condition.awaitUntil(deadline);

                if (signaled) {
                    System.out.println(Thread.currentThread().getName() + " awaitUntil()被信号唤醒");
                }
                else {
                    System.out.println(Thread.currentThread().getName() + " awaitUntil()到达指定时间点");
                    return; // 到达时间点退出
                }
            }
            System.out.println(Thread.currentThread().getName() + " awaitUntil()条件满足");
        }
        catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " awaitUntil()被中断");
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 设置条件并通知
     */
    public void setReadyAndNotify()
    {
        lock.lock();
        try {
            Thread.sleep(4000); // 等待4秒后才设置条件
            ready = true;
            System.out.println(Thread.currentThread().getName() + " 条件已设置为ready，发出通知");
            condition.signalAll(); // 通知所有等待的线程
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
        AwaitMethodDemo demo = new AwaitMethodDemo();

        // 创建使用不同await方法的线程
        Thread basicThread = new Thread(() -> {
            demo.basicAwait();
        }, "基本等待线程");

        Thread timeoutThread = new Thread(() -> {
            demo.awaitWithTimeout();
        }, "超时等待线程");

        Thread uninterruptibleThread = new Thread(() -> {
            demo.awaitUninterruptibly();
        }, "不可中断等待线程");

        Thread untilThread = new Thread(() -> {
            demo.awaitUntil();
        }, "定时等待线程");

        Thread notifierThread = new Thread(() -> {
            demo.setReadyAndNotify();
        }, "通知线程");

        // 启动所有等待线程
        basicThread.start();
        timeoutThread.start();
        uninterruptibleThread.start();
        untilThread.start();

        Thread.sleep(500); // 确保等待线程都开始等待

        // 测试中断功能
        System.out.println("主线程尝试中断不可中断等待线程（应该无效）");
        uninterruptibleThread.interrupt();

        Thread.sleep(500);

        // 启动通知线程
        notifierThread.start();

        // 等待所有线程完成
        basicThread.join();
        timeoutThread.join();
        uninterruptibleThread.join();
        untilThread.join();
        notifierThread.join();

        System.out.println("所有线程执行完毕");
    }
}
