package org.scd.day11.websearch.param;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class QianfanResponse {
    private List<SearchResult> references;

    @JSONField(name = "request_id")
    private String requestId;

    @Data
    public static class SearchResult {
        private String content;
        private String date;
        private int id;
        private String title;
        private String type;
        private String url;
        private String webAnchor;
    }
}
