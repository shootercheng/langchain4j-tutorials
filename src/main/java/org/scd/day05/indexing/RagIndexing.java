package org.scd.day05.indexing;

import dev.langchain4j.community.store.embedding.duckdb.DuckDBEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.scd.common.query.DuckDbVectorQuery;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static org.scd.common.Constant.DUCKDB_PATH;
import static org.scd.common.Utils.toPath;

public class RagIndexing {
    public static final String RAG_QUERY_COMPRESSION_TABLE = "t_rag_query_compression";

    public static void main(String[] args) {
        var documentPath = "documents/biography-of-john-doe.txt";
        Document document = loadDocument(toPath(documentPath), new TextDocumentParser());
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
                .filePath(DUCKDB_PATH)
                .tableName(RAG_QUERY_COMPRESSION_TABLE)
                .build();
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 0))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(document);
        DuckDbVectorQuery.printAllData(RAG_QUERY_COMPRESSION_TABLE);
    }
}
