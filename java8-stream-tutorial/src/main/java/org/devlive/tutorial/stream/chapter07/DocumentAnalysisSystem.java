package org.devlive.tutorial.stream.chapter07;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DocumentAnalysisSystem {
    public static void main(String[] args) {
        System.out.println("=== 文档关键词分析系统 ===");

        List<Document> documents = Arrays.asList(
                new Document("Java教程", Arrays.asList(
                        "Java是一门面向对象的编程语言",
                        "Java具有跨平台特性",
                        "Java广泛应用于企业开发"
                )),
                new Document("Stream API", Arrays.asList(
                        "Stream API是Java 8的重要特性",
                        "Stream提供了函数式编程能力",
                        "使用Stream可以简化集合操作"
                )),
                new Document("大数据处理", Arrays.asList(
                        "大数据处理需要高效的算法",
                        "Java在大数据领域有广泛应用",
                        "Stream API适合处理大量数据"
                ))
        );

        DocumentAnalyzer analyzer = new DocumentAnalyzer();

        // 1. 提取所有文档的所有单词
        List<String> allWords = analyzer.extractAllWords(documents);
        System.out.println("文档总词数: " + allWords.size());

        // 2. 统计词频
        Map<String, Long> wordFrequency = analyzer.analyzeWordFrequency(documents);
        System.out.println("\n高频词汇(出现2次以上):");
        wordFrequency.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + "次"));

        // 3. 查找包含特定关键词的文档
        String keyword = "Java";
        List<String> docsWithKeyword = analyzer.findDocumentsContaining(documents, keyword);
        System.out.println("\n包含'" + keyword + "'的文档:");
        docsWithKeyword.forEach(System.out::println);

        // 4. 生成文档摘要
        List<String> summaries = analyzer.generateDocumentSummaries(documents);
        System.out.println("\n文档摘要:");
        summaries.forEach(System.out::println);
    }
}

class DocumentAnalyzer {

    // 提取所有单词
    public List<String> extractAllWords(List<Document> documents) {
        return documents.stream()
                .flatMap(doc -> doc.getContents().stream())        // 拍扁所有段落
                .flatMap(paragraph -> Arrays.stream(paragraph.split("\\s+")))  // 拍扁所有单词
                .map(word -> word.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z]", ""))  // 清理标点
                .filter(word -> !word.isEmpty())                  // 过滤空字符串
                .collect(Collectors.toList());
    }

    // 分析词频
    public Map<String, Long> analyzeWordFrequency(List<Document> documents) {
        return extractAllWords(documents).stream()
                .collect(Collectors.groupingBy(
                        Function.identity(),              // 按单词分组
                        Collectors.counting()             // 计数
                ));
    }

    // 查找包含关键词的文档
    public List<String> findDocumentsContaining(List<Document> documents, String keyword) {
        return documents.stream()
                .filter(doc -> doc.getContents().stream()
                        .anyMatch(content -> content.contains(keyword)))
                .map(Document::getTitle)
                .collect(Collectors.toList());
    }

    // 生成文档摘要
    public List<String> generateDocumentSummaries(List<Document> documents) {
        return documents.stream()
                .map(doc -> {
                    long wordCount = doc.getContents().stream()
                            .flatMap(content -> Arrays.stream(content.split("\\s+")))
                            .count();

                    String firstSentence = doc.getContents().isEmpty() ?
                            "" : doc.getContents().get(0);

                    return String.format("%s [%d词] - %s",
                            doc.getTitle(), wordCount,
                            firstSentence.length() > 20 ?
                                    firstSentence.substring(0, 20) + "..." : firstSentence);
                })
                .collect(Collectors.toList());
    }
}

class Document {
    private String title;
    private List<String> contents;

    public Document(String title, List<String> contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() { return title; }
    public List<String> getContents() { return contents; }
}
