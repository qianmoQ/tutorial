package org.devlive.tutorial.stream.chapter05;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeFilter
{
    public static void main(String[] args)
    {
        System.out.println("=== 员工信息筛选系统 ===");

        List<Employee> employees = Arrays.asList(
                new Employee("张三", 28, "开发部", 12000, true),
                new Employee("李四", 35, "产品部", 15000, true),
                new Employee("王五", 23, "测试部", 8000, false),
                new Employee("赵六", 31, "开发部", 18000, true),
                new Employee("孙七", 26, "设计部", 10000, true),
                new Employee("周八", 42, "管理部", 25000, true),
                new Employee("吴九", 29, "开发部", 13000, false)
        );

        // 需求1: 筛选开发部的员工
        System.out.println("需求1: 开发部员工");
        List<Employee> developers = employees.stream()
                .filter(emp -> "开发部".equals(emp.getDepartment()))
                .collect(Collectors.toList());
        developers.forEach(System.out::println);

        // 需求2: 筛选活跃且薪资大于10000的员工
        System.out.println("\n需求2: 活跃且高薪员工");
        List<Employee> activeHighPaid = employees.stream()
                .filter(Employee::isActive)                    // 方法引用
                .filter(emp -> emp.getSalary() > 10000)
                .collect(Collectors.toList());
        activeHighPaid.forEach(System.out::println);

        // 需求3: 筛选30岁以下的技术岗位员工
        System.out.println("\n需求3: 30岁以下的技术岗位员工");
        List<String> techDepts = Arrays.asList("开发部", "测试部");
        List<Employee> youngTechies = employees.stream()
                .filter(emp -> emp.getAge() < 30)
                .filter(emp -> techDepts.contains(emp.getDepartment()))
                .collect(Collectors.toList());
        youngTechies.forEach(System.out::println);

        // 需求4: 复杂筛选 - 管理层候选人
        System.out.println("\n需求4: 管理层候选人（年龄>30，薪资>15000，活跃）");
        List<Employee> managementCandidates = employees.stream()
                .filter(emp -> emp.getAge() > 30)           // 年龄筛选
                .filter(emp -> emp.getSalary() > 15000)     // 薪资筛选
                .filter(Employee::isActive)                 // 活跃度筛选
                .collect(Collectors.toList());
        managementCandidates.forEach(System.out::println);
    }
}

class Employee
{
    private String name;
    private int age;
    private String department;
    private int salary;
    private boolean isActive;

    public Employee(String name, int age, String department, int salary, boolean isActive)
    {
        this.name = name;
        this.age = age;
        this.department = department;
        this.salary = salary;
        this.isActive = isActive;
    }

    // getter方法
    public String getName() {return name;}

    public int getAge() {return age;}

    public String getDepartment() {return department;}

    public int getSalary() {return salary;}

    public boolean isActive() {return isActive;}

    @Override
    public String toString()
    {
        return String.format("%s(%d岁, %s, %d元, %s)",
                name, age, department, salary, isActive ? "活跃" : "不活跃");
    }
}
