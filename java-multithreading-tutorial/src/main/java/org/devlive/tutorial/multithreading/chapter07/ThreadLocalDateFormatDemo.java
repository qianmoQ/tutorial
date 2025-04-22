package org.devlive.tutorial.multithreading.chapter07;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用ThreadLocal保证SimpleDateFormat的线程安全
 */
public class ThreadLocalDateFormatDemo
{

    // SimpleDateFormat不是线程安全的，多线程环境下共享会有问题
    // 使用ThreadLocal为每个线程创建一个独立的SimpleDateFormat实例
    private static final ThreadLocal<SimpleDateFormat> dateFormatThreadLocal =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    public static void main(String[] args)
    {
        // 创建一个固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // 提交多个任务给线程池执行
        for (int i = 0; i < 20; i++) {
            executorService.submit(() -> {
                // 在不同线程中安全地使用SimpleDateFormat
                try {
                    String dateStr = "2023-09-15 10:30:00";
                    // 获取当前线程的SimpleDateFormat实例
                    SimpleDateFormat sdf = dateFormatThreadLocal.get();
                    // 解析日期字符串
                    Date date = sdf.parse(dateStr);
                    // 格式化Date对象
                    String formatDateStr = sdf.format(date);

                    System.out.println(Thread.currentThread().getName() +
                            " 解析结果: " +
                            formatDateStr);
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                finally {
                    // 使用完后清理ThreadLocal，避免内存泄漏
                    dateFormatThreadLocal.remove();
                }
            });
        }

        // 关闭线程池
        executorService.shutdown();
    }
}
