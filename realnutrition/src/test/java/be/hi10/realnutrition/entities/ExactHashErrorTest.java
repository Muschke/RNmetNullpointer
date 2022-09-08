package be.hi10.realnutrition.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

class ExactHashErrorTest {
    private ExactHashError exactHashError, exactHashError2, exactHashError3;

    @BeforeEach
    void init() {
        exactHashError = new ExactHashError("action", "division", "endpoint", "eventcreatedon", "hashcode",
                "key", "topic");
        exactHashError2 = new ExactHashError("action2", "division2", "endpoint2", "eventcreatedon2", "hashcode2",
                "key2", "topic2");
        exactHashError3 = new ExactHashError("action", "division", "endpoint", "eventcreatedon", "hashcode",
                "key", "topic");
    }

    @Test
    void exactHashErrorDoesNotEqualExactHashError2() {
        assertThat(exactHashError.equals(exactHashError2)).isFalse();
    }

    @Test
    void exactHashErrorEqualsExactHashError3() {
        assertThat(exactHashError.equals(exactHashError3)).isTrue();
    }

    @Test
    void exactHashErrorAndExactHashError2HaveDifferentHashCodes() {
        assertThat(exactHashError.hashCode()).isNotEqualTo(exactHashError2.hashCode());
    }

    @Test
    void exactHashErrorAndExactHashError3HaveTheSameHashCode() {
        assertThat(exactHashError.hashCode()).isEqualTo(exactHashError3.hashCode());
    }
}