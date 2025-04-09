package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.TimeUnit;

public class LockComparisonDemo
{
    // 静态变量
    private static int staticCounter = 0;
    // 实例变量
    private int instanceCounter = 0;

    // 类锁方法
    public static synchronized void incrementStatic()
    {
        staticCounter++;
        System.out.println(Thread.currentThread().getName() + " - 静态计数器：" + staticCounter);
        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        // 创建同一个实例
        final LockComparisonDemo demo = new LockComparisonDemo();

        // 线程1：访问实例方法（对象锁）
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.incrementInstance();
            }
        }, "对象锁线程").start();

        // 线程2：访问静态方法（类锁）
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                LockComparisonDemo.incrementStatic();
            }
        }, "类锁线程").start();

        // 关键点：对象锁和类锁是相互独立的，不会互相阻塞
    }

    // 对象锁方法
    public synchronized void incrementInstance()
    {
        instanceCounter++;
        System.out.println(Thread.currentThread().getName() + " - 实例计数器：" + instanceCounter);
        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
