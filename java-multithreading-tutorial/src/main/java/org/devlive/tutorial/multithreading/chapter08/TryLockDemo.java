package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示tryLock方法的各种使用方式
 */
public class TryLockDemo
{

    private final ReentrantLock lock = new ReentrantLock();
    private int resource = 0;

    /**
     * 使用tryLock()立即尝试获取锁
     */
    public void immediatelyTryLock()
    {
        if (lock.tryLock()) {
            try {
                System.out.println(Thread.currentThread().getName() + " 立即获取锁成功");
                resource++;
                System.out.println(Thread.currentThread().getName() + " 处理资源，当前值：" + resource);

                // 模拟处理时间
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            finally {
                lock.unlock();
                System.out.println(Thread.currentThread().getName() + " 释放锁");
            }
        }
        else {
            System.out.println(Thread.currentThread().getName() + " 立即获取锁失败，执行替代方案");
            // 执行不需要锁的替代逻辑
            performAlternativeTask();
        }
    }

    /**
     * 使用tryLock(time, unit)在指定时间内尝试获取锁
     */
    public void tryLockWithTimeout(long timeout, TimeUnit unit)
    {
        try {
            if (lock.tryLock(timeout, unit)) {
                try {
                    System.out.println(Thread.currentThread().getName() + " 在超时时间内获取锁成功");
                    resource++;
                    System.out.println(Thread.currentThread().getName() + " 处理资源，当前值：" + resource);

                    // 模拟处理时间
                    Thread.sleep(1500);
                }
                finally {
                    lock.unlock();
                    System.out.println(Thread.currentThread().getName() + " 释放锁");
                }
            }
            else {
                System.out.println(Thread.currentThread().getName() + " 获取锁超时，执行替代方案");
                performAlternativeTask();
            }
        }
        catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 在等待锁时被中断");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 执行不需要锁的替代任务
     */
    private void performAlternativeTask()
    {
        System.out.println(Thread.currentThread().getName() + " 执行替代任务：读取资源当前值 = " + resource);
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        TryLockDemo demo = new TryLockDemo();

        System.out.println("=== 演示立即尝试获取锁 ===");

        // 创建多个线程测试立即尝试获取锁
        Thread[] threads1 = new Thread[3];
        for (int i = 0; i < 3; i++) {
            threads1[i] = new Thread(demo::immediatelyTryLock, "即时线程-" + (i + 1));
            threads1[i].start();
        }

        // 等待第一组线程完成
        for (Thread thread : threads1) {
            thread.join();
        }

        System.out.println("\n=== 演示带超时的尝试获取锁 ===");

        // 创建多个线程测试带超时的尝试获取锁
        Thread[] threads2 = new Thread[4];
        for (int i = 0; i < 4; i++) {
            threads2[i] = new Thread(() -> {
                demo.tryLockWithTimeout(1, TimeUnit.SECONDS);
            }, "超时线程-" + (i + 1));
            threads2[i].start();
        }

        // 等待第二组线程完成
        for (Thread thread : threads2) {
            thread.join();
        }

        System.out.println("\n最终资源值：" + demo.resource);
    }
}
