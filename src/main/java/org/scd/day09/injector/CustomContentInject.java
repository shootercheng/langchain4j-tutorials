package org.scd.day09.injector;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;

import java.util.List;

import static dev.langchain4j.internal.Utils.isNotNullOrBlank;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

public class CustomContentInject extends DefaultContentInjector {

    public CustomContentInject(PromptTemplate promptTemplate) {
        super(ensureNotNull(promptTemplate, "promptTemplate"), null);
    }

    @Override
    public ChatMessage inject(List<Content> contents, ChatMessage chatMessage) {

        Prompt prompt = createPrompt(chatMessage, contents);
        if (chatMessage instanceof UserMessage message && isNotNullOrBlank(message.name())) {
            return prompt.toUserMessage(message.name());
        }

        return prompt.toUserMessage();
    }
}
