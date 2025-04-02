package org.devlive.tutorial.multithreading.chapter04;

/**
 * 竞态条件示例
 */
public class RaceConditionDemo
{
    public static void main(String[] args)
            throws InterruptedException
    {
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
        for (Thread t : depositThreads) {
            t.start();
        }
        for (Thread t : withdrawThreads) {
            t.start();
        }
        // 等待所有线程完成
        for (Thread t : depositThreads) {
            t.join();
        }
        for (Thread t : withdrawThreads) {
            t.join();
        }
        // 检查最终余额
        System.out.println("所有操作完成后余额: " + account.getBalance());
        System.out.println("预期余额: 1000 (初始值) + 5*10*100 (存款) - 5*10*100 (取款) = 1000");
    }

    // 银行账户类（线程不安全）
    static class BankAccount
    {
        private int balance;  // 余额

        public BankAccount(int initialBalance)
        {
            this.balance = initialBalance;
        }

        // 存款方法
        public void deposit(int amount)
        {
            // 读取余额
            int current = balance;
            // 模拟处理延迟，使竞态条件更容易出现
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // 计算新余额并更新
            balance = current + amount;
        }

        // 取款方法
        public void withdraw(int amount)
        {
            // 读取余额
            int current = balance;
            // 模拟处理延迟
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // 只有余额充足才能取款
            if (current >= amount) {
                // 计算新余额并更新
                balance = current - amount;
            }
        }

        public int getBalance()
        {
            return balance;
        }
    }
}