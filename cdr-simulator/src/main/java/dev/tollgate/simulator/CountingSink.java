package dev.tollgate.simulator;

import dev.tollgate.domain.TelecomEvent;

import java.util.concurrent.atomic.LongAdder;

public final class CountingSink implements EventSink {
    private final LongAdder count = new LongAdder();

    public long count() {
        return count.sum();
    }

    @Override
    public void publish(TelecomEvent event) {
        count.increment();
    }
}
