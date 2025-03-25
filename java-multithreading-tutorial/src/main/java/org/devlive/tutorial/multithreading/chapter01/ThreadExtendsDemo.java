package org.devlive.tutorial.multithreading.chapter01;

/**
 * 通过继承Thread类实现多线程
 */
public class ThreadExtendsDemo
{
    // 自定义线程类，继承Thread类
    static class MyThread
            extends Thread
    {
        private final String message;

        public MyThread(String message)
        {
            this.message = message;
        }

        // 重写run方法，定义线程执行的任务
        @Override
        public void run()
        {
            // 打印信息，显示当前线程名称
            for (int i = 0; i < 5; i++) {
                System.out.println(getName() + " 执行: " + message + " - 第" + (i + 1) + "次");

                try {
                    // 线程休眠一段随机时间，模拟任务执行
                    Thread.sleep((long) (Math.random() * 1000));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(getName() + " 执行完毕!");
        }
    }

    public static void main(String[] args)
    {
        System.out.println("程序开始执行...");

        // 创建两个线程对象
        MyThread thread1 = new MyThread("你好，世界");
        MyThread thread2 = new MyThread("Hello, World");

        // 设置线程名称
        thread1.setName("线程1");
        thread2.setName("线程2");

        // 启动线程
        thread1.start(); // 注意：不要直接调用run()方法
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