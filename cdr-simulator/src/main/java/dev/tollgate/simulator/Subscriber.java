package dev.tollgate.simulator;

import dev.tollgate.domain.Msisdn;

import java.time.Duration;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

public final class Subscriber implements Runnable {

    private final Msisdn number;
    private final SimulatorConfig config;
    private final EventSink sink;

    public Subscriber(Msisdn number, SimulatorConfig config, EventSink sink) {
        this.number = number;
        this.config = config;
        this.sink = sink;
    }

    @Override
    public void run() {
        // one generator per subscriber, created on its own thread: never shared
        RandomGenerator random = new SplittableRandom();
        long meanNanos = config.meanInterval().toNanos();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // exponential inter-arrival time: -mean * ln(1 - U)
                long delay = (long) (-meanNanos * Math.log(1.0 - random.nextDouble()));
                Thread.sleep(Duration.ofNanos(delay));
                sink.publish(EventFactory.next(number, config.smsRatio(), random));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
