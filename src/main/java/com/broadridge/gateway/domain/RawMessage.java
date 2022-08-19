package com.broadridge.gateway.domain;

import com.broadridge.gateway.domain.enumeration.ExecutionTypeEnum;
import com.broadridge.gateway.domain.enumeration.MessageTypeEnum;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A RawMessage.
 */
@Table("raw_message")
public class RawMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("message_type")
    private MessageTypeEnum messageType;

    @Column("execution_type")
    private ExecutionTypeEnum executionType;

    @Column("kafka_key")
    private String kafkaKey;

    @Column("company_id")
    private String companyId;

    @Column("execution_id")
    private String executionId;

    @Column("message")
    private String message;

    @Column("creation_time")
    private Instant creationTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RawMessage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MessageTypeEnum getMessageType() {
        return this.messageType;
    }

    public RawMessage messageType(MessageTypeEnum messageType) {
        this.setMessageType(messageType);
        return this;
    }

    public void setMessageType(MessageTypeEnum messageType) {
        this.messageType = messageType;
    }

    public ExecutionTypeEnum getExecutionType() {
        return this.executionType;
    }

    public RawMessage executionType(ExecutionTypeEnum executionType) {
        this.setExecutionType(executionType);
        return this;
    }

    public void setExecutionType(ExecutionTypeEnum executionType) {
        this.executionType = executionType;
    }

    public String getKafkaKey() {
        return this.kafkaKey;
    }

    public RawMessage kafkaKey(String kafkaKey) {
        this.setKafkaKey(kafkaKey);
        return this;
    }

    public void setKafkaKey(String kafkaKey) {
        this.kafkaKey = kafkaKey;
    }

    public String getCompanyId() {
        return this.companyId;
    }

    public RawMessage companyId(String companyId) {
        this.setCompanyId(companyId);
        return this;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getExecutionId() {
        return this.executionId;
    }

    public RawMessage executionId(String executionId) {
        this.setExecutionId(executionId);
        return this;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getMessage() {
        return this.message;
    }

    public RawMessage message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreationTime() {
        return this.creationTime;
    }

    public RawMessage creationTime(Instant creationTime) {
        this.setCreationTime(creationTime);
        return this;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RawMessage)) {
            return false;
        }
        return id != null && id.equals(((RawMessage) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RawMessage{" +
            "id=" + getId() +
            ", messageType='" + getMessageType() + "'" +
            ", executionType='" + getExecutionType() + "'" +
            ", kafkaKey='" + getKafkaKey() + "'" +
            ", companyId='" + getCompanyId() + "'" +
            ", executionId='" + getExecutionId() + "'" +
            ", message='" + getMessage() + "'" +
            ", creationTime='" + getCreationTime() + "'" +
            "}";
    }
}
