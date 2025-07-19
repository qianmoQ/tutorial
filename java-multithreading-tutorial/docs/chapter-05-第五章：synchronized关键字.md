[TOC]

## 学习目标

- 理解Java中对象锁与类锁的概念与区别
- 掌握synchronized修饰方法与代码块的不同使用方式
- 了解synchronized的底层实现原理
- 能够使用synchronized解决实际并发问题

## 1. 对象锁与类锁的概念

在多线程环境中，当多个线程同时访问共享资源时，可能会导致数据不一致。Java提供了synchronized关键字来解决这个问题，它通过锁机制确保同一时刻只有一个线程可以执行被保护的代码。在Java中，锁主要分为两种：对象锁和类锁。

### 1.1 对象锁（实例锁）

对象锁是Java中最常见的锁类型，它锁定的是特定的对象实例。当一个线程获取了某个对象的锁后，其他线程必须等待这个线程释放锁后才能获取该对象的锁。
对象锁的获取方式有两种：

1. 同步实例方法：synchronized修饰非静态方法
2. 同步代码块：synchronized(this)或synchronized(实例对象)

看一个简单的对象锁示例：

```java
package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.TimeUnit;

public class ObjectLockDemo {
    // 定义一个共享变量
    private int counter = 0;
    
    // 使用synchronized修饰实例方法，锁是当前对象(this)
    public synchronized void incrementSync() {
        counter++;
        System.out.println(Thread.currentThread().getName() + " - 计数器：" + counter);
        try {
            // 线程睡眠500毫秒，便于观察效果
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // 使用synchronized代码块，锁是当前对象(this)
    public void incrementSyncBlock() {
        synchronized (this) {
            counter++;
            System.out.println(Thread.currentThread().getName() + " - 计数器：" + counter);
            try {
                // 线程睡眠500毫秒，便于观察效果
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        final ObjectLockDemo demo = new ObjectLockDemo();
        
        // 创建5个线程并发执行同步方法
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                demo.incrementSync();
            }, "线程-" + i).start();
        }
    }
}
```

运行上述代码，你会发现5个线程是按顺序执行incrementSync方法的，而不是并发执行。这是因为synchronized保证了同一时刻只有一个线程能够获取到对象锁并执行同步方法。

> **提示：** 对象锁只对同一个对象实例有效。如果创建了多个对象实例，每个实例都有自己的对象锁，不同实例的锁互不干扰。

### 1.2 类锁（静态锁）

类锁是Java中另一种重要的锁类型，它锁定的是类而不是对象实例。无论创建了多少个实例，一个类只有一个类锁。当一个线程获取了某个类的类锁后，其他线程必须等待这个线程释放锁后才能获取该类的类锁。
类锁的获取方式有两种：

1. 同步静态方法：synchronized修饰静态方法
2. 同步代码块：synchronized(类名.class)

看一个类锁的示例：

```java
package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.TimeUnit;

public class ClassLockDemo {
    // 静态计数器
    private static int counter = 0;
    
    // 使用synchronized修饰静态方法，锁是当前类的Class对象
    public static synchronized void incrementStatic() {
        counter++;
        System.out.println(Thread.currentThread().getName() + " - 计数器：" + counter);
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // 使用synchronized代码块锁定类对象
    public void incrementWithClassLock() {
        synchronized (ClassLockDemo.class) {
            counter++;
            System.out.println(Thread.currentThread().getName() + " - 计数器：" + counter);
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        // 创建两个不同的实例
        final ClassLockDemo demo1 = new ClassLockDemo();
        final ClassLockDemo demo2 = new ClassLockDemo();
        
        // 创建5个线程，使用不同实例的方法
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                new Thread(() -> {
                    ClassLockDemo.incrementStatic(); // 使用静态方法（类锁）
                }, "静态方法线程-" + i).start();
            } else {
                new Thread(() -> {
                    demo2.incrementWithClassLock(); // 使用实例方法中的类锁
                }, "类锁代码块-" + i).start();
            }
        }
    }
}
```

运行上述代码，你会发现无论是通过静态方法还是通过类锁代码块访问，所有线程都必须排队执行，因为它们争夺的是同一个类锁。

> **重要概念**： 类锁实际上是锁定类的Class对象。在Java中，无论一个类有多少个对象实例，这个类只有一个Class对象，所以类锁也只有一个。

### 1.3 对象锁与类锁的对比

下面通过一个示例来展示对象锁与类锁的区别：

```java
package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.TimeUnit;

public class LockComparisonDemo {
    // 实例变量
    private int instanceCounter = 0;
    // 静态变量
    private static int staticCounter = 0;
    
    // 对象锁方法
    public synchronized void incrementInstance() {
        instanceCounter++;
        System.out.println(Thread.currentThread().getName() + " - 实例计数器：" + instanceCounter);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // 类锁方法
    public static synchronized void incrementStatic() {
        staticCounter++;
        System.out.println(Thread.currentThread().getName() + " - 静态计数器：" + staticCounter);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // 创建同一个实例
        final LockComparisonDemo demo = new LockComparisonDemo();
        
        // 线程1：访问实例方法（对象锁）
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.incrementInstance();
            }
        }, "对象锁线程").start();
        
        // 线程2：访问静态方法（类锁）
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                LockComparisonDemo.incrementStatic();
            }
        }, "类锁线程").start();
        
        // 关键点：对象锁和类锁是相互独立的，不会互相阻塞
    }
}
```

运行这段代码，你会发现"对象锁线程"和"类锁线程"是并发执行的，这说明对象锁和类锁互不干扰。这是因为它们锁定的是不同的对象：对象锁锁定的是实例对象，而类锁锁定的是Class对象。

## 2. synchronized修饰方法与代码块的区别

synchronized关键字可以修饰方法或代码块，它们有一些重要的区别。

### 2.1 修饰方法 vs 修饰代码块

1. 锁的粒度不同
    - 修饰方法：锁的是整个方法体
    - 修饰代码块：只锁定特定的代码块
2. 性能影响
    - 修饰方法：可能导致锁的持有时间过长
    - 修饰代码块：可以精确控制锁的范围，提高并发性能
3. 灵活性
    - 修饰方法：简单直观，但不够灵活
    - 修饰代码块：可以指定不同的锁对象，更加灵活

下面是一个对比示例：

```java
package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.TimeUnit;

public class SynchronizedComparison {
    
    private int data = 0;
    
    // 修饰整个方法
    public synchronized void methodSync() {
        System.out.println(Thread.currentThread().getName() + " 开始执行方法同步...");
        
        // 执行一些非同步操作（假设是耗时的IO操作）
        try {
            System.out.println(Thread.currentThread().getName() + " 执行耗时操作...");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 真正需要同步的只是这部分数据操作
        data++;
        System.out.println(Thread.currentThread().getName() + " 数据更新为：" + data);
        
        // 再次执行一些非同步操作
        try {
            System.out.println(Thread.currentThread().getName() + " 执行额外操作...");
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println(Thread.currentThread().getName() + " 方法执行完毕");
    }
    
    // 只同步关键代码块
    public void blockSync() {
        System.out.println(Thread.currentThread().getName() + " 开始执行方法...");
        
        // 执行一些非同步操作（假设是耗时的IO操作）
        try {
            System.out.println(Thread.currentThread().getName() + " 执行耗时操作...");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 只同步关键代码块
        synchronized(this) {
            data++;
            System.out.println(Thread.currentThread().getName() + " 数据更新为：" + data);
        }
        
        // 再次执行一些非同步操作
        try {
            System.out.println(Thread.currentThread().getName() + " 执行额外操作...");
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println(Thread.currentThread().getName() + " 方法执行完毕");
    }
    
    public static void main(String[] args) {
        SynchronizedComparison demo = new SynchronizedComparison();
        
        System.out.println("====== 测试方法同步 ======");
        Thread t1 = new Thread(() -> demo.methodSync(), "线程1");
        Thread t2 = new Thread(() -> demo.methodSync(), "线程2");
        t1.start();
        t2.start();
        
        // 等待两个线程执行完毕
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\n====== 测试代码块同步 ======");
        demo.data = 0; // 重置数据
        Thread t3 = new Thread(() -> demo.blockSync(), "线程3");
        Thread t4 = new Thread(() -> demo.blockSync(), "线程4");
        t3.start();
        t4.start();
    }
}
```

运行上述代码，你会发现：

- 在方法同步中，第二个线程必须等第一个线程完全执行完整个方法才能开始执行。
- 在代码块同步中，两个线程可以并发执行方法中的非同步部分，只有在执行同步代码块时才需要等待。

这说明了代码块同步相比方法同步可以提高并发性能，尤其是在方法中包含大量非同步操作的情况下。

### 2.2 不同锁对象的选择

在使用synchronized代码块时，可以选择不同的锁对象。锁对象的选择会影响并发行为：

```java
package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.TimeUnit;

public class LockObjectDemo {
    
    // 定义不同的锁对象
    private final Object lockA = new Object();
    private final Object lockB = new Object();
    
    private int counterA = 0;
    private int counterB = 0;
    
    // 使用lockA作为锁对象
    public void incrementA() {
        synchronized (lockA) {
            counterA++;
            System.out.println(Thread.currentThread().getName() + " - 计数器A：" + counterA);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 使用lockB作为锁对象
    public void incrementB() {
        synchronized (lockB) {
            counterB++;
            System.out.println(Thread.currentThread().getName() + " - 计数器B：" + counterB);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        LockObjectDemo demo = new LockObjectDemo();
        
        // 线程1：访问A资源
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.incrementA();
            }
        }, "线程A").start();
        
        // 线程2：访问B资源
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.incrementB();
            }
        }, "线程B").start();
        
        // 线程3：也访问A资源
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                demo.incrementA();
            }
        }, "线程C").start();
    }
}
```

运行上述代码，你会观察到：

- "线程A"和"线程C"会互相阻塞，因为它们使用了相同的锁对象lockA
- "线程B"独立执行，不受其他线程影响，因为它使用了不同的锁对象lockB

这种方式称为"细粒度锁"，可以提高程序的并发性能。但是要注意，使用不同的锁对象也会增加程序的复杂性，可能导致死锁问题。

> 最佳实践： 选择锁对象时，应该遵循以下原则：
>   1. 使用final修饰锁对象，防止锁对象被修改
>   2. 不要使用String常量或基本类型的包装类作为锁对象
>   3. 尽量避免使用公共对象（如全局变量）作为锁对象

## 3. synchronized的底层实现原理

要深入理解synchronized，我们需要了解它的底层实现原理。

### 3.1 Monitor监视器

synchronized的底层实现是基于Monitor（监视器）机制。在Java虚拟机中，每个对象都有一个与之关联的Monitor。当线程执行到synchronized代码块时，会尝试获取Monitor的所有权：

如果Monitor没有被占用，线程获取Monitor的所有权并继续执行
如果Monitor已被其他线程占用，当前线程会被阻塞，进入Monitor的等待队列

以下是这个过程的简化图示：

```java
线程A                      对象的Monitor             线程B
  |                             |                   |
  |  尝试获取Monitor所有权      |                   |
  | -------------------------> |                   |
  |                             |                   |
  |  获取成功，执行同步代码块    |                   |
  |                             |                   |
  |                             |  尝试获取Monitor所有权
  |                             | <-----------------
  |                             |                   |
  |                             |  被阻塞，进入等待队列
  |                             |                   |
  |  释放Monitor所有权          |                   |
  | -------------------------> |                   |
  |                             |                   |
  |                             |  获取Monitor所有权
  |                             | ----------------> |
  |                             |                   |
  |                             |  执行同步代码块    |
```

### 3.2 字节码层面的实现

在Java字节码层面，synchronized的实现依赖于monitorenter和monitorexit指令：

- monitorenter：进入同步代码块时，尝试获取Monitor
- monitorexit：退出同步代码块时，释放Monitor

我们可以通过javap命令反编译字节码来查看这些指令。以下是一个简单示例：

```java
package org.devlive.tutorial.multithreading.chapter05;

public class SynchronizedBytecode {
    public void syncBlock() {
        synchronized (this) {
            System.out.println("同步代码块");
        }
    }
    
    public synchronized void syncMethod() {
        System.out.println("同步方法");
    }
}
```

使用javap命令查看字节码（在编译后的目录中执行）：

```bash
javap -c org.devlive.tutorial.multithreading.chapter05.SynchronizedBytecode
```

你会看到类似下面的输出（简化版）：

```java
public void syncBlock();
  Code:
    0: aload_0
    1: dup
    2: astore_1
    3: monitorenter  // 进入同步块
    4: getstatic     #2  // System.out
    ...
    12: aload_1
    13: monitorexit   // 退出同步块
    ...
    
public synchronized void syncMethod();
  Code:
    0: getstatic     #2  // System.out
    ...
    // 注意没有明显的monitorenter和monitorexit指令
```

从字节码可以看出：

- 同步代码块是通过monitorenter和monitorexit指令实现的
- 同步方法是通过方法修饰符ACC_SYNCHRONIZED标记实现的，JVM会根据这个标记自动加锁和释放锁

### 3.3 锁的优化

随着Java版本的演进，synchronized的性能得到了极大的优化。JDK 1.6引入了锁的升级机制，将锁分为以下几种状态：

1. 偏向锁：偏向于第一个获取锁的线程，如果该线程后续继续获取该锁，不需要进行同步操作
2. 轻量级锁：使用CAS（Compare and Swap）操作尝试获取锁，避免线程阻塞
3. 重量级锁：传统的锁机制，会导致线程阻塞和上下文切换

锁的状态会随着竞争的激烈程度自动升级，但不会降级。这种机制大大提高了synchronized的性能。

> 提示： JDK 1.6之前，synchronized被称为"重量级锁"，性能较差。但经过优化后，现代JVM中的synchronized性能已经非常好，在大多数场景下可以放心使用。

## 4. 实战案例：使用synchronized解决银行账户并发问题

现在，让我们通过一个实际案例来应用synchronized关键字解决并发问题。

### 4.1 问题描述：银行账户转账

假设有一个银行系统，需要处理账户之间的转账操作。在多线程环境下，如果不加锁控制，可能会出现以下问题：

- 账户余额计算错误
- 转账金额丢失
- 账户余额为负数

### 4.2 不安全的实现

首先，我们看一个不使用同步机制的实现：

```java
package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankAccountUnsafe {
    // 账户类
    static class Account {
        private int id;
        private double balance;
        
        public Account(int id, double initialBalance) {
            this.id = id;
            this.balance = initialBalance;
        }
        
        public void deposit(double amount) {
            double newBalance = balance + amount;
            
            // 模拟一些耗时操作，使问题更容易出现
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            balance = newBalance;
        }
        
        public void withdraw(double amount) {
            if (balance >= amount) {
                double newBalance = balance - amount;
                
                // 模拟一些耗时操作
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                balance = newBalance;
            } else {
                System.out.println("账户" + id + "余额不足，无法取款");
            }
        }
        
        public double getBalance() {
            return balance;
        }
        
        public int getId() {
            return id;
        }
    }
    
    // 转账操作
    static class TransferTask implements Runnable {
        private Account fromAccount;
        private Account toAccount;
        private double amount;
        
        public TransferTask(Account fromAccount, Account toAccount, double amount) {
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
            this.amount = amount;
        }
        
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " 准备从账户" + fromAccount.getId() + 
                    "转账" + amount + "元到账户" + toAccount.getId());
            
            // 取款
            fromAccount.withdraw(amount);
            // 存款
            toAccount.deposit(amount);
            
            System.out.println(Thread.currentThread().getName() + " 完成转账！");
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // 创建两个账户
        Account accountA = new Account(1, 1000);
        Account accountB = new Account(2, 1000);
        
        System.out.println("初始状态：");
        System.out.println("账户1余额: " + accountA.getBalance());
        System.out.println("账户2余额: " + accountB.getBalance());
        
        // 使用线程池创建10个线程
        ExecutorService executor = Executors.newFixedThreadPool(10);
        // 使用CountDownLatch等待所有线程完成
        CountDownLatch latch = new CountDownLatch(100);
        
        // 提交100个转账任务
        for (int i = 0; i < 50; i++) {
            // A账户向B账户转账
            executor.submit(() -> {
                new TransferTask(accountA, accountB, 10).run();
                latch.countDown();
            });
            
            // B账户向A账户转账
            executor.submit(() -> {
                new TransferTask(accountB, accountA, 10).run();
                latch.countDown();
            });
        }
        
        // 等待所有任务完成
        latch.await();
        executor.shutdown();
        
        System.out.println("\n最终状态：");
        System.out.println("账户1余额: " + accountA.getBalance());
        System.out.println("账户2余额: " + accountB.getBalance());
        System.out.println("总金额: " + (accountA.getBalance() + accountB.getBalance()));
    }
}
```

运行上述代码，你会发现总金额可能不等于初始总金额（2000元）。这是因为多个线程同时修改账户余额导致的数据不一致问题。

### 4.3 使用synchronized解决问题

现在，让我们使用synchronized来解决这个问题：

```java
package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankAccountSafe
{
    // 安全的账户类
    static class Account
    {
        private int id;
        private double balance;

        public Account(int id, double initialBalance)
        {
            this.id = id;
            this.balance = initialBalance;
        }

        // 使用synchronized修饰存款方法
        public synchronized void deposit(double amount)
        {
            double newBalance = balance + amount;

            // 模拟一些耗时操作
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            balance = newBalance;
        }

        // 使用synchronized修饰取款方法
        public synchronized void withdraw(double amount)
        {
            if (balance >= amount) {
                double newBalance = balance - amount;

                // 模拟一些耗时操作
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                balance = newBalance;
            }
            else {
                System.out.println("账户" + id + "余额不足，无法取款");
            }
        }

        public synchronized double getBalance()
        {
            return balance;
        }

        public int getId()
        {
            return id;
        }
    }

    // 安全的转账操作
    static class TransferTask
            implements Runnable
    {
        private Account fromAccount;
        private Account toAccount;
        private double amount;

        public TransferTask(Account fromAccount, Account toAccount, double amount)
        {
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
            this.amount = amount;
        }

        @Override
        public void run()
        {
            System.out.println(Thread.currentThread().getName() + " 准备从账户" + fromAccount.getId() +
                    "转账" + amount + "元到账户" + toAccount.getId());

            // 使用synchronized代码块，确保同时锁定两个账户
            // 注意：这里需要按固定顺序获取锁，避免死锁
            synchronized (fromAccount.getId() < toAccount.getId() ? fromAccount : toAccount) {
                synchronized (fromAccount.getId() < toAccount.getId() ? toAccount : fromAccount) {
                    // 取款
                    fromAccount.withdraw(amount);
                    // 存款
                    toAccount.deposit(amount);
                }
            }

            System.out.println(Thread.currentThread().getName() + " 完成转账！");
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        // 创建两个账户
        Account accountA = new Account(1, 1000);
        Account accountB = new Account(2, 1000);

        System.out.println("初始状态：");
        System.out.println("账户1余额: " + accountA.getBalance());
        System.out.println("账户2余额: " + accountB.getBalance());

        // 使用线程池创建10个线程
        ExecutorService executor = Executors.newFixedThreadPool(10);
        // 使用CountDownLatch等待所有线程完成
        CountDownLatch latch = new CountDownLatch(50);

        // 提交100个转账任务
        for (int i = 0; i < 50; i++) {
            // A账户向B账户转账
            executor.submit(() -> {
                new TransferTask(accountA, accountB, 10).run();
                latch.countDown();
            });
        }

        // 等待所有任务完成
        latch.await();
        executor.shutdown();

        System.out.println("\n最终状态：");
        System.out.println("账户1余额: " + accountA.getBalance());
        System.out.println("账户2余额: " + accountB.getBalance());
        System.out.println("总金额: " + (accountA.getBalance() + accountB.getBalance()));
    }
}
```

运行上述代码，你会发现总金额始终等于初始总金额（2000元）。这是因为我们使用了synchronized确保了账户操作的线程安全。

在这个实现中，我们做了以下改进：

1. 使用synchronized修饰deposit、withdraw和getBalance方法，确保对单个账户的操作是线程安全的。
2. 在转账操作中，使用嵌套的synchronized代码块同时锁定两个账户，确保转账过程的原子性。
3. 使用固定顺序获取锁（按账户ID排序），避免死锁问题。

> **注意：** 在上述实现中，我们使用了一个重要的技巧来避免死锁：按照固定顺序获取锁。如果不这样做，可能会出现线程A持有账户1的锁并等待账户2的锁，而线程B持有账户2的锁并等待账户1的锁，从而形成死锁。

### 4.4 进一步优化：减小锁粒度

虽然上面的实现解决了线程安全问题，但锁的粒度还可以进一步优化。我们可以只在需要修改余额的关键代码处加锁，而不是整个方法：

```java
package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankAccountOptimized {
    // 优化锁粒度的账户类
    static class Account {
        private int id;
        private double balance;
        // 使用final对象作为锁
        private final Object balanceLock = new Object();
        
        public Account(int id, double initialBalance) {
            this.id = id;
            this.balance = initialBalance;
        }
        
        public void deposit(double amount) {
            // 只在修改余额时加锁
            synchronized (balanceLock) {
                double newBalance = balance + amount;
                balance = newBalance;
            }
            
            // 模拟其他非关键操作（如日志记录等）
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        public boolean withdraw(double amount) {
            boolean success = false;
            
            // 只在检查和修改余额时加锁
            synchronized (balanceLock) {
                if (balance >= amount) {
                    double newBalance = balance - amount;
                    balance = newBalance;
                    success = true;
                }
            }
            
            // 模拟其他非关键操作
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            if (!success) {
                System.out.println("账户" + id + "余额不足，无法取款");
            }
            
            return success;
        }
        
        public double getBalance() {
            synchronized (balanceLock) {
                return balance;
            }
        }
        
        public int getId() {
            return id;
        }
    }
    
    // 转账操作
    static class TransferTask implements Runnable {
        private Account fromAccount;
        private Account toAccount;
        private double amount;
        
        public TransferTask(Account fromAccount, Account toAccount, double amount) {
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
            this.amount = amount;
        }
        
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " 准备从账户" + fromAccount.getId() + 
                    "转账" + amount + "元到账户" + toAccount.getId());
            
            // 使用两阶段锁定协议：先锁定源账户，再锁定目标账户
            // 注意：为了避免死锁，我们按照账户ID的顺序获取锁
            Account firstLock = fromAccount.getId() < toAccount.getId() ? fromAccount : toAccount;
            Account secondLock = fromAccount.getId() < toAccount.getId() ? toAccount : fromAccount;
            
            synchronized (firstLock) {
                synchronized (secondLock) {
                    // 先检查余额是否足够
                    if (fromAccount.withdraw(amount)) {
                        // 取款成功后存入另一个账户
                        toAccount.deposit(amount);
                        System.out.println(Thread.currentThread().getName() + " 完成转账！");
                    } else {
                        System.out.println(Thread.currentThread().getName() + " 转账失败！");
                    }
                }
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // 创建两个账户
        Account accountA = new Account(1, 1000);
        Account accountB = new Account(2, 1000);
        
        System.out.println("初始状态：");
        System.out.println("账户1余额: " + accountA.getBalance());
        System.out.println("账户2余额: " + accountB.getBalance());
        
        // 使用线程池创建10个线程
        ExecutorService executor = Executors.newFixedThreadPool(10);
        // 使用CountDownLatch等待所有线程完成
        CountDownLatch latch = new CountDownLatch(50);
        
        // 提交100个转账任务
        for (int i = 0; i < 50; i++) {
            // A账户向B账户转账
            executor.submit(() -> {
                new TransferTask(accountA, accountB, 10).run();
                latch.countDown();
            });
            
            // B账户向A账户转账
            executor.submit(() -> {
                new TransferTask(accountB, accountA, 10).run();
                latch.countDown();
            });
        }
        
        // 等待所有任务完成
        latch.await();
        executor.shutdown();
        
        System.out.println("\n最终状态：");
        System.out.println("账户1余额: " + accountA.getBalance());
        System.out.println("账户2余额: " + accountB.getBalance());
        System.out.println("总金额: " + (accountA.getBalance() + accountB.getBalance()));
    }
}

```

在这个优化版本中，我们做了以下改进：

1. 使用专门的锁对象（balanceLock）而不是this对象
2. 只在真正需要同步的代码块（余额操作）上加锁，提高并发性能
3. 将withdraw方法的返回值改为boolean，便于调用者知道取款是否成功
4. 使用两阶段锁定协议，按固定顺序获取锁，避免死锁

这种方式既保证了线程安全，又提高了并发性能。

## 5. 常见问题与解决方案

### 5.1 死锁问题

死锁是指两个或更多线程永远阻塞，等待对方持有的锁。

死锁示例：

```java
package org.devlive.tutorial.multithreading.chapter05;

public class DeadlockDemo {
    // 资源A
    private static final Object resourceA = new Object();
    // 资源B
    private static final Object resourceB = new Object();
    
    public static void main(String[] args) {
        // 线程1：先获取资源A，再获取资源B
        Thread thread1 = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println(Thread.currentThread().getName() + " 获取了资源A");
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                System.out.println(Thread.currentThread().getName() + " 等待获取资源B");
                synchronized (resourceB) {
                    System.out.println(Thread.currentThread().getName() + " 获取了资源B");
                }
            }
        }, "线程1");
        
        // 线程2：先获取资源B，再获取资源A
        Thread thread2 = new Thread(() -> {
            synchronized (resourceB) {
                System.out.println(Thread.currentThread().getName() + " 获取了资源B");
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                System.out.println(Thread.currentThread().getName() + " 等待获取资源A");
                synchronized (resourceA) {
                    System.out.println(Thread.currentThread().getName() + " 获取了资源A");
                }
            }
        }, "线程2");
        
        thread1.start();
        thread2.start();
    }
}
```

运行上述代码，你会发现程序会永远卡住，这就是死锁。线程1持有资源A的锁并等待资源B的锁，而线程2持有资源B的锁并等待资源A的锁。

死锁解决方案：

1. 按固定顺序获取锁：确保所有线程按照相同的顺序获取锁，可以避免循环等待。

    ```java
    package org.devlive.tutorial.multithreading.chapter05;
    
    // 修复死锁的代码
    public class DeadlockFixed {
        // 资源A
        private static final Object resourceA = new Object();
        // 资源B
        private static final Object resourceB = new Object();
        
        public static void main(String[] args) {
            // 线程1：先获取资源A，再获取资源B
            Thread thread1 = new Thread(() -> {
                synchronized (resourceA) {
                    System.out.println(Thread.currentThread().getName() + " 获取了资源A");
                    
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    System.out.println(Thread.currentThread().getName() + " 等待获取资源B");
                    synchronized (resourceB) {
                        System.out.println(Thread.currentThread().getName() + " 获取了资源B");
                    }
                }
            }, "线程1");
            
            // 线程2：也是先获取资源A，再获取资源B，遵循相同的顺序
            Thread thread2 = new Thread(() -> {
                synchronized (resourceA) {
                    System.out.println(Thread.currentThread().getName() + " 获取了资源A");
                    
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    System.out.println(Thread.currentThread().getName() + " 等待获取资源B");
                    synchronized (resourceB) {
                        System.out.println(Thread.currentThread().getName() + " 获取了资源B");
                    }
                }
            }, "线程2");
            
            thread1.start();
            thread2.start();
        }
    }
    ```
2. 使用锁超时：设定获取锁的超时时间，超时后放弃当前锁并重试。（不过synchronized不支持超时，需要使用Lock接口及其实现类）
3. 死锁检测：使用工具（如jstack）检测死锁并强制解决。

### 5.2 性能问题

synchronized在保证线程安全的同时可能会带来性能问题。

常见性能问题：

1. 锁粒度过大：整个方法加锁可能导致大部分时间都在等待锁
2. 锁竞争激烈：多个线程频繁争抢同一把锁
3. 锁持有时间过长：同步代码块中包含耗时操作

解决方案：

1. 减小锁粒度：只在必要的代码块上加锁
2. 使用不同的锁对象：将不相关的操作分到不同的锁上
3. 避免在同步代码块中进行耗时操作：如IO操作、复杂计算等
4. 考虑使用CAS操作：对于简单的原子操作，考虑使用java.util.concurrent.atomic包中的类
```java
// 减小锁粒度的示例
public class SmallerLockGranularity {
    private final Object stateLock = new Object();
    private final Object listLock = new Object();
    
    private int state;
    private List<String> list = new ArrayList<>();
    
    // 使用不同的锁对象保护不同的资源
    public void updateState() {
        synchronized (stateLock) {
            state++;
        }
    }
    
    public void updateList(String item) {
        synchronized (listLock) {
            list.add(item);
        }
    }
}
```

### 5.3 活锁问题

活锁是指线程不断重试一个总是失败的操作，导致无法继续执行，但线程并没有阻塞。

活锁示例：

```java
package org.devlive.tutorial.multithreading.chapter05;

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

        public void work(Worker otherWorker, Object sharedResource) {
            while (active) {
                // 等待对方不活跃
                if (otherWorker.isActive()) {
                    System.out.println(getName() + ": " + otherWorker.getName() + "正在工作，我稍后再试");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                synchronized (sharedResource) {
                    System.out.println(getName() + ": 我开始工作了");
                    active = false;
                    // 通知对方可以工作了
                    otherWorker.active = true;
                    System.out.println(getName() + ": 我通知" + otherWorker.getName() + "可以工作了");
                }
            }
        }
    }

    public static void main(String[] args) {
        final Object sharedResource = new Object();
        final Worker worker1 = new Worker("工人1", true);
        final Worker worker2 = new Worker("工人2", true);

        new Thread(() -> {
            worker1.work(worker2, sharedResource);
        }).start();

        new Thread(() -> {
            worker2.work(worker1, sharedResource);
        }).start();
    }
}
```

运行上述代码，你会发现两个工人一直互相谦让，都无法开始工作。这就是活锁。

活锁解决方案：

1. 引入随机等待时间：打破重试的同步性
2. 优先级调整：为竞争者分配不同的优先级

```java
// 通过随机等待时间解决活锁
public void work(Worker otherWorker, Object sharedResource) {
    while (active) {
        // 等待对方不活跃
        if (otherWorker.isActive()) {
            System.out.println(getName() + ": " + otherWorker.getName() + "正在工作，我稍后再试");
            try {
                // 引入随机等待时间，打破同步性
                Thread.sleep((long)(Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            continue;
        }
        
        // 其余代码保持不变
    }
}
```

### 5.4 嵌套锁问题

嵌套锁是指在已持有锁的情况下再次获取锁。Java的synchronized是可重入锁，允许嵌套锁，但仍需谨慎使用。

可重入性示例：

```java
package org.devlive.tutorial.multithreading.chapter05;

public class ReentrantLockDemo {
    
    public synchronized void outer() {
        System.out.println("进入outer方法");
        inner(); // 在持有锁的情况下调用另一个同步方法
        System.out.println("退出outer方法");
    }
    
    public synchronized void inner() {
        System.out.println("进入inner方法");
        // 这里可以再次获取同一个锁（this对象的锁）
        System.out.println("退出inner方法");
    }
    
    public static void main(String[] args) {
        ReentrantLockDemo demo = new ReentrantLockDemo();
        demo.outer();
    }
}
```


运行上述代码，你会发现程序能够正常执行，而不会死锁。这是因为synchronized是可重入锁，允许同一个线程多次获取同一把锁。

嵌套锁的问题与解决方案：

1. **死锁风险**：如果存在交叉调用，可能导致死锁
    - 解决方案：避免复杂的锁嵌套关系，或使用统一的锁获取顺序

2. **性能开销**：每次获取锁都有开销
    - 解决方案：减少不必要的锁嵌套

3. **代码可读性下降**：嵌套锁使代码更难理解和维护
    - 解决方案：重构代码，降低复杂性

## 6. 小结

在本章中，我们深入学习了synchronized关键字的使用和原理：

1. 理解了对象锁与类锁的概念和区别：
    - 对象锁锁定特定实例，通过synchronized修饰实例方法或synchronized(this)实现
    - 类锁锁定整个类，通过synchronized修饰静态方法或synchronized(类名.class)实现

2. 掌握了synchronized修饰方法与代码块的区别：
    - 修饰方法：锁的范围是整个方法体，简单但粒度大
    - 修饰代码块：可以精确控制锁的范围，性能更好

3. 了解了synchronized的底层实现原理：
    - 基于Monitor监视器机制
    - 通过monitorenter和monitorexit指令实现
    - JDK 1.6后引入锁升级机制，大幅提升性能

4. 通过银行账户转账案例掌握了如何使用synchronized解决实际并发问题

5. 学习了使用synchronized的常见问题与解决方案：
    - 死锁问题：按固定顺序获取锁
    - 性能问题：减小锁粒度，使用不同的锁对象
    - 活锁问题：引入随机等待时间
    - 嵌套锁问题：注意可重入性，避免复杂嵌套

通过本章的学习，你应该能够理解并正确使用synchronized关键字来解决多线程环境下的同步问题。在下一章中，我们将学习volatile关键字，它是另一种解决并发问题的重要工具。

源代码地址：https://github.com/qianmoQ/tutorial/tree/main/java-multithreading-tutorial/src/main/java/org/devlive/tutorial/multithreading/chapter05