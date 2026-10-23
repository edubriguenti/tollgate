package dev.tollgate.simulator;

import java.time.Duration;
import java.util.concurrent.Executors;

public class Simulator {

    static void run(SimulatorConfig config, CountingSink sink) throws InterruptedException {
        long deadline = System.nanoTime() + config.runFor().toNanos();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < config.subscribers(); i++) {
                executor.submit(new Subscriber(EventFactory.msisdnFor(i), config, sink));
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

    public static void main(String[] args) throws InterruptedException {
        int subscribers = args.length > 0 ? Integer.parseInt(args[0]) : 100_000;
        var config = new SimulatorConfig(subscribers, Duration.ofSeconds(10), Duration.ofSeconds(30), 0.3);
        var sink = new CountingSink();
        run(config, sink);
        System.out.println("total events: " + sink.count());
    }
}
