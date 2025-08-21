package org.devlive.tutorial.stream.chapter01;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamAdvantages
{
    public static void main(String[] args)
    {
        // 模拟员工数据
        List<Employee> employees = Arrays.asList(
                new Employee("张三", 25, 8000),
                new Employee("李四", 30, 12000),
                new Employee("王五", 28, 9000),
                new Employee("赵六", 35, 15000),
                new Employee("孙七", 23, 7000)
        );

        System.out.println("需求：找出年龄大于25岁且薪资大于8000的员工姓名");

        // Stream方式：简洁明了
        List<String> result = employees.stream()
                .filter(emp -> emp.getAge() > 25)           // 年龄筛选
                .filter(emp -> emp.getSalary() > 8000)      // 薪资筛选
                .map(Employee::getName)                     // 提取姓名
                .collect(Collectors.toList());              // 收集结果

        System.out.println("符合条件的员工: " + result);

        // 另一个需求：按薪资分组统计
        System.out.println("\n按薪资等级分组：");
        Map<String, List<String>> salaryGroups = employees.stream()
                .collect(Collectors.groupingBy(
                        emp -> emp.getSalary() >= 10000 ? "高薪" : "普通",
                        Collectors.mapping(Employee::getName, Collectors.toList())
                ));

        salaryGroups.forEach((group, names) ->
                System.out.println(group + "组: " + names));
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

    @Override
    public String toString()
    {
        return String.format("%s(年龄:%d, 薪资:%d)", name, age, salary);
    }
}
