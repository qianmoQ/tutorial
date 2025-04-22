package org.devlive.tutorial.multithreading.chapter07;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池中正确使用ThreadLocal
 */
public class ThreadLocalWithThreadPoolDemo
{

    // 定义ThreadLocal
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args)
            throws InterruptedException
    {
        // 创建一个只有2个线程的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        System.out.println("=== 演示不良实践 ===");
        // 提交3个不良任务
        for (int i = 1; i <= 3; i++) {
            executorService.execute(new BadTask("B" + i));
            Thread.sleep(200); // 稍等一会，确保上一个任务已经完成
        }

        // 稍等一会，确保所有不良任务已经完成
        Thread.sleep(500);

        System.out.println("\n=== 演示良好实践 ===");
        // 提交3个良好任务
        for (int i = 1; i <= 3; i++) {
            executorService.execute(new GoodTask("G" + i));
            Thread.sleep(200); // 稍等一会，确保上一个任务已经完成
        }

        // 关闭线程池
        executorService.shutdown();
    }

    /**
     * 不正确的任务实现 - 没有清除ThreadLocal
     */
    static class BadTask
            implements Runnable
    {
        private final String taskName;

        public BadTask(String taskName)
        {
            this.taskName = taskName;
        }

        @Override
        public void run()
        {
            // 获取当前ThreadLocal的值
            String value = threadLocal.get();
            System.out.println(Thread.currentThread().getName() +
                    " - 不良任务 " + taskName + " 开始，ThreadLocal初始值: " + value);

            // 设置新的值
            threadLocal.set("任务 " + taskName + " 的数据");

            // 模拟任务执行
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 任务结束时没有清除ThreadLocal
            // threadLocal.remove(); // 应该在这里清除，但没有做
        }
    }

    /**
     * 正确的任务实现 - 使用try-finally确保清除ThreadLocal
     */
    static class GoodTask
            implements Runnable
    {
        private final String taskName;

        public GoodTask(String taskName)
        {
            this.taskName = taskName;
        }

        @Override
        public void run()
        {
            try {
                // 获取当前ThreadLocal的值
                String value = threadLocal.get();
                System.out.println(Thread.currentThread().getName() +
                        " - 良好任务 " + taskName + " 开始，ThreadLocal初始值: " + value);

                // 设置新的值
                threadLocal.set("任务 " + taskName + " 的数据");

                // 模拟任务执行
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                // 在finally块中确保清除ThreadLocal
                System.out.println(Thread.currentThread().getName() +
                        " - 良好任务 " + taskName + " 结束，清除ThreadLocal");
                threadLocal.remove();
            }
        }
    }
}
