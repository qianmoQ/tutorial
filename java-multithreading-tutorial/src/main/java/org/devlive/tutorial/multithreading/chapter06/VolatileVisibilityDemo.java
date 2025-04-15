package org.devlive.tutorial.multithreading.chapter06;

public class VolatileVisibilityDemo
{
    // 使用volatile修饰共享变量
    private static volatile boolean flag = false;

    public static void main(String[] args)
            throws InterruptedException
    {
        // 创建线程1，当检测到flag为true时退出循环
        Thread thread1 = new Thread(() -> {
            System.out.println("线程1启动");
            // 当flag为false时，无限循环
            while (!flag) {
                // 空循环
            }
            System.out.println("线程1检测到flag变为true，退出循环");
        });

        // 创建线程2，将flag设置为true
        Thread thread2 = new Thread(() -> {
            try {
                // 休眠1秒，确保线程1先启动
                Thread.sleep(1000);
                System.out.println("线程2将flag设置为true");
                flag = true;
                System.out.println("线程2设置完成");
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 启动线程
        thread1.start();
        thread2.start();

        // 等待线程执行完毕
        thread1.join();
        thread2.join();
    }
}
