package be.hi10.realnutrition.apis;

import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.pojos.woo.WooItem;
import be.hi10.realnutrition.pojos.woo.WooStock;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.List;

@Component
public class WooApi {

    private final String API_KEY = "ck_9e252d762ab80a27606528d614abf83165cf9422";
    private final String API_SECRET = "cs_418646e6ad7ecf4f3db7b31a33884d10d0fb2d9d";

    private final String DOMAIN = "https://www.lv8dnutrition.be";

    public ResponseEntity<List<WooItem>> getProducts() throws ApiException {
        final String URI = "/wp-json/wc/v3/products";

        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization",getBasicAuthenticationString());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            return rest.exchange(DOMAIN + URI, HttpMethod.GET, entity, new ParameterizedTypeReference<List<WooItem>>() {});
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new ApiException("Something went wrong while getting all the products from WooCommerce: " + e.getMessage(),
                    e);
        }
    }

    public void updateStock(int newStock, String itemId) throws ApiException {
        final String URI = "/wp-json/wc/v3/products/" + itemId;

        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", getBasicAuthenticationString());
        headers.add("Accept", "application/json");

        WooStock DATA = new WooStock(newStock);

        HttpEntity<WooStock> entity = new HttpEntity<>(DATA, headers);

        try {
            rest.exchange(DOMAIN + URI, HttpMethod.PUT, entity, String.class);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new ApiException("Something went wrong while updating a product from WooCommerce: " + e.getMessage(), e);
        }
    }

    private String getBasicAuthenticationString() {
        String auth = API_KEY + ":" + API_SECRET;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")));

        return "Basic " + new String( encodedAuth );
    }
}
