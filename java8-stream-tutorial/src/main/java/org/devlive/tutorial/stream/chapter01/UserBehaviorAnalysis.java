package org.devlive.tutorial.stream.chapter01;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserBehaviorAnalysis
{
    public static void main(String[] args)
    {
        // 模拟用户行为数据
        List<UserBehavior> behaviors = Arrays.asList(
                new UserBehavior("user001", 25, 1200.0, true),
                new UserBehavior("user002", 35, 800.0, false),
                new UserBehavior("user003", 28, 1500.0, true),
                new UserBehavior("user004", 42, 2000.0, true),
                new UserBehavior("user005", 31, 900.0, false),
                new UserBehavior("user006", 26, 1100.0, true),
                new UserBehavior("user007", 38, 1800.0, true),
                new UserBehavior("user008", 29, 1300.0, true)
        );

        System.out.println("=== 活跃用户年龄段消费分析 ===");

        // 使用Stream进行复杂数据分析
        Map<String, Double> ageGroupAvgConsumption = behaviors.stream()
                .filter(UserBehavior::isActive)                    // 只要活跃用户
                .collect(Collectors.groupingBy(                    // 按年龄段分组
                        behavior -> {
                            int age = behavior.getAge();
                            if (age < 30) {
                                return "青年组(20-29岁)";
                            }
                            else if (age < 40) {
                                return "中年组(30-39岁)";
                            }
                            else {
                                return "成熟组(40+岁)";
                            }
                        },
                        Collectors.averagingDouble(UserBehavior::getConsumption) // 计算平均消费
                ));

        // 输出分析结果
        ageGroupAvgConsumption.forEach((ageGroup, avgConsumption) ->
                System.out.printf("%s: 平均消费 %.2f元\n", ageGroup, avgConsumption));

        // 额外分析：找出消费最高的活跃用户
        System.out.println("\n=== 活跃用户中的消费冠军 ===");
        behaviors.stream()
                .filter(UserBehavior::isActive)
                .max(Comparator.comparing(UserBehavior::getConsumption))
                .ifPresent(champion ->
                        System.out.printf("消费冠军: %s，年龄: %d岁，消费: %.2f元\n",
                                champion.getUserId(), champion.getAge(), champion.getConsumption()));
    }
}

class UserBehavior
{
    private String userId;
    private int age;
    private double consumption;
    private boolean isActive;

    public UserBehavior(String userId, int age, double consumption, boolean isActive)
    {
        this.userId = userId;
        this.age = age;
        this.consumption = consumption;
        this.isActive = isActive;
    }

    // getter methods
    public String getUserId() {return userId;}

    public int getAge() {return age;}

    public double getConsumption() {return consumption;}

    public boolean isActive() {return isActive;}
}
