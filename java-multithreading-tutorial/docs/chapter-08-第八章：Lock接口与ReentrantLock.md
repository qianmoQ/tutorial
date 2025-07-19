[TOC]

在前面几章中，我们学习了使用synchronized关键字来解决线程同步问题。虽然synchronized使用简单方便，但它也有一些限制：无法中断正在等待锁的线程、无法设置获取锁的超时时间、只能是非公平锁等。为了解决这些问题，Java 5引入了更加灵活强大的Lock接口和ReentrantLock实现类。本章将带你深入了解这些高级同步工具。

## 学习目标

通过本章的学习，你将掌握：
- 理解Lock接口与synchronized关键字的区别和优势
- 掌握ReentrantLock的基本使用方法和高级特性
- 深入理解公平锁与非公平锁的概念和应用场景
- 学会使用Lock接口的各种方法解决复杂的并发问题
- 掌握Lock接口的最佳实践和常见陷阱规避

## 1 Lock接口与synchronized的对比

### 1.1 synchronized的局限性

在前面的学习中，我们已经熟悉了synchronized关键字的使用。虽然synchronized简单易用，但在某些场景下存在一些限制：

```java
package org.devlive.tutorial.multithreading.chapter08;

/**
 * 演示synchronized的局限性
 */
public class SynchronizedLimitationDemo {
    
    private static final Object lock = new Object();
    private static int count = 0;
    
    /**
     * 使用synchronized的方法
     */
    public static void synchronizedMethod() {
        synchronized (lock) {
            count++;
            System.out.println(Thread.currentThread().getName() + " 获取锁成功，count = " + count);
            
            try {
                // 模拟长时间持有锁
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // 创建多个线程尝试获取锁
        Thread thread1 = new Thread(() -> {
            synchronizedMethod();
        }, "线程1");
        
        Thread thread2 = new Thread(() -> {
            synchronizedMethod();
        }, "线程2");
        
        thread1.start();
        Thread.sleep(100); // 确保线程1先启动
        thread2.start();
        
        // synchronized的问题：无法中断等待锁的线程
        Thread.sleep(1000);
        System.out.println("尝试中断线程2...");
        thread2.interrupt(); // 这个中断无法让线程2停止等待锁
        
        thread1.join();
        thread2.join();
        
        System.out.println("synchronized无法响应中断，线程2仍然会等待并获取锁");
    }
}
```

### 1.2 Lock接口的优势

Lock接口为我们提供了更加灵活强大的锁机制：

```java
package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示Lock接口的优势
 */
public class LockAdvantageDemo {
    
    private static final ReentrantLock lock = new ReentrantLock();
    private static int count = 0;
    
    /**
     * 使用Lock的可中断获取锁方法
     */
    public static void interruptibleMethod() {
        try {
            // 使用可中断的方式获取锁
            lock.lockInterruptibly();
            try {
                count++;
                System.out.println(Thread.currentThread().getName() + " 获取锁成功，count = " + count);
                
                // 模拟长时间持有锁
                Thread.sleep(2000);
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 在等待锁时被中断");
        }
    }
    
    /**
     * 使用Lock的超时获取锁方法
     */
    public static void timeoutMethod() {
        try {
            // 尝试在1秒内获取锁
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    count++;
                    System.out.println(Thread.currentThread().getName() + " 在超时时间内获取锁成功，count = " + count);
                    Thread.sleep(500);
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println(Thread.currentThread().getName() + " 获取锁超时");
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 在等待锁时被中断");
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 演示可中断锁 ===");
        
        Thread thread1 = new Thread(() -> {
            interruptibleMethod();
        }, "可中断线程1");
        
        Thread thread2 = new Thread(() -> {
            interruptibleMethod();
        }, "可中断线程2");
        
        thread1.start();
        Thread.sleep(100);
        thread2.start();
        
        // 1秒后中断线程2
        Thread.sleep(1000);
        System.out.println("中断线程2...");
        thread2.interrupt();
        
        thread1.join();
        thread2.join();
        
        System.out.println("\n=== 演示超时锁 ===");
        
        Thread thread3 = new Thread(() -> {
            timeoutMethod();
        }, "超时线程1");
        
        Thread thread4 = new Thread(() -> {
            timeoutMethod();
        }, "超时线程2");
        
        thread3.start();
        Thread.sleep(100);
        thread4.start();
        
        thread3.join();
        thread4.join();
    }
}
```

从上面的例子可以看出，Lock接口相比synchronized具有以下优势：

1. **可中断性**：可以响应中断请求，避免无限等待
2. **超时机制**：可以设置获取锁的超时时间
3. **非阻塞获取**：tryLock()方法可以立即返回获取结果
4. **公平性选择**：可以选择公平锁或非公平锁
5. **条件变量支持**：可以创建多个条件变量

> 💡 **设计思想**  
> Lock接口的设计遵循了"显式优于隐式"的原则。虽然使用起来比synchronized复杂一些，但提供了更多的控制选项，适用于需要高级同步功能的场景。

## 2 ReentrantLock的使用方法

### 2.1 ReentrantLock基本使用

ReentrantLock是Lock接口最常用的实现类，它是一个可重入的互斥锁：

```java
package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock基本使用示例
 */
public class ReentrantLockBasicDemo {
    
    private final ReentrantLock lock = new ReentrantLock();
    private int count = 0;
    
    /**
     * 使用ReentrantLock保护共享资源
     */
    public void increment() {
        // 获取锁
        lock.lock();
        try {
            // 临界区：修改共享资源
            count++;
            System.out.println(Thread.currentThread().getName() + " 执行increment，当前值：" + count);
            
            // 模拟一些处理时间
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // 在finally块中释放锁，确保锁一定会被释放
            lock.unlock();
        }
    }
    
    /**
     * 获取当前计数值
     */
    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 显示锁的状态信息
     */
    public void showLockInfo() {
        System.out.println("锁是否被当前线程持有：" + lock.isHeldByCurrentThread());
        System.out.println("锁的持有次数：" + lock.getHoldCount());
        System.out.println("等待获取锁的线程数：" + lock.getQueueLength());
        System.out.println("是否是公平锁：" + lock.isFair());
    }
    
    public static void main(String[] args) throws InterruptedException {
        ReentrantLockBasicDemo demo = new ReentrantLockBasicDemo();
        
        // 在主线程中显示锁的初始状态
        System.out.println("=== 锁的初始状态 ===");
        demo.showLockInfo();
        
        // 创建多个线程并发执行increment操作
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    demo.increment();
                }
            }, "工作线程-" + i);
            threads[i].start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\n=== 最终结果 ===");
        System.out.println("最终计数值：" + demo.getCount());
        demo.showLockInfo();
    }
}
```

> ⚠️ **重要提醒**  
> 使用Lock时，必须在finally块中释放锁。如果忘记释放锁或者在释放锁之前发生异常，会导致其他线程永远无法获取锁，造成死锁！

### 2.2 ReentrantLock的可重入性

"可重入"是指同一个线程可以多次获取同一把锁。这对于递归调用或者方法间相互调用非常重要：

```java
package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示ReentrantLock的可重入性
 */
public class ReentrantLockReentrantDemo {
    
    private final ReentrantLock lock = new ReentrantLock();
    
    /**
     * 外层方法，获取锁后调用内层方法
     */
    public void outerMethod() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 进入外层方法，锁持有次数：" + lock.getHoldCount());
            
            // 调用内层方法，需要再次获取同一把锁
            innerMethod();
            
            System.out.println(Thread.currentThread().getName() + " 完成外层方法");
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 内层方法，需要获取同一把锁
     */
    public void innerMethod() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 进入内层方法，锁持有次数：" + lock.getHoldCount());
            
            // 调用递归方法
            if (lock.getHoldCount() < 5) {
                recursiveMethod();
            }
            
            System.out.println(Thread.currentThread().getName() + " 完成内层方法");
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 递归方法，演示多层重入
     */
    public void recursiveMethod() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 递归调用，锁持有次数：" + lock.getHoldCount());
            
            if (lock.getHoldCount() < 5) {
                recursiveMethod();
            }
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ReentrantLockReentrantDemo demo = new ReentrantLockReentrantDemo();
        
        // 创建多个线程测试可重入性
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread(() -> {
                demo.outerMethod();
            }, "线程-" + (i + 1));
            threads[i].start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
```

这个例子展示了ReentrantLock的重入特性：
- 同一个线程可以多次获取同一把锁
- 每次获取锁都会增加持有计数
- 释放锁的次数必须与获取锁的次数相匹配
- 只有当持有计数归零时，锁才真正被释放

### 2.3 tryLock方法的灵活使用

tryLock方法提供了非阻塞的锁获取方式，避免线程无限等待：

```java
package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示tryLock方法的各种使用方式
 */
public class TryLockDemo {
    
    private final ReentrantLock lock = new ReentrantLock();
    private int resource = 0;
    
    /**
     * 使用tryLock()立即尝试获取锁
     */
    public void immediatelyTryLock() {
        if (lock.tryLock()) {
            try {
                System.out.println(Thread.currentThread().getName() + " 立即获取锁成功");
                resource++;
                System.out.println(Thread.currentThread().getName() + " 处理资源，当前值：" + resource);
                
                // 模拟处理时间
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
                System.out.println(Thread.currentThread().getName() + " 释放锁");
            }
        } else {
            System.out.println(Thread.currentThread().getName() + " 立即获取锁失败，执行替代方案");
            // 执行不需要锁的替代逻辑
            performAlternativeTask();
        }
    }
    
    /**
     * 使用tryLock(time, unit)在指定时间内尝试获取锁
     */
    public void tryLockWithTimeout(long timeout, TimeUnit unit) {
        try {
            if (lock.tryLock(timeout, unit)) {
                try {
                    System.out.println(Thread.currentThread().getName() + " 在超时时间内获取锁成功");
                    resource++;
                    System.out.println(Thread.currentThread().getName() + " 处理资源，当前值：" + resource);
                    
                    // 模拟处理时间
                    Thread.sleep(1500);
                } finally {
                    lock.unlock();
                    System.out.println(Thread.currentThread().getName() + " 释放锁");
                }
            } else {
                System.out.println(Thread.currentThread().getName() + " 获取锁超时，执行替代方案");
                performAlternativeTask();
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 在等待锁时被中断");
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 执行不需要锁的替代任务
     */
    private void performAlternativeTask() {
        System.out.println(Thread.currentThread().getName() + " 执行替代任务：读取资源当前值 = " + resource);
    }
    
    public static void main(String[] args) throws InterruptedException {
        TryLockDemo demo = new TryLockDemo();
        
        System.out.println("=== 演示立即尝试获取锁 ===");
        
        // 创建多个线程测试立即尝试获取锁
        Thread[] threads1 = new Thread[3];
        for (int i = 0; i < 3; i++) {
            threads1[i] = new Thread(() -> {
                demo.immediatelyTryLock();
            }, "即时线程-" + (i + 1));
            threads1[i].start();
        }
        
        // 等待第一组线程完成
        for (Thread thread : threads1) {
            thread.join();
        }
        
        System.out.println("\n=== 演示带超时的尝试获取锁 ===");
        
        // 创建多个线程测试带超时的尝试获取锁
        Thread[] threads2 = new Thread[4];
        for (int i = 0; i < 4; i++) {
            threads2[i] = new Thread(() -> {
                demo.tryLockWithTimeout(1, TimeUnit.SECONDS);
            }, "超时线程-" + (i + 1));
            threads2[i].start();
        }
        
        // 等待第二组线程完成
        for (Thread thread : threads2) {
            thread.join();
        }
        
        System.out.println("\n最终资源值：" + demo.resource);
    }
}
```

tryLock方法的使用场景：
- **立即tryLock()**：适用于有替代方案的场景，避免线程阻塞
- **带超时的tryLock()**：适用于不能无限等待的场景，如响应时间要求严格的系统
- **在循环中使用tryLock()**：可以实现重试机制

## 3 公平锁与非公平锁

### 3.1 公平锁与非公平锁的区别

ReentrantLock可以选择是公平锁还是非公平锁：
- **公平锁**：严格按照线程请求锁的顺序分配锁
- **非公平锁**：允许"插队"，新来的线程可能比等待时间更长的线程先获得锁

```java
package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示公平锁与非公平锁的区别
 */
public class FairVsUnfairLockDemo {
    
    // 公平锁：构造参数为true
    private final ReentrantLock fairLock = new ReentrantLock(true);
    
    // 非公平锁：构造参数为false或使用无参构造器
    private final ReentrantLock unfairLock = new ReentrantLock(false);
    
    /**
     * 使用公平锁的方法
     */
    public void fairLockMethod() {
        for (int i = 0; i < 2; i++) {
            fairLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 获取公平锁，执行第 " + (i + 1) + " 次操作");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                fairLock.unlock();
            }
        }
    }
    
    /**
     * 使用非公平锁的方法
     */
    public void unfairLockMethod() {
        for (int i = 0; i < 2; i++) {
            unfairLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 获取非公平锁，执行第 " + (i + 1) + " 次操作");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                unfairLock.unlock();
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        FairVsUnfairLockDemo demo = new FairVsUnfairLockDemo();
        
        System.out.println("=== 公平锁测试（观察获取锁的顺序是否按线程启动顺序） ===");
        
        // 测试公平锁
        Thread[] fairThreads = new Thread[4];
        for (int i = 0; i < 4; i++) {
            fairThreads[i] = new Thread(() -> {
                demo.fairLockMethod();
            }, "公平锁线程-" + (i + 1));
            fairThreads[i].start();
            Thread.sleep(50); // 确保线程按顺序启动
        }
        
        // 等待公平锁测试完成
        for (Thread thread : fairThreads) {
            thread.join();
        }
        
        System.out.println("\n=== 非公平锁测试（观察是否存在插队现象） ===");
        
        // 测试非公平锁
        Thread[] unfairThreads = new Thread[4];
        for (int i = 0; i < 4; i++) {
            unfairThreads[i] = new Thread(() -> {
                demo.unfairLockMethod();
            }, "非公平锁线程-" + (i + 1));
            unfairThreads[i].start();
            Thread.sleep(50); // 确保线程按顺序启动
        }
        
        // 等待非公平锁测试完成
        for (Thread thread : unfairThreads) {
            thread.join();
        }
    }
}
```

### 3.2 公平锁与非公平锁的性能对比

```java
package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 公平锁与非公平锁的性能对比
 */
public class LockPerformanceComparison {
    
    private static final int THREAD_COUNT = 10;
    private static final int OPERATIONS_PER_THREAD = 1000;
    
    private final ReentrantLock fairLock = new ReentrantLock(true);
    private final ReentrantLock unfairLock = new ReentrantLock(false);
    
    private volatile int fairCounter = 0;
    private volatile int unfairCounter = 0;
    
    /**
     * 使用公平锁进行计数
     */
    public void fairIncrement() {
        fairLock.lock();
        try {
            fairCounter++;
        } finally {
            fairLock.unlock();
        }
    }
    
    /**
     * 使用非公平锁进行计数
     */
    public void unfairIncrement() {
        unfairLock.lock();
        try {
            unfairCounter++;
        } finally {
            unfairLock.unlock();
        }
    }
    
    /**
     * 测试公平锁性能
     */
    public long testFairLock() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        
        // 创建线程
        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> {
                try {
                    startLatch.await(); // 等待统一开始信号
                    
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        fairIncrement();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }, "公平锁线程-" + i).start();
        }
        
        long startTime = System.nanoTime();
        startLatch.countDown(); // 发出开始信号
        endLatch.await(); // 等待所有线程完成
        long endTime = System.nanoTime();
        
        return endTime - startTime;
    }
    
    /**
     * 测试非公平锁性能
     */
    public long testUnfairLock() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        
        // 创建线程
        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> {
                try {
                    startLatch.await(); // 等待统一开始信号
                    
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        unfairIncrement();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }, "非公平锁线程-" + i).start();
        }
        
        long startTime = System.nanoTime();
        startLatch.countDown(); // 发出开始信号
        endLatch.await(); // 等待所有线程完成
        long endTime = System.nanoTime();
        
        return endTime - startTime;
    }
    
    public static void main(String[] args) throws InterruptedException {
        LockPerformanceComparison comparison = new LockPerformanceComparison();
        
        System.out.println("开始性能测试...");
        System.out.println("线程数：" + THREAD_COUNT);
        System.out.println("每线程操作次数：" + OPERATIONS_PER_THREAD);
        System.out.println("总操作次数：" + (THREAD_COUNT * OPERATIONS_PER_THREAD));
        
        // 测试公平锁
        System.out.println("\n=== 公平锁性能测试 ===");
        long fairLockTime = comparison.testFairLock();
        System.out.println("公平锁执行时间：" + (fairLockTime / 1_000_000) + " 毫秒");
        System.out.println("公平锁计数结果：" + comparison.fairCounter);
        
        // 稍等一下，让系统稳定
        Thread.sleep(1000);
        
        // 测试非公平锁
        System.out.println("\n=== 非公平锁性能测试 ===");
        long unfairLockTime = comparison.testUnfairLock();
        System.out.println("非公平锁执行时间：" + (unfairLockTime / 1_000_000) + " 毫秒");
        System.out.println("非公平锁计数结果：" + comparison.unfairCounter);
        
        // 性能对比
        System.out.println("\n=== 性能对比 ===");
        double performanceRatio = (double) fairLockTime / unfairLockTime;
        System.out.printf("公平锁/非公平锁性能比：%.2f\n", performanceRatio);
        if (performanceRatio > 1) {
            System.out.printf("非公平锁比公平锁快 %.1f%%\n", (performanceRatio - 1) * 100);
        } else {
            System.out.printf("公平锁比非公平锁快 %.1f%%\n", (1 / performanceRatio - 1) * 100);
        }
    }
}
```

通过性能测试，我们通常会发现：
- **非公平锁的性能通常更好**，因为减少了线程唤醒的开销
- **公平锁能避免线程饥饿**，但代价是性能下降
- **选择建议**：
    - 默认使用非公平锁，获得更好的性能
    - 只有在需要严格保证公平性的场景才使用公平锁
    - 如果发现某些线程长时间获取不到锁，考虑使用公平锁

## 4 常见问题与解决方案

### 4.1 忘记释放锁导致死锁

这是使用Lock最常见的错误：

```java
package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示忘记释放锁的问题及解决方案
 */
public class LockReleaseDemo {
    
    private final ReentrantLock lock = new ReentrantLock();
    private int count = 0;
    
    /**
     * 错误示例：可能导致锁泄漏
     */
    public void badExample() {
        lock.lock();
        
        try {
            count++;
            
            // 如果这里抛出运行时异常，锁将永远不会被释放
            if (count == 3) {
                throw new RuntimeException("模拟异常");
            }
            
            System.out.println(Thread.currentThread().getName() + " 执行完成，count = " + count);
        } catch (RuntimeException e) {
            System.out.println(Thread.currentThread().getName() + " 发生异常：" + e.getMessage());
            // 注意：这里没有释放锁！
            return;
        }
        
        lock.unlock(); // 这行代码在异常情况下不会执行
    }
    
    /**
     * 正确示例：确保锁总是被释放
     */
    public void goodExample() {
        lock.lock();
        try {
            count++;
            
            // 即使这里抛出异常，finally块也会执行
            if (count == 3) {
                throw new RuntimeException("模拟异常");
            }
            
            System.out.println(Thread.currentThread().getName() + " 执行完成，count = " + count);
        } catch (RuntimeException e) {
            System.out.println(Thread.currentThread().getName() + " 发生异常：" + e.getMessage());
            // 异常处理逻辑
        } finally {
            // 无论是否发生异常，都会释放锁
            lock.unlock();
        }
    }
    
    /**
     * 使用AutoCloseable的更优雅方式
     */
    static class AutoLock implements AutoCloseable {
        private final ReentrantLock lock;
        
        public AutoLock(ReentrantLock lock) {
            this.lock = lock;
            this.lock.lock();
        }
        
        @Override
        public void close() {
            lock.unlock();
        }
    }
    
    /**
     * 使用try-with-resources的方式
     */
    public void elegantExample() {
        try (AutoLock autoLock = new AutoLock(lock)) {
            count++;
            
            if (count == 3) {
                throw new RuntimeException("模拟异常");
            }
            
            System.out.println(Thread.currentThread().getName() + " 执行完成，count = " + count);
        } catch (RuntimeException e) {
            System.out.println(Thread.currentThread().getName() + " 发生异常：" + e.getMessage());
        }
        // AutoLock会自动释放锁
    }
    
    public static void main(String[] args) throws InterruptedException {
        LockReleaseDemo demo = new LockReleaseDemo();
        
        System.out.println("=== 演示错误用法（可能导致死锁） ===");
        
        // 注意：这个例子可能导致死锁，在实际环境中不要这样做
        Thread badThread1 = new Thread(() -> {
            demo.badExample();
        }, "错误线程1");
        
        Thread badThread2 = new Thread(() -> {
            demo.badExample();
        }, "错误线程2");
        
        Thread badThread3 = new Thread(() -> {
            demo.badExample(); // 这个会抛异常但不释放锁
        }, "错误线程3");
        
        badThread1.start();
        badThread2.start();
        badThread3.start();
        
        // 等待一段时间，观察第三个线程后续的线程无法获取锁
        badThread1.join();
        badThread2.join();
        Thread.sleep(2000); // badThread3可能由于锁没释放而卡住
        
        // 重置计数器，演示正确用法
        demo.count = 0;
        
        System.out.println("\n=== 演示正确用法 ===");
        
        Thread goodThread1 = new Thread(() -> {
            demo.goodExample();
        }, "正确线程1");
        
        Thread goodThread2 = new Thread(() -> {
            demo.goodExample();
        }, "正确线程2");
        
        Thread goodThread3 = new Thread(() -> {
            demo.goodExample(); // 这个会抛异常但会释放锁
        }, "正确线程3");
        
        Thread goodThread4 = new Thread(() -> {
            demo.goodExample(); // 这个能正常获取锁
        }, "正确线程4");
        
        goodThread1.start();
        goodThread2.start();
        goodThread3.start();
        goodThread4.start();
        
        goodThread1.join();
        goodThread2.join();
        goodThread3.join();
        goodThread4.join();
        
        // 重置计数器，演示优雅用法
        demo.count = 0;
        
        System.out.println("\n=== 演示优雅用法（try-with-resources） ===");
        
        Thread elegantThread1 = new Thread(() -> {
            demo.elegantExample();
        }, "优雅线程1");
        
        Thread elegantThread2 = new Thread(() -> {
            demo.elegantExample();
        }, "优雅线程2");
        
        Thread elegantThread3 = new Thread(() -> {
            demo.elegantExample(); // 这个会抛异常但会自动释放锁
        }, "优雅线程3");
        
        Thread elegantThread4 = new Thread(() -> {
            demo.elegantExample(); // 这个能正常获取锁
        }, "优雅线程4");
        
        elegantThread1.start();
        elegantThread2.start();
        elegantThread3.start();
        elegantThread4.start();
        
        elegantThread1.join();
        elegantThread2.join();
        elegantThread3.join();
        elegantThread4.join();
    }
}
```

### 4.2 Lock与synchronized的选择策略

```java
package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock与synchronized的选择策略示例
 */
public class LockVsSynchronizedStrategy {
    
    private final ReentrantLock lock = new ReentrantLock();
    private final Object syncLock = new Object();
    private int count = 0;
    
    /**
     * 使用synchronized的简单场景
     */
    public void simpleTaskWithSynchronized() {
        synchronized (syncLock) {
            count++;
            System.out.println(Thread.currentThread().getName() + " synchronized: " + count);
        }
    }
    
    /**
     * 使用Lock的需要高级功能的场景
     */
    public void advancedTaskWithLock() {
        try {
            // 尝试在1秒内获取锁
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    count++;
                    System.out.println(Thread.currentThread().getName() + " Lock: " + count);
                    
                    // 模拟长时间处理
                    Thread.sleep(500);
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println(Thread.currentThread().getName() + " 获取锁超时，执行替代逻辑");
                // 执行不需要锁的替代逻辑
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 被中断");
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 需要可中断的锁获取
     */
    public void interruptibleTask() {
        try {
            lock.lockInterruptibly();
            try {
                count++;
                System.out.println(Thread.currentThread().getName() + " 可中断Lock: " + count);
                
                // 模拟长时间处理
                Thread.sleep(2000);
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 在等待锁时被中断");
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        LockVsSynchronizedStrategy demo = new LockVsSynchronizedStrategy();
        
        System.out.println("=== 选择策略指南 ===");
        System.out.println("使用synchronized的场景：");
        System.out.println("1. 简单的互斥访问");
        System.out.println("2. 代码简洁性更重要");
        System.out.println("3. 不需要特殊的锁功能");
        
        System.out.println("\n使用Lock的场景：");
        System.out.println("1. 需要尝试获取锁（tryLock）");
        System.out.println("2. 需要可中断的锁获取");
        System.out.println("3. 需要公平锁");
        System.out.println("4. 需要超时的锁获取");
        System.out.println("5. 需要条件变量（Condition）");
        
        System.out.println("\n=== 实际演示 ===");
        
        // 演示synchronized的简单使用
        Thread[] syncThreads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            syncThreads[i] = new Thread(() -> {
                demo.simpleTaskWithSynchronized();
            }, "Sync线程-" + (i + 1));
            syncThreads[i].start();
        }
        
        for (Thread thread : syncThreads) {
            thread.join();
        }
        
        // 演示Lock的高级功能
        Thread[] lockThreads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            lockThreads[i] = new Thread(() -> {
                demo.advancedTaskWithLock();
            }, "Lock线程-" + (i + 1));
            lockThreads[i].start();
        }
        
        for (Thread thread : lockThreads) {
            thread.join();
        }
        
        // 演示可中断的锁
        Thread interruptibleThread = new Thread(() -> {
            demo.interruptibleTask();
        }, "可中断线程");
        
        interruptibleThread.start();
        Thread.sleep(1000);
        
        System.out.println("主线程中断可中断线程...");
        interruptibleThread.interrupt();
        interruptibleThread.join();
    }
}
```

### 4.3 避免死锁的最佳实践

```java
package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 避免死锁的最佳实践
 */
public class DeadlockAvoidanceDemo {
    
    private final ReentrantLock lock1 = new ReentrantLock();
    private final ReentrantLock lock2 = new ReentrantLock();
    
    /**
     * 可能导致死锁的方法
     */
    public void potentialDeadlockMethod1() {
        lock1.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 获取lock1");
            
            // 模拟一些处理时间
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            lock2.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 获取lock2");
                // 执行需要两个锁的操作
            } finally {
                lock2.unlock();
            }
        } finally {
            lock1.unlock();
        }
    }
    
    /**
     * 可能导致死锁的方法（不同的锁获取顺序）
     */
    public void potentialDeadlockMethod2() {
        lock2.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 获取lock2");
            
            // 模拟一些处理时间
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            lock1.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 获取lock1");
                // 执行需要两个锁的操作
            } finally {
                lock1.unlock();
            }
        } finally {
            lock2.unlock();
        }
    }
    
    /**
     * 避免死锁的方法1：统一锁的获取顺序
     */
    public void deadlockFreeMethod1() {
        // 始终先获取lock1，再获取lock2
        lock1.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 安全获取lock1");
            
            lock2.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 安全获取lock2");
                // 执行需要两个锁的操作
                
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } finally {
                lock2.unlock();
            }
        } finally {
            lock1.unlock();
        }
    }
    
    /**
     * 避免死锁的方法2：使用tryLock避免无限等待
     */
    public void deadlockFreeMethod2() {
        boolean acquired = false;
        
        try {
            // 尝试获取第一个锁
            if (lock1.tryLock(500, TimeUnit.MILLISECONDS)) {
                try {
                    System.out.println(Thread.currentThread().getName() + " tryLock获取lock1成功");
                    
                    // 尝试获取第二个锁
                    if (lock2.tryLock(500, TimeUnit.MILLISECONDS)) {
                        try {
                            System.out.println(Thread.currentThread().getName() + " tryLock获取lock2成功");
                            acquired = true;
                            
                            // 执行需要两个锁的操作
                            Thread.sleep(50);
                        } finally {
                            lock2.unlock();
                        }
                    } else {
                        System.out.println(Thread.currentThread().getName() + " tryLock获取lock2失败");
                    }
                } finally {
                    lock1.unlock();
                }
            } else {
                System.out.println(Thread.currentThread().getName() + " tryLock获取lock1失败");
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 被中断");
            Thread.currentThread().interrupt();
        }
        
        if (!acquired) {
            System.out.println(Thread.currentThread().getName() + " 未能获取所有锁，执行替代逻辑");
        }
    }
    
    /**
     * 避免死锁的方法3：使用锁排序
     */
    public void deadlockFreeMethod3() {
        // 根据锁的hash值排序，确保获取顺序一致
        ReentrantLock firstLock = System.identityHashCode(lock1) < System.identityHashCode(lock2) ? lock1 : lock2;
        ReentrantLock secondLock = firstLock == lock1 ? lock2 : lock1;
        
        firstLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 按顺序获取第一个锁");
            
            secondLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 按顺序获取第二个锁");
                // 执行需要两个锁的操作
                
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        DeadlockAvoidanceDemo demo = new DeadlockAvoidanceDemo();
        
        System.out.println("=== 演示可能导致死锁的情况（不要在生产环境运行） ===");
        // 注意：这段代码可能导致死锁，仅用于演示
        /*
        Thread deadlockThread1 = new Thread(() -> {
            demo.potentialDeadlockMethod1();
        }, "可能死锁线程1");
        
        Thread deadlockThread2 = new Thread(() -> {
            demo.potentialDeadlockMethod2();
        }, "可能死锁线程2");
        
        deadlockThread1.start();
        deadlockThread2.start();
        */
        
        System.out.println("=== 演示避免死锁的方法1：统一锁顺序 ===");
        
        Thread safeThread1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.deadlockFreeMethod1();
            }
        }, "安全线程1");
        
        Thread safeThread2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.deadlockFreeMethod1();
            }
        }, "安全线程2");
        
        safeThread1.start();
        safeThread2.start();
        safeThread1.join();
        safeThread2.join();
        
        System.out.println("\n=== 演示避免死锁的方法2：使用tryLock ===");
        
        Thread tryLockThread1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.deadlockFreeMethod2();
            }
        }, "tryLock线程1");
        
        Thread tryLockThread2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.deadlockFreeMethod2();
            }
        }, "tryLock线程2");
        
        tryLockThread1.start();
        tryLockThread2.start();
        tryLockThread1.join();
        tryLockThread2.join();
        
        System.out.println("\n=== 演示避免死锁的方法3：锁排序 ===");
        
        Thread sortedThread1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.deadlockFreeMethod3();
            }
        }, "排序线程1");
        
        Thread sortedThread2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.deadlockFreeMethod3();
            }
        }, "排序线程2");
        
        sortedThread1.start();
        sortedThread2.start();
        sortedThread1.join();
        sortedThread2.join();
    }
}
```

## 实战案例：使用ReentrantLock实现读写分离

让我们通过一个实际的案例来展示ReentrantLock的应用。我们将实现一个线程安全的缓存系统，使用ReentrantLock来控制并发访问，并实现读写分离的效果。

```java
package org.devlive.tutorial.multithreading.chapter08;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用ReentrantLock实现线程安全的缓存系统
 * 实战案例：展示如何在实际应用中使用ReentrantLock
 */
public class ThreadSafeCacheWithLock<K, V> {
    
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
     * @param maxSize 缓存的最大大小
     */
    public ThreadSafeCacheWithLock(int maxSize) {
        this.maxSize = maxSize;
    }
    
    /**
     * 向缓存中添加数据
     * @param key 键
     * @param value 值
     */
    public void put(K key, V value) {
        lock.lock();
        try {
            // 如果缓存已满，使用LRU策略移除最旧的元素
            if (cache.size() >= maxSize && !cache.containsKey(key)) {
                evictLRU();
            }
            
            V oldValue = cache.put(key, value);
            if (oldValue == null) {
                System.out.println(Thread.currentThread().getName() + " 添加到缓存：" + key + " -> " + value);
            } else {
                System.out.println(Thread.currentThread().getName() + " 更新缓存：" + key + " -> " + value);
            }
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 从缓存中获取数据
     * @param key 键
     * @return 值，如果不存在返回null
     */
    public V get(K key) {
        lock.lock();
        try {
            V value = cache.get(key);
            if (value != null) {
                hitCount++;
                System.out.println(Thread.currentThread().getName() + " 缓存命中：" + key + " -> " + value);
            } else {
                missCount++;
                System.out.println(Thread.currentThread().getName() + " 缓存未命中：" + key);
            }
            return value;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 从缓存中移除数据
     * @param key 键
     * @return 被移除的值
     */
    public V remove(K key) {
        lock.lock();
        try {
            V value = cache.remove(key);
            if (value != null) {
                System.out.println(Thread.currentThread().getName() + " 从缓存移除：" + key + " -> " + value);
            }
            return value;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 获取缓存大小
     * @return 缓存中元素的数量
     */
    public int size() {
        lock.lock();
        try {
            return cache.size();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 清空缓存
     */
    public void clear() {
        lock.lock();
        try {
            cache.clear();
            System.out.println(Thread.currentThread().getName() + " 清空缓存");
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 安全地获取缓存快照
     * @return 缓存的副本
     */
    public Map<K, V> getSnapshot() {
        lock.lock();
        try {
            return new HashMap<>(cache);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 尝试获取缓存中的数据，如果获取锁失败则返回null
     * @param key 键
     * @return 值，如果不存在或获取锁失败返回null
     */
    public V tryGet(K key) {
        if (lock.tryLock()) {
            try {
                V value = cache.get(key);
                if (value != null) {
                    hitCount++;
                    System.out.println(Thread.currentThread().getName() + " 通过tryLock缓存命中：" + key + " -> " + value);
                } else {
                    missCount++;
                    System.out.println(Thread.currentThread().getName() + " 通过tryLock缓存未命中：" + key);
                }
                return value;
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println(Thread.currentThread().getName() + " tryLock获取锁失败，无法访问缓存：" + key);
            return null;
        }
    }
    
    /**
     * 获取缓存统计信息
     */
    public void printStatistics() {
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
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 显示锁的状态信息
     */
    public void showLockInfo() {
        System.out.println("\n=== 锁状态信息 ===");
        System.out.println("锁是否被当前线程持有：" + lock.isHeldByCurrentThread());
        System.out.println("锁的持有次数：" + lock.getHoldCount());
        System.out.println("等待锁的线程数：" + lock.getQueueLength());
        System.out.println("是否是公平锁：" + lock.isFair());
    }
    
    /**
     * LRU淘汰策略（简化版）
     */
    private void evictLRU() {
        if (!cache.isEmpty()) {
            // 简单实现：移除第一个元素（实际的LRU需要更复杂的数据结构）
            K firstKey = cache.keySet().iterator().next();
            cache.remove(firstKey);
            evictionCount++;
            System.out.println(Thread.currentThread().getName() + " LRU淘汰：" + firstKey);
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
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
                    } catch (InterruptedException e) {
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
                    } catch (InterruptedException e) {
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
                } catch (InterruptedException e) {
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
            } catch (InterruptedException e) {
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
```

这个实战案例展示了ReentrantLock在实际应用中的使用方法，包括：

1. **基本的锁保护**：使用ReentrantLock保护共享的HashMap
2. **状态监控**：利用ReentrantLock提供的方法监控锁的状态
3. **非阻塞访问**：使用tryLock实现非阻塞的缓存访问
4. **统计信息**：在锁保护下维护缓存的统计信息
5. **完整的资源管理**：确保在finally块中释放锁

通过这个例子，我们可以看到ReentrantLock如何为我们提供比synchronized更强大的功能，同时也看到了使用Lock需要更加小心的资源管理。

## 小结

通过本章的学习，我们深入了解了Lock接口和ReentrantLock的使用方法。让我们总结一下关键要点：

**Lock接口的核心优势：**
1. **更灵活的锁控制**：可以尝试获取锁、设置超时时间、响应中断
2. **公平性选择**：可以选择公平锁或非公平锁
3. **可中断性**：等待锁的线程可以被中断
4. **条件变量支持**：可以创建多个条件变量实现复杂的线程协调

**ReentrantLock的关键特性：**
1. **可重入性**：同一线程可以多次获取同一把锁，避免自锁问题
2. **公平性控制**：可以选择公平锁（按顺序获取）或非公平锁（性能更好）
3. **尝试获取**：tryLock()方法可以避免无限等待，提供更好的响应性
4. **可中断性**：lockInterruptibly()方法可以响应中断请求

**使用建议：**
1. **始终在finally块中释放锁**，这是使用Lock最重要的原则
2. **优先选择synchronized**，除非需要Lock的特殊功能
3. **默认使用非公平锁**，只有在需要严格公平性时才使用公平锁
4. **合理使用tryLock()**，可以提高系统的响应性和健壮性

**常见陷阱与避免方法：**
1. **忘记释放锁**：使用try-finally或try-with-resources确保锁的释放
2. **死锁问题**：统一锁的获取顺序，使用tryLock设置超时，或者使用锁排序
3. **性能考虑**：了解公平锁和非公平锁的性能差异，根据需求选择

**Lock vs synchronized的选择策略：**
- **使用synchronized的场景**：简单的互斥访问、代码简洁性更重要、不需要特殊功能
- **使用Lock的场景**：需要尝试获取锁、需要可中断的锁获取、需要公平锁、需要超时机制

**最佳实践模式：**
```java
lock.lock();
try {
    // 访问共享资源
} finally {
    lock.unlock();
}
```

或者使用更优雅的AutoCloseable模式：
```java
try (AutoLock autoLock = new AutoLock(lock)) {
    // 访问共享资源
} // 自动释放锁
```

掌握了Lock接口和ReentrantLock后，你就拥有了更强大的并发控制工具。它们为复杂的并发场景提供了更多的选择和控制能力。在下一章中，我们将学习Condition条件变量，它可以与Lock接口结合使用，实现更精细的线程协调和通信机制，如经典的生产者-消费者模式。

**源代码地址：** https://github.com/qianmoQ/tutorial/tree/main/java-multithreading-tutorial/src/main/java/org/devlive/tutorial/multithreading/chapter08