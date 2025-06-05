package org.scd.rerank;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.http.client.log.LoggingHttpClient;
import dev.langchain4j.model.output.Response;
import org.junit.Test;
import org.scd.day08.reranke.GteScoreRerankModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.scd.common.Constant.MODEL_CONFIG_PATH;
import static org.scd.common.Utils.loadPropertiesByPath;

public class GteScoreRerankModelTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GteScoreRerankModelTest.class);

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

    /**
     *  "query": "什么是文本排序模型",
     *          "documents": [
     *          "文本排序模型广泛用于搜索引擎和推荐系统中，它们根据文本相关性对候选文本进行排序",
     *          "量子计算是计算科学的一个前沿领域",
     *          "预训练语言模型的发展给文本排序模型带来了新的进展"
     *          ]
     */
    @Test
    public void testScoreAll() {
        GteScoreRerankModel gteScoreRerankModel = GteScoreRerankModel.builder()
                .httpClient(loggingHttpClient)
                .scoreUrl(properties.getProperty("scoreUrl"))
                .modelName("gte-rerank-v2")
                .apiKey(properties.getProperty("apiKey"))
                .build();
        String[] documents = {
                "文本排序模型广泛用于搜索引擎和推荐系统中，它们根据文本相关性对候选文本进行排序",
                "量子计算是计算科学的一个前沿领域",
                "预训练语言模型的发展给文本排序模型带来了新的进展"
        };
        String query = "什么是文本排序模型";
        Response<List<Double>> scoreList = gteScoreRerankModel.scoreAll( Arrays.stream(documents).map(TextSegment::from).collect(Collectors.toList()),
                query);
        LOGGER.info("score list {}", scoreList);
    }
}
