# Day 10

## retrieval/RagWithMultipleRetrievers.java

本文件展示了如何使用多个检索器的 RAG 模型进行复杂的对话处理和信息检索。

### 主要功能

- 使用嵌入存储和对话模型进行复杂的对话处理和信息检索。
- 实现多个检索器。

### 示例代码

```java
private static ContentRetriever contentRetriever1() {
    EmbeddingModel embeddingModel = RagIndexing.embeddingModel;
    DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
            .filePath(DUCKDB_PATH)
            .tableName(RagIndexing.RAG_JAVA_TABLE)
            .build();
    return EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(5)
            .minScore(0.6D)
            .build();
}

private static ContentRetriever contentRetriever2() {
    EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
            .filePath(DUCKDB_PATH)
            .tableName(TEXT_TABLE_NAME)
            .build();
    return EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(5)
            .minScore(0.6D)
            .build();
}

public static void main(String[] args) {
    ChatModel chatModel = OpenAiChatModel.builder()
            .baseUrl(OPENAI_BASE_URL)
            .apiKey(OPENAI_API_KEY)
            .modelName(GPT_4_O_MINI)
            .logRequests(true)
            .logResponses(true)
            .build();
    QueryRouter queryRouter = new DefaultQueryRouter(contentRetriever1(), contentRetriever2());
    RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
            .queryRouter(queryRouter)
            .build();
    Assistant assistant = AiServices.builder(Assistant.class)
            .chatModel(chatModel)
            .retrievalAugmentor(retrievalAugmentor)
            .build();
    startConversationWith(assistant);
}
```

## retrieval/RagWithSkipRetrieval.java

本文件展示了如何使用跳过检索的 RAG 模型进行复杂的对话处理和信息检索。

### 主要功能

- 使用嵌入存储和对话模型进行复杂的对话处理和信息检索。
- 实现跳过检索。

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
        .maxResults(5)
        .minScore(0.6D)
        .build();
ChatModel chatModel = OpenAiChatModel.builder()
        .baseUrl(OPENAI_BASE_URL)
        .apiKey(OPENAI_API_KEY)
        .modelName(GPT_4_O_MINI)
        .logRequests(true)
        .logResponses(true)
        .build();

QueryRouter queryRouter = new QueryRouter() {

    private final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from(
            "Is the following query related to the business of the car rental company? " +
                    "Answer only 'yes', 'no' or 'maybe'. " +
                    "Query: {{it}}"
    );

    @Override
    public Collection<ContentRetriever> route(Query query) {

        Prompt prompt = PROMPT_TEMPLATE.apply(query.text());

        AiMessage aiMessage = chatModel.chat(prompt.toUserMessage()).aiMessage();
        System.out.println("LLM decided: " + aiMessage.text());

        if (aiMessage.text().toLowerCase().contains("no")) {
            return emptyList();
        }

        return singletonList(contentRetriever);
    }
};

RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
        .queryRouter(queryRouter)
        .build();

Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .retrievalAugmentor(retrievalAugmentor)
        .build();
startConversationWith(assistant);
```