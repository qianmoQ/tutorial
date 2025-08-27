package org.devlive.tutorial.stream.chapter06;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapFilterCombination
{
    public static void main(String[] args)
    {
        System.out.println("=== map()与filter()组合 ===");

        List<Employee> employees = Arrays.asList(
                new Employee("张三", 28, 12000),
                new Employee("李四", 35, 15000),
                new Employee("王五", 23, 8000),
                new Employee("赵六", 31, 18000),
                new Employee("孙七", 26, 10000)
        );

        // 需求：找出高薪员工(>12000)的姓名，并格式化显示
        System.out.println("高薪员工名单:");
        List<String> highSalaryNames = employees.stream()
                .filter(emp -> emp.getSalary() > 12000)     // 先过滤
                .map(emp -> emp.getName() + "(" + emp.getSalary() + "元)")  // 再转换格式
                .collect(Collectors.toList());
        highSalaryNames.forEach(System.out::println);

        // 需求：30岁以下员工的平均工资
        System.out.println("\n30岁以下员工的平均工资:");
        double avgSalary = employees.stream()
                .filter(emp -> emp.getAge() < 30)           // 筛选30岁以下
                .mapToInt(Employee::getSalary)              // 提取工资
                .average()                                  // 计算平均值
                .orElse(0);
        System.out.printf("平均工资: %.2f元\n", avgSalary);

        // 顺序很重要！先过滤再转换vs先转换再过滤
        System.out.println("\n顺序优化对比:");

        // ✅ 效率更高：先过滤，减少map()操作次数
        long count1 = employees.stream()
                .filter(emp -> emp.getAge() > 25)           // 先过滤
                .map(emp -> emp.getName().toUpperCase())    // 再转换（只处理筛选后的数据）
                .count();

        // ❌ 效率较低：先转换，对所有数据进行map()操作
        long count2 = employees.stream()
                .map(emp -> emp.getName().toUpperCase())    // 先转换（处理所有数据）
                .filter(name -> name.length() > 2)          // 再过滤
                .count();

        System.out.println("优化后处理的数据量: " + count1);
        System.out.println("未优化处理的数据量: " + count2);
    }
}

class Employee
{
    private String name;
    private int age;
    private int salary;

    public Employee(String name, int age, int salary)
    {
        this.name = name;
        this.age = age;
        this.salary = salary;
    }

    public String getName() {return name;}

    public int getAge() {return age;}

    public int getSalary() {return salary;}
}
