package com.broadridge.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.broadridge.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RawMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RawMessage.class);
        RawMessage rawMessage1 = new RawMessage();
        rawMessage1.setId(1L);
        RawMessage rawMessage2 = new RawMessage();
        rawMessage2.setId(rawMessage1.getId());
        assertThat(rawMessage1).isEqualTo(rawMessage2);
        rawMessage2.setId(2L);
        assertThat(rawMessage1).isNotEqualTo(rawMessage2);
        rawMessage1.setId(null);
        assertThat(rawMessage1).isNotEqualTo(rawMessage2);
    }
}
