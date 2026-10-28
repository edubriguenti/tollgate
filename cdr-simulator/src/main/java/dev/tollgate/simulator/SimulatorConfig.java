package dev.tollgate.simulator;

import java.time.Duration;
import java.util.Objects;

public record SimulatorConfig(int subscribers, Duration meanInterval, Duration runFor, double smsRatio, FraudMix fraudMix) {

    public SimulatorConfig {
        if (subscribers < 1) {
            throw new IllegalArgumentException("subscribers must be at least 1: " + subscribers);
        }
        Objects.requireNonNull(meanInterval, "meanInterval");
        Objects.requireNonNull(runFor, "runFor");
        Objects.requireNonNull(fraudMix, "fraudMix");
        if (fraudMix.total() > subscribers) {
            throw new IllegalArgumentException(
                    "fraudMix total (%d) exceeds subscribers (%d)".formatted(fraudMix.total(), subscribers));
        }
        if (!meanInterval.isPositive()) {
            throw new IllegalArgumentException("meanInterval must be positive: " + meanInterval);
        }
        if (!runFor.isPositive()) {
            throw new IllegalArgumentException("runFor must be positive: " + runFor);
        }
        if (!(smsRatio >= 0.0 && smsRatio <= 1.0)) {
            throw new IllegalArgumentException("smsRatio must be between 0 and 1: " + smsRatio);
        }
    }

    public double targetEventsPerSecond() {
        return subscribers / (meanInterval.toNanos() / 1e9);
    }
}
