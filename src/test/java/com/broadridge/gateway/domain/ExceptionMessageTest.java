package com.broadridge.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.broadridge.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExceptionMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExceptionMessage.class);
        ExceptionMessage exceptionMessage1 = new ExceptionMessage();
        exceptionMessage1.setId(1L);
        ExceptionMessage exceptionMessage2 = new ExceptionMessage();
        exceptionMessage2.setId(exceptionMessage1.getId());
        assertThat(exceptionMessage1).isEqualTo(exceptionMessage2);
        exceptionMessage2.setId(2L);
        assertThat(exceptionMessage1).isNotEqualTo(exceptionMessage2);
        exceptionMessage1.setId(null);
        assertThat(exceptionMessage1).isNotEqualTo(exceptionMessage2);
    }
}
