package org.scd.day08.param;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RerankRequest {
    private String model;
    private InputData input;
    private Parameters parameters;

    @Builder
    @Data
    public static class InputData {
        private String query;
        private List<String> documents;
    }

    @Builder
    @Data
    public static class Parameters {
        private boolean returnDocuments;
        private int topN;
    }
}
