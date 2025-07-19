[TOC]

## 学习目标
- 掌握线程启动(start)、休眠(sleep)和中断(interrupt)的正确使用方式
- 理解线程Join操作的原理与适用场景
- 学习如何设置和管理线程优先级
- 了解守护线程(Daemon Thread)的特性及其应用

## 1. 线程启动(start)、休眠(sleep)、中断(interrupt)

### 1.1 线程启动(start)

在Java中，调用线程的`start()`方法才能真正启动一个线程。这个方法会创建新的执行线程，并使该线程开始执行`run()`方法中的代码。

```java
package org.devlive.tutorial.multithreading.chapter03;

/**
 * 线程启动示例
 */
public class ThreadStartDemo {
    public static void main(String[] args) {
        // 创建线程
        Thread thread = new Thread(() -> {
            System.out.println("线程ID: " + Thread.currentThread().getId());
            System.out.println("线程名称: " + Thread.currentThread().getName());
            System.out.println("线程执行中...");
        });
        
        // 设置线程名称
        thread.setName("MyCustomThread");
        
        System.out.println("线程启动前的状态: " + thread.getState());
        
        // 启动线程
        thread.start();
        
        // 等待一点时间，让线程有机会执行
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("线程启动后的状态: " + thread.getState());
    }
}
```

> ⚠️ **重要提示：** 不要多次调用同一个线程的`start()`方法。线程一旦启动并终止，就无法再次启动。如果尝试再次启动同一个线程，会抛出`IllegalThreadStateException`异常。

来看一个错误示例：

```java
package org.devlive.tutorial.multithreading.chapter03;

/**
 * 线程重复启动错误示例
 */
public class ThreadDoubleStartErrorDemo {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            System.out.println("线程执行中...");
        });
        
        // 第一次启动线程（正确）
        thread.start();
        
        // 等待线程执行完毕
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("线程状态: " + thread.getState());
        
        try {
            // 尝试再次启动同一个线程（错误）
            System.out.println("尝试再次启动同一个线程...");
            thread.start();
        } catch (IllegalThreadStateException e) {
            System.out.println("捕获到异常: " + e.getMessage());
            System.out.println("线程一旦终止，就不能再次启动！");
        }
    }
}
```

> 📌 **正确做法：** 如果需要再次执行相同的任务，应该创建一个新的线程对象。

### 1.2 线程休眠(sleep)

`Thread.sleep()`方法可以使当前执行线程暂停执行指定的时间，让出CPU时间给其他线程，但不会释放当前线程所持有的锁。

```java
package org.devlive.tutorial.multithreading.chapter03;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 线程休眠示例
 */
public class ThreadSleepDemo {
    public static void main(String[] args) {
        // 时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        
        // 创建并启动3个线程
        for (int i = 1; i <= 3; i++) {
            final int threadId = i;
            new Thread(() -> {
                for (int j = 1; j <= 5; j++) {
                    // 打印当前时间和线程信息
                    String time = LocalTime.now().format(formatter);
                    System.out.println(time + " - 线程" + threadId + " 执行第" + j + "次");
                    
                    try {
                        // 线程休眠随机时间（0.5-2秒）
                        long sleepTime = 500 + (long)(Math.random() * 1500);
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        System.out.println("线程" + threadId + " 被中断");
                        return; // 提前结束线程
                    }
                }
                System.out.println("线程" + threadId + " 执行完毕");
            }, "线程" + i).start();
        }
    }
}
```

`sleep()`方法有两个重载版本：
1. `public static void sleep(long millis) throws InterruptedException`
2. `public static void sleep(long millis, int nanos) throws InterruptedException`

> 📌 **注意：** `sleep()`方法会抛出`InterruptedException`，这是一个检查型异常，必须处理。通常有两种处理方式：向上抛出或捕获处理。

`sleep()`方法与锁的关系示例：

```java
package org.devlive.tutorial.multithreading.chapter03;

/**
 * 演示sleep方法不会释放锁
 */
public class ThreadSleepWithLockDemo {
    
    private static final Object lock = new Object();
    
    public static void main(String[] args) {
        // 创建第一个线程，获取锁后休眠
        Thread thread1 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("线程1获取到锁");
                System.out.println("线程1开始休眠3秒...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("线程1休眠结束，释放锁");
            }
        });
        
        // 创建第二个线程，尝试获取锁
        Thread thread2 = new Thread(() -> {
            System.out.println("线程2尝试获取锁...");
            synchronized (lock) {
                System.out.println("线程2获取到锁");
            }
            System.out.println("线程2释放锁");
        });
        
        // 启动线程
        thread1.start();
        
        // 确保线程1先执行
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        thread2.start();
    }
}
```

在上面的例子中，当线程1调用`sleep()`方法时，它不会释放锁，因此线程2必须等待线程1的睡眠时间结束并释放锁后才能获取锁继续执行。

### 1.3 线程中断(interrupt)

Java提供了一种协作式的线程中断机制，它不会强制终止线程，而是通过设置线程的中断标志，让线程自己决定如何响应中断请求。

通过以下方法可以实现线程中断：
- `public void interrupt()`：中断线程，设置中断标志
- `public boolean isInterrupted()`：检查线程是否被中断，不清除中断状态
- `public static boolean interrupted()`：检查当前线程是否被中断，并清除中断状态

```java
package org.devlive.tutorial.multithreading.chapter03;

/**
 * 线程中断示例
 */
public class ThreadInterruptDemo {
    public static void main(String[] args) {
        // 创建一个简单的可中断线程
        Thread thread = new Thread(() -> {
            int count = 0;
            
            // 检查线程中断标志
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("线程执行中... " + (++count));
                
                // 模拟工作
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // sleep方法被中断会清除中断状态，需要重新设置
                    System.out.println("线程在sleep期间被中断");
                    Thread.currentThread().interrupt(); // 重新设置中断状态
                }
            }
            
            System.out.println("线程检测到中断信号，正常退出");
        });
        
        // 启动线程
        thread.start();
        
        // 主线程休眠3秒后中断线程
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("主线程发送中断信号");
        thread.interrupt();
    }
}
```

> ⚠️ **重要说明：** 当线程在`sleep()`、`wait()`或`join()`等阻塞方法中被中断时，这些方法会抛出`InterruptedException`并清除中断状态。因此，在捕获这些异常时，通常需要重新设置中断状态。

处理可中断阻塞操作的标准模式：

```java
try {
    // 可中断的阻塞操作
    Thread.sleep(timeoutMillis);
    // 正常处理
} catch (InterruptedException e) {
    // 可以记录日志
    
    // 重新设置中断状态
    Thread.currentThread().interrupt();
    
    // 可以选择提前返回或终止循环
    return;
}
```

## 2. 线程Join操作及其应用场景

`join()`方法允许一个线程等待另一个线程完成。当在线程A中调用线程B的`join()`方法时，线程A将被阻塞，直到线程B完成执行。

```java
package org.devlive.tutorial.multithreading.chapter03;

/**
 * 线程Join示例
 */
public class ThreadJoinDemo {
    public static void main(String[] args) {
        // 创建3个执行特定任务的线程
        Thread thread1 = new Thread(() -> {
            System.out.println("线程1开始执行...");
            try {
                // 模拟耗时操作
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程1执行完毕");
        });
        
        Thread thread2 = new Thread(() -> {
            System.out.println("线程2开始执行...");
            try {
                // 模拟耗时操作
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程2执行完毕");
        });
        
        Thread thread3 = new Thread(() -> {
            System.out.println("线程3开始执行...");
            try {
                // 模拟耗时操作
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程3执行完毕");
        });
        
        System.out.println("启动所有线程");
        
        // 启动线程
        thread1.start();
        thread2.start();
        thread3.start();
        
        System.out.println("等待所有线程完成...");
        
        try {
            // 等待线程1完成
            thread1.join();
            System.out.println("线程1已完成");
            
            // 等待线程2完成
            thread2.join();
            System.out.println("线程2已完成");
            
            // 等待线程3完成
            thread3.join();
            System.out.println("线程3已完成");
        } catch (InterruptedException e) {
            System.out.println("主线程被中断");
        }
        
        System.out.println("所有线程已完成，继续执行主线程");
    }
}
```

`join()`方法有三个重载版本：
1. `public final void join() throws InterruptedException`：等待线程终止
2. `public final void join(long millis) throws InterruptedException`：等待线程终止，最多等待指定的毫秒数
3. `public final void join(long millis, int nanos) throws InterruptedException`：等待线程终止，最多等待指定的毫秒数加纳秒数

### 2.1 Join操作的典型应用场景

1. **等待所有工作线程完成**

当主线程需要等待多个工作线程全部完成后再继续执行时，可以使用`join()`方法。

```java
package org.devlive.tutorial.multithreading.chapter03;

import java.util.ArrayList;
import java.util.List;

/**
 * 等待多个工作线程完成示例
 */
public class MultiThreadJoinDemo {
    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        
        // 创建5个工作线程
        for (int i = 1; i <= 5; i++) {
            final int threadNum = i;
            Thread thread = new Thread(() -> {
                System.out.println("工作线程" + threadNum + "开始执行...");
                
                // 模拟不同的工作负载
                try {
                    Thread.sleep(1000 + threadNum * 500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                System.out.println("工作线程" + threadNum + "执行完毕");
            });
            
            threads.add(thread);
            thread.start();
        }
        
        System.out.println("等待所有工作线程完成...");
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("所有工作线程已完成，主线程继续执行");
    }
}
```

2. **顺序执行多个任务**

当多个任务必须按照特定顺序执行时，可以使用`join()`方法确保前一个任务完成后再开始下一个任务。

```java
package org.devlive.tutorial.multithreading.chapter03;

/**
 * 顺序执行任务示例
 */
public class SequentialTasksDemo {
    public static void main(String[] args) {
        System.out.println("开始执行顺序任务");
        
        // 步骤1：准备数据
        Thread step1 = new Thread(() -> {
            System.out.println("步骤1：准备数据...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("步骤1完成：数据准备好了");
        });
        
        // 步骤2：处理数据
        Thread step2 = new Thread(() -> {
            System.out.println("步骤2：处理数据...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("步骤2完成：数据处理好了");
        });
        
        // 步骤3：保存结果
        Thread step3 = new Thread(() -> {
            System.out.println("步骤3：保存结果...");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("步骤3完成：结果已保存");
        });
        
        try {
            // 启动第一个任务并等待完成
            step1.start();
            step1.join();
            
            // 启动第二个任务并等待完成
            step2.start();
            step2.join();
            
            // 启动第三个任务并等待完成
            step3.start();
            step3.join();
        } catch (InterruptedException e) {
            System.out.println("任务执行被中断");
        }
        
        System.out.println("所有步骤已完成");
    }
}
```

3. **实现简单的任务分解与合并**

在并行计算中，可以将大任务分解为多个小任务并行执行，然后使用`join()`等待所有小任务完成后合并结果。

```java
package org.devlive.tutorial.multithreading.chapter03;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 并行计算示例：计算从1到n的总和
 */
public class ParallelSumDemo {
    public static void main(String[] args) {
        long n = 1_000_000_000L; // 计算1到10亿的和
        int numThreads = 4; // 使用4个线程并行计算
        
        // 存储最终结果
        AtomicLong totalSum = new AtomicLong(0);
        
        // 创建并启动工作线程
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final long start = i * (n / numThreads) + 1;
            final long end = (i == numThreads - 1) ? n : (i + 1) * (n / numThreads);
            
            threads[i] = new Thread(() -> {
                System.out.println("计算从 " + start + " 到 " + end + " 的和");
                long partialSum = 0;
                for (long j = start; j <= end; j++) {
                    partialSum += j;
                }
                totalSum.addAndGet(partialSum);
                System.out.println("部分和 (" + start + "-" + end + "): " + partialSum);
            });
            
            threads[i].start();
        }
        
        // 等待所有线程完成
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 输出最终结果
        System.out.println("并行计算结果: " + totalSum.get());
        
        // 验证结果
        long expectedSum = n * (n + 1) / 2;
        System.out.println("正确结果: " + expectedSum);
        System.out.println("结果正确: " + (totalSum.get() == expectedSum));
    }
}
```

## 3. 线程优先级的设置与影响

Java线程的优先级为1~10的整数，默认优先级是5。线程优先级高的线程会获得更多的执行机会，但这只是一个提示，不能保证高优先级的线程一定先于低优先级的线程执行。

```java
package org.devlive.tutorial.multithreading.chapter03;

/**
 * 线程优先级示例
 */
public class ThreadPriorityDemo {
    public static void main(String[] args) {
        // 创建3个不同优先级的线程
        Thread lowPriorityThread = new Thread(() -> {
            System.out.println("低优先级线程开始执行");
            long count = 0;
            for (long i = 0; i < 5_000_000_000L; i++) {
                count++;
                if (i % 1_000_000_000 == 0) {
                    System.out.println("低优先级线程计数: " + i / 1_000_000_000);
                }
            }
            System.out.println("低优先级线程执行完毕，计数: " + count);
        });
        
        Thread normalPriorityThread = new Thread(() -> {
            System.out.println("普通优先级线程开始执行");
            long count = 0;
            for (long i = 0; i < 5_000_000_000L; i++) {
                count++;
                if (i % 1_000_000_000 == 0) {
                    System.out.println("普通优先级线程计数: " + i / 1_000_000_000);
                }
            }
            System.out.println("普通优先级线程执行完毕，计数: " + count);
        });
        
        Thread highPriorityThread = new Thread(() -> {
            System.out.println("高优先级线程开始执行");
            long count = 0;
            for (long i = 0; i < 5_000_000_000L; i++) {
                count++;
                if (i % 1_000_000_000 == 0) {
                    System.out.println("高优先级线程计数: " + i / 1_000_000_000);
                }
            }
            System.out.println("高优先级线程执行完毕，计数: " + count);
        });
        
        // 设置线程优先级
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY); // 1
        normalPriorityThread.setPriority(Thread.NORM_PRIORITY); // 5
        highPriorityThread.setPriority(Thread.MAX_PRIORITY); // 10
        
        System.out.println("低优先级线程的优先级: " + lowPriorityThread.getPriority());
        System.out.println("普通优先级线程的优先级: " + normalPriorityThread.getPriority());
        System.out.println("高优先级线程的优先级: " + highPriorityThread.getPriority());
        
        // 启动线程
        System.out.println("启动所有线程");
        lowPriorityThread.start();
        normalPriorityThread.start();
        highPriorityThread.start();
    }
}
```

> ⚠️ **重要说明：** 线程优先级依赖于操作系统的支持，不同的操作系统对线程优先级的支持不同，有些操作系统甚至会忽略线程优先级。因此，不应该依赖线程优先级来确保程序的正确性。

线程优先级的最佳实践：
1. 仅在需要微调性能时使用线程优先级
2. 不要依赖线程优先级来确保程序的正确性
3. 避免使用过高的优先级，可能会导致其他线程饥饿
4. 避免频繁改变线程优先级

## 4. 守护线程(Daemon Thread)的应用

守护线程是为其他线程服务的线程，当所有非守护线程结束时，Java虚拟机会自动终止所有守护线程并退出。典型的守护线程有垃圾回收器、JIT编译器等。

```java
package org.devlive.tutorial.multithreading.chapter03;

/**
 * 守护线程示例
 */
public class DaemonThreadDemo {
    public static void main(String[] args) {
        // 创建一个守护线程
        Thread daemonThread = new Thread(() -> {
            int count = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                    count++;
                    System.out.println("守护线程工作中... 计数: " + count);
                } catch (InterruptedException e) {
                    System.out.println("守护线程被中断");
                    break;
                }
            }
        });
        
        // 设置为守护线程
        daemonThread.setDaemon(true);
        
        // 创建一个普通线程
        Thread userThread = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                System.out.println("用户线程工作中... " + i);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("用户线程执行完毕");
        });
        
        System.out.println("守护线程状态: " + daemonThread.isDaemon());
        System.out.println("用户线程状态: " + userThread.isDaemon());
        
        // 启动线程
        daemonThread.start();
        userThread.start();
        
        System.out.println("主线程等待用户线程完成...");
        try {
            userThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("主线程结束，JVM即将退出");
        // 不需要等待守护线程结束，JVM会自动终止所有守护线程
    }
}
```

> ⚠️ **注意：** 必须在调用`start()`方法之前设置线程为守护线程，否则会抛出`IllegalThreadStateException`异常。

### 4.1 守护线程的应用场景

1. **后台清理任务**

守护线程适合执行不需要用户交互的后台任务，如清理过期缓存、定期日志滚动等。

```java
package org.devlive.tutorial.multithreading.chapter03;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 使用守护线程实现缓存清理
 */
public class DaemonThreadCacheCleanerDemo {
    
    // 简单的内存缓存
    private static class SimpleCache {
        // 缓存数据，键是缓存项的名称，值是包含数据和过期时间的CacheItem
        private final ConcurrentHashMap<String, CacheItem> cache = new ConcurrentHashMap<>();
        
        // 缓存项，包含实际数据和过期时间
        private static class CacheItem {
            private final Object data;
            private final long expireTime; // 过期时间戳（毫秒）
            
            public CacheItem(Object data, long ttlMillis) {
                this.data = data;
                this.expireTime = System.currentTimeMillis() + ttlMillis;
            }
            
            public boolean isExpired() {
                return System.currentTimeMillis() > expireTime;
            }
            
            @Override
            public String toString() {
                return data.toString();
            }
        }
        
        // 添加缓存项
        public void put(String key, Object value, long ttlMillis) {
            cache.put(key, new CacheItem(value, ttlMillis));
            System.out.println("添加缓存项: " + key + " = " + value + ", TTL: " + ttlMillis + "ms");
        }
        
        // 获取缓存项
        public Object get(String key) {
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
        public int size() {
            return cache.size();
        }
        
        // 启动清理守护线程
        public void startCleanerThread() {
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
                    } catch (InterruptedException e) {
                        System.out.println("缓存清理线程被中断");
                        break;
                    }
                }
            });
            
            // 设置为守护线程
            cleanerThread.setDaemon(true);
            cleanerThread.start();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
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
}
```

2. **监控和维护服务**

守护线程可以用于监控应用程序的健康状态、收集性能指标等。

```java
package org.devlive.tutorial.multithreading.chapter03;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 使用守护线程实现系统监控
 */
public class DaemonThreadMonitoringDemo {
    public static void main(String[] args) {
        // 启动系统监控守护线程
        startMonitoringThread();
        
        // 模拟主应用程序
        System.out.println("主应用程序开始运行...");
        
        // 执行一些内存密集型操作，让监控线程有些变化可以报告
        for (int i = 0; i < 5; i++) {
            System.out.println("\n===== 执行任务 " + (i + 1) + " =====");
            
            // 分配一些内存
            byte[][] arrays = new byte[i + 1][1024 * 1024]; // 分配 (i+1) MB的内存
            
            // 模拟一些处理
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // 释放部分内存
            if (i % 2 == 0) {
                arrays[0] = null; // 释放第一个数组
            }
        }
        
        System.out.println("\n主应用程序执行完毕，即将退出");
    }
    
    private static void startMonitoringThread() {
        Thread monitorThread = new Thread(() -> {
            // 获取内存管理 MXBean
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            
            System.out.println("系统监控线程启动");
            
            try {
                while (true) {
                    // 获取当前时间
                    String time = LocalDateTime.now().format(formatter);
                    
                    // 收集内存使用情况
                    long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
                    long heapMax = memoryBean.getHeapMemoryUsage().getMax();
                    long nonHeapUsed = memoryBean.getNonHeapMemoryUsage().getUsed();
                    
                    // 计算内存使用百分比
                    double heapUsagePercent = (double) heapUsed / heapMax * 100;
                    
                    // 输出监控信息
                    System.out.println("\n[" + time + "] 系统监控:");
                    System.out.printf("堆内存使用: %.2f MB / %.2f MB (%.1f%%)\n", 
                            heapUsed / (1024.0 * 1024.0), 
                            heapMax / (1024.0 * 1024.0),
                            heapUsagePercent);
                    System.out.printf("非堆内存使用: %.2f MB\n", 
                            nonHeapUsed / (1024.0 * 1024.0));
                    
                    // 检查是否内存使用过高
                    if (heapUsagePercent > 70) {
                        System.out.println("警告: 内存使用率过高!");
                    }
                    
                    // 每2秒收集一次信息
                    TimeUnit.SECONDS.sleep(2);
                }
            } catch (InterruptedException e) {
                System.out.println("监控线程被中断");
            }
        });
        
        // 设置为守护线程
        monitorThread.setDaemon(true);
        monitorThread.setName("系统监控线程");
        monitorThread.start();
    }
}
```

3. **自动保存线程**

守护线程可以用于定期保存用户工作，确保即使程序意外退出，用户也不会丢失太多数据。

```java
package org.devlive.tutorial.multithreading.chapter03;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 使用守护线程实现自动保存功能
 */
public class DaemonThreadAutoSaveDemo {
    
    // 模拟文档内容
    private static StringBuilder documentContent = new StringBuilder();
    
    // 文档是否被修改
    private static volatile boolean documentModified = false;
    
    // 记录上次保存时间
    private static LocalDateTime lastSaveTime;
    
    public static void main(String[] args) {
        // 启动自动保存守护线程
        startAutoSaveThread();
        
        // 模拟文本编辑器
        System.out.println("简易文本编辑器 (输入'exit'退出)");
        System.out.println("----------------------");
        
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            
            if ("exit".equalsIgnoreCase(line)) {
                System.out.println("正在退出编辑器...");
                
                // 如果有未保存的内容，强制保存
                if (documentModified) {
                    saveDocument();
                }
                
                break;
            } else {
                // 添加用户输入到文档
                documentContent.append(line).append("\n");
                documentModified = true;
                System.out.println("文本已添加 (当前字符数: " + documentContent.length() + ")");
            }
        }
        
        System.out.println("编辑器已关闭，程序退出");
    }
    
    private static void startAutoSaveThread() {
        Thread autoSaveThread = new Thread(() -> {
            System.out.println("自动保存线程已启动 (每10秒检查一次)");
            
            while (true) {
                try {
                    // 休眠10秒
                    TimeUnit.SECONDS.sleep(10);
                    
                    // 检查文档是否被修改
                    if (documentModified) {
                        saveDocument();
                    }
                } catch (InterruptedException e) {
                    System.out.println("自动保存线程被中断");
                    break;
                }
            }
        });
        
        // 设置为守护线程
        autoSaveThread.setDaemon(true);
        autoSaveThread.setName("AutoSaveThread");
        autoSaveThread.start();
    }
    
    private static void saveDocument() {
        File file = new File("autosave_document.txt");
        
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(documentContent.toString());
            
            // 更新状态
            documentModified = false;
            lastSaveTime = LocalDateTime.now();
            
            System.out.println("\n[自动保存] 文档已保存到: " + file.getAbsolutePath() + 
                    " (" + lastSaveTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ")");
        } catch (IOException e) {
            System.out.println("\n[自动保存] 保存文档时出错: " + e.getMessage());
        }
    }
}
```

## 5. 实战案例：文件搜索工具

让我们创建一个使用多线程的文件搜索工具，它可以在指定目录中搜索符合特定条件的文件，并显示搜索结果。这个案例将综合运用我们在本章学到的线程操作知识。

```java
package org.devlive.tutorial.multithreading.chapter03;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程文件搜索工具
 */
public class ConcurrentFileSearcher {
    
    // 搜索结果
    private final ConcurrentLinkedQueue<File> results = new ConcurrentLinkedQueue<>();
    
    // 搜索线程数量
    private final int threadCount;
    
    // 搜索条件接口
    public interface SearchCriteria {
        boolean matches(File file);
    }
    
    // 构造函数
    public ConcurrentFileSearcher(int threadCount) {
        this.threadCount = threadCount;
    }
    
    /**
     * 搜索文件
     * @param startDir 起始目录
     * @param criteria 搜索条件
     * @return 匹配的文件列表
     */
    public List<File> search(File startDir, SearchCriteria criteria) {
        if (!startDir.exists() || !startDir.isDirectory()) {
            throw new IllegalArgumentException("起始目录不存在或不是一个目录: " + startDir);
        }
        
        // 清空上次搜索结果
        results.clear();
        
        // 计数器，用于跟踪处理的文件数和目录数
        AtomicInteger processedFiles = new AtomicInteger(0);
        AtomicInteger processedDirs = new AtomicInteger(0);
        
        // 创建目录队列
        ConcurrentLinkedQueue<File> directoryQueue = new ConcurrentLinkedQueue<>();
        directoryQueue.add(startDir);
        
        // 创建并启动工作线程
        Thread[] searchThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            searchThreads[i] = new Thread(() -> {
                while (true) {
                    // 从队列中获取下一个要处理的目录
                    File currentDir = directoryQueue.poll();
                    
                    // 如果队列为空，检查是否所有线程都空闲
                    if (currentDir == null) {
                        // 等待一会儿，看看其他线程是否会添加新的目录
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                        
                        // 再次检查队列
                        if (directoryQueue.isEmpty()) {
                            break; // 如果仍然为空，则结束线程
                        } else {
                            continue; // 如果有新目录，继续处理
                        }
                    }
                    
                    // 处理当前目录中的文件和子目录
                    File[] items = currentDir.listFiles();
                    if (items != null) {
                        for (File item : items) {
                            if (item.isDirectory()) {
                                // 将子目录添加到队列中
                                directoryQueue.add(item);
                                processedDirs.incrementAndGet();
                            } else {
                                // 检查文件是否匹配搜索条件
                                if (criteria.matches(item)) {
                                    results.add(item);
                                }
                                processedFiles.incrementAndGet();
                            }
                        }
                    }
                }
            });
            
            // 设置线程名称并启动
            searchThreads[i].setName("SearchThread-" + i);
            searchThreads[i].start();
        }
        
        // 创建并启动一个守护线程来显示搜索进度
        Thread progressThread = new Thread(() -> {
            try {
                while (true) {
                    int files = processedFiles.get();
                    int dirs = processedDirs.get();
                    int found = results.size();
                    
                    System.out.printf("\r处理中: %d 个文件, %d 个目录, 找到 %d 个匹配文件", 
                            files, dirs, found);
                    
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                // 忽略中断
            }
        });
        progressThread.setDaemon(true);
        progressThread.start();
        
        // 等待所有搜索线程完成
        try {
            for (Thread thread : searchThreads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            // 如果主线程被中断，中断所有搜索线程
            for (Thread thread : searchThreads) {
                thread.interrupt();
            }
            Thread.currentThread().interrupt();
        }
        
        // 打印最终结果
        System.out.println("\n搜索完成: 处理了 " + processedFiles.get() + " 个文件, " 
                + processedDirs.get() + " 个目录, 找到 " + results.size() + " 个匹配文件");
        
        // 将结果转换为List并返回
        return new ArrayList<>(results);
    }
    
    // 主程序示例
    public static void main(String[] args) {
        // 创建文件搜索器，使用4个线程
        ConcurrentFileSearcher searcher = new ConcurrentFileSearcher(4);
        
        // 定义搜索起始目录
        File startDir = new File("C:/"); // Windows系统
        // File startDir = new File("/"); // Linux/Mac系统
        
        System.out.println("开始在 " + startDir + " 中搜索...");
        
        // 定义搜索条件：查找大于10MB的Java源代码文件
        SearchCriteria criteria = file -> {
            // 检查文件扩展名
            if (file.getName().endsWith(".java")) {
                try {
                    // 检查文件大小（字节）
                    return Files.size(file.toPath()) > 10 * 1024; // > 10KB
                } catch (IOException e) {
                    return false;
                }
            }
            return false;
        };
        
        // 执行搜索
        List<File> searchResults = searcher.search(startDir, criteria);
        
        // 显示搜索结果
        System.out.println("\n搜索结果:");
        if (searchResults.isEmpty()) {
            System.out.println("未找到匹配的文件");
        } else {
            for (int i = 0; i < searchResults.size(); i++) {
                File file = searchResults.get(i);
                try {
                    long size = Files.size(file.toPath());
                    String sizeStr = size < 1024 ? size + " B" :
                                     size < 1024*1024 ? String.format("%.2f KB", size/1024.0) :
                                     String.format("%.2f MB", size/(1024.0*1024.0));
                    
                    System.out.printf("%d. %s (大小: %s)\n", i + 1, file.getAbsolutePath(), sizeStr);
                } catch (IOException e) {
                    System.out.printf("%d. %s (无法获取文件大小)\n", i + 1, file.getAbsolutePath());
                }
            }
        }
    }
}
```

在这个实战案例中，我们创建了一个多线程文件搜索工具，它可以高效地在指定目录中搜索符合特定条件的文件。主要特点：

1. 使用多个线程并行搜索，提高搜索效率
2. 使用线程安全的并发集合来共享数据
3. 使用守护线程来显示搜索进度
4. 使用原子变量来安全地计数
5. 通过`join()`方法等待所有搜索线程完成
6. 使用接口定义搜索条件，提高灵活性

## 常见问题与解决方案

### 问题1：主线程结束但程序不退出

**问题描述：** 有时主线程已经执行完所有代码，但整个Java程序却不退出。

**原因：** 这通常是因为程序中还有非守护线程在运行。只有当所有非守护线程都结束时，Java程序才会退出。

**解决方案：**
1. 确保所有工作线程都能正常结束
2. 对于不需要一直运行的后台任务，使用守护线程
3. 使用线程池时，记得调用`shutdown()`方法

### 问题2：线程无法被中断

**问题描述：** 调用`interrupt()`方法后，线程没有响应中断请求，继续执行。

**原因：** 线程必须主动检查中断状态或处理`InterruptedException`才能响应中断请求。

**解决方案：**
1. 在循环中定期检查中断状态：`Thread.currentThread().isInterrupted()`
2. 正确处理`InterruptedException`，通常是重设中断状态并返回
3. 避免屏蔽中断请求

```java
// 正确处理中断的方式
public void run() {
    try {
        while (!Thread.currentThread().isInterrupted()) {
            // 执行任务
            doTask();
            
            // 可中断的阻塞操作
            Thread.sleep(100);
        }
    } catch (InterruptedException e) {
        // 记录日志，清理资源
        
        // 重新设置中断状态
        Thread.currentThread().interrupt();
    } finally {
        // 清理资源
        cleanup();
    }
}
```

### 问题3：线程优先级设置不生效

**问题描述：** 设置了线程的优先级，但没有观察到预期的效果。

**原因：** 线程优先级依赖于操作系统的支持，不同的操作系统对线程优先级的支持程度不同。

**解决方案：**
1. 不要依赖线程优先级来保证程序的正确性
2. 使用其他机制（如线程池的任务队列）来控制任务的执行顺序
3. 在需要严格控制执行顺序的场景，使用明确的同步机制

## 小结

在本章中，我们学习了线程的基本操作及其应用：

1. **线程的基本操作：** 我们学习了如何启动线程、让线程休眠以及正确地中断线程。这些是多线程编程的基础操作，掌握这些操作可以帮助我们更好地控制线程的行为。

2. **线程Join操作：** 我们了解了`join()`方法的作用及其适用场景，如等待工作线程完成、顺序执行任务和实现简单的任务分解与合并。

3. **线程优先级：** 我们学习了如何设置线程优先级，以及线程优先级的影响。同时，我们也认识到线程优先级的局限性，不应该依赖线程优先级来确保程序的正确性。

4. **守护线程：** 我们了解了守护线程的概念和特性，以及其在后台任务、监控和自动保存等场景的应用。

5. **实战应用：** 通过文件搜索工具的实战案例，我们将所学知识应用到实际问题中，创建了一个高效的多线程文件搜索工具。

通过本章的学习，你应该能够熟练地操作线程，包括启动、休眠、中断和连接线程，以及合理地设置线程优先级和使用守护线程。这些知识为后续深入学习线程同步和并发控制奠定了基础。

在下一章中，我们将开始探讨线程安全问题，学习如何识别和解决多线程程序中的并发问题。

本章节源代码地址为 https://github.com/qianmoQ/tutorial/tree/main/java-multithreading-tutorial/src/main/java/org/devlive/tutorial/multithreading/chapter03