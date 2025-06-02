package org.scd.day01;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.scd.common.Constant.OPENAI_API_KEY;
import static org.scd.common.Constant.OPENAI_BASE_URL;

public class StreamChatModelDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamChatModelDemo.class);

    public static void main(String[] args) {
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
    }
}
