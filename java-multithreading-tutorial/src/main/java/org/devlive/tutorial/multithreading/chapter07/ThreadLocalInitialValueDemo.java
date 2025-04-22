package org.devlive.tutorial.multithreading.chapter07;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ThreadLocal初始化值示例
 */
public class ThreadLocalInitialValueDemo
{

    // 使用withInitial方法设置初始值
    private static final ThreadLocal<SimpleDateFormat> dateFormatThreadLocal =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    // 通过继承ThreadLocal并重写initialValue方法设置初始值
    private static final ThreadLocal<String> nameThreadLocal = new ThreadLocal<String>()
    {
        @Override
        protected String initialValue()
        {
            return "未命名线程";
        }
    };

    public static void main(String[] args)
    {
        // 测试dateFormatThreadLocal
        Thread thread1 = new Thread(() -> {
            // 直接获取初始值并使用
            System.out.println(Thread.currentThread().getName() +
                    " 格式化日期: " +
                    dateFormatThreadLocal.get().format(new Date()));

            // 清理资源
            dateFormatThreadLocal.remove();
        }, "日期格式化线程");

        // 测试nameThreadLocal
        Thread thread2 = new Thread(() -> {
            // 获取初始值
            System.out.println(Thread.currentThread().getName() +
                    " 的初始名称: " +
                    nameThreadLocal.get());

            // 修改值
            nameThreadLocal.set("已重命名线程");
            System.out.println(Thread.currentThread().getName() +
                    " 的当前名称: " +
                    nameThreadLocal.get());

            // 清理资源
            nameThreadLocal.remove();
        }, "名称测试线程");

        thread1.start();
        thread2.start();
    }
}
