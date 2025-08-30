package org.devlive.tutorial.stream.chapter07;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NestedObjectProcessing {
    public static void main(String[] args) {
        System.out.println("=== 学校班级学生数据处理 ===");

        // 构造测试数据
        List<School> schools = createSchoolData();

        // 需求1: 统计所有学生总数
        long totalStudents = schools.stream()
                .flatMap(school -> school.getClasses().stream())    // 拍扁班级
                .flatMap(clazz -> clazz.getStudents().stream())     // 拍扁学生
                .count();

        System.out.println("所有学生总数: " + totalStudents);

        // 需求2: 找出所有学生姓名
        List<String> allStudentNames = schools.stream()
                .flatMap(school -> school.getClasses().stream())
                .flatMap(clazz -> clazz.getStudents().stream())
                .map(Student::getName)  // 提取姓名
                .collect(Collectors.toList());

        System.out.println("所有学生姓名: " + allStudentNames);

        // 需求3: 找出所有优秀学生(成绩>85)
        List<Student> excellentStudents = schools.stream()
                .flatMap(school -> school.getClasses().stream())
                .flatMap(clazz -> clazz.getStudents().stream())
                .filter(student -> student.getScore() > 85)     // 过滤优秀学生
                .collect(Collectors.toList());

        System.out.println("\n优秀学生(>85分):");
        excellentStudents.forEach(System.out::println);

        // 需求4: 按学校统计学生数量
        System.out.println("\n各学校学生数量:");
        schools.forEach(school -> {
            long count = school.getClasses().stream()
                    .flatMap(clazz -> clazz.getStudents().stream())
                    .count();
            System.out.println(school.getName() + ": " + count + "人");
        });

        // 需求5: 找出成绩最高的学生
        Student topStudent = schools.stream()
                .flatMap(school -> school.getClasses().stream())
                .flatMap(clazz -> clazz.getStudents().stream())
                .max((s1, s2) -> Integer.compare(s1.getScore(), s2.getScore()))
                .orElse(null);

        if (topStudent != null) {
            System.out.println("\n成绩最高的学生: " + topStudent);
        }
    }

    private static List<School> createSchoolData() {
        // 第一小学
        List<Student> class1A = Arrays.asList(
                new Student("张三", 88),
                new Student("李四", 92),
                new Student("王五", 79)
        );
        List<Student> class1B = Arrays.asList(
                new Student("赵六", 85),
                new Student("孙七", 90)
        );

        School school1 = new School("第一小学", Arrays.asList(
                new Class("一年级A班", class1A),
                new Class("一年级B班", class1B)
        ));

        // 第二小学
        List<Student> class2A = Arrays.asList(
                new Student("周八", 94),
                new Student("吴九", 87),
                new Student("郑十", 82)
        );

        School school2 = new School("第二小学", Arrays.asList(
                new Class("一年级A班", class2A)
        ));

        return Arrays.asList(school1, school2);
    }
}

class School {
    private String name;
    private List<Class> classes;

    public School(String name, List<Class> classes) {
        this.name = name;
        this.classes = classes;
    }

    public String getName() { return name; }
    public List<Class> getClasses() { return classes; }
}

class Class {
    private String name;
    private List<Student> students;

    public Class(String name, List<Student> students) {
        this.name = name;
        this.students = students;
    }

    public String getName() { return name; }
    public List<Student> getStudents() { return students; }
}

class Student {
    private String name;
    private int score;

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() { return name; }
    public int getScore() { return score; }

    @Override
    public String toString() {
        return String.format("%s(分数:%d)", name, score);
    }
}
