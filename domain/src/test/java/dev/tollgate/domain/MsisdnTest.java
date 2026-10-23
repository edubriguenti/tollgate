package dev.tollgate.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MsisdnTest {

    @Test
    void acceptsE164Number() {
        assertThat(new Msisdn("+5511999998888").value()).isEqualTo("+5511999998888");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"11999998888", "+55", "+55119999988887777", "+55 11 99999-8888", ""})
    void rejectsInvalidNumber(String value) {
        assertThatThrownBy(() -> new Msisdn(value)).isInstanceOf(IllegalArgumentException.class);
    }
}
