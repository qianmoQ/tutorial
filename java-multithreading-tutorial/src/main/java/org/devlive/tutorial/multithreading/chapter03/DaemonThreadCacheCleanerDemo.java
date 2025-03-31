package org.devlive.tutorial.multithreading.chapter03;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 使用守护线程实现缓存清理
 */
public class DaemonThreadCacheCleanerDemo
{
    public static void main(String[] args)
            throws InterruptedException
    {
        // 创建缓存并启动清理线程
        SimpleCache cache = new SimpleCache();
        cache.startCleanerThread();
        // 添加一些缓存项，设置不同的过期时间
        cache.put("item1", "Value 1", 3000); // 3秒后过期
        cache.put("item2", "Value 2", 7000); // 7秒后过期
        cache.put("item3", "Value 3", 5000); // 5秒后过期
        cache.put("item4", "Value 4", 10000); // 10秒后过期
        // 主线程每隔一段时间检查缓存中的项
        for (int i = 1; i <= 12; i++) {
            System.out.println("\n===== " + i + "秒后 =====");
            System.out.println("item1: " + cache.get("item1"));
            System.out.println("item2: " + cache.get("item2"));
            System.out.println("item3: " + cache.get("item3"));
            System.out.println("item4: " + cache.get("item4"));
            // 每检查一次，等待1秒
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("\n主线程执行完毕，程序即将退出");
        // 当主线程结束后，守护线程也会自动结束
    }

    // 简单的内存缓存
    private static class SimpleCache
    {
        // 缓存数据，键是缓存项的名称，值是包含数据和过期时间的CacheItem
        private final ConcurrentHashMap<String, CacheItem> cache = new ConcurrentHashMap<>();

        // 添加缓存项
        public void put(String key, Object value, long ttlMillis)
        {
            cache.put(key, new CacheItem(value, ttlMillis));
            System.out.println("添加缓存项: " + key + " = " + value + ", TTL: " + ttlMillis + "ms");
        }

        // 获取缓存项
        public Object get(String key)
        {
            CacheItem item = cache.get(key);
            if (item == null) {
                return null; // 缓存中没有该项
            }
            if (item.isExpired()) {
                cache.remove(key); // 惰性删除过期项
                return null;
            }
            return item.data;
        }

        // 获取缓存大小
        public int size()
        {
            return cache.size();
        }

        // 启动清理守护线程
        public void startCleanerThread()
        {
            Thread cleanerThread = new Thread(() -> {
                System.out.println("缓存清理线程启动");
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(1); // 每秒检查一次
                        // 记录当前时间
                        String now = LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("HH:mm:ss"));
                        System.out.println("\n" + now + " - 开始清理过期缓存项...");
                        int beforeSize = cache.size();
                        // 清理过期的缓存项
                        cache.forEach((key, item) -> {
                            if (item.isExpired()) {
                                System.out.println("移除过期缓存项: " + key);
                                cache.remove(key);
                            }
                        });
                        int afterSize = cache.size();
                        System.out.println("清理完成: 移除了 " + (beforeSize - afterSize) + " 个过期项，当前缓存大小: " + afterSize);
                    }
                    catch (InterruptedException e) {
                        System.out.println("缓存清理线程被中断");
                        break;
                    }
                }
            });
            // 设置为守护线程
            cleanerThread.setDaemon(true);
            cleanerThread.start();
        }

        // 缓存项，包含实际数据和过期时间
        private static class CacheItem
        {
            private final Object data;
            private final long expireTime; // 过期时间戳（毫秒）

            public CacheItem(Object data, long ttlMillis)
            {
                this.data = data;
                this.expireTime = System.currentTimeMillis() + ttlMillis;
            }

            public boolean isExpired()
            {
                return System.currentTimeMillis() > expireTime;
            }

            @Override
            public String toString()
            {
                return data.toString();
            }
        }
    }
}