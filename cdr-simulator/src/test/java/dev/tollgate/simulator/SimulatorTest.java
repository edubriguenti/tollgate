package dev.tollgate.simulator;

import dev.tollgate.domain.Billing;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimulatorTest {

    @Test
    void sampleCallIsBillable() {
        assertThat(Billing.billableUnits(Simulator.sampleCall())).isEqualTo(2);
    }
}
