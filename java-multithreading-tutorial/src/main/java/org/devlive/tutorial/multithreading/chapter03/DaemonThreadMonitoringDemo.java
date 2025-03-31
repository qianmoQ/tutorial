package org.devlive.tutorial.multithreading.chapter03;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 使用守护线程实现系统监控
 */
public class DaemonThreadMonitoringDemo
{
    public static void main(String[] args)
    {
        // 启动系统监控守护线程
        startMonitoringThread();
        // 模拟主应用程序
        System.out.println("主应用程序开始运行...");
        // 执行一些内存密集型操作，让监控线程有些变化可以报告
        for (int i = 0; i < 5; i++) {
            System.out.println("\n===== 执行任务 " + (i + 1) + " =====");
            // 分配一些内存
            byte[][] arrays = new byte[i + 1][1024 * 1024]; // 分配 (i+1) MB的内存
            // 模拟一些处理
            try {
                TimeUnit.SECONDS.sleep(3);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 释放部分内存
            if (i % 2 == 0) {
                arrays[0] = null; // 释放第一个数组
            }
        }
        System.out.println("\n主应用程序执行完毕，即将退出");
    }

    private static void startMonitoringThread()
    {
        Thread monitorThread = new Thread(() -> {
            // 获取内存管理 MXBean
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            System.out.println("系统监控线程启动");
            try {
                while (true) {
                    // 获取当前时间
                    String time = LocalDateTime.now().format(formatter);
                    // 收集内存使用情况
                    long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
                    long heapMax = memoryBean.getHeapMemoryUsage().getMax();
                    long nonHeapUsed = memoryBean.getNonHeapMemoryUsage().getUsed();
                    // 计算内存使用百分比
                    double heapUsagePercent = (double) heapUsed / heapMax * 100;
                    // 输出监控信息
                    System.out.println("\n[" + time + "] 系统监控:");
                    System.out.printf("堆内存使用: %.2f MB / %.2f MB (%.1f%%)\n",
                            heapUsed / (1024.0 * 1024.0),
                            heapMax / (1024.0 * 1024.0),
                            heapUsagePercent);
                    System.out.printf("非堆内存使用: %.2f MB\n",
                            nonHeapUsed / (1024.0 * 1024.0));
                    // 检查是否内存使用过高
                    if (heapUsagePercent > 70) {
                        System.out.println("警告: 内存使用率过高!");
                    }
                    // 每2秒收集一次信息
                    TimeUnit.SECONDS.sleep(2);
                }
            }
            catch (InterruptedException e) {
                System.out.println("监控线程被中断");
            }
        });
        // 设置为守护线程
        monitorThread.setDaemon(true);
        monitorThread.setName("系统监控线程");
        monitorThread.start();
    }
}