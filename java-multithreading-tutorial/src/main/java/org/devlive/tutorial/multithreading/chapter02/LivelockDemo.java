package org.devlive.tutorial.multithreading.chapter02;

/**
 * 活锁示例
 */
public class LivelockDemo
{
    public static void main(String[] args)
    {
        final Worker worker1 = new Worker("工作者1", true);
        final Worker worker2 = new Worker("工作者2", true);
        final Object commonResource = new Object();
        new Thread(() -> {
            worker1.work(worker2, commonResource);
        }).start();
        new Thread(() -> {
            worker2.work(worker1, commonResource);
        }).start();
    }

    static class Worker
    {
        private final String name;
        private boolean active;

        public Worker(String name, boolean active)
        {
            this.name = name;
            this.active = active;
        }

        public String getName()
        {
            return name;
        }

        public boolean isActive()
        {
            return active;
        }

        public void work(Worker otherWorker, Object commonResource)
        {
            while (active) {
                // 如果其他工作者处于活动状态，则礼让
                if (otherWorker.isActive()) {
                    System.out.println(name + ": " + otherWorker.getName() + " 正在工作，我稍后再试");
                    active = false;
                    // 主动礼让CPU，让其他线程有机会运行
                    Thread.yield();
                    // 过一会儿，重新尝试工作
                    try {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    active = true;
                    continue;
                }
                // 如果其他工作者不活动，则使用共享资源
                System.out.println(name + ": 使用共享资源");
                active = false;
                // 工作完成，退出循环
                break;
            }
        }
    }
}