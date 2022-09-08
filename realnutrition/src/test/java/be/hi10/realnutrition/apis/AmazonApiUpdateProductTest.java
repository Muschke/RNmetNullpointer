package be.hi10.realnutrition.apis;
import static org.assertj.core.api.Assertions.assertThat;
import be.hi10.realnutrition.apis.amazon.AmazonApiUpdateProduct;
import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.pojos.amazon.reports.DocumentFeedResponse;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class AmazonApiUpdateProductTest {
    AmazonApiUpdateProduct api = new AmazonApiUpdateProduct();
    @BeforeEach
    void beforeEach(){
        //api = new AmazonApiGetAllProducts();
    }

    @Test
    public void getFeedDocument() throws ApiException {
        AmazonApiUpdateProduct api = new AmazonApiUpdateProduct();
        DocumentFeedResponse response = api.getFeedDocument();
        assertThat(response).isNotNull();
        assertThat(response).isInstanceOf(DocumentFeedResponse.class);
    }
}
