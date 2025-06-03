package org.scd.encode;

import java.nio.charset.StandardCharsets;

public class UsAsciiChineseExample {

    public static void main(String[] args) throws Exception {
        String uscStr = new String("中文".getBytes(StandardCharsets.US_ASCII), StandardCharsets.ISO_8859_1);
        System.out.println(uscStr);

        byte[] bytes = uscStr.getBytes(StandardCharsets.US_ASCII);
        String chinese = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(chinese);
    }
}
