package dev.tollgate.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record SmsEvent(UUID eventId, Instant timestamp, Msisdn origin, Msisdn destination,
                       int segments) implements TelecomEvent {

    public SmsEvent {
        Objects.requireNonNull(eventId, "eventId");
        Objects.requireNonNull(timestamp, "timestamp");
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(destination, "destination");
        if (segments < 1) {
            throw new IllegalArgumentException("segments cannot be less than 1");
        }
    }
}
