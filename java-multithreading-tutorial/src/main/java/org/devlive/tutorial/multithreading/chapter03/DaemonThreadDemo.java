package org.devlive.tutorial.multithreading.chapter03;

/**
 * 守护线程示例
 */
public class DaemonThreadDemo
{
    public static void main(String[] args)
    {
        // 创建一个守护线程
        Thread daemonThread = new Thread(() -> {
            int count = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                    count++;
                    System.out.println("守护线程工作中... 计数: " + count);
                }
                catch (InterruptedException e) {
                    System.out.println("守护线程被中断");
                    break;
                }
            }
        });
        // 设置为守护线程
        daemonThread.setDaemon(true);
        // 创建一个普通线程
        Thread userThread = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                System.out.println("用户线程工作中... " + i);
                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("用户线程执行完毕");
        });
        System.out.println("守护线程状态: " + daemonThread.isDaemon());
        System.out.println("用户线程状态: " + userThread.isDaemon());
        // 启动线程
        daemonThread.start();
        userThread.start();
        System.out.println("主线程等待用户线程完成...");
        try {
            userThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主线程结束，JVM即将退出");
        // 不需要等待守护线程结束，JVM会自动终止所有守护线程
    }
}