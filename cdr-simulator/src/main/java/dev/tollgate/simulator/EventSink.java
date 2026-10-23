package dev.tollgate.simulator;

import dev.tollgate.domain.TelecomEvent;

// 2. Para onde vão os eventos. Hoje: um contador. Na S2: um producer Kafka
public interface EventSink {
    void publish(TelecomEvent event);
}
