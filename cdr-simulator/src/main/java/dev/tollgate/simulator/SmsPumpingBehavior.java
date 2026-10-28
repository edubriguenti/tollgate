package dev.tollgate.simulator;

import dev.tollgate.domain.Msisdn;
import dev.tollgate.domain.SmsEvent;
import dev.tollgate.domain.TelecomEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.random.RandomGenerator;

/** SMS burst to sequential numbers inside a single premium-rate prefix. */
public final class SmsPumpingBehavior implements Behavior {
    private static final Duration MEAN_INTERVAL = Duration.ofMillis(100);

    private final String prefix;
    // only this subscriber's virtual thread touches it
    private long next;

    public SmsPumpingBehavior(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public FraudPattern pattern() {
        return FraudPattern.SMS_PUMPING;
    }

    @Override
    public Duration nextDelay(RandomGenerator rnd) {
        return Behavior.exponential(MEAN_INTERVAL, rnd);
    }

    @Override
    public TelecomEvent nextEvent(Msisdn origin, RandomGenerator rnd) {
        return new SmsEvent(UUID.randomUUID(), Instant.now(), origin,
                Msisdns.withPrefix(prefix, next++, 7), 1);
    }
}
