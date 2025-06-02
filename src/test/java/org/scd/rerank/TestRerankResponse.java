package org.scd.rerank;

import org.junit.Test;
import org.scd.day08.param.RerankResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestRerankResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRerankResponse.class);

    @Test
    public void testSortOutPutRes() {
        RerankResponse.Output output = new RerankResponse.Output();
        List<RerankResponse.Result> resultList = new ArrayList<>();
        RerankResponse.Result result1 = new RerankResponse.Result();
        result1.setIndex(2);
        result1.setRelevanceScore(0.5);
        resultList.add(result1);
        RerankResponse.Result result2 = new RerankResponse.Result();
        result2.setIndex(1);
        result2.setRelevanceScore(0.3);
        resultList.add(result2);
        output.setResults(resultList);
        List<Double> scoreList = output.getResults().stream()
                .sorted(RerankResponse.Result::getIndex)
                .map(RerankResponse.Result::getRelevanceScore)
                .collect(Collectors.toList());
        LOGGER.info("score list {}", scoreList);
    }
}
