package org.devlive.tutorial.multithreading.chapter01;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 多线程文件下载模拟器
 */
public class FileDownloaderDemo
{
    // 文件下载器，实现Runnable接口
    static class FileDownloader
            implements Runnable
    {
        private final String fileName;
        private final int fileSize; // 模拟文件大小，单位MB

        public FileDownloader(String fileName, int fileSize)
        {
            this.fileName = fileName;
            this.fileSize = fileSize;
        }

        @Override
        public void run()
        {
            System.out.println(getCurrentTime() + " - 开始下载文件：" + fileName + "（" + fileSize + "MB）");

            // 模拟下载过程
            try {
                for (int i = 1; i <= 10; i++) {
                    // 计算当前下载进度
                    int progress = i * 10;
                    int downloadedSize = fileSize * progress / 100;

                    // 休眠一段时间，模拟下载耗时
                    TimeUnit.MILLISECONDS.sleep(fileSize * 50);

                    // 打印下载进度
                    System.out.println(getCurrentTime() + " - " + Thread.currentThread().getName()
                            + " 下载 " + fileName + " 进度: " + progress + "% ("
                            + downloadedSize + "MB/" + fileSize + "MB)");
                }

                System.out.println(getCurrentTime() + " - " + Thread.currentThread().getName()
                        + " 下载完成：" + fileName);
            }
            catch (InterruptedException e) {
                System.out.println(getCurrentTime() + " - " + Thread.currentThread().getName()
                        + " 下载中断：" + fileName);
                Thread.currentThread().interrupt(); // 重设中断状态
            }
        }

        // 获取当前时间的格式化字符串
        private String getCurrentTime()
        {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            return sdf.format(new Date());
        }
    }

    public static void main(String[] args)
    {
        System.out.println("=== 文件下载模拟器 ===");

        // 创建多个下载任务
        FileDownloader task1 = new FileDownloader("电影.mp4", 200);
        FileDownloader task2 = new FileDownloader("音乐.mp3", 50);
        FileDownloader task3 = new FileDownloader("文档.pdf", 10);

        // 创建线程执行下载任务
        Thread thread1 = new Thread(task1, "下载线程-1");
        Thread thread2 = new Thread(task2, "下载线程-2");
        Thread thread3 = new Thread(task3, "下载线程-3");

        // 启动线程，开始下载
        thread1.start();
        thread2.start();
        thread3.start();

        // 主线程监控下载进度
        try {
            // 等待所有下载线程完成
            thread1.join();
            thread2.join();
            thread3.join();

            System.out.println("\n所有文件下载完成！");
        }
        catch (InterruptedException e) {
            System.out.println("主线程被中断");
        }
    }
}