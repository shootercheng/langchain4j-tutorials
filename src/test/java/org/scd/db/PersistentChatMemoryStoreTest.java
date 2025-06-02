package org.scd.db;

import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentChatMemoryStoreTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentChatMemoryStoreTest.class);

    @Test
    public void testQueryData() {
        DB db = DBMaker.fileDB("chat-memory.db").transactionEnable().make();
        var result = db.getAll();
        LOGGER.info("result {}", result);
    }
}
