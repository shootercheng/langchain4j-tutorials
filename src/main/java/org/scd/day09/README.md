# Day 09

## injector/CustomContentInject.java

本文件展示了如何实现自定义内容注入器。

### 主要功能

- 实现自定义内容注入器。
- 创建提示模板。

### 示例代码

```java
public CustomContentInject(PromptTemplate promptTemplate) {
    super(ensureNotNull(promptTemplate, "promptTemplate"), null);
}

@Override
public ChatMessage inject(List<Content> contents, ChatMessage chatMessage) {

    Prompt prompt = createPrompt(chatMessage, contents);
    if (chatMessage instanceof UserMessage message && isNotNullOrBlank(message.name())) {
        return prompt.toUserMessage(message.name());
    }

    return prompt.toUserMessage();
}
```

## retrieval/RagWithMetaDataFilterQuery.java

本文件展示了如何使用元数据过滤的 RAG 模型进行复杂的对话处理和信息检索。

### 主要功能

- 使用嵌入存储和对话模型进行复杂的对话处理和信息检索。
- 实现元数据过滤。

### 示例代码

```java
EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
        .filePath(DUCKDB_PATH)
        .tableName(TEXT_TABLE_NAME)
        .build();

Filter miles = metadataKey("file_name").isEqualTo("miles-of-smiles-terms-of-use.txt");
ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
        .embeddingStore(embeddingStore)
        .embeddingModel(embeddingModel)
        .filter(miles)
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

ContentInjector contentInjector = new CustomContentInject(
        PromptTemplate.from("""
                   ###根据已知信息回答用户输入的问题，如果已知信息无法回复用户的问题，请告诉用户根据已知信息无法回答，并且给出你的专业解答###

                   用户输入的问题:{{userMessage}}

                   已知信息:
                    {{contents}}
                """)
);
RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
        .contentRetriever(contentRetriever)
        .contentInjector(contentInjector)
        .build();

Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .retrievalAugmentor(retrievalAugmentor)
        .build();
startConversationWith(assistant);
```

## retrieval/RagWithMetaDataQuery.java

本文件展示了如何使用元数据查询的 RAG 模型进行复杂的对话处理和信息检索。

### 主要功能

- 使用嵌入存储和对话模型进行复杂的对话处理和信息检索。
- 实现元数据查询。

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

ContentInjector contentInjector = DefaultContentInjector.builder()
        .metadataKeysToInclude(asList("file_name", "index", "absolute_directory_path"))
        .build();

RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
        .contentRetriever(contentRetriever)
        .contentInjector(contentInjector)
        .build();
Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .retrievalAugmentor(retrievalAugmentor)
        .build();
startConversationWith(assistant);
```
