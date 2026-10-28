package dev.tollgate.simulator;

import dev.tollgate.domain.CallDetailRecord;
import dev.tollgate.domain.CallStatus;
import dev.tollgate.domain.Msisdn;
import dev.tollgate.domain.TelecomEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.random.RandomGenerator;

/** One-ring calls sweeping a range of domestic numbers. */
public final class WangiriBehavior implements Behavior {
    private static final Duration MEAN_INTERVAL = Duration.ofMillis(200);

    // only this subscriber's virtual thread touches it
    private int next;

    public WangiriBehavior(int start) {
        this.next = start;
    }

    @Override
    public FraudPattern pattern() {
        return FraudPattern.WANGIRI;
    }

    @Override
    public Duration nextDelay(RandomGenerator rnd) {
        return Behavior.exponential(MEAN_INTERVAL, rnd);
    }

    @Override
    public TelecomEvent nextEvent(Msisdn origin, RandomGenerator rnd) {
        return new CallDetailRecord(UUID.randomUUID(), Instant.now(), origin, Msisdns.domestic(next++),
                Duration.ofMillis(rnd.nextInt(0, 2001)), CallStatus.NO_ANSWER);
    }
}
