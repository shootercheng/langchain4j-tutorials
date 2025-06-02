package org.scd.day07.retrieval;

import dev.langchain4j.community.store.embedding.duckdb.DuckDBEmbeddingStore;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.service.AiServices;
import org.scd.common.Assistant;
import org.scd.day05.indexing.RagIndexing;

import java.util.HashMap;
import java.util.Map;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static org.scd.common.Constant.*;
import static org.scd.common.Utils.startConversationWith;

public class RagWithModeQueryRouter {

    private static ContentRetriever dataSourceOne() {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
                .filePath(DUCKDB_PATH)
                .tableName(RagIndexing.RAG_QUERY_COMPRESSION_TABLE)
                .build();
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.6D)
                .build();
    }

    private static ContentRetriever dataSourceTwo() {
        EmbeddingModel embeddingModel = org.scd.day06.indexing.RagIndexing.embeddingModel;
        DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
                .filePath(DUCKDB_PATH)
                .tableName(org.scd.day06.indexing.RagIndexing.RAG_JAVA_TABLE)
                .build();
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.6D)
                .build();
    }

    public static void main(String[] args) {
        Map<ContentRetriever, String> retrieverToDescription = new HashMap<>();
        retrieverToDescription.put(dataSourceOne(), "人物传记");
        retrieverToDescription.put(dataSourceTwo(), "Java开发手册");
        ChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl(OPENAI_BASE_URL)
                .apiKey(OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .logRequests(true)
                .logResponses(true)
                .build();
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(new LanguageModelQueryRouter(chatModel, retrieverToDescription))
                .build();
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .retrievalAugmentor(retrievalAugmentor)
                .build();
        startConversationWith(assistant);
    }
}
