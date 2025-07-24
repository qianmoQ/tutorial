package org.devlive.tutorial.multithreading.chapter09;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用Condition实现生产者-消费者模式
 * 支持多生产者、多消费者的场景
 */
public class ProducerConsumerWithCondition<T>
{

    private final T[] buffer;
    private final int capacity;
    private int count = 0;      // 当前元素数量
    private int putIndex = 0;   // 生产者索引
    private int takeIndex = 0;  // 消费者索引

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition(); // 缓冲区非空条件
    private final Condition notFull = lock.newCondition();  // 缓冲区非满条件

    // 统计信息
    private volatile int totalProduced = 0;
    private volatile int totalConsumed = 0;

    @SuppressWarnings("unchecked")
    public ProducerConsumerWithCondition(int capacity)
    {
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
    }

    /**
     * 生产者方法：向缓冲区添加元素
     */
    public void produce(T item)
            throws InterruptedException
    {
        lock.lock();
        try {
            // 当缓冲区满时，生产者等待
            while (count == capacity) {
                System.out.println(Thread.currentThread().getName() + " 缓冲区已满，生产者等待...");
                notFull.await();
            }

            // 添加元素到缓冲区
            buffer[putIndex] = item;
            putIndex = (putIndex + 1) % capacity;
            count++;
            totalProduced++;

            System.out.println(Thread.currentThread().getName() + " 生产了: " + item +
                    " [缓冲区: " + count + "/" + capacity + "]");

            // 通知消费者：缓冲区不再为空
            notEmpty.signal();
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 消费者方法：从缓冲区取出元素
     */
    public T consume()
            throws InterruptedException
    {
        lock.lock();
        try {
            // 当缓冲区空时，消费者等待
            while (count == 0) {
                System.out.println(Thread.currentThread().getName() + " 缓冲区为空，消费者等待...");
                notEmpty.await();
            }

            // 从缓冲区取出元素
            T item = buffer[takeIndex];
            buffer[takeIndex] = null; // 帮助GC
            takeIndex = (takeIndex + 1) % capacity;
            count--;
            totalConsumed++;

            System.out.println(Thread.currentThread().getName() + " 消费了: " + item +
                    " [缓冲区: " + count + "/" + capacity + "]");

            // 通知生产者：缓冲区不再满
            notFull.signal();

            return item;
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 尝试生产（非阻塞）
     */
    public boolean tryProduce(T item)
    {
        if (lock.tryLock()) {
            try {
                if (count < capacity) {
                    buffer[putIndex] = item;
                    putIndex = (putIndex + 1) % capacity;
                    count++;
                    totalProduced++;

                    System.out.println(Thread.currentThread().getName() + " 非阻塞生产了: " + item +
                            " [缓冲区: " + count + "/" + capacity + "]");

                    notEmpty.signal();
                    return true;
                }
                else {
                    System.out.println(Thread.currentThread().getName() + " 缓冲区满，非阻塞生产失败");
                    return false;
                }
            }
            finally {
                lock.unlock();
            }
        }
        else {
            System.out.println(Thread.currentThread().getName() + " 获取锁失败，非阻塞生产失败");
            return false;
        }
    }

    /**
     * 尝试消费（非阻塞）
     */
    public T tryConsume()
    {
        if (lock.tryLock()) {
            try {
                if (count > 0) {
                    T item = buffer[takeIndex];
                    buffer[takeIndex] = null;
                    takeIndex = (takeIndex + 1) % capacity;
                    count--;
                    totalConsumed++;

                    System.out.println(Thread.currentThread().getName() + " 非阻塞消费了: " + item +
                            " [缓冲区: " + count + "/" + capacity + "]");

                    notFull.signal();
                    return item;
                }
                else {
                    System.out.println(Thread.currentThread().getName() + " 缓冲区空，非阻塞消费失败");
                    return null;
                }
            }
            finally {
                lock.unlock();
            }
        }
        else {
            System.out.println(Thread.currentThread().getName() + " 获取锁失败，非阻塞消费失败");
            return null;
        }
    }

    /**
     * 获取当前状态
     */
    public void printStatus()
    {
        lock.lock();
        try {
            System.out.println("=== 缓冲区状态 ===");
            System.out.println("容量: " + capacity);
            System.out.println("当前大小: " + count);
            System.out.println("总生产数量: " + totalProduced);
            System.out.println("总消费数量: " + totalConsumed);
            System.out.println("等待非满条件的线程数: " + lock.getWaitQueueLength(notFull));
            System.out.println("等待非空条件的线程数: " + lock.getWaitQueueLength(notEmpty));
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 关闭生产者消费者系统，唤醒所有等待的线程
     */
    public void shutdown()
    {
        lock.lock();
        try {
            System.out.println("系统关闭，唤醒所有等待的线程");
            notFull.signalAll();
            notEmpty.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    public static void main(String[] args)
            throws InterruptedException
    {
        ProducerConsumerWithCondition<String> system = new ProducerConsumerWithCondition<>(5);
        Random random = new Random();

        // 创建生产者线程
        Thread[] producers = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int producerId = i;
            producers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 4; j++) {
                        String product = "产品-" + producerId + "-" + j;

                        if (random.nextBoolean()) {
                            // 50% 概率使用阻塞生产
                            system.produce(product);
                        }
                        else {
                            // 50% 概率使用非阻塞生产
                            if (!system.tryProduce(product)) {
                                // 非阻塞失败，改用阻塞方式
                                system.produce(product);
                            }
                        }

                        Thread.sleep(random.nextInt(500) + 200); // 200-700ms
                    }
                }
                catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " 生产者被中断");
                    Thread.currentThread().interrupt();
                }
            }, "生产者-" + (i + 1));
        }

        // 创建消费者线程
        Thread[] consumers = new Thread[2];
        for (int i = 0; i < 2; i++) {
            consumers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 6; j++) {
                        String product;

                        if (random.nextBoolean()) {
                            // 50% 概率使用阻塞消费
                            product = system.consume();
                        }
                        else {
                            // 50% 概率使用非阻塞消费
                            product = system.tryConsume();
                            if (product == null) {
                                // 非阻塞失败，改用阻塞方式
                                product = system.consume();
                            }
                        }

                        // 模拟消费处理时间
                        Thread.sleep(random.nextInt(400) + 300); // 300-700ms
                    }
                }
                catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " 消费者被中断");
                    Thread.currentThread().interrupt();
                }
            }, "消费者-" + (i + 1));
        }

        // 监控线程
        Thread monitor = new Thread(() -> {
            try {
                for (int i = 0; i < 6; i++) {
                    Thread.sleep(2000);
                    system.printStatus();
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "监控线程");

        // 启动所有线程
        System.out.println("启动生产者-消费者系统...");

        for (Thread producer : producers) {
            producer.start();
        }

        for (Thread consumer : consumers) {
            consumer.start();
        }

        monitor.start();

        // 等待所有生产者完成
        for (Thread producer : producers) {
            producer.join();
        }

        System.out.println("所有生产者已完成");

        // 等待所有消费者完成
        for (Thread consumer : consumers) {
            consumer.join();
        }

        System.out.println("所有消费者已完成");

        monitor.interrupt();
        monitor.join();

        // 显示最终统计
        system.printStatus();
        system.shutdown();

        System.out.println("生产者-消费者系统已关闭");
    }
}
