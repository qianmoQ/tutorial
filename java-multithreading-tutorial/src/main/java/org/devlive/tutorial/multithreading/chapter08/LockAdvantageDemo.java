package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示Lock接口的优势
 */
public class LockAdvantageDemo
{
    private static final ReentrantLock lock = new ReentrantLock();
    private static int count = 0;

    /**
     * 使用Lock的可中断获取锁方法
     */
    public static void interruptibleMethod()
    {
        try {
            // 使用可中断的方式获取锁
            lock.lockInterruptibly();
            try {
                count++;
                System.out.println(Thread.currentThread().getName() + " 获取锁成功，count = " + count);

                // 模拟长时间持有锁
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

    /**
     * 使用Lock的超时获取锁方法
     */
    public static void timeoutMethod()
    {
        try {
            // 尝试在1秒内获取锁
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    count++;
                    System.out.println(Thread.currentThread().getName() + " 在超时时间内获取锁成功，count = " + count);
                    Thread.sleep(500);
                }
                finally {
                    lock.unlock();
                }
            }
            else {
                System.out.println(Thread.currentThread().getName() + " 获取锁超时");
            }
        }
        catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 在等待锁时被中断");
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        System.out.println("=== 演示可中断锁 ===");

        Thread thread1 = new Thread(LockAdvantageDemo::interruptibleMethod, "可中断线程1");

        Thread thread2 = new Thread(LockAdvantageDemo::interruptibleMethod, "可中断线程2");

        thread1.start();
        Thread.sleep(100);
        thread2.start();

        // 1秒后中断线程2
        Thread.sleep(1000);
        System.out.println("中断线程2...");
        thread2.interrupt();

        thread1.join();
        thread2.join();

        System.out.println("\n=== 演示超时锁 ===");

        Thread thread3 = new Thread(LockAdvantageDemo::timeoutMethod, "超时线程1");

        Thread thread4 = new Thread(LockAdvantageDemo::timeoutMethod, "超时线程2");

        thread3.start();
        Thread.sleep(100);
        thread4.start();

        thread3.join();
        thread4.join();
    }
}
