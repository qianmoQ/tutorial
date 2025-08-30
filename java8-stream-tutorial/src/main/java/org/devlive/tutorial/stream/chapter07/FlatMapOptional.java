package org.devlive.tutorial.stream.chapter07;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlatMapOptional {
    public static void main(String[] args) {
        System.out.println("=== flatMap()处理Optional和空值 ===");

        List<Department> departments = Arrays.asList(
                new Department("开发部", Arrays.asList("张三", "李四", "王五")),
                new Department("测试部", Collections.emptyList()),  // 空列表
                new Department("产品部", Arrays.asList("赵六", "孙七")),
                new Department("设计部", null)  // null列表
        );

        // 安全地获取所有员工姓名
        List<String> allEmployees = departments.stream()
                .flatMap(dept -> {
                    List<String> employees = dept.getEmployees();
                    // 安全处理null和空集合
                    return employees == null ?
                            java.util.stream.Stream.empty() :
                            employees.stream();
                })
                .collect(Collectors.toList());

        System.out.println("所有员工: " + allEmployees);

        // 使用Optional更优雅地处理
        List<String> allEmployeesOptional = departments.stream()
                .flatMap(dept -> Optional.ofNullable(dept.getEmployees())
                        .orElse(Collections.emptyList())
                        .stream())
                .collect(Collectors.toList());

        System.out.println("使用Optional处理: " + allEmployeesOptional);

        // 统计非空部门数量
        long nonEmptyDepts = departments.stream()
                .flatMap(dept -> {
                    List<String> employees = dept.getEmployees();
                    return employees != null && !employees.isEmpty() ?
                            java.util.stream.Stream.of(dept) :
                            java.util.stream.Stream.empty();
                })
                .count();

        System.out.println("有员工的部门数量: " + nonEmptyDepts);
    }
}

class Department {
    private String name;
    private List<String> employees;

    public Department(String name, List<String> employees) {
        this.name = name;
        this.employees = employees;
    }

    public String getName() { return name; }
    public List<String> getEmployees() { return employees; }
}
