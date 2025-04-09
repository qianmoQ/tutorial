package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.TimeUnit;

public class ClassLockDemo
{
    // 静态计数器
    private static int counter = 0;

    // 使用synchronized修饰静态方法，锁是当前类的Class对象
    public static synchronized void incrementStatic()
    {
        counter++;
        System.out.println(Thread.currentThread().getName() + " - 计数器：" + counter);
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        // 创建两个不同的实例
        final ClassLockDemo demo1 = new ClassLockDemo();
        final ClassLockDemo demo2 = new ClassLockDemo();

        // 创建5个线程，使用不同实例的方法
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                new Thread(() -> {
                    ClassLockDemo.incrementStatic(); // 使用静态方法（类锁）
                }, "静态方法线程-" + i).start();
            }
            else {
                new Thread(() -> {
                    demo2.incrementWithClassLock(); // 使用实例方法中的类锁
                }, "类锁代码块-" + i).start();
            }
        }
    }

    // 使用synchronized代码块锁定类对象
    public void incrementWithClassLock()
    {
        synchronized (ClassLockDemo.class) {
            counter++;
            System.out.println(Thread.currentThread().getName() + " - 计数器：" + counter);
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
