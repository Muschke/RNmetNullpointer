package be.hi10.realnutrition.pojos.exactonline.item;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class ResultTest {
    private Result result, result2, result3;

    @BeforeEach
    void init() {
        result = new Result();
        result.setBarcode("barcode");
        result.setId("id");
        result2 = new Result();
        result2.setBarcode("barcode2");
        result2.setId("id2");
        result3 = new Result();
        result3.setBarcode("barcode");
        result3.setId("id");
    }

    @Test
    void resultDoesNotEqualResult2() {
        assertThat(result.equals(result2)).isFalse();
    }

    @Test
    void resultEqualsResult3() {
        assertThat(result.equals(result3)).isTrue();
    }

    @Test
    void resultAndResult2HaveDifferentHashCodes() {
        assertThat(result.hashCode()).isNotEqualTo(result2.hashCode());
    }

    @Test
    void resultAndResult3HaveTheSameHashCode() {
        assertThat(result.hashCode()).isEqualTo(result3.hashCode());
    }

}