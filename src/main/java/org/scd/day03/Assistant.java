package org.scd.day03;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

public interface Assistant {

    String chat(String userMessage);

    @SystemMessage("因中文流式输出会乱码,请输出英文")
    TokenStream chatStream(String userMessage);
}
