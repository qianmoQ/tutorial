package org.devlive.tutorial.stream.chapter06;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectTransformation
{
    public static void main(String[] args)
    {
        System.out.println("=== 对象转换应用 ===");

        List<User> users = Arrays.asList(
                new User("张三", 1995, "北京", "开发工程师"),
                new User("李四", 1988, "上海", "产品经理"),
                new User("王五", 1992, "广州", "测试工程师"),
                new User("赵六", 1985, "深圳", "架构师")
        );

        // 1. 提取用户名列表
        System.out.println("提取用户名:");
        List<String> names = users.stream()
                .map(User::getName)  // 方法引用提取姓名
                .collect(Collectors.toList());
        System.out.println("用户名: " + names);

        // 2. 计算用户年龄
        int currentYear = LocalDate.now().getYear();
        System.out.println("\n计算用户年龄:");
        List<Integer> ages = users.stream()
                .map(user -> currentYear - user.getBirthYear())  // 计算年龄
                .collect(Collectors.toList());
        System.out.println("年龄: " + ages);

        // 3. 格式化用户信息
        System.out.println("\n格式化用户信息:");
        List<String> userInfos = users.stream()
                .map(user -> String.format("%s(%d岁) - %s",
                        user.getName(),
                        currentYear - user.getBirthYear(),
                        user.getPosition()))
                .collect(Collectors.toList());
        userInfos.forEach(System.out::println);

        // 4. 转换为用户摘要对象
        System.out.println("\n转换为用户摘要:");
        List<UserSummary> summaries = users.stream()
                .map(user -> new UserSummary(
                        user.getName(),
                        currentYear - user.getBirthYear(),
                        user.getCity().length() > 2 ? user.getCity().substring(0, 2) : user.getCity()
                ))
                .collect(Collectors.toList());
        summaries.forEach(System.out::println);
    }
}

class User
{
    private String name;
    private int birthYear;
    private String city;
    private String position;

    public User(String name, int birthYear, String city, String position)
    {
        this.name = name;
        this.birthYear = birthYear;
        this.city = city;
        this.position = position;
    }

    // getter方法
    public String getName() {return name;}

    public int getBirthYear() {return birthYear;}

    public String getCity() {return city;}

    public String getPosition() {return position;}
}

class UserSummary
{
    private String name;
    private int age;
    private String cityPrefix;

    public UserSummary(String name, int age, String cityPrefix)
    {
        this.name = name;
        this.age = age;
        this.cityPrefix = cityPrefix;
    }

    @Override
    public String toString()
    {
        return String.format("摘要[%s, %d岁, %s地区]", name, age, cityPrefix);
    }
}
