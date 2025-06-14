package org.scd.day03.tools;

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

public class ChatWithTool {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatWithTool.class);

    public static void main(String[] args) {
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
    }
}
