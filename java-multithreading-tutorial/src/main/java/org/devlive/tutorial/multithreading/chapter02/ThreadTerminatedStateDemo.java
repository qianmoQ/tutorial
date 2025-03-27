package org.devlive.tutorial.multithreading.chapter02;

/**
 * 演示线程的TERMINATED状态
 */
public class ThreadTerminatedStateDemo
{
    public static void main(String[] args)
    {
        Thread thread = new Thread(() -> {
            System.out.println("线程开始执行...");
            // 执行一些简单的任务后结束
            for (int i = 0; i < 5; i++) {
                System.out.println("线程执行中: " + i);
                try {
                    Thread.sleep(200);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("线程执行结束");
        });
        // 启动线程
        thread.start();
        // 等待线程执行完毕
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 检查线程状态
        System.out.println("线程的状态: " + thread.getState());
        // 验证是否为TERMINATED状态
        System.out.println("是否为TERMINATED状态: " + (thread.getState() == Thread.State.TERMINATED));
    }
}