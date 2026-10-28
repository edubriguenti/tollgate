package dev.tollgate.simulator;

import dev.tollgate.domain.Msisdn;

import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

public final class Subscriber implements Runnable {
    private final Msisdn number;
    private final Behavior behavior;
    private final EventSink sink;

    public Subscriber(Msisdn number, Behavior behavior, EventSink sink) {
        this.number = number;
        this.behavior = behavior;
        this.sink = sink;
    }

    @Override
    public void run() {
        // one generator per subscriber, created on its own thread: never shared
        RandomGenerator random = new SplittableRandom();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(behavior.nextDelay(random));
                sink.publish(behavior.nextEvent(number, random));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
