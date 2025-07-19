package org.devlive.tutorial.multithreading.chapter08;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示忘记释放锁的问题及解决方案
 */
public class LockReleaseDemo
{

    private final ReentrantLock lock = new ReentrantLock();
    private int count = 0;

    /**
     * 错误示例：可能导致锁泄漏
     */
    public void badExample()
    {
        lock.lock();

        try {
            count++;

            // 如果这里抛出运行时异常，锁将永远不会被释放
            if (count == 3) {
                throw new RuntimeException("模拟异常");
            }

            System.out.println(Thread.currentThread().getName() + " 执行完成，count = " + count);
        }
        catch (RuntimeException e) {
            System.out.println(Thread.currentThread().getName() + " 发生异常：" + e.getMessage());
            // 注意：这里没有释放锁！
            return;
        }

        lock.unlock(); // 这行代码在异常情况下不会执行
    }

    /**
     * 正确示例：确保锁总是被释放
     */
    public void goodExample()
    {
        lock.lock();
        try {
            count++;

            // 即使这里抛出异常，finally块也会执行
            if (count == 3) {
                throw new RuntimeException("模拟异常");
            }

            System.out.println(Thread.currentThread().getName() + " 执行完成，count = " + count);
        }
        catch (RuntimeException e) {
            System.out.println(Thread.currentThread().getName() + " 发生异常：" + e.getMessage());
            // 异常处理逻辑
        }
        finally {
            // 无论是否发生异常，都会释放锁
            lock.unlock();
        }
    }

    /**
     * 使用AutoCloseable的更优雅方式
     */
    static class AutoLock
            implements AutoCloseable
    {
        private final ReentrantLock lock;

        public AutoLock(ReentrantLock lock)
        {
            this.lock = lock;
            this.lock.lock();
        }

        @Override
        public void close()
        {
            lock.unlock();
        }
    }

    /**
     * 使用try-with-resources的方式
     */
    public void elegantExample()
    {
        try (AutoLock autoLock = new AutoLock(lock)) {
            count++;

            if (count == 3) {
                throw new RuntimeException("模拟异常");
            }

            System.out.println(Thread.currentThread().getName() + " 执行完成，count = " + count);
        }
        catch (RuntimeException e) {
            System.out.println(Thread.currentThread().getName() + " 发生异常：" + e.getMessage());
        }
        // AutoLock会自动释放锁
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        LockReleaseDemo demo = new LockReleaseDemo();

        System.out.println("=== 演示错误用法（可能导致死锁） ===");

        // 注意：这个例子可能导致死锁，在实际环境中不要这样做
        Thread badThread1 = new Thread(demo::badExample, "错误线程1");

        Thread badThread2 = new Thread(demo::badExample, "错误线程2");

        // 这个会抛异常但不释放锁
        Thread badThread3 = new Thread(demo::badExample, "错误线程3");

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

        Thread goodThread1 = new Thread(demo::goodExample, "正确线程1");

        Thread goodThread2 = new Thread(demo::goodExample, "正确线程2");

        // 这个会抛异常但会释放锁
        Thread goodThread3 = new Thread(demo::goodExample, "正确线程3");

        // 这个能正常获取锁
        Thread goodThread4 = new Thread(demo::goodExample, "正确线程4");

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

        Thread elegantThread1 = new Thread(demo::elegantExample, "优雅线程1");

        Thread elegantThread2 = new Thread(demo::elegantExample, "优雅线程2");

        // 这个会抛异常但会自动释放锁
        Thread elegantThread3 = new Thread(demo::elegantExample, "优雅线程3");

        // 这个能正常获取锁
        Thread elegantThread4 = new Thread(demo::elegantExample, "优雅线程4");

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
