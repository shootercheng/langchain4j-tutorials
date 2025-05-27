package org.scd.day02.client;

import dev.langchain4j.http.client.HttpRequest;
import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.http.client.sse.ServerSentEventListener;
import dev.langchain4j.http.client.sse.ServerSentEventParser;
import org.scd.day02.sse.CustomServerSentEventParser;

public class CustomHttpClient extends JdkHttpClient {

    public CustomHttpClient(JdkHttpClientBuilder builder) {
        super(builder);
    }

    @Override
    public void execute(HttpRequest request, ServerSentEventListener listener) {
        execute(request, new CustomServerSentEventParser(), listener);
    }

    @Override
    public void execute(HttpRequest request, ServerSentEventParser parser, ServerSentEventListener listener) {
        super.execute(request, new CustomServerSentEventParser(), listener);
    }
}
