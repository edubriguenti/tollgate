package dev.tollgate.simulator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SimulatorTest {
    @Test
    void testSimulator() {
        Assertions.assertThat(Simulator.message()).isEqualTo("Hello, World!");
    }
}