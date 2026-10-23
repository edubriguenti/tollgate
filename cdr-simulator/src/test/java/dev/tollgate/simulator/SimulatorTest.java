package dev.tollgate.simulator;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class SimulatorTest {

    @Test
    void endToEndEventCountIsCloseToExpected() throws InterruptedException {
        var config = new SimulatorConfig(1_000, Duration.ofMillis(50), Duration.ofSeconds(1), 0.3);
        var sink = new CountingSink();

        Simulator.run(config, sink);

        // expected ~20,000 (1,000 subscribers * 1 s / 50 ms). Arrivals are Poisson, VTs start
        // staggered and shutdown timing is not exact, so assert a range, never an exact number.
        assertThat(sink.count()).isBetween(15_000L, 22_000L);
    }
}
