package org.devlive.tutorial.multithreading.chapter08;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用ReentrantLock实现线程安全的缓存系统
 * 实战案例：展示如何在实际应用中使用ReentrantLock
 */
public class ThreadSafeCacheWithLock<K, V>
{

    // 存储缓存数据的Map
    private final Map<K, V> cache = new HashMap<>();

    // 使用ReentrantLock保护缓存操作
    private final ReentrantLock lock = new ReentrantLock();

    // 缓存的最大大小
    private final int maxSize;

    // 统计信息
    private volatile int hitCount = 0;
    private volatile int missCount = 0;
    private volatile int evictionCount = 0;

    /**
     * 构造函数
     *
     * @param maxSize 缓存的最大大小
     */
    public ThreadSafeCacheWithLock(int maxSize)
    {
        this.maxSize = maxSize;
    }

    /**
     * 向缓存中添加数据
     *
     * @param key 键
     * @param value 值
     */
    public void put(K key, V value)
    {
        lock.lock();
        try {
            // 如果缓存已满，使用LRU策略移除最旧的元素
            if (cache.size() >= maxSize && !cache.containsKey(key)) {
                evictLRU();
            }

            V oldValue = cache.put(key, value);
            if (oldValue == null) {
                System.out.println(Thread.currentThread().getName() + " 添加到缓存：" + key + " -> " + value);
            }
            else {
                System.out.println(Thread.currentThread().getName() + " 更新缓存：" + key + " -> " + value);
            }
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 从缓存中获取数据
     *
     * @param key 键
     * @return 值，如果不存在返回null
     */
    public V get(K key)
    {
        lock.lock();
        try {
            V value = cache.get(key);
            if (value != null) {
                hitCount++;
                System.out.println(Thread.currentThread().getName() + " 缓存命中：" + key + " -> " + value);
            }
            else {
                missCount++;
                System.out.println(Thread.currentThread().getName() + " 缓存未命中：" + key);
            }
            return value;
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 从缓存中移除数据
     *
     * @param key 键
     * @return 被移除的值
     */
    public V remove(K key)
    {
        lock.lock();
        try {
            V value = cache.remove(key);
            if (value != null) {
                System.out.println(Thread.currentThread().getName() + " 从缓存移除：" + key + " -> " + value);
            }
            return value;
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 获取缓存大小
     *
     * @return 缓存中元素的数量
     */
    public int size()
    {
        lock.lock();
        try {
            return cache.size();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 清空缓存
     */
    public void clear()
    {
        lock.lock();
        try {
            cache.clear();
            System.out.println(Thread.currentThread().getName() + " 清空缓存");
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 安全地获取缓存快照
     *
     * @return 缓存的副本
     */
    public Map<K, V> getSnapshot()
    {
        lock.lock();
        try {
            return new HashMap<>(cache);
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 尝试获取缓存中的数据，如果获取锁失败则返回null
     *
     * @param key 键
     * @return 值，如果不存在或获取锁失败返回null
     */
    public V tryGet(K key)
    {
        if (lock.tryLock()) {
            try {
                V value = cache.get(key);
                if (value != null) {
                    hitCount++;
                    System.out.println(Thread.currentThread().getName() + " 通过tryLock缓存命中：" + key + " -> " + value);
                }
                else {
                    missCount++;
                    System.out.println(Thread.currentThread().getName() + " 通过tryLock缓存未命中：" + key);
                }
                return value;
            }
            finally {
                lock.unlock();
            }
        }
        else {
            System.out.println(Thread.currentThread().getName() + " tryLock获取锁失败，无法访问缓存：" + key);
            return null;
        }
    }

    /**
     * 获取缓存统计信息
     */
    public void printStatistics()
    {
        lock.lock();
        try {
            int totalRequests = hitCount + missCount;
            double hitRate = totalRequests > 0 ? (double) hitCount / totalRequests * 100 : 0;

            System.out.println("\n=== 缓存统计信息 ===");
            System.out.println("缓存大小：" + cache.size() + "/" + maxSize);
            System.out.println("命中次数：" + hitCount);
            System.out.println("未命中次数：" + missCount);
            System.out.println("淘汰次数：" + evictionCount);
            System.out.printf("命中率：%.2f%%\n", hitRate);
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 显示锁的状态信息
     */
    public void showLockInfo()
    {
        System.out.println("\n=== 锁状态信息 ===");
        System.out.println("锁是否被当前线程持有：" + lock.isHeldByCurrentThread());
        System.out.println("锁的持有次数：" + lock.getHoldCount());
        System.out.println("等待锁的线程数：" + lock.getQueueLength());
        System.out.println("是否是公平锁：" + lock.isFair());
    }

    /**
     * LRU淘汰策略（简化版）
     */
    private void evictLRU()
    {
        if (!cache.isEmpty()) {
            // 简单实现：移除第一个元素（实际的LRU需要更复杂的数据结构）
            K firstKey = cache.keySet().iterator().next();
            cache.remove(firstKey);
            evictionCount++;
            System.out.println(Thread.currentThread().getName() + " LRU淘汰：" + firstKey);
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        // 创建一个最大容量为5的缓存
        ThreadSafeCacheWithLock<String, String> cache = new ThreadSafeCacheWithLock<>(5);
        Random random = new Random();

        // 创建多个线程模拟并发访问
        Thread[] threads = new Thread[8];

        // 创建写入线程
        for (int i = 0; i < 3; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    String key = "key" + (threadIndex * 5 + j);
                    String value = "value" + (threadIndex * 5 + j);
                    cache.put(key, value);

                    try {
                        Thread.sleep(random.nextInt(200) + 100);
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "写入线程-" + (i + 1));
        }

        // 创建读取线程
        for (int i = 0; i < 3; i++) {
            final int threadIndex = i;
            threads[i + 3] = new Thread(() -> {
                for (int j = 0; j < 8; j++) {
                    String key = "key" + random.nextInt(15);
                    cache.get(key);

                    try {
                        Thread.sleep(random.nextInt(150) + 50);
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "读取线程-" + (threadIndex + 1));
        }

        // 创建使用tryGet的线程
        threads[6] = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                String key = "key" + random.nextInt(15);
                cache.tryGet(key);

                try {
                    Thread.sleep(random.nextInt(100) + 50);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "TryGet线程");

        // 创建管理线程
        threads[7] = new Thread(() -> {
            try {
                Thread.sleep(1000);
                cache.printStatistics();

                Thread.sleep(1000);
                System.out.println("\n执行缓存清理操作...");
                cache.remove("key2");
                cache.remove("key7");

                Thread.sleep(500);
                cache.printStatistics();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "管理线程");

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 显示最终状态
        System.out.println("\n=== 最终缓存状态 ===");
        System.out.println("缓存内容：" + cache.getSnapshot());
        cache.printStatistics();
        cache.showLockInfo();
    }
}
