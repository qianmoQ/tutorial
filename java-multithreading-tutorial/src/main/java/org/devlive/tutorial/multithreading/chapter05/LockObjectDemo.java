package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.TimeUnit;

public class LockObjectDemo
{

    // 定义不同的锁对象
    private final Object lockA = new Object();
    private final Object lockB = new Object();

    private int counterA = 0;
    private int counterB = 0;

    public static void main(String[] args)
    {
        LockObjectDemo demo = new LockObjectDemo();

        // 线程1：访问A资源
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.incrementA();
            }
        }, "线程A").start();

        // 线程2：访问B资源
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.incrementB();
            }
        }, "线程B").start();

        // 线程3：也访问A资源
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.incrementA();
            }
        }, "线程C").start();
    }

    // 使用lockA作为锁对象
    public void incrementA()
    {
        synchronized (lockA) {
            counterA++;
            System.out.println(Thread.currentThread().getName() + " - 计数器A：" + counterA);
            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 使用lockB作为锁对象
    public void incrementB()
    {
        synchronized (lockB) {
            counterB++;
            System.out.println(Thread.currentThread().getName() + " - 计数器B：" + counterB);
            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
