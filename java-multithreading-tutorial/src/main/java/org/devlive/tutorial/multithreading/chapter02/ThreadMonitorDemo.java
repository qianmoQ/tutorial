package org.devlive.tutorial.multithreading.chapter02;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 线程监控工具使用示例
 */
public class ThreadMonitorDemo
{
    // 共享锁对象
    private static final Object lock = new Object();
    // 用于协调线程的CountDownLatch
    private static final CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args)
    {
        // 创建线程监控器，每2秒监控一次
        ThreadMonitor monitor = new ThreadMonitor(2);
        // 创建几个用于演示不同状态的线程
        // 1. 创建一个长时间运行的线程
        Thread runningThread = new Thread(() -> {
            System.out.println("长时间运行的线程开始执行");
            long sum = 0;
            for (long i = 0; i < 10_000_000_000L; i++) {
                sum += i;
                if (i % 1_000_000_000 == 0) {
                    System.out.println("计算中: " + i / 1_000_000_000);
                }
            }
            System.out.println("长时间运行的线程计算结果: " + sum);
        });
        // 2. 创建一个会进入WAITING状态的线程
        Thread waitingThread = new Thread(() -> {
            try {
                System.out.println("等待线程开始执行");
                latch.await(); // 等待主线程计数器减为0
                System.out.println("等待线程继续执行");
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 3. 创建一个会进入TIMED_WAITING状态的线程
        Thread sleepingThread = new Thread(() -> {
            try {
                System.out.println("休眠线程开始执行");
                Thread.sleep(20000); // 休眠20秒
                System.out.println("休眠线程醒来继续执行");
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 4. 创建一个会进入BLOCKED状态的线程
        Thread blockedThread = new Thread(() -> {
            System.out.println("阻塞线程开始执行，等待获取锁");
            synchronized (lock) {
                System.out.println("阻塞线程获取到锁");
            }
        });
        // 主线程先获取锁，让blockedThread进入BLOCKED状态
        synchronized (lock) {
            // 添加线程到监控器
            monitor.addThread("运行线程", runningThread);
            monitor.addThread("等待线程", waitingThread);
            monitor.addThread("休眠线程", sleepingThread);
            monitor.addThread("阻塞线程", blockedThread);
            // 启动线程
            runningThread.start();
            waitingThread.start();
            sleepingThread.start();
            blockedThread.start();
            // 开始监控
            monitor.startMonitoring();
            // 主线程持有锁10秒钟，让blockedThread保持BLOCKED状态
            try {
                System.out.println("主线程持有锁10秒钟，blockedThread将保持BLOCKED状态");
                TimeUnit.SECONDS.sleep(10);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("主线程释放锁，blockedThread将获取锁并继续执行");
        }
        // 5秒后释放等待线程
        try {
            TimeUnit.SECONDS.sleep(5);
            System.out.println("主线程释放等待线程");
            latch.countDown();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 等待所有线程结束
        try {
            runningThread.join();
            waitingThread.join();
            sleepingThread.join();
            blockedThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 停止监控
        monitor.stopMonitoring();
    }
}