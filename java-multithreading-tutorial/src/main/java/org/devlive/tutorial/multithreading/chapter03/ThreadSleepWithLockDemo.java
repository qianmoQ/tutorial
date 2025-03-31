package org.devlive.tutorial.multithreading.chapter03;

/**
 * 演示sleep方法不会释放锁
 */
public class ThreadSleepWithLockDemo
{
    private static final Object lock = new Object();

    public static void main(String[] args)
    {
        // 创建第一个线程，获取锁后休眠
        Thread thread1 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("线程1获取到锁");
                System.out.println("线程1开始休眠3秒...");
                try {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("线程1休眠结束，释放锁");
            }
        });
        // 创建第二个线程，尝试获取锁
        Thread thread2 = new Thread(() -> {
            System.out.println("线程2尝试获取锁...");
            synchronized (lock) {
                System.out.println("线程2获取到锁");
            }
            System.out.println("线程2释放锁");
        });
        // 启动线程
        thread1.start();
        // 确保线程1先执行
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread2.start();
    }
}