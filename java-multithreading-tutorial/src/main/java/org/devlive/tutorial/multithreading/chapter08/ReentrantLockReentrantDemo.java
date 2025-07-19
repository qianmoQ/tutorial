package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示ReentrantLock的可重入性
 */
public class ReentrantLockReentrantDemo
{

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 外层方法，获取锁后调用内层方法
     */
    public void outerMethod()
    {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 进入外层方法，锁持有次数：" + lock.getHoldCount());

            // 调用内层方法，需要再次获取同一把锁
            innerMethod();

            System.out.println(Thread.currentThread().getName() + " 完成外层方法");
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 内层方法，需要获取同一把锁
     */
    public void innerMethod()
    {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 进入内层方法，锁持有次数：" + lock.getHoldCount());

            // 调用递归方法
            if (lock.getHoldCount() < 5) {
                recursiveMethod();
            }

            System.out.println(Thread.currentThread().getName() + " 完成内层方法");
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 递归方法，演示多层重入
     */
    public void recursiveMethod()
    {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 递归调用，锁持有次数：" + lock.getHoldCount());

            if (lock.getHoldCount() < 5) {
                recursiveMethod();
            }
        }
        finally {
            lock.unlock();
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        ReentrantLockReentrantDemo demo = new ReentrantLockReentrantDemo();

        // 创建多个线程测试可重入性
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread(demo::outerMethod, "线程-" + (i + 1));
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
