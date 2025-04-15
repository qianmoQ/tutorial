package org.devlive.tutorial.multithreading.chapter06;

public class SafeSingleton
{
    // 使用volatile修饰instance
    private static volatile SafeSingleton instance;

    // 私有构造函数
    private SafeSingleton()
    {
        System.out.println("创建SafeSingleton实例");
    }

    // 获取实例的方法
    public static SafeSingleton getInstance()
    {
        // 第一次检查
        if (instance == null) {
            // 同步代码块
            synchronized (SafeSingleton.class) {
                // 第二次检查
                if (instance == null) {
                    instance = new SafeSingleton();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args)
    {
        // 创建多个线程同时获取实例
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                SafeSingleton singleton = SafeSingleton.getInstance();
                System.out.println(Thread.currentThread().getName() + " 获取到实例: " + singleton);
            }).start();
        }
    }
}
