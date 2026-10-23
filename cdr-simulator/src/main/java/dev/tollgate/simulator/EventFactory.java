package dev.tollgate.simulator;

import dev.tollgate.domain.CallDetailRecord;
import dev.tollgate.domain.CallStatus;
import dev.tollgate.domain.Msisdn;
import dev.tollgate.domain.SmsEvent;
import dev.tollgate.domain.TelecomEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.random.RandomGenerator;

public final class EventFactory {

    private static final String PREFIX = "+55119";
    private static final double ANSWERED_RATIO = 0.8;
    private static final CallStatus[] UNANSWERED = {CallStatus.NO_ANSWER, CallStatus.BUSY, CallStatus.FAILED};

    private EventFactory() {
    }

    // SMS (1 a 3 segmentos) com probabilidade smsRatio; senão, um CDR
    // CDR: destino aleatório, 0 a 600 s, status quase sempre ANSWERED (~80%)
    public static TelecomEvent next(Msisdn origin, double smsRatio, RandomGenerator random) {
        UUID eventId = UUID.randomUUID();
        Instant now = Instant.now();
        Msisdn destination = msisdnFor(random.nextInt(100_000_000));
        if (random.nextDouble() < smsRatio) {
            return new SmsEvent(eventId, now, origin, destination, random.nextInt(1, 4));
        }
        CallStatus status = random.nextDouble() < ANSWERED_RATIO
                ? CallStatus.ANSWERED
                : UNANSWERED[random.nextInt(UNANSWERED.length)];
        return new CallDetailRecord(eventId, now, origin, destination,
                Duration.ofSeconds(random.nextInt(0, 601)), status);
    }

    /** +55119 followed by 8 digits taken from the index: unique and valid E.164. */
    public static Msisdn msisdnFor(int index) {
        return new Msisdn(PREFIX + "%08d".formatted(index));
    }
}
