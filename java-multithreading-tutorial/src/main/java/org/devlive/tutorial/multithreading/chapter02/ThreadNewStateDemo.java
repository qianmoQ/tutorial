package org.devlive.tutorial.multithreading.chapter02;

/**
 * 演示线程的NEW状态
 */
public class ThreadNewStateDemo
{
    public static void main(String[] args)
    {
        // 创建线程对象，此时线程处于NEW状态
        Thread thread = new Thread(() -> {
            System.out.println("线程执行中...");
        });
        // 检查线程状态
        System.out.println("线程创建后的状态: " + thread.getState());
        // 验证是否为NEW状态
        System.out.println("是否为NEW状态: " + (thread.getState() == Thread.State.NEW));
    }
}