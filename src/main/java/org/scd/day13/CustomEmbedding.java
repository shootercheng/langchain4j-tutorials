package org.scd.day13;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.OnnxEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.PoolingMode;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomEmbedding {

    public static void main(String[] args) {
        String pathToModel = "onnx/qwen0.6b/model.onnx";
        String pathToTokenizer = "onnx/qwen0.6b/tokenizer.json";
//        PoolingMode poolingMode = PoolingMode.MEAN;
//        EmbeddingModel embeddingModel = new OnnxEmbeddingModel(pathToModel, pathToTokenizer, poolingMode);
//
//        Response<Embedding> response1 = embeddingModel.embed("你好");
//        Response<Embedding> response2 = embeddingModel.embed("Hello");
//        System.out.println(CosineSimilarity.between(response1.content(), response2.content()));

        try (InputStream modelStream = new FileInputStream(pathToModel)) {
            byte[] modelBytes = modelStream.readAllBytes();
            OrtSession.SessionOptions options = new OrtSession.SessionOptions();
            OrtEnvironment env = OrtEnvironment.getEnvironment();
            OrtSession session = env.createSession(modelBytes, options);
        } catch (IOException | OrtException e) {
            throw new RuntimeException(e);
        }
    }
}
