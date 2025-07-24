package org.devlive.tutorial.multithreading.chapter09;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多条件变量使用示例
 * 模拟一个资源池，支持不同类型的等待条件
 */
public class MultipleConditionsDemo
{

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

    public MultipleConditionsDemo(int capacity)
    {
        this.capacity = capacity;
        this.buffer = new int[capacity];
    }

    /**
     * 生产者方法 - 等待非满条件和写模式条件
     */
    public void put(int value)
            throws InterruptedException
    {
        lock.lock();
        try {
            // 等待缓冲区不满且允许写入
            while (count == capacity || !writeMode) {
                if (count == capacity) {
                    System.out.println(Thread.currentThread().getName() + " 缓冲区已满，等待非满条件");
                    notFull.await();
                }
                else {
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
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 消费者方法 - 等待非空条件和读模式条件
     */
    public int take()
            throws InterruptedException
    {
        lock.lock();
        try {
            // 等待缓冲区非空且允许读取
            while (count == 0 || !readMode) {
                if (count == 0) {
                    System.out.println(Thread.currentThread().getName() + " 缓冲区为空，等待非空条件");
                    notEmpty.await();
                }
                else {
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
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 关闭读模式
     */
    public void disableReadMode()
    {
        lock.lock();
        try {
            readMode = false;
            System.out.println(Thread.currentThread().getName() + " 关闭读模式");
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 开启读模式
     */
    public void enableReadMode()
    {
        lock.lock();
        try {
            readMode = true;
            System.out.println(Thread.currentThread().getName() + " 开启读模式");
            canRead.signalAll(); // 通知所有等待读权限的线程
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 关闭写模式
     */
    public void disableWriteMode()
    {
        lock.lock();
        try {
            writeMode = false;
            System.out.println(Thread.currentThread().getName() + " 关闭写模式");
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 开启写模式
     */
    public void enableWriteMode()
    {
        lock.lock();
        try {
            writeMode = true;
            System.out.println(Thread.currentThread().getName() + " 开启写模式");
            canWrite.signalAll(); // 通知所有等待写权限的线程
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 获取当前状态
     */
    public void printStatus()
    {
        lock.lock();
        try {
            System.out.println("缓冲区状态 - 大小: " + count + "/" + capacity +
                    ", 读模式: " + readMode + ", 写模式: " + writeMode);
        }
        finally {
            lock.unlock();
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
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
                }
                catch (InterruptedException e) {
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
                }
                catch (InterruptedException e) {
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
            }
            catch (InterruptedException e) {
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
