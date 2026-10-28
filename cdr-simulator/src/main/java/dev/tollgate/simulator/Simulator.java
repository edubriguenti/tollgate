package dev.tollgate.simulator;

import dev.tollgate.domain.Msisdn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

public class Simulator {

    /** Fixed seed: the same config always puts the fraudsters on the same numbers. */
    static final long DEFAULT_SEED = 42L;

    /** One subscriber's number and behavior: the single source for both the threads and the labels. */
    record Assignment(Msisdn msisdn, Behavior behavior) {}

    static void run(SimulatorConfig config, CountingSink sink, Path groundTruth)
            throws InterruptedException, IOException {
        List<Assignment> assignments = assign(config, DEFAULT_SEED);
        // ground truth goes to its own file, before any event exists; events never carry the label
        writeGroundTruth(groundTruth, assignments);
        long deadline = System.nanoTime() + config.runFor().toNanos();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (Assignment a : assignments) {
                executor.submit(new Subscriber(a.msisdn(), a.behavior(), sink));
            }
            try {
                long last = 0;
                while (true) {
                    long remaining = deadline - System.nanoTime();
                    if (remaining <= 0) {
                        break;
                    }
                    Thread.sleep(Duration.ofNanos(Math.min(remaining, Duration.ofSeconds(1).toNanos())));
                    long total = sink.count();
                    System.out.printf("events/s: %,d (target: %,.0f)%n", total - last, config.targetEventsPerSecond());
                    last = total;
                }
            } finally {
                // close() only calls shutdown() and waits; subscribers loop until interrupted,
                // so without this an exception above would hang the JVM forever
                executor.shutdownNow();
            }
        }
    }

    /**
     * Fraudsters at random positions among the normal subscribers. Always numbers 0..N would leak
     * the label through the identity: any feature derived from the number would separate them.
     */
    static List<Assignment> assign(SimulatorConfig config, long seed) {
        var behaviors = new ArrayList<Behavior>(config.subscribers());
        behaviors.addAll(fraudBehaviors(config.fraudMix()));
        while (behaviors.size() < config.subscribers()) {
            behaviors.add(new NormalBehavior(config.meanInterval(), config.smsRatio()));
        }
        Collections.shuffle(behaviors, new Random(seed));
        var assignments = new ArrayList<Assignment>(behaviors.size());
        for (int i = 0; i < behaviors.size(); i++) {
            assignments.add(new Assignment(Msisdns.domestic(i), behaviors.get(i)));
        }
        return assignments;
    }

    /** One "msisdn,PATTERN" line per fraudster, in number order. */
    static List<String> groundTruthLines(List<Assignment> assignments) {
        return assignments.stream()
                .filter(a -> a.behavior().pattern() != FraudPattern.NONE)
                .map(a -> a.msisdn().value() + "," + a.behavior().pattern())
                .toList();
    }

    /** One fresh behavior per fraudster, in mix order: IRSF, Wangiri, SMS pumping, SIM box. */
    static List<Behavior> fraudBehaviors(FraudMix mix) {
        var prefixes = Msisdns.PREMIUM_PREFIXES;
        var behaviors = new ArrayList<Behavior>(mix.total());
        for (int k = 0; k < mix.irsf(); k++) {
            behaviors.add(new IrsfBehavior(prefixes.get(k % prefixes.size())));
        }
        for (int k = 0; k < mix.wangiri(); k++) {
            behaviors.add(new WangiriBehavior(k * 1_000_000));
        }
        for (int k = 0; k < mix.smsPumping(); k++) {
            behaviors.add(new SmsPumpingBehavior(prefixes.get(k % prefixes.size())));
        }
        for (int k = 0; k < mix.simBox(); k++) {
            behaviors.add(new SimBoxBehavior());
        }
        return behaviors;
    }

    private static void writeGroundTruth(Path file, List<Assignment> assignments) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(file, groundTruthLines(assignments));
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        int subscribers = args.length > 0 ? Integer.parseInt(args[0]) : 100_000;
        var config = new SimulatorConfig(subscribers, Duration.ofSeconds(10), Duration.ofSeconds(30), 0.3,
                new FraudMix(5, 5, 5, 5));
        var sink = new CountingSink();
        run(config, sink, Path.of("build/ground-truth.csv"));
        System.out.println("total events: " + sink.count());
    }
}
