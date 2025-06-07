# langchain4j-tutorials

## 项目简介

本项目是一个基于 Java 的教程项目，主要展示了如何使用 langchain4j 库进行自然语言处理和对话模型的应用。项目涵盖了从简单的对话模型到复杂的 RAG（Retrieval-Augmented Generation）模型的使用。

## 主要功能

- **对话模型**：使用 OpenAI 的对话模型进行简单的问答交互。
- **流式聊天模型**：支持流式响应的聊天模型，能够实时处理和显示部分响应。
- **RAG 模型**：结合嵌入存储和对话模型，进行复杂的对话处理和信息检索。

## 使用的技术和库

- **langchain4j**：用于自然语言处理和对话模型的 Java 库。
- **OpenAI**：提供对话模型和嵌入模型。
- **DuckDB**：用于嵌入存储。
- **Maven**：项目构建工具。

## 项目环境配置

### 环境要求

- **Java**: 21
- **Maven**: 3.8.1

## 依赖版本

| 依赖名称                               | 版本          |
|----------------------------------------|---------------|
| dev.langchain4j:langchain4j            | 1.0.1         |
| dev.langchain4j:langchain4j-open-ai    | 1.0.1         |
| dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2 | 1.0.1     |
| dev.langchain4j:langchain4j-document-parser-apache-tika | 1.0.1      |
| dev.langchain4j:langchain4j-web-search-engine-google-custom | 1.0.1   |
| dev.langchain4j:langchain4j-experimental-sql | 1.0.1        |
| org.jsoup:jsoup                        | 1.16.2        |
| dev.langchain4j:langchain4j-community-duckdb | 1.0.0-beta4  |
| ch.qos.logback:logback-classic         | 1.5.13        |
| com.alibaba:fastjson                  | 2.0.57        |
| junit:junit                           | 4.13.1        |
| org.projectlombok:lombok              | 1.18.38       |
| com.github.albfernandez:juniversalchardet | 2.4.0     |

## 每天学习内容和目标

### Day 01
- **学习内容**: 使用 OpenAI 的对话模型进行简单的问答交互，以及支持流式响应的聊天模型。
- **学习目标**: 掌握基本的对话模型使用方法。

### Day 02
- **学习内容**: 使用自定义 HTTP 客户端构建器的流式聊天模型，以及解析服务器发送事件。
- **学习目标**: 学习如何定制和优化聊天模型。

### Day 03
- **学习内容**: 使用嵌入存储和对话模型进行复杂的对话处理和信息检索，以及实现持久化聊天记忆。
- **学习目标**: 掌握高级对话模型和记忆管理技术。

### Day 04
- **学习内容**: 实现 RAG 模型索引和查询。
- **学习目标**: 学习如何构建和使用 RAG 模型。

### Day 05
- **学习内容**: 使用查询压缩的 RAG 模型进行复杂的对话处理和信息检索。
- **学习目标**: 掌握查询压缩技术。

### Day 06
- **学习内容**: 使用扩展查询的 RAG 模型进行复杂的对话处理和信息检索。
- **学习目标**: 学习如何扩展查询以提高检索效果。

### Day 07
- **学习内容**: 使用多个检索器的 RAG 模型进行复杂的对话处理和信息检索。
- **学习目标**: 掌握多检索器技术。

### Day 08
- **学习内容**: 使用元数据过滤和查询的 RAG 模型进行复杂的对话处理和信息检索。
- **学习目标**: 学习如何利用元数据优化检索。

### Day 09
- **学习内容**: 使用网络搜索的 RAG 模型进行复杂的对话处理和信息检索。
- **学习目标**: 掌握网络搜索集成技术。

### Day 10
- **学习内容**: 使用 SQL 数据源的 RAG 模型进行复杂的对话处理和信息检索。
- **学习目标**: 学习如何与 SQL 数据库集成。

### Day 11
- **学习内容**: 使用百度千帆搜索引擎进行网络搜索。
- **学习目标**: 掌握外部搜索引擎集成。

### Day 12
- **学习内容**: RAG返回源信息、使用SQL数据库检索器
- **学习目标**：掌握返回源信息和使用SQL数据库检索器。

您可以查看每个目录中的 README.md 文件以获取更多信息。
