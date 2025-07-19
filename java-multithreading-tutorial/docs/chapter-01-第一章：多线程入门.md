[TOC]

## 学习目标

- 理解线程与多线程的基本概念
- 掌握为什么要使用多线程编程的主要原因
- 学习Java中实现多线程的两种基本方式
- 创建并运行你的第一个多线程程序

## 1. 什么是线程与多线程

### 1.1 线程的概念

线程是操作系统能够进行运算调度的最小单位，也是程序执行流的最小单位。简单来说，线程就是一个单独的执行路径，它可以独立执行特定的代码片段。

> 📌 **提示：** 可以把线程比作是一条流水线上的工人，每个工人负责完成自己的工作。多个线程就像多个工人同时工作，提高了效率。

在Java中，当我们运行一个Java程序时，JVM会创建一个主线程来执行`main()`方法。这个主线程就是程序默认的执行路径。

```java
package org.devlive.tutorial.multithreading.chapter01;

/**
 * 演示主线程的基本概念
 */
public class MainThreadDemo
{
    public static void main(String[] args)
    {
        // 获取当前线程（主线程）
        Thread mainThread = Thread.currentThread();

        // 打印主线程信息
        System.out.println("当前执行的线程名称：" + mainThread.getName());
        System.out.println("线程ID：" + mainThread.getId());
        System.out.println("线程优先级：" + mainThread.getPriority());
        System.out.println("线程是否为守护线程：" + mainThread.isDaemon());
        System.out.println("线程状态：" + mainThread.getState());
    }
}
```

运行上面的代码，你会看到类似这样的输出：

```
当前执行的线程名称：main
线程ID：1
线程优先级：5
线程是否为守护线程：false
线程状态：RUNNABLE
```

### 1.2 多线程的概念

多线程是指在一个程序中同时运行多个线程，每个线程可以执行不同的任务，且线程之间可以并发执行。在传统的单线程程序中，任务是按顺序一个接一个地执行的，而在多线程程序中，多个任务可以看起来像是同时执行的。

> 📌 **提示：** 在单核CPU上，多线程通过时间片轮转实现"伪并行"；在多核CPU上，多线程可以实现真正的并行执行。

## 2. 为什么需要多线程编程

在实际开发中，多线程编程有很多优势：

### 2.1 提高CPU利用率

现代计算机通常有多个CPU核心，单线程程序只能使用一个核心，而多线程程序可以充分利用多核心资源，提高CPU的利用率。

### 2.2 提高程序响应性

在GUI应用程序中，如果所有操作都在一个线程中进行，那么当执行耗时操作时，整个界面会卡住无法响应用户操作。通过将耗时操作放在单独的线程中执行，可以保持界面的响应性。

### 2.3 更好的资源利用

当一个线程因为I/O操作（如读写文件、网络通信）而阻塞时，CPU可以切换到其他线程继续执行，提高整体的资源利用效率。

### 2.4 简化复杂问题的处理

有些问题天然适合使用多线程处理，比如服务器同时处理多个客户端请求，或者并行处理大量数据。

下面我们来看一个简单例子，直观感受单线程和多线程的区别：

```java
package org.devlive.tutorial.multithreading.chapter01;

/**
 * 单线程与多线程计算对比
 */
public class MultiThreadAdvantageDemo
{
    // 执行大量计算的方法
    private static void doHeavyCalculation(String threadName)
    {
        System.out.println(threadName + " 开始计算...");
        long sum = 0;
        for (long i = 0; i < 3_000_000_000L; i++) {
            sum += i;
        }
        System.out.println(threadName + " 计算完成，结果：" + sum);
    }

    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        // 单线程执行两次计算
        System.out.println("===== 单线程执行 =====");
        doHeavyCalculation("计算任务1");
        doHeavyCalculation("计算任务2");

        long endTime = System.currentTimeMillis();
        System.out.println("单线程执行总耗时：" + (endTime - startTime) + "ms");

        // 多线程执行两次计算
        System.out.println("\n===== 多线程执行 =====");
        startTime = System.currentTimeMillis();

        // 创建两个线程分别执行计算任务
        Thread thread1 = new Thread(() -> doHeavyCalculation("线程1"));
        Thread thread2 = new Thread(() -> doHeavyCalculation("线程2"));

        // 启动线程
        thread1.start();
        thread2.start();

        // 等待两个线程执行完成
        try {
            thread1.join();
            thread2.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        endTime = System.currentTimeMillis();
        System.out.println("多线程执行总耗时：" + (endTime - startTime) + "ms");
    }
}
```

在多核CPU的电脑上运行这段代码，你会发现多线程执行的总时间明显少于单线程执行的总时间，这就是多线程并行计算的优势。

## 3. Java中实现多线程的两种基本方式

Java提供了两种基本的方式来创建线程：继承`Thread`类和实现`Runnable`接口。

### 3.1 继承Thread类

通过继承`Thread`类并重写其`run()`方法来创建一个新的线程类：

```java
package org.devlive.tutorial.multithreading.chapter01;

/**
 * 通过继承Thread类实现多线程
 */
public class ThreadExtendsDemo
{
    // 自定义线程类，继承Thread类
    static class MyThread
            extends Thread
    {
        private final String message;

        public MyThread(String message)
        {
            this.message = message;
        }

        // 重写run方法，定义线程执行的任务
        @Override
        public void run()
        {
            // 打印信息，显示当前线程名称
            for (int i = 0; i < 5; i++) {
                System.out.println(getName() + " 执行: " + message + " - 第" + (i + 1) + "次");

                try {
                    // 线程休眠一段随机时间，模拟任务执行
                    Thread.sleep((long) (Math.random() * 1000));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(getName() + " 执行完毕!");
        }
    }

    public static void main(String[] args)
    {
        System.out.println("程序开始执行...");

        // 创建两个线程对象
        MyThread thread1 = new MyThread("你好，世界");
        MyThread thread2 = new MyThread("Hello, World");

        // 设置线程名称
        thread1.setName("线程1");
        thread2.setName("线程2");

        // 启动线程
        thread1.start(); // 注意：不要直接调用run()方法
        thread2.start();

        // 主线程继续执行
        for (int i = 0; i < 3; i++) {
            System.out.println("主线程执行 - 第" + (i + 1) + "次");
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("主线程执行完毕，但程序不会立即结束，因为还有其他线程在运行");
    }
}
```

> ⚠️ **重要：** 启动线程必须调用`start()`方法，而不是直接调用`run()`方法。调用`start()`方法会创建一个新线程并使这个线程开始执行`run()`方法；而直接调用`run()`方法只会在当前线程中执行该方法，不会创建新线程。

### 3.2 实现Runnable接口

通过实现`Runnable`接口并实现其`run()`方法来创建一个任务，然后将该任务传递给`Thread`对象：

```java
package org.devlive.tutorial.multithreading.chapter01;

/**
 * 通过实现Runnable接口实现多线程
 */
public class RunnableImplDemo
{
    // 自定义任务类，实现Runnable接口
    static class MyRunnable
            implements Runnable
    {
        private final String message;

        public MyRunnable(String message)
        {
            this.message = message;
        }

        // 实现run方法，定义任务执行的内容
        @Override
        public void run()
        {
            // 获取当前执行的线程
            Thread currentThread = Thread.currentThread();

            // 打印信息，显示当前线程名称
            for (int i = 0; i < 5; i++) {
                System.out.println(currentThread.getName() + " 执行: " + message + " - 第" + (i + 1) + "次");

                try {
                    // 线程休眠一段随机时间，模拟任务执行
                    Thread.sleep((long) (Math.random() * 1000));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(currentThread.getName() + " 执行完毕!");
        }
    }

    public static void main(String[] args)
    {
        System.out.println("程序开始执行...");

        // 创建两个Runnable对象
        Runnable task1 = new MyRunnable("你好，世界");
        Runnable task2 = new MyRunnable("Hello, World");

        // 创建线程对象，并传入Runnable任务
        Thread thread1 = new Thread(task1, "线程1");
        Thread thread2 = new Thread(task2, "线程2");

        // 启动线程
        thread1.start();
        thread2.start();

        // 主线程继续执行
        for (int i = 0; i < 3; i++) {
            System.out.println("主线程执行 - 第" + (i + 1) + "次");
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("主线程执行完毕，但程序不会立即结束，因为还有其他线程在运行");
    }
}
```

### 3.3 两种方式的比较

| 特点 | 继承Thread类 | 实现Runnable接口 |
|------|-------------|----------------|
| 代码结构 | 需要继承Thread类，Java不支持多继承，限制了类的扩展性 | 只需实现Runnable接口，可以继承其他类，更加灵活 |
| 资源共享 | 每个线程都是独立的对象，不方便在多个线程间共享数据 | 可以多个线程使用同一个Runnable对象，便于共享数据 |
| 耦合性 | 任务和线程高度耦合 | 任务和线程分离，解耦合 |
| 适用场景 | 简单的独立线程任务 | 需要共享数据或复用任务的场景 |

> 📌 **提示：** 在实际开发中，通常**推荐使用实现Runnable接口**的方式，因为它更加灵活，也符合设计原则中的"组合优于继承"原则。

### 3.4 使用Java 8 Lambda表达式简化Runnable实现

从Java 8开始，我们可以使用Lambda表达式大大简化Runnable的实现：

```java
package org.devlive.tutorial.multithreading.chapter01;

/**
 * 使用Lambda表达式简化多线程创建
 */
public class LambdaThreadDemo
{
    public static void main(String[] args)
    {
        System.out.println("程序开始执行...");

        // 使用Lambda表达式创建Runnable实例
        Runnable task1 = () -> {
            Thread currentThread = Thread.currentThread();
            for (int i = 0; i < 5; i++) {
                System.out.println(currentThread.getName() + " 执行: 你好，世界 - 第" + (i + 1) + "次");
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(currentThread.getName() + " 执行完毕!");
        };

        // 再简化一点，直接在创建线程时使用Lambda表达式
        Thread thread1 = new Thread(task1, "线程1");
        Thread thread2 = new Thread(() -> {
            Thread currentThread = Thread.currentThread();
            for (int i = 0; i < 5; i++) {
                System.out.println(currentThread.getName() + " 执行: Hello, World - 第" + (i + 1) + "次");
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(currentThread.getName() + " 执行完毕!");
        }, "线程2");

        // 启动线程
        thread1.start();
        thread2.start();

        // 主线程继续执行
        for (int i = 0; i < 3; i++) {
            System.out.println("主线程执行 - 第" + (i + 1) + "次");
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("主线程执行完毕，但程序不会立即结束，因为还有其他线程在运行");
    }
}
```

Lambda表达式使代码更加简洁，特别适合简单的Runnable实现。

## 4. 实战案例：创建并启动你的第一个线程

现在，让我们通过一个实战案例来综合运用所学知识。我们将创建一个模拟文件下载的程序，使用多线程同时下载多个文件。

```java
package org.devlive.tutorial.multithreading.chapter01;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 多线程文件下载模拟器
 */
public class FileDownloaderDemo
{
    // 文件下载器，实现Runnable接口
    static class FileDownloader
            implements Runnable
    {
        private final String fileName;
        private final int fileSize; // 模拟文件大小，单位MB

        public FileDownloader(String fileName, int fileSize)
        {
            this.fileName = fileName;
            this.fileSize = fileSize;
        }

        @Override
        public void run()
        {
            System.out.println(getCurrentTime() + " - 开始下载文件：" + fileName + "（" + fileSize + "MB）");

            // 模拟下载过程
            try {
                for (int i = 1; i <= 10; i++) {
                    // 计算当前下载进度
                    int progress = i * 10;
                    int downloadedSize = fileSize * progress / 100;

                    // 休眠一段时间，模拟下载耗时
                    TimeUnit.MILLISECONDS.sleep(fileSize * 50);

                    // 打印下载进度
                    System.out.println(getCurrentTime() + " - " + Thread.currentThread().getName()
                            + " 下载 " + fileName + " 进度: " + progress + "% ("
                            + downloadedSize + "MB/" + fileSize + "MB)");
                }

                System.out.println(getCurrentTime() + " - " + Thread.currentThread().getName()
                        + " 下载完成：" + fileName);
            }
            catch (InterruptedException e) {
                System.out.println(getCurrentTime() + " - " + Thread.currentThread().getName()
                        + " 下载中断：" + fileName);
                Thread.currentThread().interrupt(); // 重设中断状态
            }
        }

        // 获取当前时间的格式化字符串
        private String getCurrentTime()
        {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            return sdf.format(new Date());
        }
    }

    public static void main(String[] args)
    {
        System.out.println("=== 文件下载模拟器 ===");

        // 创建多个下载任务
        FileDownloader task1 = new FileDownloader("电影.mp4", 200);
        FileDownloader task2 = new FileDownloader("音乐.mp3", 50);
        FileDownloader task3 = new FileDownloader("文档.pdf", 10);

        // 创建线程执行下载任务
        Thread thread1 = new Thread(task1, "下载线程-1");
        Thread thread2 = new Thread(task2, "下载线程-2");
        Thread thread3 = new Thread(task3, "下载线程-3");

        // 启动线程，开始下载
        thread1.start();
        thread2.start();
        thread3.start();

        // 主线程监控下载进度
        try {
            // 等待所有下载线程完成
            thread1.join();
            thread2.join();
            thread3.join();

            System.out.println("\n所有文件下载完成！");
        }
        catch (InterruptedException e) {
            System.out.println("主线程被中断");
        }
    }
}
```

在这个实例中，我们模拟了三个不同大小文件的并行下载过程。通过使用多线程，这三个文件可以同时下载，而不需要等待一个文件下载完成后再开始下载下一个文件。`join()`方法使主线程等待所有下载线程完成后才结束程序。

## 常见问题与解决方案

### 问题1：`Thread.sleep()`方法抛出InterruptedException

**问题描述：** 为什么使用`Thread.sleep()`方法必须捕获InterruptedException异常？

**解决方案：** `sleep()`方法可能会被其他线程中断，此时会抛出InterruptedException。正确的处理方式是捕获异常并重设中断状态：

```java
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    // 记录日志或者执行必要的清理工作
    Thread.currentThread().interrupt(); // 重设中断状态
}
```

### 问题2：直接调用`run()`方法而不是`start()`方法

**问题描述：** 为什么直接调用`run()`方法不会创建新线程？

**解决方案：** 直接调用`run()`方法只是在当前线程中执行该方法，不会启动新线程。必须调用`start()`方法才能创建新线程并执行`run()`方法。

### 问题3：多线程执行顺序不确定

**问题描述：** 如何确保多个线程按特定顺序执行？

**解决方案：** 可以使用`join()`方法让一个线程等待另一个线程完成：

```java
Thread thread1 = new Thread(() -> {
    // 线程1的任务
});

Thread thread2 = new Thread(() -> {
    try {
        thread1.join(); // 等待thread1完成
        // 线程2的任务
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

thread1.start();
thread2.start();
```

## 小结

在这一章中，我们学习了以下核心内容：

1. **线程概念：** 了解了什么是线程，以及线程作为程序执行的最小单位的概念。

2. **多线程优势：** 掌握了为什么要使用多线程编程，包括提高CPU利用率、改善程序响应性、更好的资源利用以及简化复杂问题处理。

3. **线程创建方式：** 学习了Java中创建线程的两种基本方式：继承Thread类和实现Runnable接口，以及它们各自的优缺点。

4. **简化线程创建：** 了解了如何使用Java 8 Lambda表达式简化Runnable的实现。

5. **实战应用：** 通过一个文件下载模拟器的实例，综合应用了所学的多线程知识。

通过本章的学习，你已经具备了创建和启动Java线程的基本能力。在后续章节中，我们将深入探讨线程的生命周期、线程同步和安全等更高级的多线程编程主题。

记住一点：多线程编程是Java开发中的重要技能，但也是比较复杂的主题。掌握好基础概念和实践案例，是走向高级多线程编程的关键第一步。

在下一章，我们将详细介绍线程的生命周期和状态转换，帮助你更深入理解线程的工作机制。

本章节源代码地址为 https://github.com/qianmoQ/tutorial/tree/main/java-multithreading-tutorial/src/main/java/org/devlive/tutorial/multithreading/chapter01