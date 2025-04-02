package org.devlive.tutorial.multithreading.chapter04;

/**
 * 可见性问题示例
 */
public class VisibilityProblemDemo
{
    // 没有volatile修饰的标志变量
    private static boolean stopRequested = false;
    // 使用volatile修饰的标志变量
    private static volatile boolean volatileStopRequested = false;

    public static void main(String[] args)
            throws InterruptedException
    {
        // 测试没有volatile的情况
        testVisibility(false);
        // 测试使用volatile的情况
        testVisibility(true);
    }

    private static void testVisibility(boolean useVolatile)
            throws InterruptedException
    {
        System.out.println("\n======== " + (useVolatile ? "使用volatile" : "不使用volatile") + " ========");
        // 重置标志
        stopRequested = false;
        volatileStopRequested = false;
        // 创建工作线程，不断检查标志变量
        Thread workerThread = new Thread(() -> {
            long i = 0;
            System.out.println("工作线程开始执行...");
            // 根据参数选择使用哪个标志变量
            if (useVolatile) {
                while (!volatileStopRequested) {
                    i++;
                }
            }
            else {
                while (!stopRequested) {
                    i++;
                }
            }
            System.out.println("工作线程检测到停止信号，循环次数：" + i);
        });
        workerThread.start();
        // 主线程等待一会儿
        Thread.sleep(1000);
        System.out.println("主线程设置停止信号...");
        // 设置停止标志
        if (useVolatile) {
            volatileStopRequested = true;
        }
        else {
            stopRequested = true;
        }
        // 等待工作线程结束
        workerThread.join(5000);
        // 检查线程是否还活着
        if (workerThread.isAlive()) {
            System.out.println("工作线程仍在运行！可能存在可见性问题");
            workerThread.interrupt(); // 强制中断
        }
        else {
            System.out.println("工作线程正确终止");
        }
    }
}