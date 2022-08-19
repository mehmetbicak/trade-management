package com.broadridge.gateway.domain;

import com.broadridge.gateway.domain.enumeration.StatusTypeEnum;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ExceptionMessage.
 */
@Table("exception_message")
public class ExceptionMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("kafka_key")
    private String kafkaKey;

    @Column("exception_code")
    private String exceptionCode;

    @Column("exception_message")
    private String exceptionMessage;

    @Column("retry_topic")
    private String retryTopic;

    @Column("raw_message")
    private String rawMessage;

    @Column("retry_count")
    private Integer retryCount;

    @Column("creation_time")
    private Instant creationTime;

    @Column("last_execution_time")
    private Instant lastExecutionTime;

    @Column("status_type")
    private StatusTypeEnum statusType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ExceptionMessage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKafkaKey() {
        return this.kafkaKey;
    }

    public ExceptionMessage kafkaKey(String kafkaKey) {
        this.setKafkaKey(kafkaKey);
        return this;
    }

    public void setKafkaKey(String kafkaKey) {
        this.kafkaKey = kafkaKey;
    }

    public String getExceptionCode() {
        return this.exceptionCode;
    }

    public ExceptionMessage exceptionCode(String exceptionCode) {
        this.setExceptionCode(exceptionCode);
        return this;
    }

    public void setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public String getExceptionMessage() {
        return this.exceptionMessage;
    }

    public ExceptionMessage exceptionMessage(String exceptionMessage) {
        this.setExceptionMessage(exceptionMessage);
        return this;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getRetryTopic() {
        return this.retryTopic;
    }

    public ExceptionMessage retryTopic(String retryTopic) {
        this.setRetryTopic(retryTopic);
        return this;
    }

    public void setRetryTopic(String retryTopic) {
        this.retryTopic = retryTopic;
    }

    public String getRawMessage() {
        return this.rawMessage;
    }

    public ExceptionMessage rawMessage(String rawMessage) {
        this.setRawMessage(rawMessage);
        return this;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public Integer getRetryCount() {
        return this.retryCount;
    }

    public ExceptionMessage retryCount(Integer retryCount) {
        this.setRetryCount(retryCount);
        return this;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Instant getCreationTime() {
        return this.creationTime;
    }

    public ExceptionMessage creationTime(Instant creationTime) {
        this.setCreationTime(creationTime);
        return this;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public Instant getLastExecutionTime() {
        return this.lastExecutionTime;
    }

    public ExceptionMessage lastExecutionTime(Instant lastExecutionTime) {
        this.setLastExecutionTime(lastExecutionTime);
        return this;
    }

    public void setLastExecutionTime(Instant lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public StatusTypeEnum getStatusType() {
        return this.statusType;
    }

    public ExceptionMessage statusType(StatusTypeEnum statusType) {
        this.setStatusType(statusType);
        return this;
    }

    public void setStatusType(StatusTypeEnum statusType) {
        this.statusType = statusType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExceptionMessage)) {
            return false;
        }
        return id != null && id.equals(((ExceptionMessage) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExceptionMessage{" +
            "id=" + getId() +
            ", kafkaKey='" + getKafkaKey() + "'" +
            ", exceptionCode='" + getExceptionCode() + "'" +
            ", exceptionMessage='" + getExceptionMessage() + "'" +
            ", retryTopic='" + getRetryTopic() + "'" +
            ", rawMessage='" + getRawMessage() + "'" +
            ", retryCount=" + getRetryCount() +
            ", creationTime='" + getCreationTime() + "'" +
            ", lastExecutionTime='" + getLastExecutionTime() + "'" +
            ", statusType='" + getStatusType() + "'" +
            "}";
    }
}
