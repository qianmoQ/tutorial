package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 避免死锁的最佳实践
 */
public class DeadlockAvoidanceDemo
{

    private final ReentrantLock lock1 = new ReentrantLock();
    private final ReentrantLock lock2 = new ReentrantLock();

    /**
     * 可能导致死锁的方法
     */
    public void potentialDeadlockMethod1()
    {
        lock1.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 获取lock1");

            // 模拟一些处理时间
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            lock2.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 获取lock2");
                // 执行需要两个锁的操作
            }
            finally {
                lock2.unlock();
            }
        }
        finally {
            lock1.unlock();
        }
    }

    /**
     * 可能导致死锁的方法（不同的锁获取顺序）
     */
    public void potentialDeadlockMethod2()
    {
        lock2.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 获取lock2");

            // 模拟一些处理时间
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            lock1.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 获取lock1");
                // 执行需要两个锁的操作
            }
            finally {
                lock1.unlock();
            }
        }
        finally {
            lock2.unlock();
        }
    }

    /**
     * 避免死锁的方法1：统一锁的获取顺序
     */
    public void deadlockFreeMethod1()
    {
        // 始终先获取lock1，再获取lock2
        lock1.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 安全获取lock1");

            lock2.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 安全获取lock2");
                // 执行需要两个锁的操作

                try {
                    Thread.sleep(50);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            finally {
                lock2.unlock();
            }
        }
        finally {
            lock1.unlock();
        }
    }

    /**
     * 避免死锁的方法2：使用tryLock避免无限等待
     */
    public void deadlockFreeMethod2()
    {
        boolean acquired = false;

        try {
            // 尝试获取第一个锁
            if (lock1.tryLock(500, TimeUnit.MILLISECONDS)) {
                try {
                    System.out.println(Thread.currentThread().getName() + " tryLock获取lock1成功");

                    // 尝试获取第二个锁
                    if (lock2.tryLock(500, TimeUnit.MILLISECONDS)) {
                        try {
                            System.out.println(Thread.currentThread().getName() + " tryLock获取lock2成功");
                            acquired = true;

                            // 执行需要两个锁的操作
                            Thread.sleep(50);
                        }
                        finally {
                            lock2.unlock();
                        }
                    }
                    else {
                        System.out.println(Thread.currentThread().getName() + " tryLock获取lock2失败");
                    }
                }
                finally {
                    lock1.unlock();
                }
            }
            else {
                System.out.println(Thread.currentThread().getName() + " tryLock获取lock1失败");
            }
        }
        catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 被中断");
            Thread.currentThread().interrupt();
        }

        if (!acquired) {
            System.out.println(Thread.currentThread().getName() + " 未能获取所有锁，执行替代逻辑");
        }
    }

    /**
     * 避免死锁的方法3：使用锁排序
     */
    public void deadlockFreeMethod3()
    {
        // 根据锁的hash值排序，确保获取顺序一致
        ReentrantLock firstLock = System.identityHashCode(lock1) < System.identityHashCode(lock2) ? lock1 : lock2;
        ReentrantLock secondLock = firstLock == lock1 ? lock2 : lock1;

        firstLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 按顺序获取第一个锁");

            secondLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 按顺序获取第二个锁");
                // 执行需要两个锁的操作

                try {
                    Thread.sleep(50);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            finally {
                secondLock.unlock();
            }
        }
        finally {
            firstLock.unlock();
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        DeadlockAvoidanceDemo demo = new DeadlockAvoidanceDemo();

        System.out.println("=== 演示可能导致死锁的情况（不要在生产环境运行） ===");
        // 注意：这段代码可能导致死锁，仅用于演示
        /*
        Thread deadlockThread1 = new Thread(() -> {
            demo.potentialDeadlockMethod1();
        }, "可能死锁线程1");

        Thread deadlockThread2 = new Thread(() -> {
            demo.potentialDeadlockMethod2();
        }, "可能死锁线程2");

        deadlockThread1.start();
        deadlockThread2.start();
        */

        System.out.println("=== 演示避免死锁的方法1：统一锁顺序 ===");

        Thread safeThread1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.deadlockFreeMethod1();
            }
        }, "安全线程1");

        Thread safeThread2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.deadlockFreeMethod1();
            }
        }, "安全线程2");

        safeThread1.start();
        safeThread2.start();
        safeThread1.join();
        safeThread2.join();

        System.out.println("\n=== 演示避免死锁的方法2：使用tryLock ===");

        Thread tryLockThread1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.deadlockFreeMethod2();
            }
        }, "tryLock线程1");

        Thread tryLockThread2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.deadlockFreeMethod2();
            }
        }, "tryLock线程2");

        tryLockThread1.start();
        tryLockThread2.start();
        tryLockThread1.join();
        tryLockThread2.join();

        System.out.println("\n=== 演示避免死锁的方法3：锁排序 ===");

        Thread sortedThread1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.deadlockFreeMethod3();
            }
        }, "排序线程1");

        Thread sortedThread2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.deadlockFreeMethod3();
            }
        }, "排序线程2");

        sortedThread1.start();
        sortedThread2.start();
        sortedThread1.join();
        sortedThread2.join();
    }
}
