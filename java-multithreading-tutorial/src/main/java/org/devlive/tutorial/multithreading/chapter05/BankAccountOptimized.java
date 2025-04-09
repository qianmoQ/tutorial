package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankAccountOptimized
{
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

    // 优化锁粒度的账户类
    static class Account
    {
        // 使用final对象作为锁
        private final Object balanceLock = new Object();
        private final int id;
        private double balance;

        public Account(int id, double initialBalance)
        {
            this.id = id;
            this.balance = initialBalance;
        }

        public void deposit(double amount)
        {
            // 只在修改余额时加锁
            synchronized (balanceLock) {
                double newBalance = balance + amount;
                balance = newBalance;
            }

            // 模拟其他非关键操作（如日志记录等）
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public boolean withdraw(double amount)
        {
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
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!success) {
                System.out.println("账户" + id + "余额不足，无法取款");
            }

            return success;
        }

        public double getBalance()
        {
            synchronized (balanceLock) {
                return balance;
            }
        }

        public int getId()
        {
            return id;
        }
    }

    // 转账操作
    static class TransferTask
            implements Runnable
    {
        private final Account fromAccount;
        private final Account toAccount;
        private final double amount;

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
                    }
                    else {
                        System.out.println(Thread.currentThread().getName() + " 转账失败！");
                    }
                }
            }
        }
    }
}

