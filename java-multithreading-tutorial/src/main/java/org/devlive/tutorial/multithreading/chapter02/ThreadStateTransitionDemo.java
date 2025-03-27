package org.devlive.tutorial.multithreading.chapter02;

import java.util.concurrent.TimeUnit;

/**
 * 线程状态转换综合示例
 */
public class ThreadStateTransitionDemo
{
    // 共享锁对象
    private static final Object lock = new Object();
    // 标记是否应该等待
    private static boolean shouldWait = true;

    public static void main(String[] args)
            throws InterruptedException
    {
        // 创建线程，该线程会经历所有可能的状态
        Thread thread = new Thread(() -> {
            System.out.println("线程开始执行...");
            // 进入RUNNABLE状态的一些计算
            System.out.println("执行一些计算...");
            for (int i = 0; i < 1000000; i++) {
                Math.sqrt(i);
            }
            synchronized (lock) {
                // 进入条件等待
                if (shouldWait) {
                    try {
                        System.out.println("线程即将进入WAITING状态...");
                        lock.wait(); // 这会导致线程进入WAITING状态
                        System.out.println("线程从WAITING状态被唤醒！");
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 执行受锁保护的代码
                System.out.println("执行受锁保护的代码");
            }
            // 进入TIMED_WAITING状态
            try {
                System.out.println("线程即将进入TIMED_WAITING状态...");
                TimeUnit.SECONDS.sleep(2); // 休眠2秒，进入TIMED_WAITING状态
                System.out.println("线程从TIMED_WAITING状态恢复");
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程执行完毕，即将进入TERMINATED状态");
        });
        // 打印线程的初始状态（NEW）
        System.out.println("1. 初始状态: " + thread.getState());
        // 启动线程
        thread.start();
        System.out.println("2. 调用start()后: " + thread.getState());
        // 给线程一点时间运行
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("3. 线程运行中: " + thread.getState());
        // 线程应该已经进入WAITING状态
        TimeUnit.SECONDS.sleep(1);
        System.out.println("4. 线程应该在等待: " + thread.getState());
        // 在锁上调用notify，唤醒等待的线程
        synchronized (lock) {
            System.out.println("主线程获取到锁，准备唤醒等待的线程");
            shouldWait = false;
            lock.notify();
        }
        // 给线程一点时间执行并进入TIMED_WAITING状态
        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("5. 线程应该在限时等待: " + thread.getState());
        // 等待线程执行完毕
        thread.join();
        System.out.println("6. 线程执行完毕: " + thread.getState());
    }
}