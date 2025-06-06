package org.scd.day11.websearch.param;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class QianfanSearchParam {
    private List<Message> messages;

    @JSONField(name = "resource_type_filter")
    private List<Map<String, Object>> resourceTypeFilter;

    @JSONField(name = "search_filter")
    private SearchFilter searchFilter;

    @Data
    @Builder
    public static class Message {
        private String content;

        private String role;
    }

    @Data
    public static class SearchFilter {
        private Map<String, Object> match;
    }
}
