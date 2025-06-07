# Day 12

## RagReturnSource.java

本文件展示了如何使用 RAG 模型返回源信息。

### 主要功能

- 使用嵌入存储和对话模型进行复杂的对话处理和信息检索。
- 返回源信息。

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

RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
        .contentRetriever(contentRetriever)
        .build();
Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .retrievalAugmentor(retrievalAugmentor)
        .build();
Result<String> result = assistant.answerWithSource("bookings");
log.info("source : {}", result.sources());
log.info("Assistant: {}", result.content());
```

## RagWithSqlDataSource.java

本文件展示了如何使用 SQL 数据源的 RAG 模型进行复杂的对话处理和信息检索。

### 主要功能

- 使用 SQL 数据源和对话模型进行复杂的对话处理和信息检索。

### 示例代码

```java
ChatModel chatModel = OpenAiChatModel.builder()
        .baseUrl(OPENAI_BASE_URL)
        .apiKey(OPENAI_API_KEY)
        .modelName(GPT_4_O_MINI)
        .logRequests(true)
        .logResponses(true)
        .build();

ContentRetriever contentRetriever = SqlDatabaseContentRetriever.builder()
        .dataSource(new DuckDataSource(DUCKDB_PATH))
        .chatModel(chatModel)
        .build();

RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
        .contentRetriever(contentRetriever)
        .build();

Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .retrievalAugmentor(retrievalAugmentor)
        .build();
String answer = assistant.answer("查询t_text_document表id,text,metadata字段,获取前3条数据");
log.info("Assistant: {}", answer);
```