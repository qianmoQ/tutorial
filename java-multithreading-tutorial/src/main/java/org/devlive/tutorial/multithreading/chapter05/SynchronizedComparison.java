package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.TimeUnit;

public class SynchronizedComparison
{

    private int data = 0;

    public static void main(String[] args)
    {
        SynchronizedComparison demo = new SynchronizedComparison();

        System.out.println("====== 测试方法同步 ======");
        Thread t1 = new Thread(() -> demo.methodSync(), "线程1");
        Thread t2 = new Thread(() -> demo.methodSync(), "线程2");
        t1.start();
        t2.start();

        // 等待两个线程执行完毕
        try {
            t1.join();
            t2.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n====== 测试代码块同步 ======");
        demo.data = 0; // 重置数据
        Thread t3 = new Thread(() -> demo.blockSync(), "线程3");
        Thread t4 = new Thread(() -> demo.blockSync(), "线程4");
        t3.start();
        t4.start();
    }

    // 修饰整个方法
    public synchronized void methodSync()
    {
        System.out.println(Thread.currentThread().getName() + " 开始执行方法同步...");

        // 执行一些非同步操作（假设是耗时的IO操作）
        try {
            System.out.println(Thread.currentThread().getName() + " 执行耗时操作...");
            TimeUnit.SECONDS.sleep(2);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 真正需要同步的只是这部分数据操作
        data++;
        System.out.println(Thread.currentThread().getName() + " 数据更新为：" + data);

        // 再次执行一些非同步操作
        try {
            System.out.println(Thread.currentThread().getName() + " 执行额外操作...");
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " 方法执行完毕");
    }

    // 只同步关键代码块
    public void blockSync()
    {
        System.out.println(Thread.currentThread().getName() + " 开始执行方法...");

        // 执行一些非同步操作（假设是耗时的IO操作）
        try {
            System.out.println(Thread.currentThread().getName() + " 执行耗时操作...");
            TimeUnit.SECONDS.sleep(2);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 只同步关键代码块
        synchronized (this) {
            data++;
            System.out.println(Thread.currentThread().getName() + " 数据更新为：" + data);
        }

        // 再次执行一些非同步操作
        try {
            System.out.println(Thread.currentThread().getName() + " 执行额外操作...");
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " 方法执行完毕");
    }
}
