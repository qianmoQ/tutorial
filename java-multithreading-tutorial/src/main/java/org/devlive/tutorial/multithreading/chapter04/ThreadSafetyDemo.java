package org.devlive.tutorial.multithreading.chapter04;

/**
 * 线程安全与非线程安全的对比
 */
public class ThreadSafetyDemo
{
    public static void main(String[] args)
            throws InterruptedException
    {
        // 演示线程不安全的计数器
        UnsafeCounter unsafeCounter = new UnsafeCounter();
        runCounterTest("线程不安全的计数器", unsafeCounter);
        // 演示线程安全的计数器
        SafeCounter safeCounter = new SafeCounter();
        runCounterTest("线程安全的计数器", safeCounter);
    }

    // 测试计数器
    private static void runCounterTest(String name, Object counter)
            throws InterruptedException
    {
        // 创建多个线程同时增加计数器
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    if (counter instanceof UnsafeCounter) {
                        ((UnsafeCounter) counter).increment();
                    }
                    else if (counter instanceof SafeCounter) {
                        ((SafeCounter) counter).increment();
                    }
                }
            });
            threads[i].start();
        }
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        // 检查最终结果
        int finalCount = 0;
        if (counter instanceof UnsafeCounter) {
            finalCount = ((UnsafeCounter) counter).getCount();
        }
        else if (counter instanceof SafeCounter) {
            finalCount = ((SafeCounter) counter).getCount();
        }
        System.out.println(name + ": 预期结果=100000, 实际结果=" + finalCount +
                (finalCount == 100000 ? " (正确)" : " (错误)"));
    }

    // 线程不安全的计数器
    static class UnsafeCounter
    {
        private int count = 0;

        public void increment()
        {
            count++;  // 非原子操作
        }

        public int getCount()
        {
            return count;
        }
    }

    // 线程安全的计数器
    static class SafeCounter
    {
        private int count = 0;

        public synchronized void increment()
        {
            count++;  // 使用synchronized确保原子性
        }

        public synchronized int getCount()
        {
            return count;
        }
    }
}