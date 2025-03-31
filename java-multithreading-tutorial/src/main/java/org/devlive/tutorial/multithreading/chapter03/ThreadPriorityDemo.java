package org.devlive.tutorial.multithreading.chapter03;

/**
 * 线程优先级示例
 */
public class ThreadPriorityDemo
{
    public static void main(String[] args)
    {
        // 创建3个不同优先级的线程
        Thread lowPriorityThread = new Thread(() -> {
            System.out.println("低优先级线程开始执行");
            long count = 0;
            for (long i = 0; i < 5_000_000_000L; i++) {
                count++;
                if (i % 1_000_000_000 == 0) {
                    System.out.println("低优先级线程计数: " + i / 1_000_000_000);
                }
            }
            System.out.println("低优先级线程执行完毕，计数: " + count);
        });
        Thread normalPriorityThread = new Thread(() -> {
            System.out.println("普通优先级线程开始执行");
            long count = 0;
            for (long i = 0; i < 5_000_000_000L; i++) {
                count++;
                if (i % 1_000_000_000 == 0) {
                    System.out.println("普通优先级线程计数: " + i / 1_000_000_000);
                }
            }
            System.out.println("普通优先级线程执行完毕，计数: " + count);
        });
        Thread highPriorityThread = new Thread(() -> {
            System.out.println("高优先级线程开始执行");
            long count = 0;
            for (long i = 0; i < 5_000_000_000L; i++) {
                count++;
                if (i % 1_000_000_000 == 0) {
                    System.out.println("高优先级线程计数: " + i / 1_000_000_000);
                }
            }
            System.out.println("高优先级线程执行完毕，计数: " + count);
        });
        // 设置线程优先级
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY); // 1
        normalPriorityThread.setPriority(Thread.NORM_PRIORITY); // 5
        highPriorityThread.setPriority(Thread.MAX_PRIORITY); // 10
        System.out.println("低优先级线程的优先级: " + lowPriorityThread.getPriority());
        System.out.println("普通优先级线程的优先级: " + normalPriorityThread.getPriority());
        System.out.println("高优先级线程的优先级: " + highPriorityThread.getPriority());
        // 启动线程
        System.out.println("启动所有线程");
        lowPriorityThread.start();
        normalPriorityThread.start();
        highPriorityThread.start();
    }
}