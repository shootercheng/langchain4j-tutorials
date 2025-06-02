package org.scd.day06.indexing;

import dev.langchain4j.community.store.embedding.duckdb.DuckDBEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.scd.common.query.DuckDbVectorQuery;

import java.util.Properties;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static org.scd.common.Constant.DUCKDB_PATH;
import static org.scd.common.Constant.MODEL_CONFIG_PATH;
import static org.scd.common.Utils.loadPropertiesByPath;
import static org.scd.common.Utils.toPath;

public class RagIndexing {
    public static final String RAG_JAVA_TABLE = "t_rag_java";

    public static Properties properties;

    static {
        properties = loadPropertiesByPath(MODEL_CONFIG_PATH);
    }

    public static final EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
            .baseUrl(properties.getProperty("baseUrl"))
            .apiKey(properties.getProperty("apiKey"))
            .modelName("text-embedding-v3") // 通义 通用文本向量-v3
            .maxSegmentsPerBatch(10)
            .logRequests(true)
            .logResponses(true)
            .build();

    public static void main(String[] args) {
        var documentPath = "documents/java/Java开发手册(黄山版).pdf";
        Document document = loadDocument(toPath(documentPath), new ApacheTikaDocumentParser());
        DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
                .filePath(DUCKDB_PATH)
                .tableName(RAG_JAVA_TABLE)
                .build();
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 0))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(document);
        DuckDbVectorQuery.printAllData(RAG_JAVA_TABLE);
    }
}
