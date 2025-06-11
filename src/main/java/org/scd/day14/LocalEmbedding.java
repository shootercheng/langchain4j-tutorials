package org.scd.day14;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.http.client.log.LoggingHttpClient;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LocalEmbedding {
    private static final JdkHttpClient jdkHttpClient = new JdkHttpClientBuilder()
            .connectTimeout(Duration.ofMinutes(1))
            .readTimeout(Duration.ofMinutes(1))
            .build();

    private static final LoggingHttpClient loggingHttpClient = new LoggingHttpClient(jdkHttpClient,
            true, true);

    public static void main(String[] args) {
        LocalQwen3EmbeddingModel localQwen3EmbeddingModel = new LocalQwen3EmbeddingModel(
                loggingHttpClient, "http://127.0.0.1:5000/embed"
        );
        Response<Embedding> response1 = localQwen3EmbeddingModel.embed("你好");
        Response<Embedding> response2 = localQwen3EmbeddingModel.embed("你好");
        System.out.println(CosineSimilarity.between(response1.content(), response2.content()));
        String[] inputArr = {"1.【强制】所有编程相关的命名均不能以下划线或美元符号开始，也不能以下划线或美元符号结束。\n" +
                "反例：_name / __name / $Object / name_ / name$ / Object$",
                "2.【强制】所有编程相关的命名严禁使用拼音与英文混合的方式，更不允许直接使用中文的方式。\n" +
                "说明：正确的英文拼写和语法可以让阅读者易于理解，避免歧义。注意，即使纯拼音命名方式也要避免采用。\n" +
                "正例：ali / alibaba / taobao / kaikeba / aliyun / youku / hangzhou 等国际通用的名称，可视同英文。\n" +
                "反例：DaZhePromotion【打折】/ getPingfenByName()【评分】 / String fw【福娃】/ int 变量名 = 3",
                "3.【强制】代码和注释中都要避免使用任何人类语言中的种族歧视性或侮辱性词语。\n" +
                "正例：blockList / allowList / secondary\n" +
                "反例：blackList / whiteList / slave / SB / WTF\n",
                "4.【强制】类名使用 UpperCamelCase 风格，以下情形例外：DO / PO / DTO / BO / VO / UID 等。\n" +
                "正例：ForceCode / UserDO / HtmlDTO / XmlService / TcpUdpDeal / TaPromotion\n" +
                "反例：forcecode / UserDo / HTMLDto / XMLService / TCPUDPDeal / TAPromotion\n"};
        List<TextSegment> segmentList = new ArrayList<>();
        for (String item : inputArr) {
            segmentList.add(TextSegment.from(item));
        }
        Response<List<Embedding>> listResponse = localQwen3EmbeddingModel.embedAll(segmentList);
        System.out.println(listResponse.content().size());
    }
}
