package com.broadridge.gateway.repository.rowmapper;

import com.broadridge.gateway.domain.ExceptionMessage;
import com.broadridge.gateway.domain.enumeration.StatusTypeEnum;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ExceptionMessage}, with proper type conversions.
 */
@Service
public class ExceptionMessageRowMapper implements BiFunction<Row, String, ExceptionMessage> {

    private final ColumnConverter converter;

    public ExceptionMessageRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ExceptionMessage} stored in the database.
     */
    @Override
    public ExceptionMessage apply(Row row, String prefix) {
        ExceptionMessage entity = new ExceptionMessage();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setKafkaKey(converter.fromRow(row, prefix + "_kafka_key", String.class));
        entity.setExceptionCode(converter.fromRow(row, prefix + "_exception_code", String.class));
        entity.setExceptionMessage(converter.fromRow(row, prefix + "_exception_message", String.class));
        entity.setRetryTopic(converter.fromRow(row, prefix + "_retry_topic", String.class));
        entity.setRawMessage(converter.fromRow(row, prefix + "_raw_message", String.class));
        entity.setRetryCount(converter.fromRow(row, prefix + "_retry_count", Integer.class));
        entity.setCreationTime(converter.fromRow(row, prefix + "_creation_time", Instant.class));
        entity.setLastExecutionTime(converter.fromRow(row, prefix + "_last_execution_time", Instant.class));
        entity.setStatusType(converter.fromRow(row, prefix + "_status_type", StatusTypeEnum.class));
        return entity;
    }
}
