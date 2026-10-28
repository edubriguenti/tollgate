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

public final class NormalBehavior implements Behavior {
    private static final double ANSWERED_RATIO = 0.8;
    private static final double INTERNATIONAL_RATIO = 0.1;
    private static final String[] INTERNATIONAL_PREFIXES = {"+1", "+44", "+351"};
    private static final CallStatus[] UNANSWERED = {CallStatus.NO_ANSWER, CallStatus.BUSY, CallStatus.FAILED};

    private final Duration meanInterval;
    private final double smsRatio;

    public NormalBehavior(Duration meanInterval, double smsRatio) {
        this.meanInterval = meanInterval;
        this.smsRatio = smsRatio;
    }

    @Override
    public FraudPattern pattern() {
        return FraudPattern.NONE;
    }

    @Override
    public Duration nextDelay(RandomGenerator rnd) {
        return Behavior.exponential(meanInterval, rnd);
    }

    // SMS (1 a 3 segmentos) com probabilidade smsRatio; senão, um CDR de 0 a 600 s, ~80% ANSWERED
    @Override
    public TelecomEvent nextEvent(Msisdn origin, RandomGenerator rnd) {
        UUID eventId = UUID.randomUUID();
        Instant now = Instant.now();
        Msisdn destination = destination(rnd);
        if (rnd.nextDouble() < smsRatio) {
            return new SmsEvent(eventId, now, origin, destination, rnd.nextInt(1, 4));
        }
        CallStatus status = rnd.nextDouble() < ANSWERED_RATIO
                ? CallStatus.ANSWERED
                : UNANSWERED[rnd.nextInt(UNANSWERED.length)];
        return new CallDetailRecord(eventId, now, origin, destination,
                Duration.ofSeconds(rnd.nextInt(0, 601)), status);
    }

    private static Msisdn destination(RandomGenerator rnd) {
        if (rnd.nextDouble() < INTERNATIONAL_RATIO) {
            String prefix = INTERNATIONAL_PREFIXES[rnd.nextInt(INTERNATIONAL_PREFIXES.length)];
            return new Msisdn(prefix + "%09d".formatted(rnd.nextInt(1_000_000_000)));
        }
        return Msisdns.domestic(rnd.nextInt(100_000_000));
    }
}
