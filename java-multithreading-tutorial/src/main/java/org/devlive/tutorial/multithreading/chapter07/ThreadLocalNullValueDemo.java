package org.devlive.tutorial.multithreading.chapter07;

/**
 * 处理ThreadLocal值为null的情况
 */
public class ThreadLocalNullValueDemo
{

    // 使用withInitial提供默认值
    private static final ThreadLocal<String> threadLocalWithDefault =
            ThreadLocal.withInitial(() -> "默认值");

    // 普通ThreadLocal，没有默认值
    private static final ThreadLocal<String> threadLocalWithoutDefault =
            new ThreadLocal<>();

    public static void main(String[] args)
    {
        // 测试未设置值的情况
        System.out.println("未设置时，有默认值的ThreadLocal: " + threadLocalWithDefault.get());
        System.out.println("未设置时，没有默认值的ThreadLocal: " + threadLocalWithoutDefault.get());

        // 设置值
        threadLocalWithDefault.set("新值1");
        threadLocalWithoutDefault.set("新值2");

        System.out.println("设置后，有默认值的ThreadLocal: " + threadLocalWithDefault.get());
        System.out.println("设置后，没有默认值的ThreadLocal: " + threadLocalWithoutDefault.get());

        // 在另一个线程中尝试获取值
        Thread thread = new Thread(() -> {
            // 新线程无法获取到主线程设置的值
            System.out.println("新线程中，有默认值的ThreadLocal: " + threadLocalWithDefault.get());
            System.out.println("新线程中，没有默认值的ThreadLocal: " + threadLocalWithoutDefault.get());
        });
        thread.start();

        try {
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 安全地获取值的工具方法
        String value = getThreadLocalValueSafely(threadLocalWithoutDefault, "备用值");
        System.out.println("安全获取的值: " + value);
    }

    /**
     * 安全地获取ThreadLocal值的工具方法
     *
     * @param threadLocal ThreadLocal对象
     * @param defaultValue 默认值，当ThreadLocal值为null时返回
     * @return ThreadLocal的值，如果为null则返回默认值
     */
    public static <T> T getThreadLocalValueSafely(ThreadLocal<T> threadLocal, T defaultValue)
    {
        T value = threadLocal.get();
        if (value == null) {
            System.out.println("警告: ThreadLocal值为null，使用默认值");
            return defaultValue;
        }
        return value;
    }
}
