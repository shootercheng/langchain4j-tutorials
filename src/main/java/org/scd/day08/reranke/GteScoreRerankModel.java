package org.scd.day08.reranke;

import com.alibaba.fastjson.JSON;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.http.client.HttpMethod;
import dev.langchain4j.http.client.HttpRequest;
import dev.langchain4j.http.client.SuccessfulHttpResponse;
import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.http.client.log.LoggingHttpClient;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.scoring.ScoringModel;
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
    public static Properties properties;

    static {
        properties = loadPropertiesByPath(MODEL_CONFIG_PATH);
    }

    private final JdkHttpClient jdkHttpClient = new JdkHttpClientBuilder()
            .connectTimeout(Duration.ofMinutes(1))
            .readTimeout(Duration.ofMinutes(1))
            .build();

    private final LoggingHttpClient loggingHttpClient = new LoggingHttpClient(jdkHttpClient,
            true, true);

    @Override
    public Response<Double> score(String text, String query) {
        HttpRequest httpRequest = buildHttpRequest(Collections.singletonList(text), query);
        SuccessfulHttpResponse successfulHttpResponse = loggingHttpClient.execute(httpRequest);
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
                .model("gte-rerank-v2")
                .input(inputData)
                .build();
        return HttpRequest.builder()
                .url(properties.getProperty("scoreUrl"))
                .method(HttpMethod.POST)
                .addHeader("Authorization",
                        "Bearer " + properties.getProperty("apiKey"))
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
        SuccessfulHttpResponse successfulHttpResponse = loggingHttpClient.execute(httpRequest);
        RerankResponse rerankResponse = JSON.parseObject(successfulHttpResponse.body(), RerankResponse.class);
        List<Double> doubles = rerankResponse.getOutput().getResults().stream()
                .sorted(RerankResponse.Result::getIndex).map(RerankResponse.Result::getRelevanceScore)
                .collect(Collectors.toList());
        return new Response<>(doubles);
    }
}
