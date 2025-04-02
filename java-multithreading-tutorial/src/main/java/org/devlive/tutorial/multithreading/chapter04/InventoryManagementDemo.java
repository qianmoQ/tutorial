package org.devlive.tutorial.multithreading.chapter04;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 商品库存管理实战案例
 */
public class InventoryManagementDemo
{
    public static void main(String[] args)
            throws InterruptedException
    {
        // 创建不同类型的库存管理器
        UnsafeInventory unsafeInventory = new UnsafeInventory();
        SynchronizedInventory syncInventory = new SynchronizedInventory();
        ConcurrentInventory concurrentInventory = new ConcurrentInventory();
        ReadWriteLockInventory rwlInventory = new ReadWriteLockInventory();
        // 初始化库存
        String[] products = {"iPhone", "MacBook", "iPad", "AirPods"};
        for (String product : products) {
            unsafeInventory.updateStock(product, 1000);
            syncInventory.updateStock(product, 1000);
            concurrentInventory.updateStock(product, 1000);
            rwlInventory.updateStock(product, 1000);
        }
        // 测试线程不安全的库存管理
        System.out.println("测试线程不安全的库存管理");
        testInventory(unsafeInventory, products);
        // 测试synchronized的库存管理
        System.out.println("\n测试synchronized的库存管理");
        testInventory(syncInventory, products);
        // 测试ConcurrentHashMap的库存管理
        System.out.println("\n测试ConcurrentHashMap的库存管理");
        testInventory(concurrentInventory, products);
        // 测试ReadWriteLock的库存管理
        System.out.println("\n测试ReadWriteLock的库存管理");
        testInventory(rwlInventory, products);
    }

    private static void testInventory(Object inventory, String[] products)
            throws InterruptedException
    {
        // 创建多个购买线程（减少库存）
        Thread[] buyThreads = new Thread[10];
        for (int i = 0; i < buyThreads.length; i++) {
            buyThreads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    String product = products[j % products.length];
                    boolean success = false;
                    if (inventory instanceof UnsafeInventory) {
                        success = ((UnsafeInventory) inventory).decreaseStock(product, 1);
                    }
                    else if (inventory instanceof SynchronizedInventory) {
                        success = ((SynchronizedInventory) inventory).decreaseStock(product, 1);
                    }
                    else if (inventory instanceof ConcurrentInventory) {
                        success = ((ConcurrentInventory) inventory).decreaseStock(product, 1);
                    }
                    else if (inventory instanceof ReadWriteLockInventory) {
                        success = ((ReadWriteLockInventory) inventory).decreaseStock(product, 1);
                    }
                    if (!success) {
                        System.out.println("购买失败: " + product + " - 库存不足");
                    }
                }
            });
        }
        // 创建多个补货线程（增加库存）
        Thread[] restockThreads = new Thread[5];
        for (int i = 0; i < restockThreads.length; i++) {
            restockThreads[i] = new Thread(() -> {
                for (int j = 0; j < 40; j++) {
                    String product = products[j % products.length];
                    if (inventory instanceof UnsafeInventory) {
                        ((UnsafeInventory) inventory).updateStock(product, 5);
                    }
                    else if (inventory instanceof SynchronizedInventory) {
                        ((SynchronizedInventory) inventory).updateStock(product, 5);
                    }
                    else if (inventory instanceof ConcurrentInventory) {
                        ((ConcurrentInventory) inventory).updateStock(product, 5);
                    }
                    else if (inventory instanceof ReadWriteLockInventory) {
                        ((ReadWriteLockInventory) inventory).updateStock(product, 5);
                    }
                }
            });
        }
        // 创建多个查询线程（读取库存）
        Thread[] queryThreads = new Thread[20];
        for (int i = 0; i < queryThreads.length; i++) {
            queryThreads[i] = new Thread(() -> {
                for (int j = 0; j < 50; j++) {
                    String product = products[j % products.length];
                    int stock = 0;
                    if (inventory instanceof UnsafeInventory) {
                        stock = ((UnsafeInventory) inventory).getStock(product);
                    }
                    else if (inventory instanceof SynchronizedInventory) {
                        stock = ((SynchronizedInventory) inventory).getStock(product);
                    }
                    else if (inventory instanceof ConcurrentInventory) {
                        stock = ((ConcurrentInventory) inventory).getStock(product);
                    }
                    else if (inventory instanceof ReadWriteLockInventory) {
                        stock = ((ReadWriteLockInventory) inventory).getStock(product);
                    }
                    // 不打印库存信息，避免输出过多
                }
            });
        }
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        // 启动所有线程
        for (Thread t : buyThreads) {
            t.start();
        }
        for (Thread t : restockThreads) {
            t.start();
        }
        for (Thread t : queryThreads) {
            t.start();
        }
        // 等待所有线程完成
        for (Thread t : buyThreads) {
            t.join();
        }
        for (Thread t : restockThreads) {
            t.join();
        }
        for (Thread t : queryThreads) {
            t.join();
        }
        // 计算耗时
        long endTime = System.currentTimeMillis();
        // 输出最终库存和执行时间
        System.out.println("执行时间: " + (endTime - startTime) + "ms");
        System.out.println("最终库存:");
        Map<String, Integer> finalStock = null;
        if (inventory instanceof UnsafeInventory) {
            finalStock = ((UnsafeInventory) inventory).getAllStock();
        }
        else if (inventory instanceof SynchronizedInventory) {
            finalStock = ((SynchronizedInventory) inventory).getAllStock();
        }
        else if (inventory instanceof ConcurrentInventory) {
            finalStock = ((ConcurrentInventory) inventory).getAllStock();
        }
        else if (inventory instanceof ReadWriteLockInventory) {
            finalStock = ((ReadWriteLockInventory) inventory).getAllStock();
        }
        if (finalStock != null) {
            for (String product : products) {
                System.out.println(product + ": " + finalStock.get(product));
            }
        }
        // 验证库存一致性
        int expectedBaseline = 1000; // 初始库存
        int buyOps = buyThreads.length * 100; // 总购买操作
        int restockOps = restockThreads.length * 40 * 5; // 总补货操作
        // 平均到每种商品
        int opsPerProduct = (buyOps - restockOps) / products.length;
        int expectedStock = expectedBaseline - opsPerProduct;
        System.out.println("\n库存检查:");
        System.out.println("每种商品预期变化: " + opsPerProduct + " (负数表示库存减少)");
        System.out.println("预期最终库存: " + expectedStock);
    }

    // 线程不安全的库存管理
    static class UnsafeInventory
    {
        private final Map<String, Integer> productStock = new HashMap<>();

        // 添加或更新库存
        public void updateStock(String productId, int quantity)
        {
            Integer currentQuantity = productStock.get(productId);
            if (currentQuantity == null) {
                productStock.put(productId, quantity);
            }
            else {
                productStock.put(productId, currentQuantity + quantity);
            }
        }

        // 减少库存（如果库存不足，返回false）
        public boolean decreaseStock(String productId, int quantity)
        {
            Integer currentQuantity = productStock.get(productId);
            if (currentQuantity == null || currentQuantity < quantity) {
                return false;
            }
            productStock.put(productId, currentQuantity - quantity);
            return true;
        }

        // 获取当前库存
        public Integer getStock(String productId)
        {
            return productStock.getOrDefault(productId, 0);
        }

        // 获取所有商品库存
        public Map<String, Integer> getAllStock()
        {
            return new HashMap<>(productStock);
        }
    }

    // 使用synchronized的线程安全库存管理
    static class SynchronizedInventory
    {
        private final Map<String, Integer> productStock = new HashMap<>();

        // 添加或更新库存
        public synchronized void updateStock(String productId, int quantity)
        {
            Integer currentQuantity = productStock.get(productId);
            if (currentQuantity == null) {
                productStock.put(productId, quantity);
            }
            else {
                productStock.put(productId, currentQuantity + quantity);
            }
        }

        // 减少库存（如果库存不足，返回false）
        public synchronized boolean decreaseStock(String productId, int quantity)
        {
            Integer currentQuantity = productStock.get(productId);
            if (currentQuantity == null || currentQuantity < quantity) {
                return false;
            }
            productStock.put(productId, currentQuantity - quantity);
            return true;
        }

        // 获取当前库存
        public synchronized Integer getStock(String productId)
        {
            return productStock.getOrDefault(productId, 0);
        }

        // 获取所有商品库存
        public synchronized Map<String, Integer> getAllStock()
        {
            return new HashMap<>(productStock);
        }
    }

    // 使用ConcurrentHashMap和AtomicInteger的线程安全库存管理
    static class ConcurrentInventory
    {
        private final Map<String, AtomicInteger> productStock = new ConcurrentHashMap<>();

        // 添加或更新库存
        public void updateStock(String productId, int quantity)
        {
            productStock.computeIfAbsent(productId, k -> new AtomicInteger(0))
                    .addAndGet(quantity);
        }

        // 减少库存（如果库存不足，返回false）
        public boolean decreaseStock(String productId, int quantity)
        {
            AtomicInteger stock = productStock.get(productId);
            if (stock == null) {
                return false;
            }
            int currentValue;
            do {
                currentValue = stock.get();
                if (currentValue < quantity) {
                    return false;
                }
            }
            while (!stock.compareAndSet(currentValue, currentValue - quantity));
            return true;
        }

        // 获取当前库存
        public Integer getStock(String productId)
        {
            AtomicInteger stock = productStock.get(productId);
            return stock != null ? stock.get() : 0;
        }

        // 获取所有商品库存
        public Map<String, Integer> getAllStock()
        {
            Map<String, Integer> result = new HashMap<>();
            productStock.forEach((key, value) -> result.put(key, value.get()));
            return result;
        }
    }

    // 使用读写锁的线程安全库存管理
    static class ReadWriteLockInventory
    {
        private final Map<String, Integer> productStock = new HashMap<>();
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        // 添加或更新库存（写操作）
        public void updateStock(String productId, int quantity)
        {
            lock.writeLock().lock();
            try {
                Integer currentQuantity = productStock.get(productId);
                if (currentQuantity == null) {
                    productStock.put(productId, quantity);
                }
                else {
                    productStock.put(productId, currentQuantity + quantity);
                }
            }
            finally {
                lock.writeLock().unlock();
            }
        }

        // 减少库存（写操作）
        public boolean decreaseStock(String productId, int quantity)
        {
            lock.writeLock().lock();
            try {
                Integer currentQuantity = productStock.get(productId);
                if (currentQuantity == null || currentQuantity < quantity) {
                    return false;
                }
                productStock.put(productId, currentQuantity - quantity);
                return true;
            }
            finally {
                lock.writeLock().unlock();
            }
        }

        // 获取当前库存（读操作）
        public Integer getStock(String productId)
        {
            lock.readLock().lock();
            try {
                return productStock.getOrDefault(productId, 0);
            }
            finally {
                lock.readLock().unlock();
            }
        }

        // 获取所有商品库存（读操作）
        public Map<String, Integer> getAllStock()
        {
            lock.readLock().lock();
            try {
                return new HashMap<>(productStock);
            }
            finally {
                lock.readLock().unlock();
            }
        }
    }
}