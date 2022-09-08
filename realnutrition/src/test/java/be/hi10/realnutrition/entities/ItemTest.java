package be.hi10.realnutrition.entities;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {
    private Item item, item2, item3;

    @BeforeEach
    void init() {
        item = new Item("ean", "itemId", 10, "website");
        item2 = new Item("ean2", "itemId2", 10, "website2");
        item3 = new Item("ean", "itemId", 10, "website");
    }

    @Test
    void itemDoesNotEqualItem2() {
        assertThat(item.equals(item2)).isFalse();
    }

    @Test
    void itemEqualsItem3() {
        assertThat(item.equals(item3)).isTrue();
    }

    @Test
    void itemAndItem2HaveDifferentHashCodes() {
        assertThat(item.hashCode()).isNotEqualTo(item2.hashCode());
    }

    @Test
    void itemAndItem3HaveTheSameHashCode() {
        assertThat(item.hashCode()).isEqualTo(item3.hashCode());
    }
}