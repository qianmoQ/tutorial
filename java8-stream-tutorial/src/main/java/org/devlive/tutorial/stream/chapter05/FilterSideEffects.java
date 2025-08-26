package org.devlive.tutorial.stream.chapter05;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FilterSideEffects
{
    public static void main(String[] args)
    {
        System.out.println("=== 避免在filter()中的副作用 ===");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        AtomicInteger counter = new AtomicInteger(0);

        // ❌ 不好的做法：在filter中进行副作用操作
        System.out.println("❌ 不好的做法:");
        List<Integer> result1 = numbers.stream()
                .filter(n -> {
                    counter.incrementAndGet();  // 副作用！
                    System.out.println("检查数字: " + n);
                    return n > 2;
                })
                .collect(Collectors.toList());
        System.out.println("结果: " + result1 + ", 计数器: " + counter.get());

        // ✅ 好的做法：使用peek()进行调试，保持filter()纯净
        System.out.println("\n✅ 好的做法:");
        counter.set(0);
        List<Integer> result2 = numbers.stream()
                .peek(n -> {
                    counter.incrementAndGet();
                    System.out.println("处理数字: " + n);
                })
                .filter(n -> n > 2)  // 纯粹的过滤逻辑
                .collect(Collectors.toList());
        System.out.println("结果: " + result2 + ", 计数器: " + counter.get());
    }
}
