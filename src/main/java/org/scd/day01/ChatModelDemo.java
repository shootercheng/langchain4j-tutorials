package org.scd.day01;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import static org.scd.common.Constant.OPENAI_API_KEY;
import static org.scd.common.Constant.OPENAI_BASE_URL;

public class ChatModelDemo {


    public static void main(String[] args) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl(OPENAI_BASE_URL)
                .apiKey(OPENAI_API_KEY)
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .logRequests(true)
                .logResponses(true)
                .build();
        String answer = model.chat("你好,你是谁?");
        System.out.println(answer);
    }
}
