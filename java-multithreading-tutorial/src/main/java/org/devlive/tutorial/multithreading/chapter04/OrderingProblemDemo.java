package org.devlive.tutorial.multithreading.chapter04;

/**
 * 有序性问题示例
 */
public class OrderingProblemDemo
{
    private static int x = 0, y = 0;
    private static int a = 0, b = 0;

    public static void main(String[] args)
            throws InterruptedException
    {
        int iterations = 0;
        int abnormalResults = 0;
        // 多次运行测试，统计出现重排序的次数
        final int TEST_COUNT = 100000;
        System.out.println("开始测试重排序问题，运行 " + TEST_COUNT + " 次...");
        for (int i = 0; i < TEST_COUNT; i++) {
            iterations++;
            // 重置变量
            x = 0;
            y = 0;
            a = 0;
            b = 0;
            // 创建线程1
            Thread thread1 = new Thread(() -> {
                a = 1;  // 语句1
                x = b;  // 语句2
            });
            // 创建线程2
            Thread thread2 = new Thread(() -> {
                b = 1;  // 语句3
                y = a;  // 语句4
            });
            // 启动线程
            thread1.start();
            thread2.start();
            // 等待线程结束
            thread1.join();
            thread2.join();
            // 检查结果
            if (x == 0 && y == 0) {
                abnormalResults++;
                // 因为出现频率较低，只在前几次出现时打印详细信息
                if (abnormalResults <= 10) {
                    System.out.println("检测到可能的指令重排序: x=" + x + ", y=" + y);
                }
            }
        }
        // 统计结果
        System.out.println("\n测试完成:");
        System.out.println("总测试次数: " + iterations);
        System.out.println("检测到的异常结果次数(x=0, y=0): " + abnormalResults);
        System.out.println("异常结果比例: " + String.format("%.5f%%", (double) abnormalResults / iterations * 100));
        System.out.println("\n分析:");
        if (abnormalResults > 0) {
            System.out.println("检测到可能的指令重排序。在某些情况下，两个线程中的操作可能被重排序，导致x=0且y=0。");
            System.out.println("这表明Java内存模型允许某些指令重排序，可能影响多线程程序的执行结果。");
        }
        else {
            System.out.println("未检测到明显的指令重排序。这可能是由于:");
            System.out.println("1. 当前硬件和JVM实现中不太容易观察到这种重排序");
            System.out.println("2. 测试次数不够多");
            System.out.println("但这并不意味着指令重排序不存在。在复杂的多线程程序中，它仍然可能导致问题。");
        }
    }
}