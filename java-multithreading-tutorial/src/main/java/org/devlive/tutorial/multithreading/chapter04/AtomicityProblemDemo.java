package org.devlive.tutorial.multithreading.chapter04;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 原子性问题示例
 */
public class AtomicityProblemDemo
{
    public static void main(String[] args)
            throws InterruptedException
    {
        // 测试非原子操作
        testCounter("非原子计数器", new NonAtomicCounter());
        // 测试使用原子变量的计数器
        testCounter("原子计数器", new AtomicCounter());
    }

    // 测试计数器
    private static void testCounter(String name, Object counter)
            throws InterruptedException
    {
        final int NUM_THREADS = 10;
        final int ITERATIONS = 100000;
        // 创建并启动多个线程递增计数器
        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < ITERATIONS; j++) {
                    if (counter instanceof NonAtomicCounter) {
                        ((NonAtomicCounter) counter).increment();
                    }
                    else if (counter instanceof AtomicCounter) {
                        ((AtomicCounter) counter).increment();
                    }
                }
            });
            threads[i].start();
        }
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        // 检查结果
        int finalCount = 0;
        if (counter instanceof NonAtomicCounter) {
            finalCount = ((NonAtomicCounter) counter).getCount();
        }
        else if (counter instanceof AtomicCounter) {
            finalCount = ((AtomicCounter) counter).getCount();
        }
        int expectedCount = NUM_THREADS * ITERATIONS;
        System.out.println(name + " - 预期结果: " + expectedCount + ", 实际结果: " + finalCount +
                (finalCount == expectedCount ? " (正确)" : " (错误)"));
    }

    // 非原子计数器
    static class NonAtomicCounter
    {
        private int count = 0;

        public void increment()
        {
            count++; // 看似简单，实际上是读-改-写三步操作
        }

        public int getCount()
        {
            return count;
        }
    }

    // 使用原子变量的计数器
    static class AtomicCounter
    {
        private final AtomicInteger count = new AtomicInteger(0);

        public void increment()
        {
            count.incrementAndGet(); // 原子操作
        }

        public int getCount()
        {
            return count.get();
        }
    }
}