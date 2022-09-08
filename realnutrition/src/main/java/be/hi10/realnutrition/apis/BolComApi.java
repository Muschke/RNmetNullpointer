package be.hi10.realnutrition.apis;

import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.pojos.bol.AccesToken;
import be.hi10.realnutrition.pojos.bol.RequestOfferList;
import be.hi10.realnutrition.pojos.bol.ResponseEvent;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

abstract class BolComApi {
    protected static final String DOMAIN = "https://api.bol.com";
    protected boolean retried = false;

    protected String API_KEY = "";
    protected String API_SECRET = "";

    public BolComApi() {
    }

    public void updateStock(int newStock, String offerId, String accessToken) throws ApiException {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        final String URI = "/retailer/offers/" + offerId + "/stock";
        final String data = "{\"amount\": " + newStock + ",  \"managedByRetailer\": true}";

        headers.add("Accept", "application/vnd.retailer.v3+json");
        headers.add("Content-Type", "application/vnd.retailer.v3+json");

        if (accessToken == null) {
            accessToken = refreshJwtToken().getBody().getAccessToken();
        }
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(data, headers);

        try {
            rest.exchange(DOMAIN + URI, HttpMethod.PUT, entity, ResponseEvent.class);
            retried = false;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                accessToken = refreshJwtToken().getBody().getAccessToken();

                if (retried) {
                    retried = false;
                    throw new ApiException("Authorization failed twice when updating 1 product from bol.com");
                }
                retried = true;
                updateStock(newStock, offerId, accessToken);
            } else
                retried = false;
            throw new ApiException(
                    "Something went wrong while updating an offer from Bol.com: " + e.getResponseBodyAsString(), e);
        }
    }

    public List<String> requestCSVOfferList(String accessToken) throws ApiException {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        final String URI = "/retailer/offers/export";
        final RequestOfferList DATA = new RequestOfferList("CSV");

        headers.add("Content-Type", "application/vnd.retailer.v3+json");
        headers.add("Accept", "application/vnd.retailer.v3+json");

        if (accessToken == null) {
            accessToken = refreshJwtToken().getBody().getAccessToken();
        }

        headers.setBearerAuth(accessToken);
        HttpEntity<RequestOfferList> entity = new HttpEntity<>(DATA, headers);

        try {
            ResponseEntity<ResponseEvent> responseEntity = rest.exchange(DOMAIN + URI, HttpMethod.POST, entity,
                    ResponseEvent.class);
            retried = false;
            return requestCSV(responseEntity, accessToken);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                accessToken = refreshJwtToken().getBody().getAccessToken();

                if (retried) {
                    retried = false;
                    throw new ApiException("Authorization failed twice when requesting the offerlist from bol.com");
                }
                retried = true;
                return requestCSVOfferList(accessToken);
            } else
                retried = false;
            throw new ApiException("Something went wrong while making an offerlist request from Bol.com: "
                    + e.getResponseBodyAsString(), e);
        }
    }

    protected List<String> requestCSV(ResponseEntity<ResponseEvent> responseEntity, String accessToken)
            throws ApiException {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String uri = "";
        if (responseEntity.getBody().getLinks() != null)
            uri = responseEntity.getBody().getLinks().get(0).getHref();
        if (!uri.startsWith("https"))
            uri = uri.replace("http", "https");

        headers.add("Accept", "application/vnd.retailer.v3+json");
        headers.setBearerAuth(accessToken);

        HttpEntity<RequestOfferList> entity = new HttpEntity<>(headers);

        try {
            switch (responseEntity.getBody().getStatus()) {
                case FAILURE:
                case TIMEOUT:
                    return requestCSVOfferList(accessToken);
                case SUCCESS:
                    retried = false;
                    return getCSV(responseEntity.getBody().getEntityId(), accessToken);
                case PENDING:
                    retried = false;
                    Thread.sleep(3000);
                    return requestCSV(rest.exchange(uri, HttpMethod.GET, entity, ResponseEvent.class), accessToken);
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                accessToken = refreshJwtToken().getBody().getAccessToken();

                if (retried) {
                    retried = false;
                    throw new ApiException("Authorization failed twice when waiting for a csv from bol.com");
                }
                retried = true;
                return requestCSV(responseEntity, accessToken);
            } else
                retried = false;
            throw new ApiException(
                    "Something went wrong while getting an offerlist from Bol.com: " + e.getResponseBodyAsString(), e);
        } catch (InterruptedException e) {
            try {
                return requestCSV(rest.exchange(uri, HttpMethod.GET, entity, ResponseEvent.class), accessToken);
            } catch (HttpClientErrorException | HttpServerErrorException ex) {
                if (ex.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                    accessToken = refreshJwtToken().getBody().getAccessToken();

                    if (retried) {
                        retried = false;
                        throw new ApiException("Authorization failed twice when getting 1 product from bol.com");
                    }
                    retried = true;
                    return requestCSV(responseEntity, accessToken);
                } else
                    retried = false;
                throw new ApiException(
                        "Something went wrong while getting an offerlist from Bol.com: " + ex.getResponseBodyAsString(),
                        e);
            }
        }
        return Collections.emptyList();
    }

    protected List<String> getCSV(String entityID, String accessToken) throws ApiException {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        final String URI = "/retailer/offers/export/" + entityID;

        headers.add("Accept", "application/vnd.retailer.v3+csv");
        headers.setBearerAuth(accessToken);
        HttpEntity<RequestOfferList> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = rest.exchange(DOMAIN + URI, HttpMethod.GET, entity, String.class);
            retried = false;
            return Arrays.asList(responseEntity.getBody().split("\n"));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                accessToken = refreshJwtToken().getBody().getAccessToken();

                if (retried) {
                    retried = false;
                    throw new ApiException("Authorization failed twice when requesting a csv from bol.com");
                }
                retried = true;
                getCSV(entityID, accessToken);
                return Collections.emptyList();
            } else
                retried = false;
            throw new ApiException(
                    "Something went wrong while getting an offerlist from Bol.com: " + e.getResponseBodyAsString(), e);
        }

    }

    protected abstract ResponseEntity<AccesToken> refreshJwtToken();
}
