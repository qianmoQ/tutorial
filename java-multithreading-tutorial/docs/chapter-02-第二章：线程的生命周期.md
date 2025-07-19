[TOC]

## 学习目标

- 了解Java线程的六大状态及其转换过程
- 掌握线程状态的判断和监控方法
- 理解如何合理地控制线程状态转换
- 学会识别和解决线程卡死的常见问题

## 1. 线程的六大状态：NEW、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING、TERMINATED

在Java中，线程的生命周期被划分为六种状态，这些状态定义在`Thread.State`枚举类中。理解这些状态及其转换对于开发高效的多线程应用至关重要。

### 1.1 NEW（新建）

当线程对象被创建但还未调用`start()`方法时，线程处于NEW状态。

```java
package org.devlive.tutorial.multithreading.chapter02;

/**
 * 演示线程的NEW状态
 */
public class ThreadNewStateDemo {
    public static void main(String[] args) {
        // 创建线程对象，此时线程处于NEW状态
        Thread thread = new Thread(() -> {
            System.out.println("线程执行中...");
        });
        
        // 检查线程状态
        System.out.println("线程创建后的状态: " + thread.getState());
        
        // 验证是否为NEW状态
        System.out.println("是否为NEW状态: " + (thread.getState() == Thread.State.NEW));
    }
}
```

### 1.2 RUNNABLE（可运行）

当线程调用了`start()`方法后，它的状态变为RUNNABLE。这表示线程已经被启动，正在等待被线程调度器分配CPU时间片。

```java
package org.devlive.tutorial.multithreading.chapter02;

/**
 * 演示线程的RUNNABLE状态
 */
public class ThreadRunnableStateDemo {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            // 一个长时间运行的任务，确保线程保持RUNNABLE状态
            for (long i = 0; i < 1_000_000_000L; i++) {
                // 执行一些计算，让线程保持活动状态
                Math.sqrt(i);
            }
        });
        
        // 启动线程，状态从NEW变为RUNNABLE
        thread.start();
        
        // 给线程一点时间启动
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 检查线程状态
        System.out.println("线程启动后的状态: " + thread.getState());
        
        // 等待线程结束
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

### 1.3 BLOCKED（阻塞）

当线程尝试获取一个被其他线程持有的对象锁时，该线程会进入BLOCKED状态。线程会一直保持BLOCKED状态，直到它获得了锁。

```java
package org.devlive.tutorial.multithreading.chapter02;

/**
 * 演示线程的BLOCKED状态
 */
public class ThreadBlockedStateDemo {
    
    // 共享资源，用于线程同步
    private static final Object lock = new Object();
    
    public static void main(String[] args) {
        // 创建第一个线程，它会获取并持有锁
        Thread thread1 = new Thread(() -> {
            System.out.println("线程1开始执行");
            synchronized (lock) {
                // 持有锁10秒钟
                System.out.println("线程1获取到锁");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("线程1释放锁");
            }
        });
        
        // 创建第二个线程，它会尝试获取锁但会被阻塞
        Thread thread2 = new Thread(() -> {
            System.out.println("线程2开始执行");
            synchronized (lock) {
                // 如果获取到锁，则执行这里的代码
                System.out.println("线程2获取到锁");
            }
        });
        
        // 启动第一个线程
        thread1.start();
        
        // 给线程1一点时间获取锁
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 启动第二个线程
        thread2.start();
        
        // 给线程2一点时间尝试获取锁
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 检查线程2的状态
        System.out.println("线程2的状态: " + thread2.getState());
        
        // 等待两个线程都结束
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

### 1.4 WAITING（等待）

当线程调用某些方法主动放弃CPU执行权，进入无限期等待状态时，线程处于WAITING状态。以下方法可以导致线程进入WAITING状态：

- `Object.wait()`：等待其他线程调用同一对象的`notify()`或`notifyAll()`
- `Thread.join()`：等待指定的线程结束
- `LockSupport.park()`：等待许可

```java
package org.devlive.tutorial.multithreading.chapter02;

/**
 * 演示线程的WAITING状态
 */
public class ThreadWaitingStateDemo {
    
    private static final Object lock = new Object();
    
    public static void main(String[] args) {
        // 创建等待线程
        Thread waitingThread = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("等待线程进入等待状态...");
                    lock.wait(); // 调用wait()方法，线程进入WAITING状态
                    System.out.println("等待线程被唤醒，继续执行");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        // 创建唤醒线程
        Thread notifyThread = new Thread(() -> {
            try {
                // 先休眠2秒，确保waitingThread已经进入WAITING状态
                Thread.sleep(2000);
                
                synchronized (lock) {
                    System.out.println("唤醒线程准备唤醒等待线程...");
                    lock.notify(); // 唤醒在lock上等待的线程
                    System.out.println("唤醒线程已发出唤醒信号");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        // 启动等待线程
        waitingThread.start();
        
        // 给等待线程一点时间进入等待状态
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 检查等待线程的状态
        System.out.println("等待线程的状态: " + waitingThread.getState());
        
        // 启动唤醒线程
        notifyThread.start();
        
        // 等待两个线程结束
        try {
            waitingThread.join();
            notifyThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

### 1.5 TIMED_WAITING（限时等待）

当线程调用带有超时参数的方法主动放弃CPU执行权，进入有限期等待状态时，线程处于TIMED_WAITING状态。以下方法可以导致线程进入TIMED_WAITING状态：

- `Thread.sleep(long millis)`：休眠指定的毫秒数
- `Object.wait(long timeout)`：等待指定的毫秒数，或者直到被通知
- `Thread.join(long millis)`：等待指定线程结束，但最多等待指定的毫秒数
- `LockSupport.parkNanos(long nanos)`：等待许可，但最多等待指定的纳秒数
- `LockSupport.parkUntil(long deadline)`：等待许可，但最多等待到指定的时间戳

```java
package org.devlive.tutorial.multithreading.chapter02;

/**
 * 演示线程的TIMED_WAITING状态
 */
public class ThreadTimedWaitingStateDemo {
    public static void main(String[] args) {
        Thread sleepingThread = new Thread(() -> {
            try {
                System.out.println("线程开始休眠5秒...");
                Thread.sleep(5000); // 线程休眠5秒，进入TIMED_WAITING状态
                System.out.println("线程休眠结束");
            } catch (InterruptedException e) {
                System.out.println("线程被中断");
            }
        });
        
        // 启动线程
        sleepingThread.start();
        
        // 给线程一点时间进入休眠状态
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 检查线程状态
        System.out.println("休眠线程的状态: " + sleepingThread.getState());
        
        // 等待线程结束
        try {
            sleepingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

### 1.6 TERMINATED（终止）

当线程正常结束或因异常而结束时，线程进入TERMINATED状态。

```java
package org.devlive.tutorial.multithreading.chapter02;

/**
 * 演示线程的TERMINATED状态
 */
public class ThreadTerminatedStateDemo {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            System.out.println("线程开始执行...");
            // 执行一些简单的任务后结束
            for (int i = 0; i < 5; i++) {
                System.out.println("线程执行中: " + i);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("线程执行结束");
        });
        
        // 启动线程
        thread.start();
        
        // 等待线程执行完毕
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 检查线程状态
        System.out.println("线程的状态: " + thread.getState());
        
        // 验证是否为TERMINATED状态
        System.out.println("是否为TERMINATED状态: " + (thread.getState() == Thread.State.TERMINATED));
    }
}
```

## 2. 状态转换图解与示例代码

线程在其生命周期中可以在不同状态之间转换。下面是一个Java线程状态转换图，它展示了各状态之间可能的转换路径：

```
    +--------------------+
    |      NEW           |
    +--------------------+
              |
              | start()
              v
    +--------------------+
    |     RUNNABLE       |<---+
    +--------------------+    |
        |     ^     |         |
        |     |     |         | 获取到锁/唤醒/超时
        |     |     |         |
        |     |     v         |
        |  +--------------------+
        |  |     BLOCKED       |
        |  +--------------------+
        |          
        |   获取锁失败
        |
        |
        |   wait()/join()
        v
    +--------------------+
    |     WAITING        |--------+
    +--------------------+        |
              |                   |
              | notify()/notifyAll()  |
              |                   |
              v                   |
    +--------------------+        |
    |  TIMED_WAITING     |<-------+
    +--------------------+  wait(timeout)/
              |             sleep(timeout)/
              | 超时         join(timeout)
              |
              v
    +--------------------+
    |    TERMINATED      |
    +--------------------+
```

让我们创建一个综合示例，展示线程在其生命周期中如何在不同状态之间转换：

```java
package org.devlive.tutorial.multithreading.chapter02;

import java.util.concurrent.TimeUnit;

/**
 * 线程状态转换综合示例
 */
public class ThreadStateTransitionDemo {
    
    // 共享锁对象
    private static final Object lock = new Object();
    
    // 标记是否应该等待
    private static boolean shouldWait = true;
    
    public static void main(String[] args) throws InterruptedException {
        // 创建线程，该线程会经历所有可能的状态
        Thread thread = new Thread(() -> {
            System.out.println("线程开始执行...");
            
            // 进入RUNNABLE状态的一些计算
            System.out.println("执行一些计算...");
            for (int i = 0; i < 1000000; i++) {
                Math.sqrt(i);
            }
            
            synchronized (lock) {
                // 进入条件等待
                if (shouldWait) {
                    try {
                        System.out.println("线程即将进入WAITING状态...");
                        lock.wait(); // 这会导致线程进入WAITING状态
                        System.out.println("线程从WAITING状态被唤醒！");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
                // 执行受锁保护的代码
                System.out.println("执行受锁保护的代码");
            }
            
            // 进入TIMED_WAITING状态
            try {
                System.out.println("线程即将进入TIMED_WAITING状态...");
                TimeUnit.SECONDS.sleep(2); // 休眠2秒，进入TIMED_WAITING状态
                System.out.println("线程从TIMED_WAITING状态恢复");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println("线程执行完毕，即将进入TERMINATED状态");
        });
        
        // 打印线程的初始状态（NEW）
        System.out.println("1. 初始状态: " + thread.getState());
        
        // 启动线程
        thread.start();
        System.out.println("2. 调用start()后: " + thread.getState());
        
        // 给线程一点时间运行
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("3. 线程运行中: " + thread.getState());
        
        // 线程应该已经进入WAITING状态
        TimeUnit.SECONDS.sleep(1);
        System.out.println("4. 线程应该在等待: " + thread.getState());
        
        // 在锁上调用notify，唤醒等待的线程
        synchronized (lock) {
            System.out.println("主线程获取到锁，准备唤醒等待的线程");
            shouldWait = false;
            lock.notify();
        }
        
        // 给线程一点时间执行并进入TIMED_WAITING状态
        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("5. 线程应该在限时等待: " + thread.getState());
        
        // 等待线程执行完毕
        thread.join();
        System.out.println("6. 线程执行完毕: " + thread.getState());
    }
}
```

运行这个示例，你将看到线程经历从NEW到RUNNABLE，然后到WAITING，然后回到RUNNABLE，再到TIMED_WAITING，最后到TERMINATED的完整生命周期过程。

> 📌 **提示：** 使用`Thread.getState()`方法可以获取线程的当前状态，但由于线程状态可能随时变化，因此获取到的状态可能已经过时。这个方法主要用于调试和监控。

## 3. 多线程执行障碍分析与解决

在多线程程序中，线程卡死（线程无法继续执行但也没有终止）是一种常见的问题。了解线程卡死的常见原因及其解决方案非常重要。

### 3.1 死锁（Deadlock）

**问题描述：** 死锁是指两个或多个线程互相等待对方持有的锁，导致所有线程都无法继续执行的情况。

**示例代码：**

```java
package org.devlive.tutorial.multithreading.chapter02;

/**
 * 死锁示例
 */
public class DeadlockDemo {
    // 两个共享资源
    private static final Object resource1 = new Object();
    private static final Object resource2 = new Object();
    
    public static void main(String[] args) {
        // 创建第一个线程，先获取resource1，再获取resource2
        Thread thread1 = new Thread(() -> {
            System.out.println("线程1尝试获取resource1...");
            synchronized (resource1) {
                System.out.println("线程1获取到resource1");
                
                // 让线程休眠一会儿，确保线程2有时间获取resource2
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                System.out.println("线程1尝试获取resource2...");
                synchronized (resource2) {
                    System.out.println("线程1同时获取到resource1和resource2");
                }
            }
        });
        
        // 创建第二个线程，先获取resource2，再获取resource1
        Thread thread2 = new Thread(() -> {
            System.out.println("线程2尝试获取resource2...");
            synchronized (resource2) {
                System.out.println("线程2获取到resource2");
                
                // 让线程休眠一会儿，确保线程1有时间获取resource1
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                System.out.println("线程2尝试获取resource1...");
                synchronized (resource1) {
                    System.out.println("线程2同时获取到resource1和resource2");
                }
            }
        });
        
        // 启动线程
        thread1.start();
        thread2.start();
    }
}
```

**产生原因：** 死锁通常由以下因素共同导致：

1. 互斥：资源只能被一个线程占用
2. 请求与保持：线程在等待资源时不释放已有资源
3. 不可剥夺：资源只能由持有者自愿释放
4. 循环等待：存在一个线程等待链

**解决方案：**

1. **按顺序获取锁：** 确保所有线程按照相同的顺序获取锁，可以避免循环等待
2. **使用超时：** 使用`tryLock(timeout)`等方法设置获取锁的超时时间
3. **死锁检测：** 使用工具（如JConsole、jstack）检测死锁
4. **使用更高级的并发工具：** 如java.util.concurrent包中的工具

### 3.2 活锁（Livelock）

**问题描述：** 活锁是指线程一直在运行，但是无法向前推进，通常是因为线程互相礼让导致的。

**示例代码：**

```java
package org.devlive.tutorial.multithreading.chapter02;

/**
 * 活锁示例
 */
public class LivelockDemo {
    
    static class Worker {
        private String name;
        private boolean active;
        
        public Worker(String name, boolean active) {
            this.name = name;
            this.active = active;
        }
        
        public String getName() {
            return name;
        }
        
        public boolean isActive() {
            return active;
        }
        
        public void work(Worker otherWorker, Object commonResource) {
            while (active) {
                // 如果其他工作者处于活动状态，则礼让
                if (otherWorker.isActive()) {
                    System.out.println(name + ": " + otherWorker.getName() + " 正在工作，我稍后再试");
                    active = false;
                    // 主动礼让CPU，让其他线程有机会运行
                    Thread.yield();
                    // 过一会儿，重新尝试工作
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    active = true;
                    continue;
                }
                
                // 如果其他工作者不活动，则使用共享资源
                System.out.println(name + ": 使用共享资源");
                active = false;
                // 工作完成，退出循环
                break;
            }
        }
    }
    
    public static void main(String[] args) {
        final Worker worker1 = new Worker("工作者1", true);
        final Worker worker2 = new Worker("工作者2", true);
        final Object commonResource = new Object();
        
        new Thread(() -> {
            worker1.work(worker2, commonResource);
        }).start();
        
        new Thread(() -> {
            worker2.work(worker1, commonResource);
        }).start();
    }
}
```

**产生原因：** 活锁通常是由于线程过度谦让或反应过度调整导致的。

**解决方案：**

1. **增加随机性：** 在重试机制中加入随机延迟
2. **优先级调整：** 调整线程优先级，避免线程互相礼让
3. **使用锁超时：** 设置锁获取的超时机制

### 3.3 饥饿（Starvation）

**问题描述：** 饥饿是指线程因无法获取所需资源而长时间无法执行的情况。

**示例代码：**

```java
package org.devlive.tutorial.multithreading.chapter02;

/**
 * 线程饥饿示例
 */
public class StarvationDemo {
    
    private static Object sharedResource = new Object();
    
    public static void main(String[] args) {
        // 创建5个高优先级线程，它们会反复获取共享资源
        for (int i = 0; i < 5; i++) {
            Thread highPriorityThread = new Thread(() -> {
                while (true) {
                    synchronized (sharedResource) {
                        // 高优先级线程持有锁时的操作
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            highPriorityThread.setPriority(Thread.MAX_PRIORITY);
            highPriorityThread.start();
        }
        
        // 创建一个低优先级线程，它很难获取到共享资源
        Thread lowPriorityThread = new Thread(() -> {
            while (true) {
                synchronized (sharedResource) {
                    System.out.println("低优先级线程终于获取到了锁！");
                    // 低优先级线程持有锁时的操作
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);
        lowPriorityThread.start();
    }
}
```

**产生原因：** 饥饿通常是由于资源分配不公平或线程优先级设置不合理导致的。

**解决方案：**

1. **公平锁：** 使用公平锁（如`ReentrantLock(true)`）确保先到先得
2. **合理设置优先级：** 避免过大的优先级差异
3. **资源限制：** 限制高优先级线程对资源的占用时间

### 3.4 线程泄漏（Thread Leak）

**问题描述：** 线程泄漏是指创建的线程没有被正确终止或回收，导致系统资源被持续占用的情况。

**示例代码：**

```java
package org.devlive.tutorial.multithreading.chapter02;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程泄漏示例
 */
public class ThreadLeakDemo {
    
    public static void main(String[] args) {
        // 错误的线程池使用方式，没有关闭线程池
        badThreadPoolUsage();
        
        // 正确的线程池使用方式
        goodThreadPoolUsage();
    }
    
    private static void badThreadPoolUsage() {
        System.out.println("=== 错误的线程池使用方式 ===");
        // 创建一个固定大小的线程池
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // 提交任务
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("执行任务 " + taskId);
                return taskId;
            });
        }
        
        // 没有调用shutdown()方法，线程池中的线程会一直存在
        System.out.println("任务提交完毕，但线程池没有被关闭");
    }
    
    private static void goodThreadPoolUsage() {
        System.out.println("=== 正确的线程池使用方式 ===");
        // 创建一个固定大小的线程池
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        try {
            // 提交任务
            for (int i = 0; i < 10; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println("执行任务 " + taskId);
                    return taskId;
                });
            }
        } finally {
            // 确保线程池被关闭
            executor.shutdown();
            System.out.println("任务提交完毕，线程池已关闭");
        }
    }
}
```

**产生原因：** 线程泄漏通常是由于未正确关闭线程池、忘记等待线程结束或线程被卡在无限循环中导致的。

**解决方案：**

1. **正确关闭线程池：** 使用`shutdown()`或`shutdownNow()`方法关闭线程池
2. **使用try-finally块：** 确保即使出现异常，资源也能被正确释放
3. **设置超时机制：** 为长时间运行的任务设置超时机制

## 4. 实战案例：线程状态监控工具

现在，让我们将所学知识应用到一个实际案例中。下面是一个简单的线程状态监控工具，可以定期打印指定线程的状态：

```java
package org.devlive.tutorial.multithreading.chapter02;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 线程状态监控工具
 */
public class ThreadMonitor {
    
    // 存储要监控的线程
    private final Map<String, Thread> monitoredThreads = new ConcurrentHashMap<>();
    
    // 用于执行定期监控任务的调度器
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // 监控时间间隔（秒）
    private final int monitorInterval;
    
    // 是否正在监控
    private boolean monitoring = false;
    
    /**
     * 创建线程监控器
     * @param monitorInterval 监控间隔（秒）
     */
    public ThreadMonitor(int monitorInterval) {
        this.monitorInterval = monitorInterval;
    }
    
    /**
     * 添加要监控的线程
     * @param name 线程名称
     * @param thread 线程对象
     */
    public void addThread(String name, Thread thread) {
        monitoredThreads.put(name, thread);
        System.out.println("添加线程 '" + name + "' 到监控列表");
    }
    
    /**
     * 移除监控的线程
     * @param name 线程名称
     */
    public void removeThread(String name) {
        monitoredThreads.remove(name);
        System.out.println("从监控列表中移除线程 '" + name + "'");
    }
    
    /**
     * 开始监控
     */
    public void startMonitoring() {
        if (monitoring) {
            System.out.println("监控已经在运行中");
            return;
        }
        
        monitoring = true;
        
        // 创建并调度监控任务
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\n=== 线程状态监控报告 ===");
            System.out.println("时间: " + System.currentTimeMillis());
            System.out.println("监控的线程数量: " + monitoredThreads.size());
            
            // 打印每个线程的状态
            monitoredThreads.forEach((name, thread) -> {
                Thread.State state = thread.getState();
                String statusInfo = String.format("线程 '%s' (ID: %d) - 状态: %s",
                        name, thread.getId(), state);
                
                // 根据状态提供额外信息
                switch (state) {
                    case BLOCKED:
                        statusInfo += " - 等待获取监视器锁";
                        break;
                    case WAITING:
                        statusInfo += " - 无限期等待另一个线程执行特定操作";
                        break;
                    case TIMED_WAITING:
                        statusInfo += " - 等待另一个线程执行操作，最多等待指定的时间";
                        break;
                    case TERMINATED:
                        statusInfo += " - 线程已结束执行";
                        break;
                }
                
                System.out.println(statusInfo);
            });
            
            // 清理已终止的线程
            monitoredThreads.entrySet().removeIf(entry -> 
                    entry.getValue().getState() == Thread.State.TERMINATED);
            
            System.out.println("===========================");
        }, 0, monitorInterval, TimeUnit.SECONDS);
        
        System.out.println("开始监控线程状态，间隔: " + monitorInterval + " 秒");
    }
    
    /**
     * 停止监控
     */
    public void stopMonitoring() {
        monitoring = false;
        scheduler.shutdown();
        System.out.println("停止线程状态监控");
    }
}
```

现在，让我们使用这个监控工具来监控不同状态的线程：

```java
package org.devlive.tutorial.multithreading.chapter02;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 线程监控工具使用示例
 */
public class ThreadMonitorDemo {
    
    // 共享锁对象
    private static final Object lock = new Object();
    
    // 用于协调线程的CountDownLatch
    private static final CountDownLatch latch = new CountDownLatch(1);
    
    public static void main(String[] args) {
        // 创建线程监控器，每2秒监控一次
        ThreadMonitor monitor = new ThreadMonitor(2);
        
        // 创建几个用于演示不同状态的线程
        
        // 1. 创建一个长时间运行的线程
        Thread runningThread = new Thread(() -> {
            System.out.println("长时间运行的线程开始执行");
            long sum = 0;
            for (long i = 0; i < 10_000_000_000L; i++) {
                sum += i;
                if (i % 1_000_000_000 == 0) {
                    System.out.println("计算中: " + i / 1_000_000_000);
                }
            }
            System.out.println("长时间运行的线程计算结果: " + sum);
        });
        
        // 2. 创建一个会进入WAITING状态的线程
        Thread waitingThread = new Thread(() -> {
            try {
                System.out.println("等待线程开始执行");
                latch.await(); // 等待主线程计数器减为0
                System.out.println("等待线程继续执行");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        // 3. 创建一个会进入TIMED_WAITING状态的线程
        Thread sleepingThread = new Thread(() -> {
            try {
                System.out.println("休眠线程开始执行");
                Thread.sleep(20000); // 休眠20秒
                System.out.println("休眠线程醒来继续执行");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        // 4. 创建一个会进入BLOCKED状态的线程
        Thread blockedThread = new Thread(() -> {
            System.out.println("阻塞线程开始执行，等待获取锁");
            synchronized (lock) {
                System.out.println("阻塞线程获取到锁");
            }
        });
        
        // 主线程先获取锁，让blockedThread进入BLOCKED状态
        synchronized (lock) {
            // 添加线程到监控器
            monitor.addThread("运行线程", runningThread);
            monitor.addThread("等待线程", waitingThread);
            monitor.addThread("休眠线程", sleepingThread);
            monitor.addThread("阻塞线程", blockedThread);
            
            // 启动线程
            runningThread.start();
            waitingThread.start();
            sleepingThread.start();
            blockedThread.start();
            
            // 开始监控
            monitor.startMonitoring();
            
            // 主线程持有锁10秒钟，让blockedThread保持BLOCKED状态
            try {
                System.out.println("主线程持有锁10秒钟，blockedThread将保持BLOCKED状态");
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println("主线程释放锁，blockedThread将获取锁并继续执行");
        }
        
        // 5秒后释放等待线程
        try {
            TimeUnit.SECONDS.sleep(5);
            System.out.println("主线程释放等待线程");
            latch.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 等待所有线程结束
        try {
            runningThread.join();
            waitingThread.join();
            sleepingThread.join();
            blockedThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 停止监控
        monitor.stopMonitoring();
    }
}
```

这个实战案例展示了如何创建一个工具来监控线程的状态变化，这对于调试复杂的多线程应用非常有用。

## 常见问题与解决方案

### 问题1：如何正确终止一个线程？

**问题描述：** Java中没有安全的方法可以直接强制终止一个线程。`Thread.stop()`、`Thread.suspend()`和`Thread.resume()`方法已被弃用，因为它们可能导致数据不一致。

**解决方案：** 使用中断机制实现线程的协作终止：

```java
public class GracefulThreadTermination {
    public static void main(String[] args) throws InterruptedException {
        // 创建一个能响应中断的线程
        Thread thread = new Thread(() -> {
            // 检查中断标志
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 执行任务...
                    System.out.println("线程执行中...");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // 捕获中断异常，重新设置中断标志，并退出循环
                    System.out.println("线程被中断，准备清理资源并退出");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            // 线程结束前的清理工作
            System.out.println("线程正在清理资源...");
            System.out.println("线程已安全终止");
        });
        
        thread.start();
        
        // 主线程休眠3秒
        Thread.sleep(3000);
        
        // 中断线程
        System.out.println("主线程发送中断信号");
        thread.interrupt();
    }
}
```

### 问题2：如何解决死锁？

**问题描述：** 死锁是多线程编程中的常见问题，一旦发生很难恢复。

**解决方案：**

1. **预防死锁：** 按固定顺序获取锁

   ```java
   // 正确的获取多个锁的方式
   public void transferMoney(Account fromAccount, Account toAccount, double amount) {
       // 确保按账户ID的顺序获取锁
       Account firstLock = fromAccount.getId() < toAccount.getId() ? fromAccount : toAccount;
       Account secondLock = fromAccount.getId() < toAccount.getId() ? toAccount : fromAccount;
       
       synchronized (firstLock) {
           synchronized (secondLock) {
               // 执行转账逻辑
           }
       }
   }
   ```

2. **使用tryLock带超时的锁获取：**

   ```java
   Lock lock1 = new ReentrantLock();
   Lock lock2 = new ReentrantLock();
   
   public void doSomething() {
       boolean gotFirstLock = false;
       boolean gotSecondLock = false;
       
       try {
           gotFirstLock = lock1.tryLock(1, TimeUnit.SECONDS);
           if (gotFirstLock) {
               gotSecondLock = lock2.tryLock(1, TimeUnit.SECONDS);
           }
           
           if (gotFirstLock && gotSecondLock) {
               // 成功获取两个锁，执行操作
           } else {
               // 无法获取锁，放弃本次操作
           }
       } catch (InterruptedException e) {
           Thread.currentThread().interrupt();
       } finally {
           if (gotSecondLock) lock2.unlock();
           if (gotFirstLock) lock1.unlock();
       }
   }
   ```

### 问题3：如何检测和解决线程泄漏？

**问题描述：** 线程泄漏会导致系统资源不断被消耗，最终可能导致OutOfMemoryError。

**解决方案：**

1. **使用线程池：** 而不是直接创建线程
2. **始终关闭线程池：** 使用try-finally确保关闭
3. **设置合理的线程池大小：** 避免创建过多线程
4. **监控线程数量：** 定期检查应用程序的线程数量

```java
// 正确使用线程池的方式
ExecutorService executor = null;
try {
    executor = Executors.newFixedThreadPool(10);
    
    // 提交任务到线程池
    for (int i = 0; i < 100; i++) {
        executor.submit(new Task(i));
    }
    
} finally {
    // 确保线程池被关闭
    if (executor != null) {
        executor.shutdown();
        try {
            // 等待所有任务完成，但最多等待60秒
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                // 如果等待超时，强制关闭
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            // 如果当前线程被中断，重新中断，并强制关闭线程池
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }
    }
}
```

## 小结

在本章中，我们深入学习了Java线程的生命周期和状态转换：

1. **线程的六大状态：** 我们详细了解了Java线程的六种状态（NEW、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING和TERMINATED）及其特点。

2. **状态转换机制：** 我们通过代码示例和状态转换图，理解了线程如何在不同状态之间转换，以及哪些方法会触发这些转换。

3. **线程卡死问题：** 我们学习了几种常见的线程卡死情况（死锁、活锁、饥饿和线程泄漏）及其解决方案。

4. **实战应用：** 我们创建了一个线程状态监控工具，可以实时监控线程状态的变化，这对调试多线程应用非常有用。

理解线程的生命周期对于开发高效、可靠的多线程应用至关重要。通过掌握线程状态转换的规律和可能出现的问题，你可以更好地设计和调试多线程程序。

在下一章中，我们将学习线程的基本操作，包括启动、休眠、中断、连接等，进一步增强你的多线程编程技能。

本章节源代码地址为 https://github.com/qianmoQ/tutorial/tree/main/java-multithreading-tutorial/src/main/java/org/devlive/tutorial/multithreading/chapter02