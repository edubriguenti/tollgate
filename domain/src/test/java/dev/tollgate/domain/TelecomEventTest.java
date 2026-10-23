package dev.tollgate.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TelecomEventTest {

    private static final Msisdn FROM = new Msisdn("+5511999998888");
    private static final Msisdn TO = new Msisdn("+447700900123");

    @Test
    void recordAccessorsImplementTheInterface() {
        UUID id = UUID.randomUUID();
        TelecomEvent event = new SmsEvent(id, Instant.EPOCH, FROM, TO, 1);

        assertThat(event.eventId()).isEqualTo(id);
        assertThat(event.timestamp()).isEqualTo(Instant.EPOCH);
        assertThat(event.origin()).isEqualTo(FROM);
        assertThat(event.destination()).isEqualTo(TO);
    }

    @Test
    void callRejectsNegativeDuration() {
        assertThatThrownBy(() -> new CallDetailRecord(
                UUID.randomUUID(), Instant.now(), FROM, TO, Duration.ofSeconds(-1), CallStatus.ANSWERED))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void callRejectsMissingField() {
        assertThatThrownBy(() -> new CallDetailRecord(
                UUID.randomUUID(), Instant.now(), FROM, null, Duration.ZERO, CallStatus.ANSWERED))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("destination");
    }

    @Test
    void smsRejectsZeroSegments() {
        assertThatThrownBy(() -> new SmsEvent(UUID.randomUUID(), Instant.now(), FROM, TO, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
