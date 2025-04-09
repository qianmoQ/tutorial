package org.devlive.tutorial.multithreading.chapter05;

public class DeadlockDemo
{
    // 资源A
    private static final Object resourceA = new Object();
    // 资源B
    private static final Object resourceB = new Object();

    public static void main(String[] args)
    {
        // 线程1：先获取资源A，再获取资源B
        Thread thread1 = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println(Thread.currentThread().getName() + " 获取了资源A");

                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(Thread.currentThread().getName() + " 等待获取资源B");
                synchronized (resourceB) {
                    System.out.println(Thread.currentThread().getName() + " 获取了资源B");
                }
            }
        }, "线程1");

        // 线程2：先获取资源B，再获取资源A
        Thread thread2 = new Thread(() -> {
            synchronized (resourceB) {
                System.out.println(Thread.currentThread().getName() + " 获取了资源B");

                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(Thread.currentThread().getName() + " 等待获取资源A");
                synchronized (resourceA) {
                    System.out.println(Thread.currentThread().getName() + " 获取了资源A");
                }
            }
        }, "线程2");

        thread1.start();
        thread2.start();
    }
}
