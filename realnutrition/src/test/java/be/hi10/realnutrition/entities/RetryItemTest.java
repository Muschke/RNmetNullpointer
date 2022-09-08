package be.hi10.realnutrition.entities;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class RetryItemTest {
    private RetryItem retryItem, retryItem2, retryItem3;

    @BeforeEach
    void init() {
        retryItem = new RetryItem("ean", "itemId", "website");
        retryItem2 = new RetryItem("ean2", "itemId2", "website2");
        retryItem3 = new RetryItem("ean", "itemId", "website");
    }

    @Test
    void retryItemDoesNotEqualRetryItem2() {
        assertThat(retryItem.equals(retryItem2)).isFalse();
    }

    @Test
    void retryItemEqualsRetryItem3() {
        assertThat(retryItem.equals(retryItem3)).isTrue();
    }

    @Test
    void retryItemAndRetryItem2HaveDifferentHashCodes() {
        assertThat(retryItem.hashCode()).isNotEqualTo(retryItem2.hashCode());
    }

    @Test
    void retryItemAndRetryItem3HaveTheSameHashCode() {
        assertThat(retryItem.hashCode()).isEqualTo(retryItem3.hashCode());
    }

}