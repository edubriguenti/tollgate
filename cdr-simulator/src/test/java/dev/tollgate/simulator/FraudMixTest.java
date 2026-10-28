package dev.tollgate.simulator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FraudMixTest {

    @Test
    void rejectsNegativeCounts() {
        assertThatThrownBy(() -> new FraudMix(-1, 0, 0, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new FraudMix(0, -1, 0, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new FraudMix(0, 0, -1, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new FraudMix(0, 0, 0, -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void totalSumsAllPatterns() {
        assertThat(new FraudMix(1, 2, 3, 4).total()).isEqualTo(10);
    }
}
