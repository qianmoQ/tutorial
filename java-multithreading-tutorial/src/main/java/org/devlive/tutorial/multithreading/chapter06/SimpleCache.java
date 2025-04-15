package org.devlive.tutorial.multithreading.chapter06;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SimpleCache
{
    // 使用volatile修饰缓存对象，确保可见性
    private static volatile Map<String, Object> cache = new HashMap<>();

    // 模拟从数据库加载数据
    private static Object loadFromDB(String key)
    {
        System.out.println("从数据库加载数据：" + key);
        try {
            // 模拟数据库操作的延迟
            TimeUnit.MILLISECONDS.sleep(200);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Data for " + key;
    }

    // 从缓存获取数据，如果缓存中没有则从数据库加载
    public static Object get(String key)
    {
        // 从缓存中获取数据
        Object value = cache.get(key);
        if (value == null) {
            synchronized (SimpleCache.class) {
                // 双重检查，防止多个线程同时加载同一个数据
                value = cache.get(key);
                if (value == null) {
                    // 从数据库加载数据
                    value = loadFromDB(key);
                    // 更新缓存
                    Map<String, Object> newCache = new HashMap<>(cache);
                    newCache.put(key, value);
                    cache = newCache; // 原子更新整个缓存，确保可见性
                }
            }
        }
        return value;
    }

    // 更新缓存
    public static void put(String key, Object value)
    {
        synchronized (SimpleCache.class) {
            Map<String, Object> newCache = new HashMap<>(cache);
            newCache.put(key, value);
            cache = newCache; // 原子更新整个缓存，确保可见性
        }
    }

    // 清除缓存
    public static void clear()
    {
        synchronized (SimpleCache.class) {
            cache = new HashMap<>();
        }
    }

    public static void main(String[] args)
    {
        // 创建多个线程同时访问缓存
        for (int i = 0; i < 10; i++) {
            final int index = i;
            new Thread(() -> {
                Object data = get("key" + (index % 5));
                System.out.println(Thread.currentThread().getName() + " 获取数据：" + data);
            }).start();
        }
    }
}
