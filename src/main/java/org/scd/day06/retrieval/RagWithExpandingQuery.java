package org.scd.day06.retrieval;

import dev.langchain4j.community.store.embedding.duckdb.DuckDBEmbeddingStore;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import org.scd.common.Assistant;
import org.scd.day06.indexing.RagIndexing;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static org.scd.common.Constant.*;
import static org.scd.common.Utils.startConversationWith;

public class RagWithExpandingQuery {

    public static void main(String[] args) {
        EmbeddingModel embeddingModel = RagIndexing.embeddingModel;
        DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
                .filePath(DUCKDB_PATH)
                .tableName(RagIndexing.RAG_JAVA_TABLE)
                .build();
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.6D)
                .build();
        ChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl(OPENAI_BASE_URL)
                .apiKey(OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .logRequests(true)
                .logResponses(true)
                .build();
        // 默认生成3个类似问题输入的查询, 使用同义词或替代句子结构，但它们都应该保留原始含义。
        QueryTransformer queryTransformer = new ExpandingQueryTransformer(chatModel);
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer)
                .contentRetriever(contentRetriever)
                .build();
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .retrievalAugmentor(retrievalAugmentor)
                .build();
        startConversationWith(assistant);
    }
}
