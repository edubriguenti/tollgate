package dev.tollgate.simulator;

import dev.tollgate.domain.CallDetailRecord;
import dev.tollgate.domain.Msisdn;
import dev.tollgate.domain.SmsEvent;
import dev.tollgate.domain.TelecomEvent;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class EventFactoryTest {

    private static final Msisdn ORIGIN = EventFactory.msisdnFor(1);

    @Test
    void smsRatioOneAlwaysProducesSms() {
        var random = new Random(42);
        assertThat(IntStream.range(0, 1_000).mapToObj(i -> EventFactory.next(ORIGIN, 1.0, random)))
                .allSatisfy(event -> assertThat(event).isInstanceOf(SmsEvent.class));
    }

    @Test
    void smsRatioZeroAlwaysProducesCdr() {
        var random = new Random(42);
        assertThat(IntStream.range(0, 1_000).mapToObj(i -> EventFactory.next(ORIGIN, 0.0, random)))
                .allSatisfy(event -> assertThat(event).isInstanceOf(CallDetailRecord.class));
    }

    @Test
    void thousandMixedEventsAreAllValid() {
        var random = new Random(42);
        for (int i = 0; i < 1_000; i++) {
            TelecomEvent event = EventFactory.next(ORIGIN, 0.5, random);
            assertThat(event.origin()).isEqualTo(ORIGIN);
            switch (event) {
                case SmsEvent sms -> assertThat(sms.segments()).isBetween(1, 3);
                case CallDetailRecord cdr -> assertThat(cdr.duration())
                        .isBetween(Duration.ZERO, Duration.ofSeconds(600));
            }
        }
    }

    @Test
    void subscriberNumbersAreUniqueE164() {
        assertThat(EventFactory.msisdnFor(0).value()).isEqualTo("+5511900000000");
        assertThat(EventFactory.msisdnFor(99_999).value()).isEqualTo("+5511900099999");
    }
}
