package org.devlive.tutorial.multithreading.chapter07;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟ThreadLocal的实现原理
 */
public class ThreadLocalPrincipleDemo
{

    // 测试我们的ThreadLocal实现
    public static void main(String[] args)
    {
        // 创建ThreadLocal对象
        MyThreadLocal<String> nameThreadLocal = new MyThreadLocal<>();
        MyThreadLocal<Integer> ageThreadLocal = new MyThreadLocal<>();

        // 创建两个线程
        MyThread thread1 = new MyThread(() -> {
            // 设置线程1的ThreadLocal值
            nameThreadLocal.set("张三");
            ageThreadLocal.set(25);

            // 获取并打印线程1的ThreadLocal值
            System.out.println(Thread.currentThread().getName() +
                    " 的name: " + nameThreadLocal.get() +
                    ", age: " + ageThreadLocal.get());

            // 清理资源
            nameThreadLocal.remove();
            ageThreadLocal.remove();
        }, "线程1");

        MyThread thread2 = new MyThread(() -> {
            // 设置线程2的ThreadLocal值
            nameThreadLocal.set("李四");
            ageThreadLocal.set(30);

            // 获取并打印线程2的ThreadLocal值
            System.out.println(Thread.currentThread().getName() +
                    " 的name: " + nameThreadLocal.get() +
                    ", age: " + ageThreadLocal.get());

            // 清理资源
            nameThreadLocal.remove();
            ageThreadLocal.remove();
        }, "线程2");

        // 启动线程
        thread1.start();
        thread2.start();
    }

    // 模拟Thread类
    static class MyThread
            extends Thread
    {
        // 每个线程都有自己的ThreadLocalMap
        private final Map<MyThreadLocal<?>, Object> threadLocalMap = new HashMap<>();

        public MyThread(Runnable target, String name)
        {
            super(target, name);
        }

        // 获取当前线程的threadLocalMap
        public Map<MyThreadLocal<?>, Object> getThreadLocalMap()
        {
            return threadLocalMap;
        }
    }

    // 模拟ThreadLocal类
    static class MyThreadLocal<T>
    {
        // 用于生成唯一的ThreadLocal ID
        private static int nextId = 0;
        private final int threadLocalId = nextId++;

        // 设置值
        public void set(T value)
        {
            // 获取当前线程
            MyThread currentThread = (MyThread) Thread.currentThread();
            // 获取当前线程的threadLocalMap
            Map<MyThreadLocal<?>, Object> threadLocalMap = currentThread.getThreadLocalMap();
            // 将值放入map中
            threadLocalMap.put(this, value);
        }

        // 获取值
        @SuppressWarnings("unchecked")
        public T get()
        {
            // 获取当前线程
            MyThread currentThread = (MyThread) Thread.currentThread();
            // 获取当前线程的threadLocalMap
            Map<MyThreadLocal<?>, Object> threadLocalMap = currentThread.getThreadLocalMap();
            // 从map中获取值
            return (T) threadLocalMap.get(this);
        }

        // 移除值
        public void remove()
        {
            // 获取当前线程
            MyThread currentThread = (MyThread) Thread.currentThread();
            // 获取当前线程的threadLocalMap
            Map<MyThreadLocal<?>, Object> threadLocalMap = currentThread.getThreadLocalMap();
            // 从map中移除值
            threadLocalMap.remove(this);
        }

        @Override
        public String toString()
        {
            return "MyThreadLocal-" + threadLocalId;
        }
    }
}
