package org.scd.day04.query;

import org.scd.common.query.DuckDbVectorQuery;

import static org.scd.common.Constant.TEXT_TABLE_NAME;

public class DuckJdbcQuery {

    public static void main(String[] args) {
        DuckDbVectorQuery.printAllData(TEXT_TABLE_NAME);
    }
}
