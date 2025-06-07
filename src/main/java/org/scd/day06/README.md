# Day 06

## indexing/RagIndexing.java

本文件展示了如何进行 RAG 索引处理，特别是针对 PDF 文档。

### 主要功能

- 加载 PDF 文档并进行嵌入存储。
- 打印存储的数据。

### 示例代码

```java
var documentPath = "documents/java/Java开发手册(黄山版).pdf";
Document document = loadDocument(toPath(documentPath), new ApacheTikaDocumentParser());
DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
        .filePath(DUCKDB_PATH)
        .tableName(RAG_JAVA_TABLE)
        .build();
EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
        .documentSplitter(DocumentSplitters.recursive(300, 0))
        .embeddingModel(embeddingModel)
        .embeddingStore(embeddingStore)
        .build();
ingestor.ingest(document);
DuckDbVectorQuery.printAllData(RAG_JAVA_TABLE);
```

## retrieval/RagWithExpandingQuery.java

本文件展示了如何使用扩展查询的 RAG 模型进行复杂的对话处理和信息检索。

### 主要功能

- 使用嵌入存储和对话模型进行复杂的对话处理和信息检索。
- 实现扩展查询。

### 示例代码

```java
EmbeddingModel embeddingModel = RagIndexing.embeddingModel;
DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
        .filePath(DUCKDB_PATH)
        .tableName(RagIndexing.RAG_JAVA_TABLE)
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
QueryTransformer queryTransformer = new ExpandingQueryTransformer(chatModel);
RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
        .queryTransformer(queryTransformer)
        .contentRetriever(contentRetriever)
        .build();
Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .retrievalAugmentor(retrievalAugmentor)
        .build();
startConversationWith(assistant);
