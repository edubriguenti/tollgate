package dev.tollgate.simulator;

import dev.tollgate.domain.Msisdn;

import java.util.List;

/** Number helpers shared by the behaviors. */
final class Msisdns {
    private static final String DOMESTIC_PREFIX = "+55119";

    /** Illustrative premium-rate prefixes; completed with digits they form a valid E.164. */
    static final List<String> PREMIUM_PREFIXES = List.of("+88213", "+88346", "+67570", "+23990");

    private Msisdns() {}

    /** +55119 followed by 8 digits taken from the index: unique and valid E.164. */
    static Msisdn domestic(int index) {
        return new Msisdn(DOMESTIC_PREFIX + "%08d".formatted(index));
    }

    static Msisdn withPrefix(String prefix, long sequence, int width) {
        return new Msisdn(prefix + String.format("%0" + width + "d", sequence));
    }
}
