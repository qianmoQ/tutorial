package org.devlive.tutorial.multithreading.chapter07;

/**
 * ThreadLocal基本使用示例
 */
public class ThreadLocalBasicDemo
{
    // 创建ThreadLocal对象
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args)
    {
        // 创建并启动第一个线程
        Thread thread1 = new Thread(() -> {
            // 设置当前线程的ThreadLocal值
            threadLocal.set("线程1的数据");
            System.out.println(Thread.currentThread().getName() + " 设置了值");

            // 稍后获取当前线程的ThreadLocal值
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 获取值并打印
            System.out.println(Thread.currentThread().getName() + " 获取的值: " + threadLocal.get());

            // 使用完毕后清除值
            threadLocal.remove();
        }, "线程1");

        // 创建并启动第二个线程
        Thread thread2 = new Thread(() -> {
            // 稍等片刻，确保线程1先设置值
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 线程2没有设置值，直接获取
            System.out.println(Thread.currentThread().getName() + " 获取的值: " + threadLocal.get());

            // 设置当前线程的ThreadLocal值
            threadLocal.set("线程2的数据");
            System.out.println(Thread.currentThread().getName() + " 设置了值");

            // 再次获取
            System.out.println(Thread.currentThread().getName() + " 再次获取的值: " + threadLocal.get());

            // 使用完毕后清除值
            threadLocal.remove();
        }, "线程2");

        // 启动线程
        thread1.start();
        thread2.start();
    }
}
