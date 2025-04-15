package org.devlive.tutorial.multithreading.chapter06;

public class StaticInnerClassSingleton
{
    // 私有构造函数
    private StaticInnerClassSingleton()
    {
        System.out.println("创建StaticInnerClassSingleton实例");
    }

    // 获取实例的方法
    public static StaticInnerClassSingleton getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    public static void main(String[] args)
    {
        // 创建多个线程同时获取实例
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                StaticInnerClassSingleton singleton = StaticInnerClassSingleton.getInstance();
                System.out.println(Thread.currentThread().getName() + " 获取到实例: " + singleton);
            }).start();
        }
    }

    // 静态内部类，持有单例实例
    private static class SingletonHolder
    {
        private static final StaticInnerClassSingleton INSTANCE = new StaticInnerClassSingleton();
    }
}
