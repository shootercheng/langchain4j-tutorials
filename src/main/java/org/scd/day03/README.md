# Day 03

## AiServiceChatModel.java

本文件展示了如何使用 OpenAI 的对话模型创建助手服务。

### 主要功能

- 使用 OpenAI 的对话模型创建助手服务。
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
Assistant assistant = AiServices.create(Assistant.class, model);
String answer = assistant.chat("你好,你是谁?");
System.out.println(answer);
```

## AiServiceStreamChatModel.java

本文件展示了如何使用 OpenAI 的流式聊天模型创建助手服务。

### 主要功能

- 使用 OpenAI 的流式聊天模型创建助手服务。
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
Assistant assistant = AiServices.create(Assistant.class, model);
TokenStream tokenStream = assistant.chatStream("你好,你是谁?");
CompletableFuture<ChatResponse> completableFuture = new CompletableFuture<>();
tokenStream.onPartialResponse(LOGGER::info);
tokenStream.onError(throwable -> {
    LOGGER.error("{}", throwable.toString(), throwable);
});
tokenStream.onCompleteResponse(completableFuture::complete);
tokenStream.start();
ChatResponse chatResponse = completableFuture.get(2, TimeUnit.MINUTES);
String res = chatResponse.aiMessage().text();
LOGGER.info("result {}", res);
```

## Assistant.java

本文件定义了一个助手接口，包含 `chat` 和 `chatStream` 方法。

### 接口定义

```java
public interface Assistant {

    String chat(String userMessage);

    @SystemMessage("因中文流式输出会乱码,请输出英文")
    TokenStream chatStream(String userMessage);
}
```

## memory/AiServiceChatMemory.java

本文件展示了如何使用 OpenAI 的对话模型和聊天记忆创建助手服务。

### 主要功能

- 使用 OpenAI 的对话模型和聊天记忆创建助手服务。
- 处理多轮对话。

### 示例代码

```java
OpenAiChatModel model = OpenAiChatModel.builder()
        .baseUrl(OPENAI_BASE_URL)
        .apiKey(OPENAI_API_KEY)
        .modelName(OpenAiChatModelName.GPT_4_O_MINI)
        .logRequests(true)
        .logResponses(true)
        .build();
ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(model)
        .chatMemory(chatMemory)
        .build();
String answer = assistant.chat("你好,我是chengdu");
LOGGER.info("answer {}", answer);
String answer2 = assistant.chat("我是谁?");
LOGGER.info("answer2 {}", answer2);
```

## tools/ChatWithTool.java

本文件展示了如何使用 OpenAI 的对话模型和工具创建助手服务。

### 主要功能

- 使用 OpenAI 的对话模型和工具创建助手服务。
- 处理工具调用。

### 示例代码

```java
OpenAiChatModel model = OpenAiChatModel.builder()
        .baseUrl(OPENAI_BASE_URL)
        .apiKey(OPENAI_API_KEY)
        .modelName(OpenAiChatModelName.GPT_4_O_MINI)
        .logRequests(true)
        .logResponses(true)
        .build();
Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(model)
        .tools(new MathTool())
        .build();
String answer = assistant.chat("What is 1+2 and 3*4?");
LOGGER.info("answer {}", answer);
```

## tools/MathTool.java

本文件定义了两个数学工具方法：`add` 和 `multiply`。

### 工具定义

```java
public class MathTool {

    @Tool
    public int add(int a, int b) {
        return a + b;
    }

    @Tool
    public int multiply(int a, int b) {
        return a * b;
    }
}
