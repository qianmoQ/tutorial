package org.devlive.tutorial.multithreading.chapter06;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class VolatileArrayDemo
{
    // 使用volatile修饰数组，只有数组引用是volatile的，数组元素不是
    private static final int[] volatileArray = new int[10];

    // 使用AtomicIntegerArray保证元素的原子性
    private static final AtomicIntegerArray atomicArray = new AtomicIntegerArray(10);

    public static void main(String[] args)
            throws InterruptedException
    {
        // 创建10个线程，每个线程操作不同索引的元素
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    // 对volatileArray[index]进行非原子的自增操作
                    volatileArray[index]++;

                    // 对atomicArray[index]进行原子的自增操作
                    atomicArray.incrementAndGet(index);
                }
            });
            threads[i].start();
        }

        // 等待所有线程执行完毕
        for (Thread thread : threads) {
            thread.join();
        }

        // 输出结果
        boolean volatileArrayCorrect = true;
        boolean atomicArrayCorrect = true;

        for (int i = 0; i < 10; i++) {
            if (volatileArray[i] != 1000) {
                volatileArrayCorrect = false;
            }
            if (atomicArray.get(i) != 1000) {
                atomicArrayCorrect = false;
            }
        }

        System.out.println("Expected value for each element: 1000");
        System.out.println("Volatile Array correct: " + volatileArrayCorrect);
        System.out.println("Atomic Array correct: " + atomicArrayCorrect);
    }
}
