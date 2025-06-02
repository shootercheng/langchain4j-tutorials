package org.scd.rerank;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;
import org.junit.Test;
import org.scd.day08.reranke.GteScoreRerankModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GteScoreRerankModelTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GteScoreRerankModelTest.class);

    /**
     *  "query": "什么是文本排序模型",
     *          "documents": [
     *          "文本排序模型广泛用于搜索引擎和推荐系统中，它们根据文本相关性对候选文本进行排序",
     *          "量子计算是计算科学的一个前沿领域",
     *          "预训练语言模型的发展给文本排序模型带来了新的进展"
     *          ]
     */
    @Test
    public void testScoreAll() {
        GteScoreRerankModel gteScoreRerankModel = new GteScoreRerankModel();
        String[] documents = {
                "文本排序模型广泛用于搜索引擎和推荐系统中，它们根据文本相关性对候选文本进行排序",
                "量子计算是计算科学的一个前沿领域",
                "预训练语言模型的发展给文本排序模型带来了新的进展"
        };
        String query = "什么是文本排序模型";
        Response<List<Double>> scoreList = gteScoreRerankModel.scoreAll( Arrays.stream(documents).map(TextSegment::from).collect(Collectors.toList()),
                query);
        LOGGER.info("score list {}", scoreList);
    }
}
