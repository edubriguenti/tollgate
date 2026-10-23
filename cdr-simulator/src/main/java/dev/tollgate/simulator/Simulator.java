package dev.tollgate.simulator;

import dev.tollgate.domain.Billing;
import dev.tollgate.domain.CallDetailRecord;
import dev.tollgate.domain.CallStatus;
import dev.tollgate.domain.Msisdn;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class Simulator {

    static CallDetailRecord sampleCall() {
        return new CallDetailRecord(
                UUID.randomUUID(), Instant.now(),
                new Msisdn("+5511999998888"), new Msisdn("+447700900123"),
                Duration.ofSeconds(61), CallStatus.ANSWERED);
    }

    public static void main(String[] args) {
        CallDetailRecord cdr = sampleCall();
        System.out.println(cdr);
        System.out.println("billable units: " + Billing.billableUnits(cdr));
    }
}
