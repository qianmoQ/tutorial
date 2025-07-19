package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示公平锁与非公平锁的区别
 */
public class FairVsUnfairLockDemo
{

    // 公平锁：构造参数为true
    private final ReentrantLock fairLock = new ReentrantLock(true);

    // 非公平锁：构造参数为false或使用无参构造器
    private final ReentrantLock unfairLock = new ReentrantLock(false);

    /**
     * 使用公平锁的方法
     */
    public void fairLockMethod()
    {
        for (int i = 0; i < 2; i++) {
            fairLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 获取公平锁，执行第 " + (i + 1) + " 次操作");
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            finally {
                fairLock.unlock();
            }
        }
    }

    /**
     * 使用非公平锁的方法
     */
    public void unfairLockMethod()
    {
        for (int i = 0; i < 2; i++) {
            unfairLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 获取非公平锁，执行第 " + (i + 1) + " 次操作");
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            finally {
                unfairLock.unlock();
            }
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        FairVsUnfairLockDemo demo = new FairVsUnfairLockDemo();

        System.out.println("=== 公平锁测试（观察获取锁的顺序是否按线程启动顺序） ===");

        // 测试公平锁
        Thread[] fairThreads = new Thread[4];
        for (int i = 0; i < 4; i++) {
            fairThreads[i] = new Thread(demo::fairLockMethod, "公平锁线程-" + (i + 1));
            fairThreads[i].start();
            Thread.sleep(50); // 确保线程按顺序启动
        }

        // 等待公平锁测试完成
        for (Thread thread : fairThreads) {
            thread.join();
        }

        System.out.println("\n=== 非公平锁测试（观察是否存在插队现象） ===");

        // 测试非公平锁
        Thread[] unfairThreads = new Thread[4];
        for (int i = 0; i < 4; i++) {
            unfairThreads[i] = new Thread(demo::unfairLockMethod, "非公平锁线程-" + (i + 1));
            unfairThreads[i].start();
            Thread.sleep(50); // 确保线程按顺序启动
        }

        // 等待非公平锁测试完成
        for (Thread thread : unfairThreads) {
            thread.join();
        }
    }
}
