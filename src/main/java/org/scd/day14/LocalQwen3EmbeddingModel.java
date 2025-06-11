package org.scd.day14;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.http.client.HttpClient;
import dev.langchain4j.http.client.HttpMethod;
import dev.langchain4j.http.client.HttpRequest;
import dev.langchain4j.http.client.SuccessfulHttpResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.scd.day13.param.QwenEmbeddingParam;
import org.scd.day13.param.QwenEmbeddingResult;

import java.util.Collections;
import java.util.List;

@Slf4j
public class LocalQwen3EmbeddingModel implements EmbeddingModel {
    private HttpClient httpClient;

    private String embeddingUrl;

    public LocalQwen3EmbeddingModel(HttpClient httpClient, String embeddingUrl) {
        this.httpClient = httpClient;
        this.embeddingUrl = embeddingUrl;
    }

    @Override
    public Response<Embedding> embed(String text) {
        return embed(TextSegment.from(text));
    }

    @Override
    public Response<Embedding> embed(TextSegment textSegment) {
        QwenEmbeddingParam qwenEmbeddingParam = new QwenEmbeddingParam();
        qwenEmbeddingParam.setText(Collections.singletonList(textSegment.text()));
        QwenEmbeddingResult qwenEmbeddingResult = apiEmbedRequest(qwenEmbeddingParam);
        if (!qwenEmbeddingResult.embedSuccess()) {
            throw new RuntimeException("embedding error");
        }
        return new Response<>(new Embedding(qwenEmbeddingResult.getData().get(0)));
    }

    private QwenEmbeddingResult apiEmbedRequest(QwenEmbeddingParam qwenEmbeddingParam) {
        HttpRequest httpRequest = HttpRequest.builder()
                .url(embeddingUrl)
                .method(HttpMethod.POST)
                .addHeader("Content-Type", "application/json")
                .body(JSON.toJSONString(qwenEmbeddingParam))
                .build();
        SuccessfulHttpResponse successfulHttpResponse = httpClient.execute(httpRequest);
        return JSONObject.parseObject(successfulHttpResponse.body(), QwenEmbeddingResult.class);
    }

    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
        QwenEmbeddingParam qwenEmbeddingParam = new QwenEmbeddingParam();
        List<String> textList = textSegments.stream().map(TextSegment::text).toList();
        qwenEmbeddingParam.setText(textList);
        QwenEmbeddingResult qwenEmbeddingResult = apiEmbedRequest(qwenEmbeddingParam);
        if (!qwenEmbeddingResult.embedSuccess()) {
            throw new RuntimeException("embedding error");
        }
        List<Embedding> embeddingList = qwenEmbeddingResult.getData().stream()
                .map(Embedding::new).toList();
        return new Response<>(embeddingList);
    }

    @Override
    public int dimension() {
        return 1024;
    }
}
