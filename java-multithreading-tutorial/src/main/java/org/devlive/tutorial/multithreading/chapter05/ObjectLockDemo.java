package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.TimeUnit;

public class ObjectLockDemo
{
    // 定义一个共享变量
    private int counter = 0;

    public static void main(String[] args)
    {
        final ObjectLockDemo demo = new ObjectLockDemo();

        // 创建5个线程并发执行同步方法
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                demo.incrementSync();
            }, "线程-" + i).start();
        }
    }

    // 使用synchronized修饰实例方法，锁是当前对象(this)
    public synchronized void incrementSync()
    {
        counter++;
        System.out.println(Thread.currentThread().getName() + " - 计数器：" + counter);
        try {
            // 线程睡眠500毫秒，便于观察效果
            TimeUnit.MILLISECONDS.sleep(500);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 使用synchronized代码块，锁是当前对象(this)
    public void incrementSyncBlock()
    {
        synchronized (this) {
            counter++;
            System.out.println(Thread.currentThread().getName() + " - 计数器：" + counter);
            try {
                // 线程睡眠500毫秒，便于观察效果
                TimeUnit.MILLISECONDS.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
