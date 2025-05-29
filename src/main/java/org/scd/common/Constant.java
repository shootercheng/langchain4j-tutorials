package org.scd.common;

import static dev.langchain4j.internal.Utils.getOrDefault;

public class Constant {
    public static final String OPENAI_API_KEY = getOrDefault(System.getenv("OPENAI_API_KEY"), "demo");

    public static final String OPENAI_BASE_URL = getOrDefault(System.getenv("OPENAI_BASE_URL"),
            "http://langchain4j.dev/demo/openai/v1");

    public static final String DUCKDB_PATH = getOrDefault(System.getenv("DUCKDB_PATH"),
            "rag.duck");

    public static final String TEXT_TABLE_NAME = "t_text_document";
}
