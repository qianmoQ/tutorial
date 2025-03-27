package org.devlive.tutorial.multithreading.chapter02;

/**
 * 死锁示例
 */
public class DeadlockDemo
{
    // 两个共享资源
    private static final Object resource1 = new Object();
    private static final Object resource2 = new Object();

    public static void main(String[] args)
    {
        // 创建第一个线程，先获取resource1，再获取resource2
        Thread thread1 = new Thread(() -> {
            System.out.println("线程1尝试获取resource1...");
            synchronized (resource1) {
                System.out.println("线程1获取到resource1");
                // 让线程休眠一会儿，确保线程2有时间获取resource2
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("线程1尝试获取resource2...");
                synchronized (resource2) {
                    System.out.println("线程1同时获取到resource1和resource2");
                }
            }
        });
        // 创建第二个线程，先获取resource2，再获取resource1
        Thread thread2 = new Thread(() -> {
            System.out.println("线程2尝试获取resource2...");
            synchronized (resource2) {
                System.out.println("线程2获取到resource2");
                // 让线程休眠一会儿，确保线程1有时间获取resource1
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("线程2尝试获取resource1...");
                synchronized (resource1) {
                    System.out.println("线程2同时获取到resource1和resource2");
                }
            }
        });
        // 启动线程
        thread1.start();
        thread2.start();
    }
}