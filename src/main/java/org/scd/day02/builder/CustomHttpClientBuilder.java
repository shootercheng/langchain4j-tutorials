package org.scd.day02.builder;

import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import org.scd.day02.client.CustomHttpClient;

public class CustomHttpClientBuilder extends JdkHttpClientBuilder {
    @Override
    public JdkHttpClient build() {
        return new CustomHttpClient(this);
    }
}
