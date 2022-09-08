package be.hi10.realnutrition.services;

import be.hi10.realnutrition.entities.*;
import org.junit.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.junit4.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DefaultRetryItemServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
    private RetryItem retryItem, retryItem2, retryItem3, retryItem4, retryItem5;
    @Autowired
    private DefaultRetryItemService service;
    private static final String RETRYITEMS = "retryitems";

    @Before
    public void init() {
        retryItem = new RetryItem("ean", "itemId", "website");
        retryItem2 = new RetryItem("ean2", "itemId2", "website2");
        retryItem3 = new RetryItem("ean3", "itemId3", "website3");
        retryItem4 = new RetryItem("ean4", "itemId4", "website4");
        retryItem5 = new RetryItem("ean5", "itemId5", "website5");
        service.save(retryItem);
        service.save(retryItem2);
        service.save(retryItem3);
    }

    @Test
    public void findFirstByWebsiteAndItemId() {
        assertThat(service.findFirstByWebsiteAndItemId("website", "itemId").get()).isEqualTo(retryItem);
    }

    @Test
    public void findFirstByWebsiteAndEan() {
        assertThat(service.findFirstByWebsiteAndEan("website", "ean").get()).isEqualTo(retryItem);
    }

    @Test
    public void save() {
        service.save(retryItem4);
        service.save(retryItem5);
        assertThat(service.findFirstByWebsiteAndItemId("website4", "itemId4").get()).isEqualTo(retryItem4);
        assertThat(service.findFirstByWebsiteAndItemId("website5", "itemId5").get()).isEqualTo(retryItem5);
        assertThat(service.findAll()).hasSize(5);
    }

    @Test
    public void findByWebsiteIsNotOrderByCreatedOnAsc() {
        assertThat(service.findByWebsiteIsNotOrderByCreatedOnAsc("noWebsite")).hasSize(3);
    }

    @Test
    public void delete() {
        service.delete(retryItem);
        service.delete(retryItem2);
        assertThat(service.findAll()).hasSize(1);
    }

    @Test
    public void findAll() {
        assertThat(service.findAll()).hasSize(countRowsInTable(RETRYITEMS));
    }
}