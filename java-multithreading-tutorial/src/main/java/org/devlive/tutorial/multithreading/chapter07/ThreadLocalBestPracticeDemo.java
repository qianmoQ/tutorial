package org.devlive.tutorial.multithreading.chapter07;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 正确使用ThreadLocal避免内存泄漏
 */
public class ThreadLocalBestPracticeDemo
{

    // 使用try-finally确保ThreadLocal资源被正确清理
    private static void executeTaskSafely()
    {
        // 定义ThreadLocal变量
        ThreadLocal<byte[]> threadLocal = new ThreadLocal<>();

        try {
            // 设置值
            threadLocal.set(new byte[1024 * 1024]); // 1MB

            // 使用ThreadLocal
            System.out.println(Thread.currentThread().getName() +
                    " 安全地使用了ThreadLocal");

            // 模拟业务逻辑
            // ...

        }
        finally {
            // 在finally块中确保资源被清理
            threadLocal.remove();
            System.out.println(Thread.currentThread().getName() +
                    " 清理了ThreadLocal资源");
        }
    }

    // 使用AutoCloseable接口和try-with-resources语法更优雅地管理ThreadLocal
    private static void executeTaskMoreSafely()
    {
        // 使用try-with-resources自动管理资源
        try (ThreadLocalResource<byte[]> resource = new ThreadLocalResource<>()) {
            // 设置值
            resource.set(new byte[1024 * 1024]); // 1MB

            // 使用ThreadLocal
            System.out.println(Thread.currentThread().getName() +
                    " 更安全地使用了ThreadLocal (通过AutoCloseable)");

            // 模拟业务逻辑
            // ...

        } // 自动调用resource.close()，清理ThreadLocal
        System.out.println(Thread.currentThread().getName() +
                " 自动清理了ThreadLocal资源 (通过AutoCloseable)");
    }

    public static void main(String[] args)
    {
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // 提交一些使用普通try-finally方式的任务
        for (int i = 0; i < 5; i++) {
            executorService.execute(ThreadLocalBestPracticeDemo::executeTaskSafely);
        }

        // 提交一些使用try-with-resources方式的任务
        for (int i = 0; i < 5; i++) {
            executorService.execute(ThreadLocalBestPracticeDemo::executeTaskMoreSafely);
        }

        // 关闭线程池
        executorService.shutdown();
    }

    // 更好的实践：将ThreadLocal的创建和清理封装在一个工具类中
    static class ThreadLocalResource<T>
            implements AutoCloseable
    {
        private final ThreadLocal<T> threadLocal = new ThreadLocal<>();

        public void set(T value)
        {
            threadLocal.set(value);
        }

        public T get()
        {
            return threadLocal.get();
        }

        @Override
        public void close()
        {
            threadLocal.remove();
        }
    }
}
