# Day 08

## param/RerankRequest.java

本文件定义了重新排序请求的数据结构。

### 数据结构

- `model`: 模型名称
- `input`: 输入数据
  - `query`: 查询字符串
  - `documents`: 文档列表
- `parameters`: 参数
  - `returnDocuments`: 是否返回文档
  - `topN`: 返回的前 N 个结果

### 示例代码

```java
@Builder
@Data
public class RerankRequest {
    private String model;
    private InputData input;
    private Parameters parameters;

    @Builder
    @Data
    public static class InputData {
        private String query;
        private List<String> documents;
    }

    @Builder
    @Data
    public static class Parameters {
        private boolean returnDocuments;
        private int topN;
    }
}
```

## param/RerankResponse.java

本文件定义了重新排序响应的数据结构。

### 数据结构

- `output`: 输出数据
  - `results`: 结果列表
    - `document`: 文档
    - `index`: 索引
    - `relevanceScore`: 相关性分数
- `usage`: 使用情况
  - `totalTokens`: 总令牌数
- `requestId`: 请求 ID

### 示例代码

```java
@Data
public class RerankResponse {
    private Output output;
    private Usage usage;
    @JSONField(name = "request_id")
    private String requestId;

    @Data
    public static class Output {
        private List<Result> results;
    }

    @Data
    public static class Result {
        private Document document;
        private Integer index;
        @JSONField(name = "relevance_score")
        private double relevanceScore;

        public static int getIndex(Result o1, Result o2) {
            return o1.getIndex() - o2.getIndex();
        }
    }

    @Data
    public static class Document {
        private String text;
    }

    @Data
    public static class Usage {
        @JSONField(name = "total_tokens")
        private int totalTokens;
    }
}
```

## reranke/GteScoreRerankModel.java

本文件展示了如何实现重新排序模型。

### 主要功能

- 实现重新排序模型。
- 执行 HTTP 请求并解析响应。

### 示例代码

```java
@Override
public Response<Double> score(String text, String query) {
    HttpRequest httpRequest = buildHttpRequest(Collections.singletonList(text), query);
    SuccessfulHttpResponse successfulHttpResponse = httpClient.execute(httpRequest);
    RerankResponse rerankResponse = JSON.parseObject(successfulHttpResponse.body(), RerankResponse.class);
    return new Response<>(rerankResponse.getOutput().getResults()
            .getFirst().getRelevanceScore());
}

private HttpRequest buildHttpRequest(List<String> textList, String query) {
    RerankRequest.InputData inputData = RerankRequest.InputData.builder()
            .query(query)
            .documents(textList)
            .build();
    RerankRequest rerankRequest = RerankRequest.builder()
            .model(modelName)
            .input(inputData)
            .build();
    return HttpRequest.builder()
            .url(scoreUrl)
            .method(HttpMethod.POST)
            .addHeader("Authorization",
                    "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .body(JSON.toJSONString(rerankRequest))
            .build();
}
```

## retrieval/RagWithReRankQuery.java

本文件展示了如何使用重新排序查询的 RAG 模型进行复杂的对话处理和信息检索。

### 主要功能

- 使用嵌入存储和对话模型进行复杂的对话处理和信息检索。
- 实现重新排序查询。

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
        .maxResults(20)
        .build();

JdkHttpClient jdkHttpClient = new JdkHttpClientBuilder()
        .connectTimeout(Duration.ofMinutes(1))
        .readTimeout(Duration.ofMinutes(1))
        .build();
LoggingHttpClient loggingHttpClient = new LoggingHttpClient(jdkHttpClient,
        true, true);
GteScoreRerankModel scoringModel = GteScoreRerankModel.builder()
        .httpClient(loggingHttpClient)
        .scoreUrl(properties.getProperty("scoreUrl"))
        .modelName("gte-rerank-v2")
        .apiKey(properties.getProperty("apiKey"))
        .build();
ContentAggregator contentAggregator = ReRankingContentAggregator.builder()
        .scoringModel(scoringModel)
        .minScore(0.8)
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
        .contentAggregator(contentAggregator)
        .build();
Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .retrievalAugmentor(retrievalAugmentor)
        .build();
String agentAnswer = assistant.answer("Entrepreneurial Ventures");
LOGGER.info("Assistant: {}", agentAnswer);
