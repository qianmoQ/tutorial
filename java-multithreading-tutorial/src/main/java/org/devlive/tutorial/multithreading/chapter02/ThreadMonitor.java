package org.devlive.tutorial.multithreading.chapter02;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 线程状态监控工具
 */
public class ThreadMonitor
{
    // 存储要监控的线程
    private final Map<String, Thread> monitoredThreads = new ConcurrentHashMap<>();
    // 用于执行定期监控任务的调度器
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    // 监控时间间隔（秒）
    private final int monitorInterval;
    // 是否正在监控
    private boolean monitoring = false;

    /**
     * 创建线程监控器
     *
     * @param monitorInterval 监控间隔（秒）
     */
    public ThreadMonitor(int monitorInterval)
    {
        this.monitorInterval = monitorInterval;
    }

    /**
     * 添加要监控的线程
     *
     * @param name 线程名称
     * @param thread 线程对象
     */
    public void addThread(String name, Thread thread)
    {
        monitoredThreads.put(name, thread);
        System.out.println("添加线程 '" + name + "' 到监控列表");
    }

    /**
     * 移除监控的线程
     *
     * @param name 线程名称
     */
    public void removeThread(String name)
    {
        monitoredThreads.remove(name);
        System.out.println("从监控列表中移除线程 '" + name + "'");
    }

    /**
     * 开始监控
     */
    public void startMonitoring()
    {
        if (monitoring) {
            System.out.println("监控已经在运行中");
            return;
        }
        monitoring = true;
        // 创建并调度监控任务
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\n=== 线程状态监控报告 ===");
            System.out.println("时间: " + System.currentTimeMillis());
            System.out.println("监控的线程数量: " + monitoredThreads.size());
            // 打印每个线程的状态
            monitoredThreads.forEach((name, thread) -> {
                Thread.State state = thread.getState();
                String statusInfo = String.format("线程 '%s' (ID: %d) - 状态: %s",
                        name, thread.getId(), state);
                // 根据状态提供额外信息
                switch (state) {
                    case BLOCKED:
                        statusInfo += " - 等待获取监视器锁";
                        break;
                    case WAITING:
                        statusInfo += " - 无限期等待另一个线程执行特定操作";
                        break;
                    case TIMED_WAITING:
                        statusInfo += " - 等待另一个线程执行操作，最多等待指定的时间";
                        break;
                    case TERMINATED:
                        statusInfo += " - 线程已结束执行";
                        break;
                }
                System.out.println(statusInfo);
            });
            // 清理已终止的线程
            monitoredThreads.entrySet().removeIf(entry ->
                    entry.getValue().getState() == Thread.State.TERMINATED);
            System.out.println("===========================");
        }, 0, monitorInterval, TimeUnit.SECONDS);
        System.out.println("开始监控线程状态，间隔: " + monitorInterval + " 秒");
    }

    /**
     * 停止监控
     */
    public void stopMonitoring()
    {
        monitoring = false;
        scheduler.shutdown();
        System.out.println("停止线程状态监控");
    }
}