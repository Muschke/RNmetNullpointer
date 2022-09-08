package be.hi10.realnutrition.services;

import be.hi10.realnutrition.entities.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.junit4.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DefaultExactHashErrorServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
    private ExactHashError exactHashError, exactHashError2;
    @Autowired
    private DefaultExactHashErrorService service;
    private static final String EXACT_HASH_ERRORS = "exact_hash_errors";

    @Before
    public void before(){
        exactHashError = new ExactHashError("action", "division", "endpoint", "eventcreatedon", "hashcode",
                "key", "topic");
        exactHashError2 = new ExactHashError("action2", "division2", "endpoint2", "eventcreatedon2", "hashcode2",
                "key2", "topic2");
    }

    @Test
    public void findAll() {
        assertThat(service.findAll()).hasSize(countRowsInTable(EXACT_HASH_ERRORS));
    }

    @Test
    public void save(){
        service.save(exactHashError);
        assertThat(service.findByKey("key")).hasSize(1);
    }

    @Test
    public void delete(){
        service.save(exactHashError);
        assertThat(service.findByKey("key")).hasSize(1);
        service.delete(exactHashError);
        assertThat(service.findByKey("key")).hasSize(0);
    }

    @Test
    public void deleteAll(){
        service.save(exactHashError);
        service.save(exactHashError2);
        var exactHashErrors = service.findAll();
        service.deleteAll(exactHashErrors);
        assertThat(service.findAll()).hasSize(0);

    }

    @Test
    public void findByKey() {
        assertThat(service.findByKey("key"))
                .hasSize(countRowsInTableWhere(EXACT_HASH_ERRORS, "Key = 'key'"))
                .allSatisfy(exactHashError -> assertThat(exactHashError.getKey()).isEqualTo("key"));
    }
}
