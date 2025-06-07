# Day 11

## retrieval/RagWithWebSearch.java

本文件展示了如何使用网络搜索的 RAG 模型进行复杂的对话处理和信息检索。

### 主要功能

- 使用嵌入存储和对话模型进行复杂的对话处理和信息检索。
- 实现网络搜索。

### 示例代码

```java
JdkHttpClient jdkHttpClient = new JdkHttpClientBuilder()
        .connectTimeout(Duration.ofMinutes(1))
        .readTimeout(Duration.ofMinutes(1))
        .build();
LoggingHttpClient loggingHttpClient = new LoggingHttpClient(jdkHttpClient,
        true, true);

WebSearchEngine webSearchEngine = new BaiduQianfanSearchEngine(loggingHttpClient,
        properties.getProperty("searchUrl"),
        properties.getProperty("baiduApiKey")
);
ContentRetriever webSearchContentRetriever = WebSearchContentRetriever.builder()
        .webSearchEngine(webSearchEngine)
        .maxResults(10)
        .build();

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

QueryRouter queryRouter = new DefaultQueryRouter(contentRetriever, webSearchContentRetriever);
RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
        .queryRouter(queryRouter)
        .build();

Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .retrievalAugmentor(retrievalAugmentor)
        .build();
startConversationWith(assistant);
```

## websearch/BaiduQianfanSearchEngine.java

本文件展示了如何实现百度千帆搜索引擎。

### 主要功能

- 实现百度千帆搜索引擎。
- 执行 HTTP 请求并解析响应。

### 示例代码

```java
public BaiduQianfanSearchEngine(HttpClient httpClient, String searchUrl,
                                String baiduApiKey) {
    this.httpClient = httpClient;
    this.searchUrl = searchUrl;
    this.baiduApiKey = baiduApiKey;
}

@Override
public WebSearchResults search(WebSearchRequest webSearchRequest) {
    QianfanSearchParam.Message message = QianfanSearchParam.Message.builder()
            .role("user")
            .content(webSearchRequest.searchTerms())
            .build();
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("type", "web");
    paramMap.put("top_k", webSearchRequest.maxResults() != null ?
            webSearchRequest.maxResults() : 10);
    QianfanSearchParam qianfanSearchParam = QianfanSearchParam.builder()
            .messages(Collections.singletonList(message))
            .resourceTypeFilter(List.of(paramMap))
            .build();
    HttpRequest httpRequest = HttpRequest.builder()
            .url(searchUrl)
            .method(HttpMethod.POST)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Appbuilder-Authorization", "Bearer " + baiduApiKey)
            .body(JSON.toJSONString(qianfanSearchParam))
            .build();
    SuccessfulHttpResponse successfulHttpResponse = httpClient.execute(httpRequest);
    QianfanResponse qianfanResponse = JSON.parseObject(successfulHttpResponse.body(), QianfanResponse.class);
    List<WebSearchOrganicResult> results = new ArrayList<>();
    qianfanResponse.getReferences().forEach(item -> {
        try {
            WebSearchOrganicResult webSearchOrganicResult = WebSearchOrganicResult.from(
                    item.getTitle(),
                    new URI(item.getUrl()),
                    item.getWebAnchor(),
                    item.getContent()
            );
            results.add(webSearchOrganicResult);
        } catch (URISyntaxException ignored) {
        }
    });
    return new WebSearchResults(
            WebSearchInformationResult.from((long) results.size()), results
    );
}
```

## param/QianfanResponse.java

本文件定义了百度千帆搜索引擎的响应数据结构。

### 数据结构

- `references`: 参考结果列表
  - `content`: 内容
  - `date`: 日期
  - `id`: ID
  - `title`: 标题
  - `type`: 类型
  - `url`: URL
  - `webAnchor`: 网页锚点
- `requestId`: 请求 ID

### 示例代码

```java
@Data
public class QianfanResponse {
    private List<SearchResult> references;

    @JSONField(name = "request_id")
    private String requestId;

    @Data
    public static class SearchResult {
        private String content;
        private String date;
        private int id;
        private String title;
        private String type;
        private String url;
        private String webAnchor;
    }
}
```

## param/QianfanSearchParam.java

本文件定义了百度千帆搜索引擎的参数数据结构。

### 数据结构

- `messages`: 消息列表
  - `content`: 内容
  - `role`: 角色
- `resourceTypeFilter`: 资源类型过滤器
- `searchFilter`: 搜索过滤器
  - `match`: 匹配条件

### 示例代码

```java
@Data
@Builder
public class QianfanSearchParam {
    private List<Message> messages;

    @JSONField(name = "resource_type_filter")
    private List<Map<String, Object>> resourceTypeFilter;

    @JSONField(name = "search_filter")
    private SearchFilter searchFilter;

    @Data
    @Builder
    public static class Message {
        private String content;
        private String role;
    }

    @Data
    public static class SearchFilter {
        private Map<String, Object> match;
    }
}
```
