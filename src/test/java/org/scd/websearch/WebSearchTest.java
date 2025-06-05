package org.scd.websearch;

import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.http.client.log.LoggingHttpClient;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchResults;
import org.junit.Test;
import org.scd.day11.websearch.BaiduQianfanSearchEngine;
import org.scd.rerank.GteScoreRerankModelTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;

import static org.scd.common.Constant.MODEL_CONFIG_PATH;
import static org.scd.common.Utils.loadPropertiesByPath;

public class WebSearchTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSearchTest.class);

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

    @Test
    public void testWebSearch() {
        BaiduQianfanSearchEngine baiduQianfanSearchEngine = new BaiduQianfanSearchEngine(loggingHttpClient,
                properties.getProperty("searchUrl"),
                properties.getProperty("baiduApiKey")
                );
        WebSearchResults webSearchResults = baiduQianfanSearchEngine.search("什么是联网搜索");
        LOGGER.info("web search result {}", webSearchResults);
    }
}
