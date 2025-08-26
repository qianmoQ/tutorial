package org.devlive.tutorial.stream.chapter05;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProductFilterSystem
{
    public static void main(String[] args)
    {
        System.out.println("=== 商品筛选系统 ===");

        List<Product> products = Arrays.asList(
                new Product("iPhone14", "电子产品", 6999.0, 4.8, true),
                new Product("华为Mate50", "电子产品", 5999.0, 4.7, true),
                new Product("Nike跑鞋", "运动用品", 899.0, 4.5, true),
                new Product("Adidas篮球鞋", "运动用品", 1299.0, 4.6, false),
                new Product("《Java编程思想》", "图书", 89.0, 4.9, true),
                new Product("MacBook Pro", "电子产品", 18999.0, 4.9, true),
                new Product("小米电视", "电子产品", 2999.0, 4.3, true)
        );

        ProductFilter filter = new ProductFilter();

        // 场景1: 价格筛选
        System.out.println("场景1: 价格在1000-10000元的商品");
        List<Product> priceFiltered = filter.filterByPriceRange(products, 1000, 10000);
        priceFiltered.forEach(System.out::println);

        // 场景2: 多条件组合筛选
        System.out.println("\n场景2: 电子产品 + 有库存 + 评分>4.5");
        List<Product> complexFiltered = filter.filterByMultipleConditions(
                products, "电子产品", 4.5, true);
        complexFiltered.forEach(System.out::println);

        // 场景3: 动态条件筛选
        System.out.println("\n场景3: 用户自定义筛选");
        FilterCriteria criteria = new FilterCriteria();
        criteria.minPrice = 500.0;
        criteria.maxPrice = 8000.0;
        criteria.minRating = 4.6;
        criteria.inStock = true;

        List<Product> dynamicFiltered = filter.filterByCriteria(products, criteria);
        dynamicFiltered.forEach(System.out::println);
    }
}

class ProductFilter
{

    // 价格区间筛选
    public List<Product> filterByPriceRange(List<Product> products, double minPrice, double maxPrice)
    {
        return products.stream()
                .filter(p -> p.getPrice() >= minPrice)
                .filter(p -> p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    // 多条件组合筛选
    public List<Product> filterByMultipleConditions(List<Product> products,
            String category, double minRating, boolean inStock)
    {
        return products.stream()
                .filter(p -> category.equals(p.getCategory()))
                .filter(p -> p.getRating() > minRating)
                .filter(p -> p.isInStock() == inStock)
                .collect(Collectors.toList());
    }

    // 动态条件筛选
    public List<Product> filterByCriteria(List<Product> products, FilterCriteria criteria)
    {
        Predicate<Product> predicate = p -> true;  // 初始条件：全部通过

        if (criteria.category != null) {
            predicate = predicate.and(p -> criteria.category.equals(p.getCategory()));
        }
        if (criteria.minPrice != null) {
            predicate = predicate.and(p -> p.getPrice() >= criteria.minPrice);
        }
        if (criteria.maxPrice != null) {
            predicate = predicate.and(p -> p.getPrice() <= criteria.maxPrice);
        }
        if (criteria.minRating != null) {
            predicate = predicate.and(p -> p.getRating() >= criteria.minRating);
        }
        if (criteria.inStock != null) {
            predicate = predicate.and(p -> p.isInStock() == criteria.inStock);
        }

        return products.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
}

class Product
{
    private String name;
    private String category;
    private double price;
    private double rating;
    private boolean inStock;

    public Product(String name, String category, double price, double rating, boolean inStock)
    {
        this.name = name;
        this.category = category;
        this.price = price;
        this.rating = rating;
        this.inStock = inStock;
    }

    // getter方法
    public String getName() {return name;}

    public String getCategory() {return category;}

    public double getPrice() {return price;}

    public double getRating() {return rating;}

    public boolean isInStock() {return inStock;}

    @Override
    public String toString()
    {
        return String.format("%s [%s] ¥%.0f 评分:%.1f %s",
                name, category, price, rating, inStock ? "有库存" : "无库存");
    }
}

class FilterCriteria
{
    String category;
    Double minPrice;
    Double maxPrice;
    Double minRating;
    Boolean inStock;
}
