[TOC]

在上一章中，我们学习了Lock接口和ReentrantLock的使用。虽然Lock为我们提供了比synchronized更灵活的锁机制，但仅仅有锁还不够。在实际的并发编程中，我们经常需要让线程在特定条件下等待，或者在条件满足时通知等待的线程。这就是Condition条件变量的用武之地。本章将带你深入了解Condition的使用方法和实际应用。

## 学习目标

通过本章的学习，你将掌握：
- 理解Condition条件变量的概念和作用机制
- 掌握await()、signal()、signalAll()方法的正确使用
- 学会使用Condition实现经典的生产者-消费者模式
- 了解Condition与Object的wait/notify机制的区别和优势
- 掌握多条件变量的使用场景和最佳实践

## 1 Condition的概念与作用

### 1.1 什么是Condition

Condition（条件变量）是与Lock配合使用的线程协调机制。它提供了类似Object的wait()、notify()和notifyAll()方法的功能，但更加灵活和强大。

Condition的核心概念：
- **条件等待**：线程可以在某个条件不满足时进入等待状态
- **条件通知**：当条件满足时，可以通知一个或多个等待的线程
- **与锁绑定**：每个Condition都与一个Lock相关联

让我们先看一个简单的例子来理解Condition的基本概念：

```java
package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition基本概念演示
 */
public class ConditionBasicDemo {
    
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean ready = false;
    
    /**
     * 等待条件满足的方法
     */
    public void waitForCondition() {
        lock.lock();
        try {
            // 当条件不满足时，线程进入等待状态
            while (!ready) {
                System.out.println(Thread.currentThread().getName() + " 条件未满足，开始等待...");
                condition.await(); // 等待条件变量
                System.out.println(Thread.currentThread().getName() + " 被唤醒，重新检查条件");
            }
            
            // 条件满足，执行相应的操作
            System.out.println(Thread.currentThread().getName() + " 条件已满足，开始执行任务");
            
            // 模拟任务执行
            Thread.sleep(100);
            System.out.println(Thread.currentThread().getName() + " 任务执行完毕");
            
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 在等待时被中断");
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 设置条件并通知等待的线程
     */
    public void setConditionAndNotify() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 准备设置条件");
            
            // 模拟准备工作
            Thread.sleep(2000);
            
            // 设置条件为满足
            ready = true;
            System.out.println(Thread.currentThread().getName() + " 条件已设置，通知等待的线程");
            
            // 通知一个等待的线程
            condition.signal();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ConditionBasicDemo demo = new ConditionBasicDemo();
        
        // 创建等待线程
        Thread waiter1 = new Thread(() -> {
            demo.waitForCondition();
        }, "等待线程1");
        
        Thread waiter2 = new Thread(() -> {
            demo.waitForCondition();
        }, "等待线程2");
        
        // 创建通知线程
        Thread notifier = new Thread(() -> {
            demo.setConditionAndNotify();
        }, "通知线程");
        
        // 启动等待线程
        waiter1.start();
        waiter2.start();
        
        // 稍等一下，确保等待线程已经开始等待
        Thread.sleep(500);
        
        // 启动通知线程
        notifier.start();
        
        // 等待所有线程完成
        waiter1.join();
        waiter2.join();
        notifier.join();
        
        System.out.println("所有线程执行完毕");
    }
}
```

### 1.2 Condition与Object.wait/notify的对比

在学习Condition之前，我们可能已经熟悉了Object类的wait()和notify()方法。让我们对比一下它们的差异：

```java
package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition与Object.wait/notify的对比演示
 */
public class ConditionVsWaitNotifyDemo {
    
    // 使用Object的wait/notify机制
    private final Object monitor = new Object();
    private boolean objectCondition = false;
    
    // 使用Condition机制
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean conditionFlag = false;
    
    /**
     * 使用Object.wait()的等待方法
     */
    public void waitWithObjectMonitor() {
        synchronized (monitor) {
            try {
                while (!objectCondition) {
                    System.out.println(Thread.currentThread().getName() + " 使用Object.wait()等待");
                    monitor.wait();
                }
                System.out.println(Thread.currentThread().getName() + " Object.wait()被唤醒，继续执行");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * 使用Object.notify()的通知方法
     */
    public void notifyWithObjectMonitor() {
        synchronized (monitor) {
            try {
                Thread.sleep(1000);
                objectCondition = true;
                System.out.println(Thread.currentThread().getName() + " 使用Object.notify()通知");
                monitor.notify();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * 使用Condition.await()的等待方法
     */
    public void waitWithCondition() {
        lock.lock();
        try {
            while (!conditionFlag) {
                System.out.println(Thread.currentThread().getName() + " 使用Condition.await()等待");
                condition.await();
            }
            System.out.println(Thread.currentThread().getName() + " Condition.await()被唤醒，继续执行");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 使用Condition.signal()的通知方法
     */
    public void notifyWithCondition() {
        lock.lock();
        try {
            Thread.sleep(1000);
            conditionFlag = true;
            System.out.println(Thread.currentThread().getName() + " 使用Condition.signal()通知");
            condition.signal();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ConditionVsWaitNotifyDemo demo = new ConditionVsWaitNotifyDemo();
        
        System.out.println("=== Object.wait/notify机制演示 ===");
        
        Thread objectWaiter = new Thread(() -> {
            demo.waitWithObjectMonitor();
        }, "Object等待线程");
        
        Thread objectNotifier = new Thread(() -> {
            demo.notifyWithObjectMonitor();
        }, "Object通知线程");
        
        objectWaiter.start();
        Thread.sleep(100);
        objectNotifier.start();
        
        objectWaiter.join();
        objectNotifier.join();
        
        System.out.println("\n=== Condition机制演示 ===");
        
        Thread conditionWaiter = new Thread(() -> {
            demo.waitWithCondition();
        }, "Condition等待线程");
        
        Thread conditionNotifier = new Thread(() -> {
            demo.notifyWithCondition();
        }, "Condition通知线程");
        
        conditionWaiter.start();
        Thread.sleep(100);
        conditionNotifier.start();
        
        conditionWaiter.join();
        conditionNotifier.join();
    }
}
```

**Condition相比Object.wait/notify的优势：**

1. **多个条件变量**：一个Lock可以创建多个Condition，而synchronized只能有一个等待集合
2. **更精确的通知**：可以选择通知特定条件上等待的线程
3. **更好的异常处理**：await()方法可以抛出InterruptedException
4. **更灵活的超时机制**：提供了带超时的await方法
5. **与Lock的一致性**：与Lock接口配合使用，保持API的一致性

> 💡 **设计思想**  
> Condition的设计遵循了"关注点分离"的原则。锁负责互斥访问，条件变量负责线程协调。这种分离使得代码更加清晰，功能更加专一。

## 2 await()、signal()、signalAll()方法使用

### 2.1 await()方法详解

await()方法是Condition接口的核心方法，它有多个重载版本：

```java
package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * await()方法的各种使用方式
 */
public class AwaitMethodDemo {
    
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean ready = false;
    
    /**
     * 基本的await()方法使用
     */
    public void basicAwait() {
        lock.lock();
        try {
            while (!ready) {
                System.out.println(Thread.currentThread().getName() + " 使用基本await()等待");
                condition.await(); // 无限期等待
            }
            System.out.println(Thread.currentThread().getName() + " 基本await()被唤醒");
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 基本await()被中断");
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 带超时的await()方法使用
     */
    public void awaitWithTimeout() {
        lock.lock();
        try {
            while (!ready) {
                System.out.println(Thread.currentThread().getName() + " 使用带超时的await()等待，最多等待2秒");
                boolean signaled = condition.await(2, TimeUnit.SECONDS);
                
                if (signaled) {
                    System.out.println(Thread.currentThread().getName() + " 带超时的await()被信号唤醒");
                } else {
                    System.out.println(Thread.currentThread().getName() + " 带超时的await()超时返回");
                    return; // 超时退出
                }
            }
            System.out.println(Thread.currentThread().getName() + " 带超时的await()条件满足");
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 带超时的await()被中断");
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 不可中断的await()方法使用
     */
    public void awaitUninterruptibly() {
        lock.lock();
        try {
            while (!ready) {
                System.out.println(Thread.currentThread().getName() + " 使用不可中断的awaitUninterruptibly()等待");
                condition.awaitUninterruptibly(); // 不会抛出InterruptedException
            }
            System.out.println(Thread.currentThread().getName() + " 不可中断的awaitUninterruptibly()被唤醒");
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 等到指定时间的await()方法使用
     */
    public void awaitUntil() {
        lock.lock();
        try {
            // 设置等待到的具体时间点（当前时间后3秒）
            long deadlineTime = System.currentTimeMillis() + 3000;
            java.util.Date deadline = new java.util.Date(deadlineTime);
            
            while (!ready) {
                System.out.println(Thread.currentThread().getName() + " 使用awaitUntil()等待到: " + deadline);
                boolean signaled = condition.awaitUntil(deadline);
                
                if (signaled) {
                    System.out.println(Thread.currentThread().getName() + " awaitUntil()被信号唤醒");
                } else {
                    System.out.println(Thread.currentThread().getName() + " awaitUntil()到达指定时间点");
                    return; // 到达时间点退出
                }
            }
            System.out.println(Thread.currentThread().getName() + " awaitUntil()条件满足");
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " awaitUntil()被中断");
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 设置条件并通知
     */
    public void setReadyAndNotify() {
        lock.lock();
        try {
            Thread.sleep(4000); // 等待4秒后才设置条件
            ready = true;
            System.out.println(Thread.currentThread().getName() + " 条件已设置为ready，发出通知");
            condition.signalAll(); // 通知所有等待的线程
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        AwaitMethodDemo demo = new AwaitMethodDemo();
        
        // 创建使用不同await方法的线程
        Thread basicThread = new Thread(() -> {
            demo.basicAwait();
        }, "基本等待线程");
        
        Thread timeoutThread = new Thread(() -> {
            demo.awaitWithTimeout();
        }, "超时等待线程");
        
        Thread uninterruptibleThread = new Thread(() -> {
            demo.awaitUninterruptibly();
        }, "不可中断等待线程");
        
        Thread untilThread = new Thread(() -> {
            demo.awaitUntil();
        }, "定时等待线程");
        
        Thread notifierThread = new Thread(() -> {
            demo.setReadyAndNotify();
        }, "通知线程");
        
        // 启动所有等待线程
        basicThread.start();
        timeoutThread.start();
        uninterruptibleThread.start();
        untilThread.start();
        
        Thread.sleep(500); // 确保等待线程都开始等待
        
        // 测试中断功能
        System.out.println("主线程尝试中断不可中断等待线程（应该无效）");
        uninterruptibleThread.interrupt();
        
        Thread.sleep(500);
        
        // 启动通知线程
        notifierThread.start();
        
        // 等待所有线程完成
        basicThread.join();
        timeoutThread.join();
        uninterruptibleThread.join();
        untilThread.join();
        notifierThread.join();
        
        System.out.println("所有线程执行完毕");
    }
}
```

### 2.2 signal()和signalAll()方法详解

signal()和signalAll()方法用于唤醒等待在条件变量上的线程：

```java
package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * signal()和signalAll()方法的区别演示
 */
public class SignalMethodDemo {
    
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private int value = 0;
    
    /**
     * 等待值变为正数的方法
     */
    public void waitForPositive() {
        lock.lock();
        try {
            while (value <= 0) {
                System.out.println(Thread.currentThread().getName() + " 等待值变为正数，当前值: " + value);
                condition.await();
            }
            System.out.println(Thread.currentThread().getName() + " 检测到正数值: " + value + "，开始处理");
            
            // 模拟处理时间
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + " 处理完毕");
            
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 被中断");
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 使用signal()通知一个等待的线程
     */
    public void incrementAndSignal() {
        lock.lock();
        try {
            value++;
            System.out.println(Thread.currentThread().getName() + " 将值增加到: " + value + "，使用signal()通知一个线程");
            condition.signal(); // 只通知一个等待的线程
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 使用signalAll()通知所有等待的线程
     */
    public void incrementAndSignalAll() {
        lock.lock();
        try {
            value++;
            System.out.println(Thread.currentThread().getName() + " 将值增加到: " + value + "，使用signalAll()通知所有线程");
            condition.signalAll(); // 通知所有等待的线程
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 重置值为0
     */
    public void resetValue() {
        lock.lock();
        try {
            value = 0;
            System.out.println(Thread.currentThread().getName() + " 重置值为: " + value);
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        SignalMethodDemo demo = new SignalMethodDemo();
        
        System.out.println("=== 演示signal()方法 - 只唤醒一个线程 ===");
        
        // 创建多个等待线程
        Thread[] waiters = new Thread[3];
        for (int i = 0; i < 3; i++) {
            waiters[i] = new Thread(() -> {
                demo.waitForPositive();
            }, "等待线程-" + (i + 1));
            waiters[i].start();
        }
        
        Thread.sleep(1000); // 确保所有等待线程都开始等待
        
        // 使用signal()通知
        Thread signalThread = new Thread(() -> {
            demo.incrementAndSignal();
        }, "Signal通知线程");
        signalThread.start();
        signalThread.join();
        
        // 等待被唤醒的线程完成
        Thread.sleep(2000);
        
        // 重置并演示signalAll()
        demo.resetValue();
        
        System.out.println("\n=== 演示signalAll()方法 - 唤醒所有线程 ===");
        
        // 创建新的等待线程
        Thread[] waiters2 = new Thread[3];
        for (int i = 0; i < 3; i++) {
            waiters2[i] = new Thread(() -> {
                demo.waitForPositive();
            }, "等待线程2-" + (i + 1));
            waiters2[i].start();
        }
        
        Thread.sleep(1000); // 确保所有等待线程都开始等待
        
        // 使用signalAll()通知
        Thread signalAllThread = new Thread(() -> {
            demo.incrementAndSignalAll();
        }, "SignalAll通知线程");
        signalAllThread.start();
        signalAllThread.join();
        
        // 等待所有线程完成
        for (Thread waiter : waiters2) {
            waiter.join();
        }
        
        // 清理剩余的等待线程（如果有的话）
        for (Thread waiter : waiters) {
            if (waiter.isAlive()) {
                waiter.interrupt();
                waiter.join();
            }
        }
        
        System.out.println("所有线程执行完毕");
    }
}
```

**signal()与signalAll()的选择原则：**

1. **使用signal()的场景**：
    - 只需要唤醒一个线程就足够
    - 所有等待的线程执行相同的任务
    - 条件满足后只允许一个线程继续执行

2. **使用signalAll()的场景**：
    - 条件的改变可能影响所有等待的线程
    - 不确定哪个线程应该被唤醒
    - 需要让所有线程重新检查条件

> ⚠️ **注意事项**
> 1. 调用signal()或signalAll()之前必须获得对应的锁
> 2. 被唤醒的线程需要重新获得锁才能继续执行
> 3. 使用while循环而不是if来检查条件，防止虚假唤醒

### 2.3 多条件变量的使用

一个Lock可以创建多个Condition，这允许我们对不同的条件进行精确控制：

```java
package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多条件变量使用示例
 * 模拟一个资源池，支持不同类型的等待条件
 */
public class MultipleConditionsDemo {
    
    private final ReentrantLock lock = new ReentrantLock();
    
    // 不同的条件变量
    private final Condition notEmpty = lock.newCondition();  // 非空条件
    private final Condition notFull = lock.newCondition();   // 非满条件
    private final Condition canRead = lock.newCondition();   // 可读条件
    private final Condition canWrite = lock.newCondition();  // 可写条件
    
    private final int[] buffer;
    private final int capacity;
    private int count = 0;      // 当前元素数量
    private int putIndex = 0;   // 写入位置
    private int takeIndex = 0;  // 读取位置
    
    private boolean readMode = true;   // 读模式标志
    private boolean writeMode = true;  // 写模式标志
    
    public MultipleConditionsDemo(int capacity) {
        this.capacity = capacity;
        this.buffer = new int[capacity];
    }
    
    /**
     * 生产者方法 - 等待非满条件和写模式条件
     */
    public void put(int value) throws InterruptedException {
        lock.lock();
        try {
            // 等待缓冲区不满且允许写入
            while (count == capacity || !writeMode) {
                if (count == capacity) {
                    System.out.println(Thread.currentThread().getName() + " 缓冲区已满，等待非满条件");
                    notFull.await();
                } else {
                    System.out.println(Thread.currentThread().getName() + " 写模式关闭，等待写权限");
                    canWrite.await();
                }
            }
            
            // 执行写入操作
            buffer[putIndex] = value;
            putIndex = (putIndex + 1) % capacity;
            count++;
            
            System.out.println(Thread.currentThread().getName() + " 生产了: " + value + 
                    ", 当前缓冲区大小: " + count);
            
            // 通知非空条件的等待者
            notEmpty.signal();
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 消费者方法 - 等待非空条件和读模式条件
     */
    public int take() throws InterruptedException {
        lock.lock();
        try {
            // 等待缓冲区非空且允许读取
            while (count == 0 || !readMode) {
                if (count == 0) {
                    System.out.println(Thread.currentThread().getName() + " 缓冲区为空，等待非空条件");
                    notEmpty.await();
                } else {
                    System.out.println(Thread.currentThread().getName() + " 读模式关闭，等待读权限");
                    canRead.await();
                }
            }
            
            // 执行读取操作
            int value = buffer[takeIndex];
            takeIndex = (takeIndex + 1) % capacity;
            count--;
            
            System.out.println(Thread.currentThread().getName() + " 消费了: " + value + 
                    ", 当前缓冲区大小: " + count);
            
            // 通知非满条件的等待者
            notFull.signal();
            
            return value;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 关闭读模式
     */
    public void disableReadMode() {
        lock.lock();
        try {
            readMode = false;
            System.out.println(Thread.currentThread().getName() + " 关闭读模式");
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 开启读模式
     */
    public void enableReadMode() {
        lock.lock();
        try {
            readMode = true;
            System.out.println(Thread.currentThread().getName() + " 开启读模式");
            canRead.signalAll(); // 通知所有等待读权限的线程
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 关闭写模式
     */
    public void disableWriteMode() {
        lock.lock();
        try {
            writeMode = false;
            System.out.println(Thread.currentThread().getName() + " 关闭写模式");
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 开启写模式
     */
    public void enableWriteMode() {
        lock.lock();
        try {
            writeMode = true;
            System.out.println(Thread.currentThread().getName() + " 开启写模式");
            canWrite.signalAll(); // 通知所有等待写权限的线程
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 获取当前状态
     */
    public void printStatus() {
        lock.lock();
        try {
            System.out.println("缓冲区状态 - 大小: " + count + "/" + capacity + 
                    ", 读模式: " + readMode + ", 写模式: " + writeMode);
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        MultipleConditionsDemo demo = new MultipleConditionsDemo(3);
        
        // 创建生产者线程
        Thread[] producers = new Thread[2];
        for (int i = 0; i < 2; i++) {
            final int producerId = i;
            producers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 3; j++) {
                        demo.put(producerId * 10 + j);
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "生产者-" + (i + 1));
        }
        
        // 创建消费者线程
        Thread[] consumers = new Thread[2];
        for (int i = 0; i < 2; i++) {
            consumers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 3; j++) {
                        demo.take();
                        Thread.sleep(700);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "消费者-" + (i + 1));
        }
        
        // 控制线程
        Thread controller = new Thread(() -> {
            try {
                Thread.sleep(2000);
                demo.printStatus();
            
                // 暂时关闭读模式
                demo.disableReadMode();
                Thread.sleep(1000);
                demo.printStatus();
                
                // 重新开启读模式
                demo.enableReadMode();
                Thread.sleep(1000);
                
                // 暂时关闭写模式
                demo.disableWriteMode();
                Thread.sleep(1000);
                demo.printStatus();
                
                // 重新开启写模式
                demo.enableWriteMode();
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "控制线程");
        
        // 启动所有线程
        for (Thread producer : producers) {
            producer.start();
        }
        
        for (Thread consumer : consumers) {
            consumer.start();
        }
        
        controller.start();
        
        // 等待所有线程完成
        for (Thread producer : producers) {
            producer.join();
        }
        
        for (Thread consumer : consumers) {
            consumer.join();
        }
        
        controller.join();
        
        demo.printStatus();
        System.out.println("所有线程执行完毕");
    }
}
```

这个例子展示了多条件变量的强大功能：
- **notEmpty/notFull**：控制缓冲区的空满状态
- **canRead/canWrite**：控制读写权限
- **精确通知**：只唤醒需要特定条件的线程
- **灵活控制**：可以独立控制不同的条件状态

## 3 实现生产者-消费者模式

生产者-消费者模式是并发编程中的经典模式，Condition为我们提供了优雅的实现方式：

```java
package org.devlive.tutorial.multithreading.chapter09;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用Condition实现生产者-消费者模式
 * 支持多生产者、多消费者的场景
 */
public class ProducerConsumerWithCondition<T> {
    
    private final T[] buffer;
    private final int capacity;
    private int count = 0;      // 当前元素数量
    private int putIndex = 0;   // 生产者索引
    private int takeIndex = 0;  // 消费者索引
    
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition(); // 缓冲区非空条件
    private final Condition notFull = lock.newCondition();  // 缓冲区非满条件
    
    // 统计信息
    private volatile int totalProduced = 0;
    private volatile int totalConsumed = 0;
    
    @SuppressWarnings("unchecked")
    public ProducerConsumerWithCondition(int capacity) {
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
    }
    
    /**
     * 生产者方法：向缓冲区添加元素
     */
    public void produce(T item) throws InterruptedException {
        lock.lock();
        try {
            // 当缓冲区满时，生产者等待
            while (count == capacity) {
                System.out.println(Thread.currentThread().getName() + " 缓冲区已满，生产者等待...");
                notFull.await();
            }
            
            // 添加元素到缓冲区
            buffer[putIndex] = item;
            putIndex = (putIndex + 1) % capacity;
            count++;
            totalProduced++;
            
            System.out.println(Thread.currentThread().getName() + " 生产了: " + item + 
                    " [缓冲区: " + count + "/" + capacity + "]");
            
            // 通知消费者：缓冲区不再为空
            notEmpty.signal();
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 消费者方法：从缓冲区取出元素
     */
    public T consume() throws InterruptedException {
        lock.lock();
        try {
            // 当缓冲区空时，消费者等待
            while (count == 0) {
                System.out.println(Thread.currentThread().getName() + " 缓冲区为空，消费者等待...");
                notEmpty.await();
            }
            
            // 从缓冲区取出元素
            T item = buffer[takeIndex];
            buffer[takeIndex] = null; // 帮助GC
            takeIndex = (takeIndex + 1) % capacity;
            count--;
            totalConsumed++;
            
            System.out.println(Thread.currentThread().getName() + " 消费了: " + item + 
                    " [缓冲区: " + count + "/" + capacity + "]");
            
            // 通知生产者：缓冲区不再满
            notFull.signal();
            
            return item;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 尝试生产（非阻塞）
     */
    public boolean tryProduce(T item) {
        if (lock.tryLock()) {
            try {
                if (count < capacity) {
                    buffer[putIndex] = item;
                    putIndex = (putIndex + 1) % capacity;
                    count++;
                    totalProduced++;
                    
                    System.out.println(Thread.currentThread().getName() + " 非阻塞生产了: " + item + 
                            " [缓冲区: " + count + "/" + capacity + "]");
                    
                    notEmpty.signal();
                    return true;
                } else {
                    System.out.println(Thread.currentThread().getName() + " 缓冲区满，非阻塞生产失败");
                    return false;
                }
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println(Thread.currentThread().getName() + " 获取锁失败，非阻塞生产失败");
            return false;
        }
    }
    
    /**
     * 尝试消费（非阻塞）
     */
    public T tryConsume() {
        if (lock.tryLock()) {
            try {
                if (count > 0) {
                    T item = buffer[takeIndex];
                    buffer[takeIndex] = null;
                    takeIndex = (takeIndex + 1) % capacity;
                    count--;
                    totalConsumed++;
                    
                    System.out.println(Thread.currentThread().getName() + " 非阻塞消费了: " + item + 
                            " [缓冲区: " + count + "/" + capacity + "]");
                    
                    notFull.signal();
                    return item;
                } else {
                    System.out.println(Thread.currentThread().getName() + " 缓冲区空，非阻塞消费失败");
                    return null;
                }
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println(Thread.currentThread().getName() + " 获取锁失败，非阻塞消费失败");
            return null;
        }
    }
    
    /**
     * 获取当前状态
     */
    public void printStatus() {
        lock.lock();
        try {
            System.out.println("=== 缓冲区状态 ===");
            System.out.println("容量: " + capacity);
            System.out.println("当前大小: " + count);
            System.out.println("总生产数量: " + totalProduced);
            System.out.println("总消费数量: " + totalConsumed);
            System.out.println("等待非满条件的线程数: " + lock.getWaitQueueLength(notFull));
            System.out.println("等待非空条件的线程数: " + lock.getWaitQueueLength(notEmpty));
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 关闭生产者消费者系统，唤醒所有等待的线程
     */
    public void shutdown() {
        lock.lock();
        try {
            System.out.println("系统关闭，唤醒所有等待的线程");
            notFull.signalAll();
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ProducerConsumerWithCondition<String> system = new ProducerConsumerWithCondition<>(5);
        Random random = new Random();
        
        // 创建生产者线程
        Thread[] producers = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int producerId = i;
            producers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 4; j++) {
                        String product = "产品-" + producerId + "-" + j;
                        
                        if (random.nextBoolean()) {
                            // 50% 概率使用阻塞生产
                            system.produce(product);
                        } else {
                            // 50% 概率使用非阻塞生产
                            if (!system.tryProduce(product)) {
                                // 非阻塞失败，改用阻塞方式
                                system.produce(product);
                            }
                        }
                        
                        Thread.sleep(random.nextInt(500) + 200); // 200-700ms
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " 生产者被中断");
                    Thread.currentThread().interrupt();
                }
            }, "生产者-" + (i + 1));
        }
        
        // 创建消费者线程
        Thread[] consumers = new Thread[2];
        for (int i = 0; i < 2; i++) {
            consumers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 6; j++) {
                        String product;
                        
                        if (random.nextBoolean()) {
                            // 50% 概率使用阻塞消费
                            product = system.consume();
                        } else {
                            // 50% 概率使用非阻塞消费
                            product = system.tryConsume();
                            if (product == null) {
                                // 非阻塞失败，改用阻塞方式
                                product = system.consume();
                            }
                        }
                        
                        // 模拟消费处理时间
                        Thread.sleep(random.nextInt(400) + 300); // 300-700ms
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " 消费者被中断");
                    Thread.currentThread().interrupt();
                }
            }, "消费者-" + (i + 1));
        }
        
        // 监控线程
        Thread monitor = new Thread(() -> {
            try {
                for (int i = 0; i < 6; i++) {
                    Thread.sleep(2000);
                    system.printStatus();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "监控线程");
        
        // 启动所有线程
        System.out.println("启动生产者-消费者系统...");
        
        for (Thread producer : producers) {
            producer.start();
        }
        
        for (Thread consumer : consumers) {
            consumer.start();
        }
        
        monitor.start();
        
        // 等待所有生产者完成
        for (Thread producer : producers) {
            producer.join();
        }
        
        System.out.println("所有生产者已完成");
        
        // 等待所有消费者完成
        for (Thread consumer : consumers) {
            consumer.join();
        }
        
        System.out.println("所有消费者已完成");
        
        monitor.interrupt();
        monitor.join();
        
        // 显示最终统计
        system.printStatus();
        system.shutdown();
        
        System.out.println("生产者-消费者系统已关闭");
    }
}
```

这个生产者-消费者实现展示了Condition的几个重要特性：

1. **精确的条件控制**：分别使用notEmpty和notFull条件变量
2. **避免虚假唤醒**：使用while循环检查条件
3. **灵活的操作模式**：同时支持阻塞和非阻塞操作
4. **完善的状态监控**：提供详细的系统状态信息
5. **优雅的关闭机制**：通过signalAll()唤醒所有等待的线程

## 4 常见问题与解决方案

### 4.1 虚假唤醒问题

虚假唤醒是指线程在没有收到signal信号的情况下从await状态返回。虽然这种情况很少发生，但我们必须正确处理：

```java
package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示虚假唤醒问题及解决方案
 */
public class SpuriousWakeupDemo {
    
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean ready = false;
    private int waitCount = 0;
    
    /**
     * 错误的做法：使用if检查条件（可能受虚假唤醒影响）
     */
    public void wrongWayToWait() {
        lock.lock();
        try {
            waitCount++;
            System.out.println(Thread.currentThread().getName() + " 开始等待（错误方式 - 使用if）");
            
            if (!ready) {  // 错误：使用if而不是while
                condition.await();
            }
            
            // 这里可能在条件未满足时执行
            System.out.println(Thread.currentThread().getName() + " 停止等待，ready状态: " + ready);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 正确的做法：使用while循环检查条件（防止虚假唤醒）
     */
    public void correctWayToWait() {
        lock.lock();
        try {
            waitCount++;
            System.out.println(Thread.currentThread().getName() + " 开始等待（正确方式 - 使用while）");
            
            while (!ready) {  // 正确：使用while循环
                condition.await();
                System.out.println(Thread.currentThread().getName() + " 被唤醒，重新检查条件，ready: " + ready);
            }
            
            // 只有在条件满足时才会执行到这里
            System.out.println(Thread.currentThread().getName() + " 条件满足，继续执行");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 设置条件并通知
     */
    public void notifyWaiters() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 当前等待线程数: " + waitCount);
            
            // 模拟一些准备工作
            Thread.sleep(1000);
            
            ready = true;
            System.out.println(Thread.currentThread().getName() + " 设置ready为true，发出通知");
            condition.signalAll();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 模拟虚假唤醒的场景
     */
    public void simulateSpuriousWakeup() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 模拟虚假唤醒 - 在条件未满足时发出信号");
            condition.signalAll(); // 在ready仍为false时发出信号，模拟虚假唤醒
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        SpuriousWakeupDemo demo = new SpuriousWakeupDemo();
        
        System.out.println("=== 演示错误的等待方式（可能受虚假唤醒影响） ===");
        
        // 创建使用错误方式等待的线程
        Thread wrongWaiter = new Thread(() -> {
            demo.wrongWayToWait();
        }, "错误等待线程");
        
        wrongWaiter.start();
        Thread.sleep(500);
        
        // 模拟虚假唤醒
        Thread spuriousWaker = new Thread(() -> {
            demo.simulateSpuriousWakeup();
        }, "虚假唤醒线程");
        
        spuriousWaker.start();
        spuriousWaker.join();
        wrongWaiter.join();
        
        // 重置状态
        demo.ready = false;
        demo.waitCount = 0;
        
        System.out.println("\n=== 演示正确的等待方式（防止虚假唤醒） ===");
        
        // 创建使用正确方式等待的线程
        Thread correctWaiter = new Thread(() -> {
            demo.correctWayToWait();
        }, "正确等待线程");
        
        correctWaiter.start();
        Thread.sleep(500);
        
        // 再次模拟虚假唤醒
        Thread spuriousWaker2 = new Thread(() -> {
            demo.simulateSpuriousWakeup();
        }, "虚假唤醒线程2");
        
        spuriousWaker2.start();
        spuriousWaker2.join();
        
        // 等待一下，然后发出真正的通知
        Thread.sleep(1000);
        
        Thread realNotifier = new Thread(() -> {
            demo.notifyWaiters();
        }, "真正通知线程");
        
        realNotifier.start();
        realNotifier.join();
        correctWaiter.join();
        
        System.out.println("演示完成");
    }
}
```

**防止虚假唤醒的最佳实践：**

1. **使用while循环**：始终在while循环中检查条件，而不是if语句
2. **重新检查条件**：被唤醒后立即重新检查等待条件
3. **正确的模式**：
   ```java
   while (!condition) {
       conditionVariable.await();
   }
   ```

### 4.2 死锁和条件变量

条件变量的不当使用也可能导致死锁：

```java
package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示条件变量使用中的死锁问题及解决方案
 */
public class ConditionDeadlockDemo {
    
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean flag = false;
    
    /**
     * 可能导致死锁的错误实现
     */
    public void potentialDeadlockWait() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 开始等待");
            
            // 错误：没有检查条件就直接等待
            condition.await();
            
            System.out.println(Thread.currentThread().getName() + " 等待结束");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 正确的等待实现
     */
    public void correctWait() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 开始等待，flag: " + flag);
            
            // 正确：在循环中检查条件
            while (!flag) {
                condition.await();
            }
            
            System.out.println(Thread.currentThread().getName() + " 等待结束，flag: " + flag);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 忘记在持有锁的情况下调用signal的错误做法
     */
    public void wrongNotify() {
        // 错误：没有获取锁就调用signal
        try {
            System.out.println(Thread.currentThread().getName() + " 尝试在没有锁的情况下通知");
            condition.signal(); // 这会抛出IllegalMonitorStateException
        } catch (IllegalMonitorStateException e) {
            System.out.println(Thread.currentThread().getName() + " 错误：" + e.getMessage());
        }
    }
    
    /**
     * 正确的通知实现
     */
    public void correctNotify() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 设置flag并通知");
            flag = true;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 演示条件变量的嵌套等待可能导致的问题
     */
    public void nestedWait() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 外层等待开始");
            
            // 模拟一个复杂的等待逻辑
            while (!flag) {
                condition.await();
                
                // 错误的做法：在等待中调用可能再次等待的方法
                // innerWait(); // 这可能导致复杂的同步问题
            }
            
            System.out.println(Thread.currentThread().getName() + " 外层等待结束");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ConditionDeadlockDemo demo = new ConditionDeadlockDemo();
        
        System.out.println("=== 演示错误的signal调用 ===");
        
        Thread wrongNotifier = new Thread(() -> {
            demo.wrongNotify();
        }, "错误通知线程");
        
        wrongNotifier.start();
        wrongNotifier.join();
        
        System.out.println("\n=== 演示正确的等待和通知 ===");
        
        Thread correctWaiter = new Thread(() -> {
            demo.correctWait();
        }, "正确等待线程");
        
        correctWaiter.start();
        Thread.sleep(1000);
        
        Thread correctNotifier = new Thread(() -> {
            demo.correctNotify();
        }, "正确通知线程");
        
        correctNotifier.start();
        
        correctWaiter.join();
        correctNotifier.join();
        
        System.out.println("演示完成");
    }
}
```

**避免条件变量死锁的建议：**

1. **必须持有锁**：调用await()、signal()、signalAll()前必须持有对应的锁
2. **避免嵌套等待**：不要在条件等待中调用可能再次等待的方法
3. **合理的锁粒度**：避免在持有条件变量锁时调用其他可能阻塞的方法
4. **正确的异常处理**：确保在异常情况下也能正确释放锁

### 4.3 性能优化考虑

在使用条件变量时，也需要考虑性能问题：

```java
package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 条件变量的性能优化示例
 */
public class ConditionPerformanceDemo {
    
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private volatile boolean ready = false;
    
    // 性能统计
    private volatile long signalCount = 0;
    private volatile long awaitCount = 0;
    
    /**
     * 优化的等待方法：减少不必要的锁获取
     */
    public void optimizedWait() {
        // 先进行无锁检查，减少锁竞争
        if (ready) {
            return;
        }
        
        lock.lock();
        try {
            // 双重检查模式
            while (!ready) {
                awaitCount++;
                condition.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 批量通知优化：减少signal调用次数
     */
    public void batchNotify(int count) {
        lock.lock();
        try {
            for (int i = 0; i < count; i++) {
                // 执行一些准备工作
                processItem(i);
            }
            
            // 批量设置条件并一次性通知
            ready = true;
            signalCount++;
            condition.signalAll(); // 一次通知所有等待者
            
        } finally {
            lock.unlock();
        }
    }
    
    private void processItem(int item) {
        // 模拟处理工作
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 条件检查优化：使用更精确的条件
     */
    public void precisConditionWait(int expectedValue) {
        lock.lock();
        try {
            // 使用更精确的条件，减少虚假唤醒的影响
            while (getCurrentValue() != expectedValue) {
                condition.await();
            }
            
            System.out.println(Thread.currentThread().getName() + 
                    " 精确条件满足，值为: " + expectedValue);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    
    private int getCurrentValue() {
        // 模拟获取当前值
        return ready ? 100 : 0;
    }
    
    /**
     * 获取性能统计信息
     */
    public void printPerformanceStats() {
        lock.lock();
        try {
            System.out.println("=== 性能统计 ===");
            System.out.println("信号发送次数: " + signalCount);
            System.out.println("等待次数: " + awaitCount);
            System.out.println("等待队列长度: " + lock.getWaitQueueLength(condition));
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ConditionPerformanceDemo demo = new ConditionPerformanceDemo();
        
        // 创建多个等待线程
        Thread[] waiters = new Thread[5];
        for (int i = 0; i < 5; i++) {
            waiters[i] = new Thread(() -> {
                demo.optimizedWait();
                System.out.println(Thread.currentThread().getName() + " 完成等待");
            }, "等待线程-" + (i + 1));
            waiters[i].start();
        }
        
        Thread.sleep(1000);
        demo.printPerformanceStats();
        
        // 批量通知
        Thread notifier = new Thread(() -> {
            demo.batchNotify(3);
            System.out.println("批量通知完成");
        }, "通知线程");
        
        notifier.start();
        
        // 等待所有线程完成
        for (Thread waiter : waiters) {
            waiter.join();
        }
        notifier.join();
        
        demo.printPerformanceStats();
    }
}
```

**性能优化建议：**

1. **减少锁竞争**：使用double-checked locking模式减少不必要的锁获取
2. **批量操作**：将多个操作合并，减少signal调用次数
3. **精确条件**：使用更精确的等待条件，减少虚假唤醒
4. **合理的线程数量**：避免创建过多的等待线程
5. **监控性能**：定期检查等待队列长度和锁竞争情况

## 小结

通过本章的学习，我们深入了解了Condition条件变量的使用方法和最佳实践。让我们总结一下关键要点：

**Condition的核心概念：**
1. **线程协调机制**：与Lock配合使用，提供精确的线程等待和通知功能
2. **多条件支持**：一个Lock可以创建多个Condition，实现精细的条件控制
3. **与锁绑定**：每个Condition都必须与特定的Lock一起使用

**主要方法的使用：**
1. **await()方法**：让线程在条件不满足时等待，支持多种变体（超时、不可中断等）
2. **signal()方法**：唤醒一个等待在该条件上的线程
3. **signalAll()方法**：唤醒所有等待在该条件上的线程

**最佳实践模式：**
```java
lock.lock();
try {
    while (!condition) {
        conditionVariable.await();
    }
    // 执行条件满足后的操作
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    lock.unlock();
}
```

**Condition相比Object.wait/notify的优势：**
1. **多条件变量**：可以为不同的条件创建独立的Condition
2. **精确通知**：可以选择性地通知特定条件的等待者
3. **更好的异常处理**：提供更清晰的异常处理机制
4. **更灵活的超时机制**：支持多种超时等待方式

**常见陷阱及避免方法：**
1. **虚假唤醒**：始终在while循环中检查条件
2. **忘记获取锁**：调用await/signal前必须持有对应的锁
3. **死锁风险**：避免在条件等待中进行嵌套调用
4. **性能问题**：合理使用批量通知和精确条件检查

**实际应用场景：**
1. **生产者-消费者模式**：使用不同的条件变量控制生产和消费
2. **资源池管理**：控制资源的分配和回收
3. **任务调度**：实现复杂的任务等待和通知机制
4. **状态机实现**：根据不同状态条件进行线程协调

掌握了Condition条件变量，我们就能够实现更加精细和高效的线程协调机制。它为复杂的并发场景提供了强大而灵活的解决方案。在下一章中，我们将学习ReadWriteLock读写锁，它专门针对读多写少的场景进行了优化，能够显著提高并发性能。

**源代码地址：** https://github.com/qianmoQ/tutorial/tree/main/java-multithreading-tutorial/src/main/java/org/devlive/tutorial/multithreading/chapter09