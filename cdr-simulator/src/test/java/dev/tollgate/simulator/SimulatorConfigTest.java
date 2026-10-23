package dev.tollgate.simulator;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimulatorConfigTest {

    private static final Duration ONE_SECOND = Duration.ofSeconds(1);

    @Test
    void rejectsInvalidSubscribers() {
        assertThatThrownBy(() -> new SimulatorConfig(0, ONE_SECOND, ONE_SECOND, 0.5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNonPositiveDurations() {
        assertThatThrownBy(() -> new SimulatorConfig(1, Duration.ZERO, ONE_SECOND, 0.5))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SimulatorConfig(1, ONE_SECOND.negated(), ONE_SECOND, 0.5))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SimulatorConfig(1, ONE_SECOND, Duration.ZERO, 0.5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsSmsRatioOutOfRange() {
        assertThatThrownBy(() -> new SimulatorConfig(1, ONE_SECOND, ONE_SECOND, -0.1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SimulatorConfig(1, ONE_SECOND, ONE_SECOND, 1.1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SimulatorConfig(1, ONE_SECOND, ONE_SECOND, Double.NaN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void targetEventsPerSecond() {
        var config = new SimulatorConfig(100_000, Duration.ofSeconds(10), ONE_SECOND, 0.5);
        assertThat(config.targetEventsPerSecond()).isEqualTo(10_000.0);
    }

    @Test
    void targetEventsPerSecondWithSubMillisecondInterval() {
        var config = new SimulatorConfig(1, Duration.ofNanos(500_000), ONE_SECOND, 0.5);
        assertThat(config.targetEventsPerSecond()).isEqualTo(2_000.0);
    }
}
