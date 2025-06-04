package org.scd.encode;

import java.nio.charset.StandardCharsets;

public class Iso88591ChineseExample {
    public static void main(String[] args) throws Exception {
        // 模拟ISO-8859-1编码的UTF-8中文数据
        String isoString = new String("中文".getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        System.out.println(isoString);

        // 转换步骤
        byte[] bytes = isoString.getBytes(StandardCharsets.ISO_8859_1);
        String chinese = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(chinese); // 输出: 中文
    }
}
