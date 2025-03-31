package org.devlive.tutorial.multithreading.chapter03;

/**
 * 线程重复启动错误示例
 */
public class ThreadDoubleStartErrorDemo
{
    public static void main(String[] args)
    {
        Thread thread = new Thread(() -> {
            System.out.println("线程执行中...");
        });
        // 第一次启动线程（正确）
        thread.start();
        // 等待线程执行完毕
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("线程状态: " + thread.getState());
        try {
            // 尝试再次启动同一个线程（错误）
            System.out.println("尝试再次启动同一个线程...");
            thread.start();
        }
        catch (IllegalThreadStateException e) {
            System.out.println("捕获到异常: " + e.getMessage());
            System.out.println("线程一旦终止，就不能再次启动！");
        }
    }
}