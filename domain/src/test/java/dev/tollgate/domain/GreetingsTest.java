package dev.tollgate.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GreetingsTest {
    @Test
    void greet() {
        Greetings greetings = new Greetings();
        assertThat(greetings.greet("Telnyx")).isEqualTo("Hello, Telnyx!");
    }

}