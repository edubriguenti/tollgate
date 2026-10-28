package dev.tollgate.simulator;

import dev.tollgate.domain.CallDetailRecord;
import dev.tollgate.domain.CallStatus;
import dev.tollgate.domain.Msisdn;
import dev.tollgate.domain.SmsEvent;
import dev.tollgate.domain.TelecomEvent;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class BehaviorTest {

    private static final Msisdn ORIGIN = Msisdns.domestic(1);

    private static List<TelecomEvent> events(Behavior behavior) {
        var random = new Random(42);
        return IntStream.range(0, 1_000).mapToObj(i -> behavior.nextEvent(ORIGIN, random)).toList();
    }

    private static List<CallDetailRecord> cdrs(List<TelecomEvent> events) {
        assertThat(events).allSatisfy(e -> assertThat(e).isInstanceOf(CallDetailRecord.class));
        return events.stream().map(CallDetailRecord.class::cast).toList();
    }

    private static List<SmsEvent> smss(List<TelecomEvent> events) {
        assertThat(events).allSatisfy(e -> assertThat(e).isInstanceOf(SmsEvent.class));
        return events.stream().map(SmsEvent.class::cast).toList();
    }

    private static double distinctRatio(List<? extends TelecomEvent> events) {
        return (double) events.stream().map(e -> e.destination().value()).distinct().count() / events.size();
    }

    @Test
    void subscriberNumbersAreUniqueE164() {
        assertThat(Msisdns.domestic(0).value()).isEqualTo("+5511900000000");
        assertThat(Msisdns.domestic(99_999).value()).isEqualTo("+5511900099999");
    }

    @Test
    void normalSmsShareIsAboutThirtyPercent() {
        var events = events(new NormalBehavior(Duration.ofSeconds(10), 0.3));

        long sms = events.stream().filter(SmsEvent.class::isInstance).count();

        assertThat(sms / 1_000.0).isBetween(0.27, 0.33);
        for (TelecomEvent event : events) {
            assertThat(event.origin()).isEqualTo(ORIGIN);
            switch (event) {
                case SmsEvent s -> assertThat(s.segments()).isBetween(1, 3);
                case CallDetailRecord c -> assertThat(c.duration())
                        .isBetween(Duration.ZERO, Duration.ofSeconds(600));
            }
        }
    }

    @Test
    void normalSendsAboutTenPercentInternational() {
        var events = events(new NormalBehavior(Duration.ofSeconds(10), 0.3));

        long international = events.stream().filter(e -> !e.destination().value().startsWith("+55")).count();

        assertThat(international / 1_000.0).isBetween(0.07, 0.13);
    }

    @Test
    void irsfIsLongAnsweredCallsToPremiumNumbers() {
        var cdrs = cdrs(events(new IrsfBehavior("+88213")));

        assertThat(cdrs).allSatisfy(cdr -> {
            assertThat(cdr.callStatus()).isEqualTo(CallStatus.ANSWERED);
            assertThat(cdr.duration()).isGreaterThanOrEqualTo(Duration.ofMinutes(10));
            assertThat(Msisdns.PREMIUM_PREFIXES.stream().anyMatch(cdr.destination().value()::startsWith)).isTrue();
        });
    }

    @Test
    void wangiriIsUnansweredNearZeroDurationSweep() {
        var cdrs = cdrs(events(new WangiriBehavior(0)));

        assertThat(cdrs).allSatisfy(cdr -> {
            assertThat(cdr.callStatus()).isEqualTo(CallStatus.NO_ANSWER);
            assertThat(cdr.duration()).isLessThan(Duration.ofSeconds(3));
        });
        assertThat(distinctRatio(cdrs)).isGreaterThanOrEqualTo(0.99);
    }

    @Test
    void smsPumpingIsSequentialSmsInOnePrefix() {
        var smss = smss(events(new SmsPumpingBehavior("+88346")));

        assertThat(smss).allSatisfy(sms -> assertThat(sms.destination().value()).startsWith("+88346"));
        List<String> destinations = smss.stream().map(s -> s.destination().value()).toList();
        assertThat(destinations).isSorted().doesNotHaveDuplicates();
    }

    @Test
    void simBoxIsDomesticAnsweredCallsWithDistinctDestinations() {
        var events = events(new SimBoxBehavior());

        assertThat(events).noneMatch(SmsEvent.class::isInstance);
        assertThat(events).allSatisfy(e -> assertThat(e.destination().value()).startsWith("+55"));
        assertThat(cdrs(events)).allSatisfy(cdr -> {
            assertThat(cdr.callStatus()).isEqualTo(CallStatus.ANSWERED);
            assertThat(cdr.duration()).isBetween(Duration.ofMinutes(1), Duration.ofMinutes(5));
        });
        assertThat(distinctRatio(events)).isGreaterThanOrEqualTo(0.95);
    }

    @Test
    void wangiriMeanDelayIsAbout200Ms() {
        var behavior = new WangiriBehavior(0);
        var random = new Random(42);

        double meanMs = IntStream.range(0, 10_000)
                .mapToLong(i -> behavior.nextDelay(random).toNanos())
                .average().orElseThrow() / 1e6;

        assertThat(meanMs).isBetween(190.0, 210.0);
    }
}
