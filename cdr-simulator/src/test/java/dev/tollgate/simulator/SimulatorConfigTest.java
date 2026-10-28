package dev.tollgate.simulator;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimulatorConfigTest {

    private static final Duration ONE_SECOND = Duration.ofSeconds(1);
    private static final FraudMix NO_FRAUD = new FraudMix(0, 0, 0, 0);

    @Test
    void rejectsInvalidSubscribers() {
        assertThatThrownBy(() -> new SimulatorConfig(0, ONE_SECOND, ONE_SECOND, 0.5, NO_FRAUD))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNonPositiveDurations() {
        assertThatThrownBy(() -> new SimulatorConfig(1, Duration.ZERO, ONE_SECOND, 0.5, NO_FRAUD))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SimulatorConfig(1, ONE_SECOND.negated(), ONE_SECOND, 0.5, NO_FRAUD))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SimulatorConfig(1, ONE_SECOND, Duration.ZERO, 0.5, NO_FRAUD))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsSmsRatioOutOfRange() {
        assertThatThrownBy(() -> new SimulatorConfig(1, ONE_SECOND, ONE_SECOND, -0.1, NO_FRAUD))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SimulatorConfig(1, ONE_SECOND, ONE_SECOND, 1.1, NO_FRAUD))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SimulatorConfig(1, ONE_SECOND, ONE_SECOND, Double.NaN, NO_FRAUD))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsFraudMixLargerThanSubscribers() {
        assertThatThrownBy(() -> new SimulatorConfig(3, ONE_SECOND, ONE_SECOND, 0.5, new FraudMix(1, 1, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(new SimulatorConfig(4, ONE_SECOND, ONE_SECOND, 0.5, new FraudMix(1, 1, 1, 1)).fraudMix().total())
                .isEqualTo(4);
    }

    @Test
    void targetEventsPerSecond() {
        var config = new SimulatorConfig(100_000, Duration.ofSeconds(10), ONE_SECOND, 0.5, NO_FRAUD);
        assertThat(config.targetEventsPerSecond()).isEqualTo(10_000.0);
    }

    @Test
    void targetEventsPerSecondWithSubMillisecondInterval() {
        var config = new SimulatorConfig(1, Duration.ofNanos(500_000), ONE_SECOND, 0.5, NO_FRAUD);
        assertThat(config.targetEventsPerSecond()).isEqualTo(2_000.0);
    }
}
