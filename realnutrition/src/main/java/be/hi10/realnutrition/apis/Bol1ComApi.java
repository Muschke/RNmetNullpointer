package be.hi10.realnutrition.apis;

import java.util.Arrays;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.pojos.bol.AccesToken;
import be.hi10.realnutrition.pojos.bol.RequestOfferList;
import be.hi10.realnutrition.pojos.bol.ResponseEvent;

@Component
public class Bol1ComApi {
	private static final String DOMAIN = "https://api.bol.com";
	private boolean retried = false;

	public Bol1ComApi() {
	}

	public void updateStock(int newStock, String offerId, String accessToken) throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		final String URI = "/retailer/offers/" + offerId + "/stock";
		final String data = "{\"amount\": " + newStock + ",  \"managedByRetailer\": true}";

		headers.add("Accept", "application/vnd.retailer.v5+json");
		headers.add("Content-Type", "application/vnd.retailer.v5+json");

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

		headers.add("Content-Type", "application/vnd.retailer.v5+json");
		headers.add("Accept", "application/vnd.retailer.v5+json");

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

	private List<String> requestCSV(ResponseEntity<ResponseEvent> responseEntity, String accessToken)
			throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		String uri = "";
		if (responseEntity.getBody().getLinks() != null)
			uri = responseEntity.getBody().getLinks().get(0).getHref();
		if (!uri.startsWith("https"))
			uri = uri.replace("http", "https");

		headers.add("Accept", "application/vnd.retailer.v5+json");
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

	private List<String> getCSV(String entityID, String accessToken) throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		final String URI = "/retailer/offers/export/" + entityID;

		headers.add("Accept", "application/vnd.retailer.v5+csv");
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

	private ResponseEntity<AccesToken> refreshJwtToken() throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		final String URI = "https://login.bol.com/token";
		final String DATA = "client_id=78f16fd8-8206-4df4-a984-06889649bf73&"
				+ "client_secret=ZY1om_7k4-Ozi2tNUccMnUi1YZO_Cw-WtGM0xDPjkux16hvrgngkq0CmpUwuNUQQtMPM9dsTW7QNsjWdlXrDFA&"
				+ "grant_type=client_credentials";

		headers.add("Content-Type", "application/x-www-form-urlencoded");
		headers.add("Accept", "application/json");
		HttpEntity<String> entity = new HttpEntity<>(DATA, headers);

		try {
			return rest.exchange(URI, HttpMethod.POST, entity, AccesToken.class);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throw new ApiException(
					"Something went wrong while getting a bearer token from Bol.com: " + e.getResponseBodyAsString(),
					e);
		}
	}
}
