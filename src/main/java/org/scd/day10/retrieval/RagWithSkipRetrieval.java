package org.scd.day10.retrieval;

import dev.langchain4j.community.store.embedding.duckdb.DuckDBEmbeddingStore;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import org.scd.common.Assistant;

import java.util.Collection;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.scd.common.Constant.*;
import static org.scd.common.Constant.OPENAI_API_KEY;
import static org.scd.common.Utils.startConversationWith;

public class RagWithSkipRetrieval {

    public static void main(String[] args) {
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

        // Let's create a query router.
        QueryRouter queryRouter = new QueryRouter() {

            private final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from(
                    "Is the following query related to the business of the car rental company? " +
                            "Answer only 'yes', 'no' or 'maybe'. " +
                            "Query: {{it}}"
            );

            @Override
            public Collection<ContentRetriever> route(Query query) {

                Prompt prompt = PROMPT_TEMPLATE.apply(query.text());

                AiMessage aiMessage = chatModel.chat(prompt.toUserMessage()).aiMessage();
                System.out.println("LLM decided: " + aiMessage.text());

                if (aiMessage.text().toLowerCase().contains("no")) {
                    return emptyList();
                }

                return singletonList(contentRetriever);
            }
        };

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .retrievalAugmentor(retrievalAugmentor)
                .build();
        // bookings -> yes -> retrieval -> bookings + contents
        // 你好 -> no -> 你好
        startConversationWith(assistant);
    }
}
