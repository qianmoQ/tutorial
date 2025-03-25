package org.devlive.tutorial.multithreading.chapter01;

/**
 * 通过实现Runnable接口实现多线程
 */
public class RunnableImplDemo
{
    // 自定义任务类，实现Runnable接口
    static class MyRunnable
            implements Runnable
    {
        private final String message;

        public MyRunnable(String message)
        {
            this.message = message;
        }

        // 实现run方法，定义任务执行的内容
        @Override
        public void run()
        {
            // 获取当前执行的线程
            Thread currentThread = Thread.currentThread();

            // 打印信息，显示当前线程名称
            for (int i = 0; i < 5; i++) {
                System.out.println(currentThread.getName() + " 执行: " + message + " - 第" + (i + 1) + "次");

                try {
                    // 线程休眠一段随机时间，模拟任务执行
                    Thread.sleep((long) (Math.random() * 1000));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(currentThread.getName() + " 执行完毕!");
        }
    }

    public static void main(String[] args)
    {
        System.out.println("程序开始执行...");

        // 创建两个Runnable对象
        Runnable task1 = new MyRunnable("你好，世界");
        Runnable task2 = new MyRunnable("Hello, World");

        // 创建线程对象，并传入Runnable任务
        Thread thread1 = new Thread(task1, "线程1");
        Thread thread2 = new Thread(task2, "线程2");

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