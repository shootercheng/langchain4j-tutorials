package org.scd.common.jdbc;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class DuckDataSource implements DataSource {
    private final String duckFilePath;

    public DuckDataSource(String duckFilePath) {
        this.duckFilePath = duckFilePath;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DuckDbJdbc.getFileConnection(duckFilePath);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DuckDbJdbc.getFileConnection(duckFilePath);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
