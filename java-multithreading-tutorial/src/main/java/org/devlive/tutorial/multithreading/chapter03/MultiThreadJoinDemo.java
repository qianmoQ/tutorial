package org.devlive.tutorial.multithreading.chapter03;

import java.util.ArrayList;
import java.util.List;

/**
 * 等待多个工作线程完成示例
 */
public class MultiThreadJoinDemo
{
    public static void main(String[] args)
    {
        List<Thread> threads = new ArrayList<>();
        // 创建5个工作线程
        for (int i = 1; i <= 5; i++) {
            final int threadNum = i;
            Thread thread = new Thread(() -> {
                System.out.println("工作线程" + threadNum + "开始执行...");
                // 模拟不同的工作负载
                try {
                    Thread.sleep(1000 + threadNum * 500);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("工作线程" + threadNum + "执行完毕");
            });
            threads.add(thread);
            thread.start();
        }
        System.out.println("等待所有工作线程完成...");
        // 等待所有线程完成
        for (Thread thread : threads) {
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("所有工作线程已完成，主线程继续执行");
    }
}