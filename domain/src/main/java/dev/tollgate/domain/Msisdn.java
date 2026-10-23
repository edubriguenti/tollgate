package dev.tollgate.domain;

import java.util.regex.Pattern;

public record Msisdn(String value) {

    private static final Pattern E164 = Pattern.compile("\\+\\d{8,15}");

    public Msisdn {
        if (value == null || !E164.matcher(value).matches()) {
            throw new IllegalArgumentException("Msisdn must be E.164 (+ followed by 8 to 15 digits): " + value);
        }
    }
}
