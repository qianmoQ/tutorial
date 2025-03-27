package org.devlive.tutorial.multithreading.chapter02;

/**
 * 线程饥饿示例
 */
public class StarvationDemo
{
    private static final Object sharedResource = new Object();

    public static void main(String[] args)
    {
        // 创建5个高优先级线程，它们会反复获取共享资源
        for (int i = 0; i < 5; i++) {
            Thread highPriorityThread = new Thread(() -> {
                while (true) {
                    synchronized (sharedResource) {
                        // 高优先级线程持有锁时的操作
                        try {
                            Thread.sleep(200);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            highPriorityThread.setPriority(Thread.MAX_PRIORITY);
            highPriorityThread.start();
        }
        // 创建一个低优先级线程，它很难获取到共享资源
        Thread lowPriorityThread = new Thread(() -> {
            while (true) {
                synchronized (sharedResource) {
                    System.out.println("低优先级线程终于获取到了锁！");
                    // 低优先级线程持有锁时的操作
                    try {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);
        lowPriorityThread.start();
    }
}