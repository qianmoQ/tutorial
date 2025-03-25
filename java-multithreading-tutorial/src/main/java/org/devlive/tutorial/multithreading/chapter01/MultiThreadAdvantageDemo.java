package org.devlive.tutorial.multithreading.chapter01;

/**
 * 单线程与多线程计算对比
 */
public class MultiThreadAdvantageDemo
{
    // 执行大量计算的方法
    private static void doHeavyCalculation(String threadName)
    {
        System.out.println(threadName + " 开始计算...");
        long sum = 0;
        for (long i = 0; i < 3_000_000_000L; i++) {
            sum += i;
        }
        System.out.println(threadName + " 计算完成，结果：" + sum);
    }

    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        // 单线程执行两次计算
        System.out.println("===== 单线程执行 =====");
        doHeavyCalculation("计算任务1");
        doHeavyCalculation("计算任务2");

        long endTime = System.currentTimeMillis();
        System.out.println("单线程执行总耗时：" + (endTime - startTime) + "ms");

        // 多线程执行两次计算
        System.out.println("\n===== 多线程执行 =====");
        startTime = System.currentTimeMillis();

        // 创建两个线程分别执行计算任务
        Thread thread1 = new Thread(() -> doHeavyCalculation("线程1"));
        Thread thread2 = new Thread(() -> doHeavyCalculation("线程2"));

        // 启动线程
        thread1.start();
        thread2.start();

        // 等待两个线程执行完成
        try {
            thread1.join();
            thread2.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        endTime = System.currentTimeMillis();
        System.out.println("多线程执行总耗时：" + (endTime - startTime) + "ms");
    }
}