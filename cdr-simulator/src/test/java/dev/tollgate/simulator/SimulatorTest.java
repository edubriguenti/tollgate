package dev.tollgate.simulator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class SimulatorTest {

    @TempDir
    Path tempDir;

    @Test
    void endToEndEventCountIsCloseToExpected() throws Exception {
        var config = new SimulatorConfig(1_000, Duration.ofMillis(50), Duration.ofSeconds(1), 0.3,
                new FraudMix(0, 0, 0, 0));
        var sink = new CountingSink();

        Simulator.run(config, sink, tempDir.resolve("ground-truth.csv"));

        // expected ~20,000 (1,000 subscribers * 1 s / 50 ms). Arrivals are Poisson, VTs start
        // staggered and shutdown timing is not exact, so assert a range, never an exact number.
        assertThat(sink.count()).isBetween(15_000L, 22_000L);
    }

    @Test
    void writesOneGroundTruthLinePerFraudster() throws Exception {
        var config = new SimulatorConfig(20, Duration.ofSeconds(10), Duration.ofMillis(100), 0.3,
                new FraudMix(2, 1, 1, 1));
        Path file = tempDir.resolve("nested/ground-truth.csv");

        Simulator.run(config, new CountingSink(), file);

        // the file must match the very assignment the threads ran with, not a recomputation
        var lines = Files.readAllLines(file);
        assertThat(lines).isEqualTo(Simulator.groundTruthLines(Simulator.assign(config, Simulator.DEFAULT_SEED)));
        assertThat(lines).extracting(line -> line.split(",")[1])
                .containsExactlyInAnyOrder("IRSF", "IRSF", "WANGIRI", "SMS_PUMPING", "SIM_BOX");
    }

    @Test
    void sameSeedGivesSameAssignment() {
        var config = new SimulatorConfig(1_000, Duration.ofSeconds(10), Duration.ofSeconds(1), 0.3,
                new FraudMix(5, 5, 5, 5));

        var first = Simulator.groundTruthLines(Simulator.assign(config, 7));
        var second = Simulator.groundTruthLines(Simulator.assign(config, 7));
        var otherSeed = Simulator.groundTruthLines(Simulator.assign(config, 8));

        assertThat(first).hasSize(20).isEqualTo(second).isNotEqualTo(otherSeed);
    }

    @Test
    void fraudstersAreNotTheLowestNumbers() {
        var config = new SimulatorConfig(1_000, Duration.ofSeconds(10), Duration.ofSeconds(1), 0.3,
                new FraudMix(5, 5, 5, 5));
        var lowest = IntStream.range(0, 20).mapToObj(i -> Msisdns.domestic(i).value()).toList();

        var fraudNumbers = Simulator.groundTruthLines(Simulator.assign(config, Simulator.DEFAULT_SEED))
                .stream().map(line -> line.split(",")[0]).toList();

        // with the label on numbers 0..19, "is the number low?" would be a perfect fraud feature
        assertThat(fraudNumbers).isNotEqualTo(lowest);
    }
}
