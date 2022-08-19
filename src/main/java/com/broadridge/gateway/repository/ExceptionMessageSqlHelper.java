package com.broadridge.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ExceptionMessageSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("kafka_key", table, columnPrefix + "_kafka_key"));
        columns.add(Column.aliased("exception_code", table, columnPrefix + "_exception_code"));
        columns.add(Column.aliased("exception_message", table, columnPrefix + "_exception_message"));
        columns.add(Column.aliased("retry_topic", table, columnPrefix + "_retry_topic"));
        columns.add(Column.aliased("raw_message", table, columnPrefix + "_raw_message"));
        columns.add(Column.aliased("retry_count", table, columnPrefix + "_retry_count"));
        columns.add(Column.aliased("creation_time", table, columnPrefix + "_creation_time"));
        columns.add(Column.aliased("last_execution_time", table, columnPrefix + "_last_execution_time"));
        columns.add(Column.aliased("status_type", table, columnPrefix + "_status_type"));

        return columns;
    }
}
