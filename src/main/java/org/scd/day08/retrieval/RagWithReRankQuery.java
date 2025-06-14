package org.scd.day08.retrieval;

import dev.langchain4j.community.store.embedding.duckdb.DuckDBEmbeddingStore;
import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.http.client.log.LoggingHttpClient;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.scoring.ScoringModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.ReRankingContentAggregator;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import org.scd.common.Assistant;
import org.scd.day05.indexing.RagIndexing;
import org.scd.day08.reranke.GteScoreRerankModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static org.scd.common.Constant.*;
import static org.scd.common.Utils.loadPropertiesByPath;

public class RagWithReRankQuery {
    private static final Logger LOGGER = LoggerFactory.getLogger(RagWithReRankQuery.class);

    public static Properties properties;

    static {
        properties = loadPropertiesByPath(MODEL_CONFIG_PATH);
    }

    public static void main(String[] args) {
        // 向量检索
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
                .filePath(DUCKDB_PATH)
                .tableName(RagIndexing.RAG_QUERY_COMPRESSION_TABLE)
                .build();
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(20)
                .build();

        JdkHttpClient jdkHttpClient = new JdkHttpClientBuilder()
                .connectTimeout(Duration.ofMinutes(1))
                .readTimeout(Duration.ofMinutes(1))
                .build();
        LoggingHttpClient loggingHttpClient = new LoggingHttpClient(jdkHttpClient,
                true, true);
        // 重排
        GteScoreRerankModel scoringModel = GteScoreRerankModel.builder()
                .httpClient(loggingHttpClient)
                .scoreUrl(properties.getProperty("scoreUrl"))
                .modelName("gte-rerank-v2")
                .apiKey(properties.getProperty("apiKey"))
                .build();
        ContentAggregator contentAggregator = ReRankingContentAggregator.builder()
                .scoringModel(scoringModel)
                .minScore(0.8)
                .build();
        ChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl(OPENAI_BASE_URL)
                .apiKey(OPENAI_API_KEY)
                .modelName(GPT_4_O_MINI)
                .logRequests(true)
                .logResponses(true)
                .build();
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .contentAggregator(contentAggregator)
                .build();
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .retrievalAugmentor(retrievalAugmentor)
                .build();
        String agentAnswer = assistant.answer("Entrepreneurial Ventures");
        LOGGER.info("Assistant: {}", agentAnswer);
    }
}
