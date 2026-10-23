package dev.tollgate.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BillingTest {

    private static final Msisdn FROM = new Msisdn("+5511999998888");
    private static final Msisdn TO = new Msisdn("+447700900123");

    private static CallDetailRecord call(long seconds, CallStatus status) {
        return new CallDetailRecord(UUID.randomUUID(), Instant.now(), FROM, TO, Duration.ofSeconds(seconds), status);
    }

    @ParameterizedTest(name = "{0}s -> {1} unit(s)")
    @CsvSource({"0, 0", "59, 1", "60, 1", "61, 2"})
    void answeredCallIsBilledInMinutesRoundedUp(long seconds, long expectedUnits) {
        assertThat(Billing.billableUnits(call(seconds, CallStatus.ANSWERED))).isEqualTo(expectedUnits);
    }

    @ParameterizedTest
    @EnumSource(value = CallStatus.class, names = "ANSWERED", mode = EnumSource.Mode.EXCLUDE)
    void callNotAnsweredIsFree(CallStatus status) {
        assertThat(Billing.billableUnits(call(120, status))).isZero();
    }

    @Test
    void smsIsBilledBySegment() {
        SmsEvent sms = new SmsEvent(UUID.randomUUID(), Instant.now(), FROM, TO, 3);

        assertThat(Billing.billableUnits(sms)).isEqualTo(3);
    }
}
