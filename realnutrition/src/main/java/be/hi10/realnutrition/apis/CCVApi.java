package be.hi10.realnutrition.apis;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.pojos.ccv.CCVResponse;
import be.hi10.realnutrition.util.Hasher;

@Component
public class CCVApi {
	private final String API_KEY = "82b2270bwryv90z8";
	private final String API_SECRET = "bb08r2z95x6gqov0y2o7ql1lj70xdl72";

	private final String DOMAIN = "https://www.realnutritionwholesale.be";

	public ResponseEntity<CCVResponse> getProductsinc(int start) throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		final String timestamp = getTimestamp();
		final String URI = "/api/rest/v1/products/?expand=attributecombinations&start=" + start + "&size=100";
		final String DATA = "";

		final String HASH;
		try {
			HASH = Hasher.createHash(String.format("%s|%s|%s|%s|%s", API_KEY, "GET", URI, DATA, timestamp), API_SECRET,
					"HmacSHA512");
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new ApiException("Something went wrong while hashing to get all products from CCV", e);
		}

		headers.add("x-public", API_KEY);
		headers.add("x-date", timestamp);
		headers.add("x-hash", HASH);

		HttpEntity<String> entity = new HttpEntity<>(DATA, headers);

		try {
			return rest.exchange(DOMAIN + URI, HttpMethod.GET, entity, CCVResponse.class);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throw new ApiException("Something went wrong while getting all the products from CCV: " + e.getMessage(),
					e);
		}
	}

	public void updateStock(int newStock, String attributeCombinationId) throws ApiException {

		RestTemplate rest = new RestTemplate();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(9999999);
		requestFactory.setReadTimeout(9999999);

		rest.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();

		final String timestamp = getTimestamp();
		final String URI = "/api/rest/v1/attributecombinations/" + attributeCombinationId + "/";
		final String DATA = "{\"stock\": " + newStock + "}";

		final String HASH;
		try {
			HASH = Hasher.createHash(String.format("%s|%s|%s|%s|%s", API_KEY, "PATCH", URI, DATA, timestamp),
					API_SECRET, "HmacSHA512");
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new ApiException("Something went wrong while hashing to updating a product from CCV", e);
		}

		headers.add("x-public", API_KEY);
		headers.add("x-date", timestamp);
		headers.add("x-hash", HASH);
		headers.add("Content-Type", "application/internal.resource.products.patch.v1.json");

		HttpEntity<String> entity = new HttpEntity<>(DATA, headers);

		try {
			rest.exchange(DOMAIN + URI, HttpMethod.PATCH, entity, String.class);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throw new ApiException("Something went wrong while updating a product from CCV: " + e.getMessage(), e);
		}
	}

	private String getTimestamp() {
		String timestamp = ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT);
		return timestamp.substring(0, 19) + "Z";
	}
}
