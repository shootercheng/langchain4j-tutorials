package org.scd.day03;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AiServiceStreamChatModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(AiServiceStreamChatModel.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
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
    }
}
