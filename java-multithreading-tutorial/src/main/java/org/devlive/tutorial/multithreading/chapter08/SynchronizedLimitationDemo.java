package org.devlive.tutorial.multithreading.chapter08;

/**
 * 演示synchronized的局限性
 */
public class SynchronizedLimitationDemo
{
    private static final Object lock = new Object();
    private static int count = 0;

    /**
     * 使用synchronized的方法
     */
    public static void synchronizedMethod()
    {
        synchronized (lock) {
            count++;
            System.out.println(Thread.currentThread().getName() + " 获取锁成功，count = " + count);

            try {
                // 模拟长时间持有锁
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        // 创建多个线程尝试获取锁
        Thread thread1 = new Thread(SynchronizedLimitationDemo::synchronizedMethod, "线程1");

        Thread thread2 = new Thread(SynchronizedLimitationDemo::synchronizedMethod, "线程2");

        thread1.start();
        Thread.sleep(100); // 确保线程1先启动
        thread2.start();

        // synchronized的问题：无法中断等待锁的线程
        Thread.sleep(1000);
        System.out.println("尝试中断线程2...");
        thread2.interrupt(); // 这个中断无法让线程2停止等待锁

        thread1.join();
        thread2.join();

        System.out.println("synchronized无法响应中断，线程2仍然会等待并获取锁");
    }
}
