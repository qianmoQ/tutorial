package org.devlive.tutorial.multithreading.chapter07;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 演示ThreadLocal可能导致的内存泄漏
 */
public class ThreadLocalMemoryLeakDemo
{

    // 执行一个任务，这个任务会使用ThreadLocal
    private static void executeTask()
    {
        // 创建一个ThreadLocal对象
        ThreadLocal<byte[]> threadLocal = new ThreadLocal<>();

        // 在ThreadLocal中存储一个1MB的数组
        threadLocal.set(new byte[1024 * 1024]); // 1MB

        // 使用这个变量
        System.out.println(Thread.currentThread().getName() +
                " 使用ThreadLocal存储了1MB数据");

        // 不再使用这个变量，但忘记调用remove
        // threadLocal.remove(); // 这行被注释掉了，模拟忘记清理
    }

    public static void main(String[] args)
    {
        // 创建一个只有3个线程的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // 模拟提交10个任务，这些任务会重用线程池中的线程
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                executeTask();

                // 强制执行GC（实际应用中不应这样做，这里仅用于演示）
                System.gc();
                try {
                    Thread.sleep(100); // 给GC一点时间
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // 关闭线程池
        executorService.shutdown();

        System.out.println("所有任务已提交，可能存在内存泄漏。在实际应用中，" +
                "应该在finally块中调用ThreadLocal.remove()方法");
    }
}
