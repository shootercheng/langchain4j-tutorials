package org.scd.day08.param;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import java.util.List;

@Data
public class RerankResponse {
    private Output output;
    private Usage usage;
    @JSONField(name = "request_id")
    private String requestId;

    @Data
    public static class Output {
        private List<Result> results;
    }

    @Data
    public static class Result {
        private Document document;
        private Integer index;
        @JSONField(name = "relevance_score")
        private double relevanceScore;

        public static int getIndex(Result o1, Result o2) {
            return o1.getIndex() - o2.getIndex();
        }
    }

    @Data
    public static class Document {
        private String text;
    }

    @Data
    public static class Usage {
        @JSONField(name = "total_tokens")
        private int totalTokens;
    }
}

