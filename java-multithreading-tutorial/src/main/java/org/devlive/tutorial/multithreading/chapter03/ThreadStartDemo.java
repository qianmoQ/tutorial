package org.devlive.tutorial.multithreading.chapter03;

/**
 * 线程启动示例
 */
public class ThreadStartDemo
{
    public static void main(String[] args)
    {
        // 创建线程
        Thread thread = new Thread(() -> {
            System.out.println("线程ID: " + Thread.currentThread().getId());
            System.out.println("线程名称: " + Thread.currentThread().getName());
            System.out.println("线程执行中...");
        });
        // 设置线程名称
        thread.setName("MyCustomThread");
        System.out.println("线程启动前的状态: " + thread.getState());
        // 启动线程
        thread.start();
        // 等待一点时间，让线程有机会执行
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("线程启动后的状态: " + thread.getState());
    }
}