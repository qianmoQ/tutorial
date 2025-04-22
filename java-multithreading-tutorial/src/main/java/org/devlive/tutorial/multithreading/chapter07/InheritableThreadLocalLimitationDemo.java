package org.devlive.tutorial.multithreading.chapter07;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 演示InheritableThreadLocal的局限性
 */
public class InheritableThreadLocalLimitationDemo
{

    // 创建InheritableThreadLocal对象
    private static final ThreadLocal<String> inheritableThreadLocal =
            new InheritableThreadLocal<>();

    public static void main(String[] args)
            throws InterruptedException
    {
        System.out.println("=== 演示直接创建线程的情况 ===");
        // 在主线程设置值
        inheritableThreadLocal.set("主线程设置的值");

        // 创建并启动子线程
        Thread childThread = new Thread(() -> {
            // 子线程获取值
            System.out.println("子线程获取到的值: " + inheritableThreadLocal.get());

            // 子线程修改值
            inheritableThreadLocal.set("子线程修改的值");
            System.out.println("子线程修改后的值: " + inheritableThreadLocal.get());

            // 创建并启动孙子线程
            Thread grandChildThread = new Thread(() -> {
                // 孙子线程获取值
                System.out.println("孙子线程获取到的值: " + inheritableThreadLocal.get());
            });
            grandChildThread.start();

            try {
                grandChildThread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        childThread.start();
        childThread.join();

        // 主线程再次获取值
        System.out.println("子线程执行后，主线程的值: " + inheritableThreadLocal.get());

        System.out.println("\n=== 演示线程池的情况 ===");
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        // 提交第一个任务
        executorService.submit(() -> {
            System.out.println("线程池-任务1: " + inheritableThreadLocal.get());
            inheritableThreadLocal.set("线程池中设置的值");
            System.out.println("线程池-任务1设置后: " + inheritableThreadLocal.get());
        });

        // 确保第一个任务执行完毕
        Thread.sleep(100);

        // 在主线程修改值
        inheritableThreadLocal.set("主线程修改后的值");

        // 提交第二个任务
        executorService.submit(() -> {
            // 由于线程池中的线程是复用的，且在第一个任务中已经设置了值，
            // 所以这里获取到的是第一个任务设置的值，而不是主线程修改后的值
            System.out.println("线程池-任务2: " + inheritableThreadLocal.get());
        });

        // 关闭线程池
        executorService.shutdown();

        System.out.println("\n=== 演示InheritableThreadLocal只能继承创建时的值 ===");
        // 在主线程设置值
        inheritableThreadLocal.set("创建子线程前的值");

        // 创建子线程但不立即启动
        Thread delayedThread = new Thread(() -> {
            System.out.println("延迟启动的子线程获取到的值: " + inheritableThreadLocal.get());
        });

        // 修改主线程中的值
        inheritableThreadLocal.set("创建子线程后修改的值");

        // 启动子线程
        delayedThread.start();
        delayedThread.join();

        // 清理资源
        inheritableThreadLocal.remove();
    }
}
