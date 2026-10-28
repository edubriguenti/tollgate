package dev.tollgate.simulator;

import dev.tollgate.domain.CallDetailRecord;
import dev.tollgate.domain.CallStatus;
import dev.tollgate.domain.Msisdn;
import dev.tollgate.domain.TelecomEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.random.RandomGenerator;

/** Long answered calls to sequential numbers in a premium-rate range. */
public final class IrsfBehavior implements Behavior {
    private static final Duration MEAN_INTERVAL = Duration.ofSeconds(1);

    private final String prefix;
    // only this subscriber's virtual thread touches it
    private int next;

    public IrsfBehavior(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public FraudPattern pattern() {
        return FraudPattern.IRSF;
    }

    @Override
    public Duration nextDelay(RandomGenerator rnd) {
        return Behavior.exponential(MEAN_INTERVAL, rnd);
    }

    @Override
    public TelecomEvent nextEvent(Msisdn origin, RandomGenerator rnd) {
        Msisdn destination = Msisdns.withPrefix(prefix, next++ % 1000, 3);
        return new CallDetailRecord(UUID.randomUUID(), Instant.now(), origin, destination,
                Duration.ofSeconds(rnd.nextLong(10 * 60, 60 * 60 + 1)), CallStatus.ANSWERED);
    }
}
