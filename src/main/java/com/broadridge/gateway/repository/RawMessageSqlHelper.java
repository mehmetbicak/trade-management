package com.broadridge.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class RawMessageSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("message_type", table, columnPrefix + "_message_type"));
        columns.add(Column.aliased("execution_type", table, columnPrefix + "_execution_type"));
        columns.add(Column.aliased("kafka_key", table, columnPrefix + "_kafka_key"));
        columns.add(Column.aliased("company_id", table, columnPrefix + "_company_id"));
        columns.add(Column.aliased("execution_id", table, columnPrefix + "_execution_id"));
        columns.add(Column.aliased("message", table, columnPrefix + "_message"));
        columns.add(Column.aliased("creation_time", table, columnPrefix + "_creation_time"));

        return columns;
    }
}
