package org.devlive.tutorial.multithreading.chapter01;

/**
 * 使用Lambda表达式简化多线程创建
 */
public class LambdaThreadDemo
{
    public static void main(String[] args)
    {
        System.out.println("程序开始执行...");

        // 使用Lambda表达式创建Runnable实例
        Runnable task1 = () -> {
            Thread currentThread = Thread.currentThread();
            for (int i = 0; i < 5; i++) {
                System.out.println(currentThread.getName() + " 执行: 你好，世界 - 第" + (i + 1) + "次");
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(currentThread.getName() + " 执行完毕!");
        };

        // 再简化一点，直接在创建线程时使用Lambda表达式
        Thread thread1 = new Thread(task1, "线程1");
        Thread thread2 = new Thread(() -> {
            Thread currentThread = Thread.currentThread();
            for (int i = 0; i < 5; i++) {
                System.out.println(currentThread.getName() + " 执行: Hello, World - 第" + (i + 1) + "次");
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(currentThread.getName() + " 执行完毕!");
        }, "线程2");

        // 启动线程
        thread1.start();
        thread2.start();

        // 主线程继续执行
        for (int i = 0; i < 3; i++) {
            System.out.println("主线程执行 - 第" + (i + 1) + "次");
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("主线程执行完毕，但程序不会立即结束，因为还有其他线程在运行");
    }
}