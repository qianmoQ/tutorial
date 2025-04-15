package org.devlive.tutorial.multithreading.chapter06;

public class VolatileCounterDemo
{
    // 使用volatile修饰计数器
    private static volatile int counter = 0;

    public static void main(String[] args)
            throws InterruptedException
    {
        // 创建10个线程，每个线程将counter递增1000次
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter++; // 非原子操作
                }
            });
            threads[i].start();
        }

        // 等待所有线程执行完毕
        for (Thread thread : threads) {
            thread.join();
        }

        // 输出结果
        System.out.println("Expected: " + (10 * 1000));
        System.out.println("Actual: " + counter);
    }
}
