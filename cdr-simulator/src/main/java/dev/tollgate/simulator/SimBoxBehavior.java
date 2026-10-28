package dev.tollgate.simulator;

import dev.tollgate.domain.CallDetailRecord;
import dev.tollgate.domain.CallStatus;
import dev.tollgate.domain.Msisdn;
import dev.tollgate.domain.TelecomEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.random.RandomGenerator;

/** Steady answered calls of normal length to many distinct domestic numbers. */
public final class SimBoxBehavior implements Behavior {
    private static final Duration MEAN_INTERVAL = Duration.ofSeconds(2);

    @Override
    public FraudPattern pattern() {
        return FraudPattern.SIM_BOX;
    }

    @Override
    public Duration nextDelay(RandomGenerator rnd) {
        return Behavior.exponential(MEAN_INTERVAL, rnd);
    }

    @Override
    public TelecomEvent nextEvent(Msisdn origin, RandomGenerator rnd) {
        return new CallDetailRecord(UUID.randomUUID(), Instant.now(), origin,
                Msisdns.domestic(rnd.nextInt(100_000_000)),
                Duration.ofSeconds(rnd.nextLong(60, 5 * 60 + 1)), CallStatus.ANSWERED);
    }
}
