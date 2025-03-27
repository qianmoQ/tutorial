package org.devlive.tutorial.multithreading.chapter02;

/**
 * 演示线程的TIMED_WAITING状态
 */
public class ThreadTimedWaitingStateDemo
{
    public static void main(String[] args)
    {
        Thread sleepingThread = new Thread(() -> {
            try {
                System.out.println("线程开始休眠5秒...");
                Thread.sleep(5000); // 线程休眠5秒，进入TIMED_WAITING状态
                System.out.println("线程休眠结束");
            }
            catch (InterruptedException e) {
                System.out.println("线程被中断");
            }
        });
        // 启动线程
        sleepingThread.start();
        // 给线程一点时间进入休眠状态
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 检查线程状态
        System.out.println("休眠线程的状态: " + sleepingThread.getState());
        // 等待线程结束
        try {
            sleepingThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}