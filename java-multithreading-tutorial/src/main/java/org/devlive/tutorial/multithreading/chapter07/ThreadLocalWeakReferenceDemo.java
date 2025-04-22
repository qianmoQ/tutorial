package org.devlive.tutorial.multithreading.chapter07;

import java.lang.ref.WeakReference;

/**
 * 演示弱引用在ThreadLocal中的作用
 */
public class ThreadLocalWeakReferenceDemo
{

    public static void main(String[] args)
            throws InterruptedException
    {
        // 创建一个ThreadLocal对象
        ThreadLocal<String> threadLocal = new ThreadLocal<>();

        // 创建一个Entry，模拟ThreadLocalMap中的Entry
        Entry entry = new Entry(threadLocal, "一些数据");

        // 获取Entry引用的ThreadLocal对象，此时应该能获取到
        ThreadLocal<?> threadLocalFromEntry = entry.get();
        System.out.println("初始状态 - Entry引用的ThreadLocal: " +
                (threadLocalFromEntry != null ? "存在" : "已被回收"));
        System.out.println("初始状态 - Entry的值: " + entry.value);

        // 清除对ThreadLocal的强引用
        threadLocal = null;

        // 强制执行垃圾回收
        System.gc();
        Thread.sleep(1000); // 给GC一些时间

        // 再次尝试获取Entry引用的ThreadLocal对象
        threadLocalFromEntry = entry.get();
        System.out.println("GC后 - Entry引用的ThreadLocal: " +
                (threadLocalFromEntry != null ? "存在" : "已被回收"));
        System.out.println("GC后 - Entry的值: " + entry.value);

        // 手动清除Entry的值，模拟ThreadLocal.remove()的操作
        entry.value = null;
        System.out.println("手动清除后 - Entry的值: " + entry.value);
    }

    // 模拟ThreadLocalMap中的Entry
    static class Entry
            extends WeakReference<ThreadLocal<?>>
    {
        Object value;

        Entry(ThreadLocal<?> k, Object v)
        {
            super(k);  // 使用WeakReference引用ThreadLocal对象
            value = v; // 强引用存储值
        }
    }
}
