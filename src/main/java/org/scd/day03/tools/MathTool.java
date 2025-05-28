package org.scd.day03.tools;

import dev.langchain4j.agent.tool.Tool;

public class MathTool {

    @Tool
    public int add(int a, int b) {
        return a + b;
    }

    @Tool
    public int multiply(int a, int b) {
        return a * b;
    }
}
