package com.broadridge.gateway.repository.rowmapper;

import com.broadridge.gateway.domain.RawMessage;
import com.broadridge.gateway.domain.enumeration.ExecutionTypeEnum;
import com.broadridge.gateway.domain.enumeration.MessageTypeEnum;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RawMessage}, with proper type conversions.
 */
@Service
public class RawMessageRowMapper implements BiFunction<Row, String, RawMessage> {

    private final ColumnConverter converter;

    public RawMessageRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RawMessage} stored in the database.
     */
    @Override
    public RawMessage apply(Row row, String prefix) {
        RawMessage entity = new RawMessage();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setMessageType(converter.fromRow(row, prefix + "_message_type", MessageTypeEnum.class));
        entity.setExecutionType(converter.fromRow(row, prefix + "_execution_type", ExecutionTypeEnum.class));
        entity.setKafkaKey(converter.fromRow(row, prefix + "_kafka_key", String.class));
        entity.setCompanyId(converter.fromRow(row, prefix + "_company_id", String.class));
        entity.setExecutionId(converter.fromRow(row, prefix + "_execution_id", String.class));
        entity.setMessage(converter.fromRow(row, prefix + "_message", String.class));
        entity.setCreationTime(converter.fromRow(row, prefix + "_creation_time", Instant.class));
        return entity;
    }
}
