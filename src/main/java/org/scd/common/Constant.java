package org.scd.common;

import static dev.langchain4j.internal.Utils.getOrDefault;

public class Constant {
    public static final String OPENAI_API_KEY = getOrDefault(System.getenv("OPENAI_API_KEY"), "demo");

    public static final String OPENAI_BASE_URL = getOrDefault(System.getenv("OPENAI_BASE_URL"),
            OPENAI_BASE_URL);

    public static final String DUCKDB_PATH = getOrDefault(System.getenv("DUCKDB_PATH"),
            "rag.duck");

    public static final String TEXT_TABLE_NAME = "t_text_document";
}
