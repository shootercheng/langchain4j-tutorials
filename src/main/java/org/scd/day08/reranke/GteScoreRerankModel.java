package org.scd.day08.reranke;

import com.alibaba.fastjson.JSON;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.http.client.HttpClient;
import dev.langchain4j.http.client.HttpMethod;
import dev.langchain4j.http.client.HttpRequest;
import dev.langchain4j.http.client.SuccessfulHttpResponse;
import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.http.client.log.LoggingHttpClient;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.scoring.ScoringModel;
import lombok.Builder;
import org.scd.day08.param.RerankRequest;
import org.scd.day08.param.RerankResponse;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.scd.common.Constant.MODEL_CONFIG_PATH;
import static org.scd.common.Utils.loadPropertiesByPath;

public class GteScoreRerankModel implements ScoringModel {
    private HttpClient httpClient;

    private String scoreUrl;

    private String apiKey;

    private String modelName;

    public GteScoreRerankModel(HttpClient httpClient, String scoreUrl, String apiKey, String modelName) {
        this.httpClient = httpClient;
        this.scoreUrl = scoreUrl;
        this.apiKey = apiKey;
        this.modelName = modelName;
    }

    public static GteScoreRerankModelBuilder builder() {
        return new GteScoreRerankModelBuilder();
    }

    public static class GteScoreRerankModelBuilder {
        private HttpClient httpClient;
        private String scoreUrl;
        private String apiKey;
        private String modelName;

        public GteScoreRerankModelBuilder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public GteScoreRerankModelBuilder scoreUrl(String scoreUrl) {
            this.scoreUrl = scoreUrl;
            return this;
        }

        public GteScoreRerankModelBuilder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public GteScoreRerankModelBuilder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public GteScoreRerankModel build() {
            return new GteScoreRerankModel(httpClient, scoreUrl, apiKey, modelName);
        }

    }

    @Override
    public Response<Double> score(String text, String query) {
        HttpRequest httpRequest = buildHttpRequest(Collections.singletonList(text), query);
        SuccessfulHttpResponse successfulHttpResponse = httpClient.execute(httpRequest);
        RerankResponse rerankResponse = JSON.parseObject(successfulHttpResponse.body(), RerankResponse.class);
        return new Response<>(rerankResponse.getOutput().getResults()
                .getFirst().getRelevanceScore());
    }

    private HttpRequest buildHttpRequest(List<String> textList, String query) {
        RerankRequest.InputData inputData = RerankRequest.InputData.builder()
                .query(query)
                .documents(textList)
                .build();
        RerankRequest rerankRequest = RerankRequest.builder()
                .model(modelName)
                .input(inputData)
                .build();
        return HttpRequest.builder()
                .url(scoreUrl)
                .method(HttpMethod.POST)
                .addHeader("Authorization",
                        "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .body(JSON.toJSONString(rerankRequest))
                .build();
    }

    @Override
    public Response<Double> score(TextSegment segment, String query) {
        return score(segment.text(), query);
    }

    @Override
    public Response<List<Double>> scoreAll(List<TextSegment> segments, String query) {
        List<String> textList = segments.stream().map(TextSegment::text)
                .collect(Collectors.toList());
        HttpRequest httpRequest = buildHttpRequest(textList, query);
        SuccessfulHttpResponse successfulHttpResponse = httpClient.execute(httpRequest);
        RerankResponse rerankResponse = JSON.parseObject(successfulHttpResponse.body(), RerankResponse.class);
        List<Double> doubles = rerankResponse.getOutput().getResults().stream()
                .sorted(RerankResponse.Result::getIndex).map(RerankResponse.Result::getRelevanceScore)
                .collect(Collectors.toList());
        return new Response<>(doubles);
    }
}
