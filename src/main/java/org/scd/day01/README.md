# Day 01

## ChatModelDemo.java

本文件展示了如何使用 OpenAI 的对话模型进行简单的问答交互。

### 主要功能

- 使用 OpenAI 的对话模型进行问答交互。
- 输出模型的响应。

### 示例代码

```java
OpenAiChatModel model = OpenAiChatModel.builder()
        .baseUrl(OPENAI_BASE_URL)
        .apiKey(OPENAI_API_KEY)
        .modelName(OpenAiChatModelName.GPT_4_O_MINI)
        .logRequests(true)
        .logResponses(true)
        .build();
String answer = model.chat("你好,你是谁?");
System.out.println(answer);
```

## StreamChatModelDemo.java

本文件展示了如何使用 OpenAI 的流式聊天模型进行实时响应处理。

### 主要功能

- 使用 OpenAI 的流式聊天模型进行实时响应处理。
- 处理部分响应和完整响应。

### 示例代码

```java
StreamingChatModel model = OpenAiStreamingChatModel.builder()
        .baseUrl(OPENAI_BASE_URL)
        .apiKey(OPENAI_API_KEY)
        .modelName(OpenAiChatModelName.GPT_4_O_MINI)
        .logRequests(true)
        .logResponses(true)
        .build();

CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();
SystemMessage systemMessage = new SystemMessage("因中文流式输出会乱码,请输出英文");
UserMessage userMessage = new UserMessage("你好,你是谁?");
model.chat(Arrays.asList(systemMessage, userMessage), new StreamingChatResponseHandler() {

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
