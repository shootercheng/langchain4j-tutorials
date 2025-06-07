# Day 02

## StreamChatModelWithCustomDemo.java

本文件展示了如何使用自定义 HTTP 客户端构建器的流式聊天模型。

### 主要功能

- 使用自定义 HTTP 客户端构建器的流式聊天模型进行实时响应处理。
- 处理部分响应和完整响应。

### 示例代码

```java
StreamingChatModel model = OpenAiStreamingChatModel.builder()
        .httpClientBuilder(new CustomHttpClientBuilder())
        .baseUrl(OPENAI_BASE_URL)
        .apiKey(OPENAI_API_KEY)
        .modelName(OpenAiChatModelName.GPT_4_O_MINI)
        .logRequests(true)
        .logResponses(true)
        .build();

CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();
model.chat("你好", new StreamingChatResponseHandler() {

    @Override
    public void onPartialResponse(String partialResponse) {
        LOGGER.info("part res {}", partialResponse);
    }

    @Override
    public void onCompleteResponse(ChatResponse completeResponse) {
        futureResponse.complete(completeResponse);
    }

    @Override
    public void onError(Throwable error) {
        error.printStackTrace();
    }
});
try {
    ChatResponse chatResponse = futureResponse.get(3, TimeUnit.MINUTES);
    LOGGER.info("res {}", chatResponse.aiMessage().text());
} catch (InterruptedException | ExecutionException | TimeoutException e) {
    throw new RuntimeException(e);
}
```

## builder/CustomHttpClientBuilder.java

本文件展示了如何构建自定义的 HTTP 客户端。

### 主要功能

- 构建自定义的 HTTP 客户端。

### 示例代码

```java
public class CustomHttpClientBuilder extends JdkHttpClientBuilder {
    @Override
    public JdkHttpClient build() {
        return new CustomHttpClient(this);
    }
}
```

## client/CustomHttpClient.java

本文件展示了如何实现自定义的 HTTP 客户端，包括处理服务器发送事件。

### 主要功能

- 实现自定义的 HTTP 客户端。
- 处理服务器发送事件。

### 示例代码

```java
public class CustomHttpClient extends JdkHttpClient {

    public CustomHttpClient(JdkHttpClientBuilder builder) {
        super(builder);
    }

    @Override
    public void execute(HttpRequest request, ServerSentEventListener listener) {
        execute(request, new CustomServerSentEventParser(), listener);
    }

    @Override
    public void execute(HttpRequest request, ServerSentEventParser parser, ServerSentEventListener listener) {
        super.execute(request, new CustomServerSentEventParser(), listener);
    }
}
```

## sse/CustomServerSentEventParser.java

本文件展示了如何解析服务器发送事件。

### 主要功能

- 解析服务器发送事件。
- 检测字符集编码。

### 示例代码

```java
public class CustomServerSentEventParser implements ServerSentEventParser {

    @Override
    public void parse(InputStream httpResponseBody, ServerSentEventListener listener) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            while ((bytesRead = httpResponseBody.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(output.toByteArray());
        detector.dataEnd();
        String charset = detector.getDetectedCharset();
        System.out.println(charset);
    }
}
