package org.devlive.tutorial.multithreading.chapter02;

/**
 * 演示线程的WAITING状态
 */
public class ThreadWaitingStateDemo
{
    private static final Object lock = new Object();

    public static void main(String[] args)
    {
        // 创建等待线程
        Thread waitingThread = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("等待线程进入等待状态...");
                    lock.wait(); // 调用wait()方法，线程进入WAITING状态
                    System.out.println("等待线程被唤醒，继续执行");
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        // 创建唤醒线程
        Thread notifyThread = new Thread(() -> {
            try {
                // 先休眠2秒，确保waitingThread已经进入WAITING状态
                Thread.sleep(2000);
                synchronized (lock) {
                    System.out.println("唤醒线程准备唤醒等待线程...");
                    lock.notify(); // 唤醒在lock上等待的线程
                    System.out.println("唤醒线程已发出唤醒信号");
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 启动等待线程
        waitingThread.start();
        // 给等待线程一点时间进入等待状态
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 检查等待线程的状态
        System.out.println("等待线程的状态: " + waitingThread.getState());
        // 启动唤醒线程
        notifyThread.start();
        // 等待两个线程结束
        try {
            waitingThread.join();
            notifyThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}