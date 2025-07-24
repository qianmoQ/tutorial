package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示条件变量使用中的死锁问题及解决方案
 */
public class ConditionDeadlockDemo
{

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean flag = false;

    /**
     * 可能导致死锁的错误实现
     */
    public void potentialDeadlockWait()
    {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 开始等待");

            // 错误：没有检查条件就直接等待
            condition.await();

            System.out.println(Thread.currentThread().getName() + " 等待结束");
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 正确的等待实现
     */
    public void correctWait()
    {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 开始等待，flag: " + flag);

            // 正确：在循环中检查条件
            while (!flag) {
                condition.await();
            }

            System.out.println(Thread.currentThread().getName() + " 等待结束，flag: " + flag);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 忘记在持有锁的情况下调用signal的错误做法
     */
    public void wrongNotify()
    {
        // 错误：没有获取锁就调用signal
        try {
            System.out.println(Thread.currentThread().getName() + " 尝试在没有锁的情况下通知");
            condition.signal(); // 这会抛出IllegalMonitorStateException
        }
        catch (IllegalMonitorStateException e) {
            System.out.println(Thread.currentThread().getName() + " 错误：" + e.getMessage());
        }
    }

    /**
     * 正确的通知实现
     */
    public void correctNotify()
    {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 设置flag并通知");
            flag = true;
            condition.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 演示条件变量的嵌套等待可能导致的问题
     */
    public void nestedWait()
    {
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
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
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
