package org.devlive.tutorial.multithreading.chapter02;

/**
 * 演示线程的RUNNABLE状态
 */
public class ThreadRunnableStateDemo
{
    public static void main(String[] args)
    {
        Thread thread = new Thread(() -> {
            // 一个长时间运行的任务，确保线程保持RUNNABLE状态
            for (long i = 0; i < 1_000_000_000L; i++) {
                // 执行一些计算，让线程保持活动状态
                Math.sqrt(i);
            }
        });
        // 启动线程，状态从NEW变为RUNNABLE
        thread.start();
        // 给线程一点时间启动
        try {
            Thread.sleep(10);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 检查线程状态
        System.out.println("线程启动后的状态: " + thread.getState());
        // 等待线程结束
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}