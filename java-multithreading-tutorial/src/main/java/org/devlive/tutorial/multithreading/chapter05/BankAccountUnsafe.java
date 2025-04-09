package org.devlive.tutorial.multithreading.chapter05;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankAccountUnsafe
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

    // 账户类
    static class Account
    {
        private final int id;
        private double balance;

        public Account(int id, double initialBalance)
        {
            this.id = id;
            this.balance = initialBalance;
        }

        public void deposit(double amount)
        {
            double newBalance = balance + amount;

            // 模拟一些耗时操作，使问题更容易出现
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            balance = newBalance;
        }

        public void withdraw(double amount)
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

        public double getBalance()
        {
            return balance;
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

            // 取款
            fromAccount.withdraw(amount);
            // 存款
            toAccount.deposit(amount);

            System.out.println(Thread.currentThread().getName() + " 完成转账！");
        }
    }
}
