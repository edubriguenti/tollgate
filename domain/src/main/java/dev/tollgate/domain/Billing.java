package dev.tollgate.domain;

public final class Billing {

    private Billing() {
    }

    public static long billableUnits(TelecomEvent event) {
        return switch (event) {
            case CallDetailRecord(var id, var ts, var from, var to, var duration, var status)
                    when status == CallStatus.ANSWERED -> Math.ceilDiv(duration.toSeconds(), 60);
            case CallDetailRecord cdr -> 0;
            case SmsEvent(var id, var ts, var from, var to, var segments) -> segments;
        };
    }
}
