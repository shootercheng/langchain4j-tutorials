package org.scd.day02.sse;

import dev.langchain4j.http.client.sse.ServerSentEvent;
import dev.langchain4j.http.client.sse.ServerSentEventListener;
import dev.langchain4j.http.client.sse.ServerSentEventParser;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static dev.langchain4j.http.client.sse.ServerSentEventListenerUtils.ignoringExceptions;

public class CustomServerSentEventParser implements ServerSentEventParser {

    @Override
    public void parse(InputStream httpResponseBody, ServerSentEventListener listener) {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponseBody, StandardCharsets.US_ASCII))) {
//
//            String event = null;
//            StringBuilder data = new StringBuilder();
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                if (line.isEmpty()) {
//                    if (!data.isEmpty()) {
//                        ServerSentEvent sse = new ServerSentEvent(event, data.toString());
//                        ignoringExceptions(() -> listener.onEvent(sse));
//                        event = null;
//                        data.setLength(0);
//                    }
//                    continue;
//                }
//
//                if (line.startsWith("event:")) {
//                    event = line.substring("event:".length()).trim();
//                } else if (line.startsWith("data:")) {
//                    String content = line.substring("data:".length());
//                    if (!data.isEmpty()) {
//                        data.append("\n");
//                    }
//                    data.append(content.trim());
//                }
//            }
//
//            if (!data.isEmpty()) {
//                ServerSentEvent sse = new ServerSentEvent(event, data.toString());
//                ignoringExceptions(() -> listener.onEvent(sse));
//            }
//        } catch (IOException e) {
//            ignoringExceptions(() -> listener.onError(e));
//        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            while ((bytesRead = httpResponseBody.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(output.toByteArray());
        detector.dataEnd();
        String charset = detector.getDetectedCharset();
//        US-ASCII
        System.out.println(charset);
    }
}
