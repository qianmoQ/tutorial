package org.devlive.tutorial.multithreading.chapter03;

/**
 * 顺序执行任务示例
 */
public class SequentialTasksDemo
{
    public static void main(String[] args)
    {
        System.out.println("开始执行顺序任务");
        // 步骤1：准备数据
        Thread step1 = new Thread(() -> {
            System.out.println("步骤1：准备数据...");
            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("步骤1完成：数据准备好了");
        });
        // 步骤2：处理数据
        Thread step2 = new Thread(() -> {
            System.out.println("步骤2：处理数据...");
            try {
                Thread.sleep(3000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("步骤2完成：数据处理好了");
        });
        // 步骤3：保存结果
        Thread step3 = new Thread(() -> {
            System.out.println("步骤3：保存结果...");
            try {
                Thread.sleep(1500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("步骤3完成：结果已保存");
        });
        try {
            // 启动第一个任务并等待完成
            step1.start();
            step1.join();
            // 启动第二个任务并等待完成
            step2.start();
            step2.join();
            // 启动第三个任务并等待完成
            step3.start();
            step3.join();
        }
        catch (InterruptedException e) {
            System.out.println("任务执行被中断");
        }
        System.out.println("所有步骤已完成");
    }
}