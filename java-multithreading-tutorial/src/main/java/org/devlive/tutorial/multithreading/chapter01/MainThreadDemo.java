package org.devlive.tutorial.multithreading.chapter01;

/**
 * 演示主线程的基本概念
 */
public class MainThreadDemo
{
    public static void main(String[] args)
    {
        // 获取当前线程（主线程）
        Thread mainThread = Thread.currentThread();

        // 打印主线程信息
        System.out.println("当前执行的线程名称：" + mainThread.getName());
        System.out.println("线程ID：" + mainThread.getId());
        System.out.println("线程优先级：" + mainThread.getPriority());
        System.out.println("线程是否为守护线程：" + mainThread.isDaemon());
        System.out.println("线程状态：" + mainThread.getState());
    }
}