package org.scd.day13.param;

import lombok.Data;

import java.util.List;

@Data
public class QwenEmbeddingResult {
    public static final String SUCCESS_CODE = "0000";

    private String code;

    private List<float[]> data;

    private String msg;

    public boolean embedSuccess() {
        return SUCCESS_CODE.equals(code);
    }
}
