# Day 05

## indexing/RagIndexing.java

本文件展示了如何进行 RAG 索引处理。

### 主要功能

- 加载文档并进行嵌入存储。
- 打印存储的数据。

### 示例代码

```java
var documentPath = "documents/biography-of-john-doe.txt";
Document document = loadDocument(toPath(documentPath), new TextDocumentParser());
EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
        .filePath(DUCKDB_PATH)
        .tableName(RAG_QUERY_COMPRESSION_TABLE)
        .build();
EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
        .documentSplitter(DocumentSplitters.recursive(300, 0))
        .embeddingModel(embeddingModel)
        .embeddingStore(embeddingStore)
        .build();
ingestor.ingest(document);
DuckDbVectorQuery.printAllData(RAG_QUERY_COMPRESSION_TABLE);
```

## persistent/PersistentChatMemoryStore.java

本文件展示了如何实现持久化聊天记忆存储。

### 主要功能

- 获取聊天消息。
- 更新聊天消息。
- 删除聊天消息。

### 示例代码

```java
public class PersistentChatMemoryStore implements ChatMemoryStore {
    private final DB db = DBMaker.fileDB("chat-memory.db").transactionEnable().make();
    private final Map<String, String> map = db.hashMap("messages", STRING, STRING).createOrOpen();

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String json = map.get((String) memoryId);
        return messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String json = messagesToJson(messages);
        map.put((String) memoryId, json);
        db.commit();
    }

    @Override
    public void deleteMessages(Object memoryId) {
        map.remove((String) memoryId);
        db.commit();
    }
}
```

## retrieval/RagWithQueryCompression.java

本文件展示了如何使用查询压缩的 RAG 模型进行复杂的对话处理和信息检索。

### 主要功能

- 使用嵌入存储和对话模型进行复杂的对话处理和信息检索。
- 实现查询压缩。

### 示例代码

```java
EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
        .filePath(DUCKDB_PATH)
        .tableName(RagIndexing.RAG_QUERY_COMPRESSION_TABLE)
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
QueryTransformer queryTransformer = new CompressingQueryTransformer(chatModel);
RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
        .queryTransformer(queryTransformer)
        .contentRetriever(contentRetriever)
        .build();
ChatMemory chatMemory = MessageWindowChatMemory.builder()
        .maxMessages(10)
        .chatMemoryStore(new PersistentChatMemoryStore())
        .build();
Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .retrievalAugmentor(retrievalAugmentor)
        .chatMemory(chatMemory)
        .build();
startConversationWith(assistant);
