package org.scd.day11.websearch;

import com.alibaba.fastjson.JSON;
import dev.langchain4j.http.client.HttpClient;
import dev.langchain4j.http.client.HttpMethod;
import dev.langchain4j.http.client.HttpRequest;
import dev.langchain4j.http.client.SuccessfulHttpResponse;
import dev.langchain4j.web.search.*;
import lombok.extern.slf4j.Slf4j;
import org.scd.day11.websearch.param.QianfanResponse;
import org.scd.day11.websearch.param.QianfanSearchParam;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class BaiduQianfanSearchEngine implements WebSearchEngine {
    private String searchUrl;

    private String baiduApiKey;

    private HttpClient httpClient;

    public BaiduQianfanSearchEngine(HttpClient httpClient, String searchUrl,
                                    String baiduApiKey) {
        this.httpClient = httpClient;
        this.searchUrl = searchUrl;
        this.baiduApiKey = baiduApiKey;
    }

    @Override
    public WebSearchResults search(WebSearchRequest webSearchRequest) {
        QianfanSearchParam.Message message = QianfanSearchParam.Message.builder()
                .role("user")
                .content(webSearchRequest.searchTerms())
                .build();
        QianfanSearchParam qianfanSearchParam = QianfanSearchParam.builder()
                .messages(Collections.singletonList(message))
                .build();
        HttpRequest httpRequest = HttpRequest.builder()
                .url(searchUrl)
                .method(HttpMethod.POST)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Appbuilder-Authorization", "Bearer " + baiduApiKey)
                .body(JSON.toJSONString(qianfanSearchParam))
                .build();
        SuccessfulHttpResponse successfulHttpResponse = httpClient.execute(httpRequest);
        QianfanResponse qianfanResponse = JSON.parseObject(successfulHttpResponse.body(), QianfanResponse.class);
        List<WebSearchOrganicResult> results = new ArrayList<>();
        qianfanResponse.getReferences().forEach(item -> {
            try {
                WebSearchOrganicResult webSearchOrganicResult = WebSearchOrganicResult.from(
                        item.getTitle(),
                        new URI(item.getUrl()),
                        item.getWebAnchor(),
                        item.getContent()
                );
                results.add(webSearchOrganicResult);
            } catch (URISyntaxException ignored) {
            }
        });
        return new WebSearchResults(
                WebSearchInformationResult.from((long) results.size()), results
        );
    }
}
