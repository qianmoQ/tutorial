package org.devlive.tutorial.multithreading.chapter03;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 线程休眠示例
 */
public class ThreadSleepDemo
{
    public static void main(String[] args)
    {
        // 时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        // 创建并启动3个线程
        for (int i = 1; i <= 3; i++) {
            final int threadId = i;
            new Thread(() -> {
                for (int j = 1; j <= 5; j++) {
                    // 打印当前时间和线程信息
                    String time = LocalTime.now().format(formatter);
                    System.out.println(time + " - 线程" + threadId + " 执行第" + j + "次");
                    try {
                        // 线程休眠随机时间（0.5-2秒）
                        long sleepTime = 500 + (long) (Math.random() * 1500);
                        Thread.sleep(sleepTime);
                    }
                    catch (InterruptedException e) {
                        System.out.println("线程" + threadId + " 被中断");
                        return; // 提前结束线程
                    }
                }
                System.out.println("线程" + threadId + " 执行完毕");
            }, "线程" + i).start();
        }
    }
}