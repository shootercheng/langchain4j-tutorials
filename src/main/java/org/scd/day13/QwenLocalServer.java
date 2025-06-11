package org.scd.day13;

import com.alibaba.fastjson.JSON;
import dev.langchain4j.http.client.HttpMethod;
import dev.langchain4j.http.client.HttpRequest;
import dev.langchain4j.http.client.SuccessfulHttpResponse;
import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.http.client.log.LoggingHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.scd.day13.param.QwenEmbeddingParam;
import org.scd.day13.param.QwenEmbeddingResult;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
public class QwenLocalServer {
    private static final JdkHttpClient jdkHttpClient = new JdkHttpClientBuilder()
            .connectTimeout(Duration.ofMinutes(1))
            .readTimeout(Duration.ofMinutes(1))
            .build();

    private static final LoggingHttpClient loggingHttpClient = new LoggingHttpClient(jdkHttpClient,
            true, true);

    public static void main(String[] args) {
        QwenEmbeddingParam qwenEmbeddingParam = new QwenEmbeddingParam();
        qwenEmbeddingParam.setText(Arrays.asList("你好","你是谁?"));
        HttpRequest httpRequest = HttpRequest.builder()
                .url("http://127.0.0.1:5000/embed")
                .method(HttpMethod.POST)
                .addHeader("Content-Type", "application/json")
                .body(JSON.toJSONString(qwenEmbeddingParam))
                .build();
        SuccessfulHttpResponse successfulHttpResponse = loggingHttpClient.execute(httpRequest);
        log.info("result {}", successfulHttpResponse.body());
        QwenEmbeddingResult qwenEmbeddingResult = JSON.parseObject(successfulHttpResponse.body(),
                QwenEmbeddingResult.class);
        log.info("embed success {}", qwenEmbeddingResult.embedSuccess());
    }
}
