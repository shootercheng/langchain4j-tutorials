# Day 04

## step.md

本文件描述了运行步骤。

### 运行步骤

1. StoreDocument 存储向量入库
2. DuckJdbcQuery 检查是否存储数据
3. RagQuery 查询向量库，模型根据已知内容回答信息

## query/DuckJdbcQuery.java

本文件展示了如何查询向量库中的数据。

### 主要功能

- 查询向量库中的数据。

### 示例代码

```java
DuckDbVectorQuery.printAllData(TEXT_TABLE_NAME);
```

## query/RagQuery.java

本文件展示了如何使用 RAG 模型进行复杂的对话处理和信息检索。

### 主要功能

- 使用嵌入存储和对话模型进行复杂的对话处理和信息检索。

### 示例代码

```java
EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
        .filePath(DUCKDB_PATH)
        .tableName(TEXT_TABLE_NAME)
        .build();
ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
        .embeddingStore(embeddingStore)
        .embeddingModel(embeddingModel)
        .maxResults(2)
        .minScore(0.6D)
        .build();
ChatModel chatModel = OpenAiChatModel.builder()
        .baseUrl(OPENAI_BASE_URL)
        .apiKey(OPENAI_API_KEY)
        .modelName(GPT_4_O_MINI)
        .logRequests(true)
        .logResponses(true)
        .build();
Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .contentRetriever(contentRetriever)
        .build();
startConversationWith(assistant);
```

## rag/EasyRag.java

本文件展示了如何实现一个简单的 RAG（Retrieval-Augmented Generation）应用。

### 主要功能

- 加载文档并创建助手服务。
- 使用嵌入存储和对话模型进行复杂的对话处理和信息检索。

### 示例代码

```java
List<Document> documents = loadDocuments(toPath("documents/"), glob("*.txt"));
Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(CHAT_MODEL)
        .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
        .contentRetriever(createContentRetriever(documents))
        .build();
startConversationWith(assistant);

private static ContentRetriever createContentRetriever(List<Document> documents) {
    InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
    EmbeddingStoreIngestor.ingest(documents, embeddingStore);
    return EmbeddingStoreContentRetriever.from(embeddingStore);
}
```

## store/StoreDocument.java

本文件展示了如何存储文档向量到数据库中。

### 主要功能

- 存储文档向量到数据库中。

### 示例代码

```java
List<Document> documents = loadDocuments(toPath("documents/"), glob("*.txt"));
DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
List<TextSegment> segments = splitter.splitAll(documents);
EmbeddingModel allMiniLmL6V2EmbeddingModel = new AllMiniLmL6V2EmbeddingModel();
Response<List<Embedding>> listResponse = allMiniLmL6V2EmbeddingModel.embedAll(segments);
DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
        .filePath(DUCKDB_PATH)
        .tableName(TEXT_TABLE_NAME)
        .build();
embeddingStore.addAll(listResponse.content(), segments);
