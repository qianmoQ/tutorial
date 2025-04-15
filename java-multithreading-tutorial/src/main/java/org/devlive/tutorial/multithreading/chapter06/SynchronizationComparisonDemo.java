package org.devlive.tutorial.multithreading.chapter06;

import java.util.concurrent.atomic.AtomicInteger;

public class SynchronizationComparisonDemo
{
    // 使用volatile修饰的计数器，不能保证原子性
    private static volatile int volatileCounter = 0;

    // 使用synchronized保护的计数器
    private static int synchronizedCounter = 0;

    // 使用AtomicInteger的计数器
    private static final AtomicInteger atomicCounter = new AtomicInteger(0);

    // synchronized方法，确保原子性
    private static synchronized void incrementSynchronizedCounter()
    {
        synchronizedCounter++;
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        // 创建10个线程，分别递增三种计数器
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    volatileCounter++; // 非原子操作
                    incrementSynchronizedCounter(); // 使用synchronized保证原子性
                    atomicCounter.incrementAndGet(); // 使用AtomicInteger保证原子性
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
        System.out.println("Volatile Counter: " + volatileCounter);
        System.out.println("Synchronized Counter: " + synchronizedCounter);
        System.out.println("Atomic Counter: " + atomicCounter.get());
    }
}
