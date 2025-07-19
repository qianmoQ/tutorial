[TOC]

## 学习目标

- 理解什么是线程安全以及为什么它在多线程编程中至关重要
- 掌握共享资源访问时产生竞态条件的原因和表现
- 学习识别典型的线程安全问题和并发风险
- 通过实例学习如何解决多线程计数器问题

## 1. 什么是线程安全

### 1.1 线程安全的定义

线程安全是指在多线程环境下，程序能够正确地处理共享资源，确保数据的一致性和正确性。一个线程安全的程序，无论有多少线程并发执行，都不会出现数据破坏或者得到错误结果的情况。

```java
package org.devlive.tutorial.multithreading.chapter04;

/**
 * 线程安全与非线程安全的对比
 */
public class ThreadSafetyDemo {
    public static void main(String[] args) throws InterruptedException {
        // 演示线程不安全的计数器
        UnsafeCounter unsafeCounter = new UnsafeCounter();
        runCounterTest("线程不安全的计数器", unsafeCounter);
        
        // 演示线程安全的计数器
        SafeCounter safeCounter = new SafeCounter();
        runCounterTest("线程安全的计数器", safeCounter);
    }
    
    // 线程不安全的计数器
    static class UnsafeCounter {
        private int count = 0;
        
        public void increment() {
            count++;  // 非原子操作
        }
        
        public int getCount() {
            return count;
        }
    }
    
    // 线程安全的计数器
    static class SafeCounter {
        private int count = 0;
        
        public synchronized void increment() {
            count++;  // 使用synchronized确保原子性
        }
        
        public synchronized int getCount() {
            return count;
        }
    }
    
    // 测试计数器
    private static void runCounterTest(String name, Object counter) throws InterruptedException {
        // 创建多个线程同时增加计数器
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    if (counter instanceof UnsafeCounter) {
                        ((UnsafeCounter) counter).increment();
                    } else if (counter instanceof SafeCounter) {
                        ((SafeCounter) counter).increment();
                    }
                }
            });
            threads[i].start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        // 检查最终结果
        int finalCount = 0;
        if (counter instanceof UnsafeCounter) {
            finalCount = ((UnsafeCounter) counter).getCount();
        } else if (counter instanceof SafeCounter) {
            finalCount = ((SafeCounter) counter).getCount();
        }
        
        System.out.println(name + ": 预期结果=100000, 实际结果=" + finalCount + 
                (finalCount == 100000 ? " (正确)" : " (错误)"));
    }
}
```

> 📌 **提示：** 在上面的例子中，线程不安全的计数器通常会得到小于预期的结果，因为多个线程同时执行`count++`操作时会发生数据竞争。

### 1.2 线程安全的重要性

线程安全对于多线程程序至关重要，原因包括：

1. **数据一致性**：确保程序的数据状态始终保持一致，不会出现数据损坏或不一致的情况。

2. **正确性**：确保程序的行为符合预期，产生正确的结果。

3. **避免难以调试的问题**：线程安全问题通常表现为间歇性故障，很难复现和调试。

4. **提高可靠性**：线程安全的程序在各种并发场景下都能稳定运行，提高整体可靠性。

## 2. 共享资源访问的竞态条件

### 2.1 什么是竞态条件

竞态条件(Race Condition)是指当多个线程同时访问共享资源，并且尝试同时修改该资源时，最终的结果依赖于线程执行的顺序和时机，导致结果不可预测。

在多线程环境下，竞态条件主要出现在以下场景：

1. **读-改-写** 操作序列不是原子的，可能被其他线程中断
2. **检查再执行** 模式，在检查和执行之间状态可能发生变化
3. **多步骤操作** 不是以原子方式执行的

### 2.2 竞态条件示例

```java
package org.devlive.tutorial.multithreading.chapter04;

/**
 * 竞态条件示例
 */
public class RaceConditionDemo {
    
    // 银行账户类（线程不安全）
    static class BankAccount {
        private int balance;  // 余额
        
        public BankAccount(int initialBalance) {
            this.balance = initialBalance;
        }
        
        // 存款方法
        public void deposit(int amount) {
            // 读取余额
            int current = balance;
            
            // 模拟处理延迟，使竞态条件更容易出现
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // 计算新余额并更新
            balance = current + amount;
        }
        
        // 取款方法
        public void withdraw(int amount) {
            // 读取余额
            int current = balance;
            
            // 模拟处理延迟
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // 只有余额充足才能取款
            if (current >= amount) {
                // 计算新余额并更新
                balance = current - amount;
            }
        }
        
        public int getBalance() {
            return balance;
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // 创建账户，初始余额为1000
        BankAccount account = new BankAccount(1000);
        
        // 创建多个存款线程，每个存款100
        Thread[] depositThreads = new Thread[5];
        for (int i = 0; i < depositThreads.length; i++) {
            depositThreads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    account.deposit(100);
                }
            });
        }
        
        // 创建多个取款线程，每个取款100
        Thread[] withdrawThreads = new Thread[5];
        for (int i = 0; i < withdrawThreads.length; i++) {
            withdrawThreads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    account.withdraw(100);
                }
            });
        }
        
        // 启动所有线程
        System.out.println("启动线程，模拟账户并发操作...");
        System.out.println("初始余额: " + account.getBalance());
        
        for (Thread t : depositThreads) t.start();
        for (Thread t : withdrawThreads) t.start();
        
        // 等待所有线程完成
        for (Thread t : depositThreads) t.join();
        for (Thread t : withdrawThreads) t.join();
        
        // 检查最终余额
        System.out.println("所有操作完成后余额: " + account.getBalance());
        System.out.println("预期余额: 1000 (初始值) + 5*10*100 (存款) - 5*10*100 (取款) = 1000");
    }
}
```

在上面的示例中，银行账户的存取款操作不是原子的，由于竞态条件，最终的余额可能不等于预期的1000。

> ⚠️ **警告：** 竞态条件是多线程编程中最常见也是最危险的问题之一，因为它可能导致不可重现的错误和数据损坏。

### 2.3 竞态条件的类型

#### 读-改-写竞态条件

```java
// 线程A和线程B同时执行这段代码:
int temp = counter;  // 读
temp = temp + 1;     // 改
counter = temp;      // 写
```

如果`counter`初始值为0，预期两个线程执行后`counter`应该是2，但可能会出现以下情况：
1. 线程A读取`counter`为0
2. 线程B读取`counter`为0
3. 线程A计算`temp + 1 = 1`并更新`counter`为1
4. 线程B计算`temp + 1 = 1`并更新`counter`为1

最终结果是1，而不是预期的2。

#### 检查再执行竞态条件

```java
// 方法示例：转账
public void transfer(Account to, double amount) {
    if (this.balance >= amount) {  // 检查
        this.balance -= amount;    // 执行
        to.balance += amount;      // 执行
    }
}
```

如果两个线程同时从同一账户转账，且账户余额仅足够一次转账，可能会出现以下情况：
1. 线程A检查余额足够并准备转账
2. 线程B检查余额足够并准备转账
3. 线程A执行转账，减少余额
4. 线程B执行转账，再次减少余额，可能导致余额为负

## 3. 线程安全问题的表现形式

### 3.1 原子性问题

当一个操作本应该是不可分割的，但在多线程环境下被拆分执行时，就会出现原子性问题。Java中许多看起来是一条语句的操作实际上并不是原子的。

```java
package org.devlive.tutorial.multithreading.chapter04;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 原子性问题示例
 */
public class AtomicityProblemDemo {
    public static void main(String[] args) throws InterruptedException {
        // 测试非原子操作
        testCounter("非原子计数器", new NonAtomicCounter());
        
        // 测试使用原子变量的计数器
        testCounter("原子计数器", new AtomicCounter());
    }
    
    // 非原子计数器
    static class NonAtomicCounter {
        private int count = 0;
        
        public void increment() {
            count++; // 看似简单，实际上是读-改-写三步操作
        }
        
        public int getCount() {
            return count;
        }
    }
    
    // 使用原子变量的计数器
    static class AtomicCounter {
        private AtomicInteger count = new AtomicInteger(0);
        
        public void increment() {
            count.incrementAndGet(); // 原子操作
        }
        
        public int getCount() {
            return count.get();
        }
    }
    
    // 测试计数器
    private static void testCounter(String name, Object counter) throws InterruptedException {
        final int NUM_THREADS = 10;
        final int ITERATIONS = 100000;
        
        // 创建并启动多个线程递增计数器
        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < ITERATIONS; j++) {
                    if (counter instanceof NonAtomicCounter) {
                        ((NonAtomicCounter) counter).increment();
                    } else if (counter instanceof AtomicCounter) {
                        ((AtomicCounter) counter).increment();
                    }
                }
            });
            threads[i].start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        // 检查结果
        int finalCount = 0;
        if (counter instanceof NonAtomicCounter) {
            finalCount = ((NonAtomicCounter) counter).getCount();
        } else if (counter instanceof AtomicCounter) {
            finalCount = ((AtomicCounter) counter).getCount();
        }
        
        int expectedCount = NUM_THREADS * ITERATIONS;
        System.out.println(name + " - 预期结果: " + expectedCount + ", 实际结果: " + finalCount +
                (finalCount == expectedCount ? " (正确)" : " (错误)"));
    }
}
```

> 📌 **提示：** 在Java中，以下操作都不是原子的：
> - 递增/递减操作（如`i++`, `i--`）
> - 复合赋值操作（如`i += 5`）
> - 在volatile变量上执行的非赋值操作

### 3.2 可见性问题

可见性问题指的是一个线程对共享变量的修改，另一个线程可能看不到这个修改的最新值。这通常是由于CPU缓存、编译器优化或指令重排序导致的。

```java
package org.devlive.tutorial.multithreading.chapter04;

/**
 * 可见性问题示例
 */
public class VisibilityProblemDemo {
    // 没有volatile修饰的标志变量
    private static boolean stopRequested = false;
    
    // 使用volatile修饰的标志变量
    private static volatile boolean volatileStopRequested = false;
    
    public static void main(String[] args) throws InterruptedException {
        // 测试没有volatile的情况
        testVisibility(false);
        
        // 测试使用volatile的情况
        testVisibility(true);
    }
    
    private static void testVisibility(boolean useVolatile) throws InterruptedException {
        System.out.println("\n======== " + (useVolatile ? "使用volatile" : "不使用volatile") + " ========");
        
        // 重置标志
        stopRequested = false;
        volatileStopRequested = false;
        
        // 创建工作线程，不断检查标志变量
        Thread workerThread = new Thread(() -> {
            long i = 0;
            System.out.println("工作线程开始执行...");
            
            // 根据参数选择使用哪个标志变量
            if (useVolatile) {
                while (!volatileStopRequested) {
                    i++;
                }
            } else {
                while (!stopRequested) {
                    i++;
                }
            }
            
            System.out.println("工作线程检测到停止信号，循环次数：" + i);
        });
        
        workerThread.start();
        
        // 主线程等待一会儿
        Thread.sleep(1000);
        
        System.out.println("主线程设置停止信号...");
        
        // 设置停止标志
        if (useVolatile) {
            volatileStopRequested = true;
        } else {
            stopRequested = true;
        }
        
        // 等待工作线程结束
        workerThread.join(5000);
        
        // 检查线程是否还活着
        if (workerThread.isAlive()) {
            System.out.println("工作线程仍在运行！可能存在可见性问题");
            workerThread.interrupt(); // 强制中断
        } else {
            System.out.println("工作线程正确终止");
        }
    }
}
```

在不使用`volatile`的情况下，由于可见性问题，工作线程可能无法看到主线程对`stopRequested`的修改，导致它无限循环下去。

> 📌 **提示：** Java提供了`volatile`关键字来解决可见性问题。被`volatile`修饰的变量，对它的读写都会直接在主内存中进行，而不是在CPU缓存中。

### 3.3 有序性问题

有序性问题是指程序的执行顺序可能与代码编写的顺序不同。这是由于编译器优化、CPU指令重排序等导致的。在单线程环境下，重排序后的执行结果与顺序执行的结果相同，但在多线程环境下可能导致问题。

```java
package org.devlive.tutorial.multithreading.chapter04;

/**
 * 有序性问题示例
 */
public class OrderingProblemDemo {
    private static int x = 0, y = 0;
    private static int a = 0, b = 0;
    
    public static void main(String[] args) throws InterruptedException {
        int iterations = 0;
        int abnormalResults = 0;
        
        // 多次运行测试，统计出现重排序的次数
        final int TEST_COUNT = 100000;
        System.out.println("开始测试重排序问题，运行 " + TEST_COUNT + " 次...");
        
        for (int i = 0; i < TEST_COUNT; i++) {
            iterations++;
            
            // 重置变量
            x = 0; y = 0;
            a = 0; b = 0;
            
            // 创建线程1
            Thread thread1 = new Thread(() -> {
                a = 1;  // 语句1
                x = b;  // 语句2
            });
            
            // 创建线程2
            Thread thread2 = new Thread(() -> {
                b = 1;  // 语句3
                y = a;  // 语句4
            });
            
            // 启动线程
            thread1.start();
            thread2.start();
            
            // 等待线程结束
            thread1.join();
            thread2.join();
            
            // 检查结果
            if (x == 0 && y == 0) {
                abnormalResults++;
                // 因为出现频率较低，只在前几次出现时打印详细信息
                if (abnormalResults <= 10) {
                    System.out.println("检测到可能的指令重排序: x=" + x + ", y=" + y);
                }
            }
        }
        
        // 统计结果
        System.out.println("\n测试完成:");
        System.out.println("总测试次数: " + iterations);
        System.out.println("检测到的异常结果次数(x=0, y=0): " + abnormalResults);
        System.out.println("异常结果比例: " + String.format("%.5f%%", (double)abnormalResults / iterations * 100));
        
        System.out.println("\n分析:");
        if (abnormalResults > 0) {
            System.out.println("检测到可能的指令重排序。在某些情况下，两个线程中的操作可能被重排序，导致x=0且y=0。");
            System.out.println("这表明Java内存模型允许某些指令重排序，可能影响多线程程序的执行结果。");
        } else {
            System.out.println("未检测到明显的指令重排序。这可能是由于:");
            System.out.println("1. 当前硬件和JVM实现中不太容易观察到这种重排序");
            System.out.println("2. 测试次数不够多");
            System.out.println("但这并不意味着指令重排序不存在。在复杂的多线程程序中，它仍然可能导致问题。");
        }
    }
}
```

在上面的例子中，如果没有指令重排序，理论上`x`和`y`不可能同时为0。但由于指令重排序的存在，它们确实可能同时为0。

> 📌 **提示：** Java内存模型通过`happens-before`关系保证有序性。此外，`volatile`、`synchronized`和`final`关键字也能在一定程度上阻止有害的指令重排序。

## 4. 实战案例：商品库存管理

下面我们通过一个商品库存管理的实战案例，来综合应用所学的线程安全知识：

```java
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
public class InventoryManagementDemo {
    
    // 线程不安全的库存管理
    static class UnsafeInventory {
        private final Map<String, Integer> productStock = new HashMap<>();
        
        // 添加或更新库存
        public void updateStock(String productId, int quantity) {
            Integer currentQuantity = productStock.get(productId);
            if (currentQuantity == null) {
                productStock.put(productId, quantity);
            } else {
                productStock.put(productId, currentQuantity + quantity);
            }
        }
        
        // 减少库存（如果库存不足，返回false）
        public boolean decreaseStock(String productId, int quantity) {
            Integer currentQuantity = productStock.get(productId);
            if (currentQuantity == null || currentQuantity < quantity) {
                return false;
            }
            
            productStock.put(productId, currentQuantity - quantity);
            return true;
        }
        
        // 获取当前库存
        public Integer getStock(String productId) {
            return productStock.getOrDefault(productId, 0);
        }
        
        // 获取所有商品库存
        public Map<String, Integer> getAllStock() {
            return new HashMap<>(productStock);
        }
    }
    
    // 使用synchronized的线程安全库存管理
    static class SynchronizedInventory {
        private final Map<String, Integer> productStock = new HashMap<>();
        
        // 添加或更新库存
        public synchronized void updateStock(String productId, int quantity) {
            Integer currentQuantity = productStock.get(productId);
            if (currentQuantity == null) {
                productStock.put(productId, quantity);
            } else {
                productStock.put(productId, currentQuantity + quantity);
            }
        }
        
        // 减少库存（如果库存不足，返回false）
        public synchronized boolean decreaseStock(String productId, int quantity) {
            Integer currentQuantity = productStock.get(productId);
            if (currentQuantity == null || currentQuantity < quantity) {
                return false;
            }
            
            productStock.put(productId, currentQuantity - quantity);
            return true;
        }
        
        // 获取当前库存
        public synchronized Integer getStock(String productId) {
            return productStock.getOrDefault(productId, 0);
        }
        
        // 获取所有商品库存
        public synchronized Map<String, Integer> getAllStock() {
            return new HashMap<>(productStock);
        }
    }
    
    // 使用ConcurrentHashMap和AtomicInteger的线程安全库存管理
    static class ConcurrentInventory {
        private final Map<String, AtomicInteger> productStock = new ConcurrentHashMap<>();
        
        // 添加或更新库存
        public void updateStock(String productId, int quantity) {
            productStock.computeIfAbsent(productId, k -> new AtomicInteger(0))
                    .addAndGet(quantity);
        }
        
        // 减少库存（如果库存不足，返回false）
        public boolean decreaseStock(String productId, int quantity) {
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
            } while (!stock.compareAndSet(currentValue, currentValue - quantity));
            
            return true;
        }
        
        // 获取当前库存
        public Integer getStock(String productId) {
            AtomicInteger stock = productStock.get(productId);
            return stock != null ? stock.get() : 0;
        }
        
        // 获取所有商品库存
        public Map<String, Integer> getAllStock() {
            Map<String, Integer> result = new HashMap<>();
            productStock.forEach((key, value) -> result.put(key, value.get()));
            return result;
        }
    }
    
    // 使用读写锁的线程安全库存管理
    static class ReadWriteLockInventory {
        private final Map<String, Integer> productStock = new HashMap<>();
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        
        // 添加或更新库存（写操作）
        public void updateStock(String productId, int quantity) {
            lock.writeLock().lock();
            try {
                Integer currentQuantity = productStock.get(productId);
                if (currentQuantity == null) {
                    productStock.put(productId, quantity);
                } else {
                    productStock.put(productId, currentQuantity + quantity);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        // 减少库存（写操作）
        public boolean decreaseStock(String productId, int quantity) {
            lock.writeLock().lock();
            try {
                Integer currentQuantity = productStock.get(productId);
                if (currentQuantity == null || currentQuantity < quantity) {
                    return false;
                }
                
                productStock.put(productId, currentQuantity - quantity);
                return true;
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        // 获取当前库存（读操作）
        public Integer getStock(String productId) {
            lock.readLock().lock();
            try {
                return productStock.getOrDefault(productId, 0);
            } finally {
                lock.readLock().unlock();
            }
        }
        
        // 获取所有商品库存（读操作）
        public Map<String, Integer> getAllStock() {
            lock.readLock().lock();
            try {
                return new HashMap<>(productStock);
            } finally {
                lock.readLock().unlock();
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
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
    
    private static void testInventory(Object inventory, String[] products) throws InterruptedException {
        // 创建多个购买线程（减少库存）
        Thread[] buyThreads = new Thread[10];
        for (int i = 0; i < buyThreads.length; i++) {
            buyThreads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    String product = products[j % products.length];
                    boolean success = false;
                    
                    if (inventory instanceof UnsafeInventory) {
                        success = ((UnsafeInventory) inventory).decreaseStock(product, 1);
                    } else if (inventory instanceof SynchronizedInventory) {
                        success = ((SynchronizedInventory) inventory).decreaseStock(product, 1);
                    } else if (inventory instanceof ConcurrentInventory) {
                        success = ((ConcurrentInventory) inventory).decreaseStock(product, 1);
                    } else if (inventory instanceof ReadWriteLockInventory) {
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
                    } else if (inventory instanceof SynchronizedInventory) {
                        ((SynchronizedInventory) inventory).updateStock(product, 5);
                    } else if (inventory instanceof ConcurrentInventory) {
                        ((ConcurrentInventory) inventory).updateStock(product, 5);
                    } else if (inventory instanceof ReadWriteLockInventory) {
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
                    } else if (inventory instanceof SynchronizedInventory) {
                        stock = ((SynchronizedInventory) inventory).getStock(product);
                    } else if (inventory instanceof ConcurrentInventory) {
                        stock = ((ConcurrentInventory) inventory).getStock(product);
                    } else if (inventory instanceof ReadWriteLockInventory) {
                        stock = ((ReadWriteLockInventory) inventory).getStock(product);
                    }
                    
                    // 不打印库存信息，避免输出过多
                }
            });
        }
        
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        
        // 启动所有线程
        for (Thread t : buyThreads) t.start();
        for (Thread t : restockThreads) t.start();
        for (Thread t : queryThreads) t.start();
        
        // 等待所有线程完成
        for (Thread t : buyThreads) t.join();
        for (Thread t : restockThreads) t.join();
        for (Thread t : queryThreads) t.join();
        
        // 计算耗时
        long endTime = System.currentTimeMillis();
        
        // 输出最终库存和执行时间
        System.out.println("执行时间: " + (endTime - startTime) + "ms");
        System.out.println("最终库存:");
        
        Map<String, Integer> finalStock = null;
        if (inventory instanceof UnsafeInventory) {
            finalStock = ((UnsafeInventory) inventory).getAllStock();
        } else if (inventory instanceof SynchronizedInventory) {
            finalStock = ((SynchronizedInventory) inventory).getAllStock();
        } else if (inventory instanceof ConcurrentInventory) {
            finalStock = ((ConcurrentInventory) inventory).getAllStock();
        } else if (inventory instanceof ReadWriteLockInventory) {
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
}
```

在这个案例中，我们实现了四种不同的库存管理方式，并比较了它们在多线程环境下的表现：
1. 不安全的库存管理（使用HashMap）
2. 使用synchronized的库存管理
3. 使用ConcurrentHashMap和AtomicInteger的库存管理
4. 使用读写锁的库存管理

测试结果可能显示线程不安全的实现存在数据不一致的问题，而其他三种线程安全的实现能够保证数据的正确性，但性能各有差异。

## 常见问题与解决方案

### 问题1：多个操作需要作为一个原子单元执行

**问题描述：** 有时我们需要确保多个操作作为一个不可分割的单元执行，以保持数据的一致性。

**解决方案：** 使用锁（synchronized或显式锁）来确保互斥访问。

```java
// 使用synchronized
public synchronized boolean transferMoney(Account from, Account to, double amount) {
    if (from.getBalance() < amount) {
        return false;
    }
    from.debit(amount);
    to.credit(amount);
    return true;
}

// 使用显式锁
public boolean transferMoney(Account from, Account to, double amount) {
    lock.lock();
    try {
        if (from.getBalance() < amount) {
            return false;
        }
        from.debit(amount);
        to.credit(amount);
        return true;
    } finally {
        lock.unlock();
    }
}
```

### 问题2：频繁读取但很少修改的数据

**问题描述：** 有些数据结构被频繁读取但很少修改，使用互斥锁会导致读操作也被阻塞，影响性能。

**解决方案：** 使用读写锁（ReadWriteLock）允许多个读操作并发执行，而写操作依然互斥。

```java
private final Map<String, Product> productCatalog = new HashMap<>();
private final ReadWriteLock lock = new ReentrantReadWriteLock();

// 读操作（多个线程可以同时读取）
public Product getProduct(String productId) {
    lock.readLock().lock();
    try {
        return productCatalog.get(productId);
    } finally {
        lock.readLock().unlock();
    }
}

// 写操作（互斥访问）
public void updateProduct(Product product) {
    lock.writeLock().lock();
    try {
        productCatalog.put(product.getId(), product);
    } finally {
        lock.writeLock().unlock();
    }
}
```

### 问题3：在检查和执行之间保持一致性

**问题描述：** 在执行某个操作前需要先检查条件，但在多线程环境下，检查和执行之间的状态可能已经变化。

**解决方案：** 使用锁确保检查和执行的原子性，或者使用原子变量的CAS操作。

```java
// 使用锁确保原子性
public synchronized boolean decrementIfPositive(int[] array, int index) {
    if (array[index] > 0) {
        array[index]--;
        return true;
    }
    return false;
}

// 使用AtomicIntegerArray的CAS操作
private final AtomicIntegerArray atomicArray = new AtomicIntegerArray(size);

public boolean decrementIfPositive(int index) {
    while (true) {
        int current = atomicArray.get(index);
        if (current <= 0) {
            return false;
        }
        if (atomicArray.compareAndSet(index, current, current - 1)) {
            return true;
        }
        // 如果CAS失败，循环重试
    }
}
```

## 小结

在本章中，我们深入探讨了线程安全问题，学习了以下关键内容：

1. **线程安全的概念**：线程安全指在多线程环境下，程序能够正确处理共享资源，确保数据的一致性和正确性。

2. **竞态条件**：当多个线程同时访问共享资源并尝试修改时，结果依赖于线程执行的顺序和时机，可能导致不可预测的结果。

3. **线程安全问题的三种表现**：
   - 原子性问题：操作被拆分执行，导致数据不一致
   - 可见性问题：一个线程的修改对其他线程不可见
   - 有序性问题：指令重排序导致的执行顺序问题

4. **解决方案**：
   - 使用`synchronized`关键字确保互斥访问
   - 使用原子变量（如`AtomicInteger`）进行原子操作
   - 使用显式锁（如`ReentrantLock`）提供更灵活的锁机制
   - 使用读写锁（`ReadWriteLock`）优化读多写少的场景
   - 使用线程安全的集合类（如`ConcurrentHashMap`）

5. **实战应用**：通过商品库存管理案例，我们实践了不同的线程安全实现方式，并比较了它们的性能和正确性。

理解和解决线程安全问题是多线程编程的核心挑战。通过合理使用Java提供的同步工具，我们可以开发出高效、可靠的多线程应用程序。

在下一章，我们将详细探讨`synchronized`关键字的使用和底层实现原理。

本章节源代码地址为 https://github.com/qianmoQ/tutorial/tree/main/java-multithreading-tutorial/src/main/java/org/devlive/tutorial/multithreading/chapter04