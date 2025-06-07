# Day 07

## retrieval/RagWithModeQueryRouter.java

本文件展示了如何使用模式查询路由的 RAG 模型进行复杂的对话处理和信息检索。

### 主要功能

- 使用嵌入存储和对话模型进行复杂的对话处理和信息检索。
- 实现模式查询路由。

### 示例代码

```java
Map<ContentRetriever, String> retrieverToDescription = new HashMap<>();
retrieverToDescription.put(dataSourceOne(), "人物传记");
retrieverToDescription.put(dataSourceTwo(), "Java开发手册");
ChatModel chatModel = OpenAiChatModel.builder()
        .baseUrl(OPENAI_BASE_URL)
        .apiKey(OPENAI_API_KEY)
        .modelName(GPT_4_O_MINI)
        .logRequests(true)
        .logResponses(true)
        .build();
RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
        .queryRouter(new LanguageModelQueryRouter(chatModel, retrieverToDescription))
        .build();
Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .retrievalAugmentor(retrievalAugmentor)
        .build();
startConversationWith(assistant);

private static ContentRetriever dataSourceOne() {
    EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
            .filePath(DUCKDB_PATH)
            .tableName(RagIndexing.RAG_QUERY_COMPRESSION_TABLE)
            .build();
    return EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(2)
            .minScore(0.6D)
            .build();
}

private static ContentRetriever dataSourceTwo() {
    EmbeddingModel embeddingModel = org.scd.day06.indexing.RagIndexing.embeddingModel;
    DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
            .filePath(DUCKDB_PATH)
            .tableName(org.scd.day06.indexing.RagIndexing.RAG_JAVA_TABLE)
            .build();
    return EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(2)
            .minScore(0.6D)
            .build();
}
