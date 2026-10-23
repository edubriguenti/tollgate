package dev.tollgate.domain;

import java.time.Instant;
import java.util.UUID;

public sealed interface TelecomEvent permits CallDetailRecord, SmsEvent {
    UUID eventId();
    Instant timestamp();
    Msisdn origin();
    Msisdn destination();
}
