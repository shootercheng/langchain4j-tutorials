package org.scd.day04.query;

import org.scd.day04.jdbc.DuckDbJdbc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static org.scd.common.Constant.DUCKDB_PATH;
import static org.scd.common.Constant.TEXT_TABLE_NAME;

public class DuckJdbcQuery {
    private static final Logger LOGGER = LoggerFactory.getLogger(DuckJdbcQuery.class);

    public static void main(String[] args) {
        var sql = "select * from " + TEXT_TABLE_NAME;
        try (var connection = DuckDbJdbc.getFileConnection(DUCKDB_PATH);
             var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // id, embedding, text, metadata
                var id = resultSet.getString("id");
                var text = resultSet.getString("text");
                var metaData = resultSet.getString("metadata");
                var embedding = resultSet.getString("embedding");
                LOGGER.info("id:{}, text:{}, metadata: {}, embedding: {}", id, text,
                        metaData, embedding.substring(0, 30));
                LOGGER.info("-----------------------------");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
