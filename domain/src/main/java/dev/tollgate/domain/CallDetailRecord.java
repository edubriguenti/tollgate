package dev.tollgate.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record CallDetailRecord(UUID eventId, Instant timestamp, Msisdn origin, Msisdn destination,
                               Duration duration, CallStatus callStatus) implements TelecomEvent {

    public CallDetailRecord {
        Objects.requireNonNull(eventId, "eventId");
        Objects.requireNonNull(timestamp, "timestamp");
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(destination, "destination");
        Objects.requireNonNull(duration, "duration");
        Objects.requireNonNull(callStatus, "callStatus");
        if (duration.isNegative()) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
    }
}
