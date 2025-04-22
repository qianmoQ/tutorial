package org.devlive.tutorial.multithreading.chapter07;

/**
 * InheritableThreadLocal示例 - 演示线程变量的继承性
 */
public class InheritableThreadLocalDemo
{

    // 创建普通的ThreadLocal
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    // 创建可继承的InheritableThreadLocal
    private static final ThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();

    public static void main(String[] args)
    {
        // 在主线程中设置两种ThreadLocal的值
        threadLocal.set("主线程的ThreadLocal数据");
        inheritableThreadLocal.set("主线程的InheritableThreadLocal数据");

        // 创建子线程
        Thread childThread = new Thread(() -> {
            // 尝试获取从父线程继承的值
            System.out.println("子线程获取普通ThreadLocal值: " + threadLocal.get());
            System.out.println("子线程获取InheritableThreadLocal值: " + inheritableThreadLocal.get());

            // 修改InheritableThreadLocal的值
            inheritableThreadLocal.set("子线程修改后的InheritableThreadLocal数据");
            System.out.println("子线程修改后获取InheritableThreadLocal值: " + inheritableThreadLocal.get());

            // 创建孙子线程
            Thread grandChildThread = new Thread(() -> {
                // 尝试获取从父线程继承的值
                System.out.println("孙子线程获取普通ThreadLocal值: " + threadLocal.get());
                System.out.println("孙子线程获取InheritableThreadLocal值: " + inheritableThreadLocal.get());
            }, "孙子线程");

            grandChildThread.start();
            try {
                grandChildThread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 清理资源
            threadLocal.remove();
            inheritableThreadLocal.remove();
        }, "子线程");

        // 启动子线程
        childThread.start();

        try {
            childThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 主线程再次获取值
        System.out.println("主线程再次获取普通ThreadLocal值: " + threadLocal.get());
        System.out.println("主线程再次获取InheritableThreadLocal值: " + inheritableThreadLocal.get());

        // 清理资源
        threadLocal.remove();
        inheritableThreadLocal.remove();
    }
}
