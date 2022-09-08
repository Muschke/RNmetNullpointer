package be.hi10.realnutrition.services;

import be.hi10.realnutrition.entities.*;
import org.junit.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.junit4.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DefaultItemServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
    private Item item, item2, item3, item4, item5;
    private Set<Item> items;
    @Autowired
    private DefaultItemService service;
    //private static final String ITEMS = "items";

    @Before
    public void init() {
        item = new Item("ean", "itemId", 10, "website");
        item2 = new Item("ean2", "itemId2", 10, "website2");
        item3 = new Item("ean3", "itemId3", 10, "website3");
        item4 = new Item("ean4", "itemId4", 10, "website4");
        item5 = new Item("ean5", "itemId5", 10, "website5");
        items = new HashSet<>();
        items.add(item);
        items.add(item2);
        items.add(item3);
        service.saveAll(items);
    }

    @Test
    public void findByWebsite() {
        assertThat(service.findByWebsite("website")).hasSize(1);
    }

    @Test
    public void findByEan() {
        assertThat(service.findByEan("ean")).hasSize(1);

    }

    @Test
    public void saveAll() {
        Set <Item> twoItems = new HashSet<>();
        twoItems.add(item4);
        twoItems.add(item5);
        service.saveAll(twoItems);
        assertThat(service.count()).isEqualTo(5);
    }

    @Test
    public void findFirstByWebsiteAndEan() {
        assertThat(service.findFirstByWebsiteAndEan("website", "ean").get()).isEqualTo(item);
    }

    @Test
    public void deleteAllFromSetOfItems() {
        Set<Item> twoItems = new HashSet<>();
        twoItems.add(item);
        twoItems.add(item2);
        service.deleteAll(twoItems);
        assertThat(service.findByEan("ean3")).hasSize(1);
        assertThat(service.findByEan("ean")).hasSize(0);
        assertThat(service.findByEan("ean2")).hasSize(0);
        assertThat(service.count()).isOne();
    }

    @Test
    public void deleteAll() {
        assertThat(service.count()).isEqualTo(3);
        service.deleteAll();
        assertThat(service.count()).isEqualTo(0);

    }

    @Test
    public void count() {
        assertThat(service.count()).isEqualTo(3);
    }
}
