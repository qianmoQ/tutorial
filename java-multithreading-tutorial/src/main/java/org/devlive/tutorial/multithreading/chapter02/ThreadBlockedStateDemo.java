package org.devlive.tutorial.multithreading.chapter02;

/**
 * 演示线程的BLOCKED状态
 */
public class ThreadBlockedStateDemo
{
    // 共享资源，用于线程同步
    private static final Object lock = new Object();

    public static void main(String[] args)
    {
        // 创建第一个线程，它会获取并持有锁
        Thread thread1 = new Thread(() -> {
            System.out.println("线程1开始执行");
            synchronized (lock) {
                // 持有锁10秒钟
                System.out.println("线程1获取到锁");
                try {
                    Thread.sleep(10000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("线程1释放锁");
            }
        });
        // 创建第二个线程，它会尝试获取锁但会被阻塞
        Thread thread2 = new Thread(() -> {
            System.out.println("线程2开始执行");
            synchronized (lock) {
                // 如果获取到锁，则执行这里的代码
                System.out.println("线程2获取到锁");
            }
        });
        // 启动第一个线程
        thread1.start();
        // 给线程1一点时间获取锁
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 启动第二个线程
        thread2.start();
        // 给线程2一点时间尝试获取锁
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 检查线程2的状态
        System.out.println("线程2的状态: " + thread2.getState());
        // 等待两个线程都结束
        try {
            thread1.join();
            thread2.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}