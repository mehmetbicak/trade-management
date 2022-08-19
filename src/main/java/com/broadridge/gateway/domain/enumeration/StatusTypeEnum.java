package com.broadridge.gateway.domain.enumeration;

/**
 * The StatusTypeEnum enumeration.
 */
public enum StatusTypeEnum {
    NON_RETRYABLE,
    RETRYABLE,
    IN_PROGRESS,
    RESOLVED,
    MAX_RETRY_EXCEEDED,
}
