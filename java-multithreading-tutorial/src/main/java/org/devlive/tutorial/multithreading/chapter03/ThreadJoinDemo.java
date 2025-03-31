package org.devlive.tutorial.multithreading.chapter03;

/**
 * 线程Join示例
 */
public class ThreadJoinDemo
{
    public static void main(String[] args)
    {
        // 创建3个执行特定任务的线程
        Thread thread1 = new Thread(() -> {
            System.out.println("线程1开始执行...");
            try {
                // 模拟耗时操作
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程1执行完毕");
        });
        Thread thread2 = new Thread(() -> {
            System.out.println("线程2开始执行...");
            try {
                // 模拟耗时操作
                Thread.sleep(3000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程2执行完毕");
        });
        Thread thread3 = new Thread(() -> {
            System.out.println("线程3开始执行...");
            try {
                // 模拟耗时操作
                Thread.sleep(1500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程3执行完毕");
        });
        System.out.println("启动所有线程");
        // 启动线程
        thread1.start();
        thread2.start();
        thread3.start();
        System.out.println("等待所有线程完成...");
        try {
            // 等待线程1完成
            thread1.join();
            System.out.println("线程1已完成");
            // 等待线程2完成
            thread2.join();
            System.out.println("线程2已完成");
            // 等待线程3完成
            thread3.join();
            System.out.println("线程3已完成");
        }
        catch (InterruptedException e) {
            System.out.println("主线程被中断");
        }
        System.out.println("所有线程已完成，继续执行主线程");
    }
}