package org.scd.day04.store;

import dev.langchain4j.community.store.embedding.duckdb.DuckDBEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;
import static org.scd.common.Constant.DUCKDB_PATH;
import static org.scd.common.Constant.TEXT_TABLE_NAME;
import static org.scd.common.Utils.glob;
import static org.scd.common.Utils.toPath;

public class StoreDocument {


    public static void main(String[] args) {
        List<Document> documents = loadDocuments(toPath("documents/"), glob("*.txt"));
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.splitAll(documents);
        AllMiniLmL6V2EmbeddingModel allMiniLmL6V2EmbeddingModel = new AllMiniLmL6V2EmbeddingModel();
        Response<List<Embedding>> listResponse = allMiniLmL6V2EmbeddingModel.embedAll(segments);
        DuckDBEmbeddingStore embeddingStore = DuckDBEmbeddingStore.builder()
                .filePath(DUCKDB_PATH)
                .tableName(TEXT_TABLE_NAME)
                .build();

        embeddingStore.addAll(listResponse.content(), segments);
    }
}
