package org.scd.day03.memory;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import org.scd.day03.Assistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.scd.common.Constant.OPENAI_API_KEY;
import static org.scd.common.Constant.OPENAI_BASE_URL;

public class AiServiceChatMemory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AiServiceChatMemory.class);

    public static void main(String[] args) {
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

    }
}
