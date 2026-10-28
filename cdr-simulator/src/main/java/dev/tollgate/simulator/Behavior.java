package dev.tollgate.simulator;

import dev.tollgate.domain.Msisdn;
import dev.tollgate.domain.TelecomEvent;

import java.time.Duration;
import java.util.random.RandomGenerator;

/** How one subscriber behaves. One instance per subscriber: sequential state is never shared. */
public interface Behavior {
    FraudPattern pattern();

    Duration nextDelay(RandomGenerator rnd);

    TelecomEvent nextEvent(Msisdn origin, RandomGenerator rnd);

    /** Exponential inter-arrival time (Poisson arrivals): -mean * ln(1 - U). */
    static Duration exponential(Duration mean, RandomGenerator rnd) {
        return Duration.ofNanos((long) (-mean.toNanos() * Math.log(1.0 - rnd.nextDouble())));
    }
}
