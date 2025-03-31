package org.devlive.tutorial.multithreading.chapter03;

/**
 * 线程中断示例
 */
public class ThreadInterruptDemo
{
    public static void main(String[] args)
    {
        // 创建一个简单的可中断线程
        Thread thread = new Thread(() -> {
            int count = 0;
            // 检查线程中断标志
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("线程执行中... " + (++count));
                // 模拟工作
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    // sleep方法被中断会清除中断状态，需要重新设置
                    System.out.println("线程在sleep期间被中断");
                    Thread.currentThread().interrupt(); // 重新设置中断状态
                }
            }
            System.out.println("线程检测到中断信号，正常退出");
        });
        // 启动线程
        thread.start();
        // 主线程休眠3秒后中断线程
        try {
            Thread.sleep(3000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主线程发送中断信号");
        thread.interrupt();
    }
}