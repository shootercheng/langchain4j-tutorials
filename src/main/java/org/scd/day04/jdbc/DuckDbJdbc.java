package org.scd.day04.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DuckDbJdbc {
    private static final Logger LOGGER = LoggerFactory.getLogger(DuckDbJdbc.class);

    private static final String DRIVER = "org.duckdb.DuckDBDriver";
    private static final String URL_PREFIX = "jdbc:duckdb:";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load DuckDB JDBC driver", e);
        }
    }

    public static Connection getInMemoryConnection() throws SQLException {
        return DriverManager.getConnection(URL_PREFIX + ":memory:");
    }

    public static Connection getFileConnection(String filePath) throws SQLException {
        return DriverManager.getConnection(URL_PREFIX + filePath);
    }

    // 关闭连接
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("connection close error ", e);
                throw new RuntimeException(e);
            }
        }
    }
}
