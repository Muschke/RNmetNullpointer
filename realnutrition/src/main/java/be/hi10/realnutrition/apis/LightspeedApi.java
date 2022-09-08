package be.hi10.realnutrition.apis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.pojos.lightspeed.LightspeedResponse;
import be.hi10.realnutrition.pojos.updatemessages.LightspeedStockUpdateMessage;
import be.hi10.realnutrition.pojos.updatemessages.LightspeedVariant;



@Component
public class LightspeedApi {

	@SuppressWarnings("unused")
	private final String DOMAIN = "api.webshopapp.com";
	@SuppressWarnings("unused")
	private final static Logger LOGGER = LoggerFactory.getLogger(LightspeedApi.class);

	public String getProductId(String ean, String ACCESS_TOKEN_KEY) throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		headers.add("Authorization", ACCESS_TOKEN_KEY);

		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		final String URI = "/nl/variants.json?ean=" + ean;

		try {
			
			ResponseEntity<LightspeedResponse> response = 
					rest.exchange("https://api.webshopapp.com" + URI,
					HttpMethod.GET, entity,LightspeedResponse.class);
			if (response.getBody().getVariants().isEmpty() || response.getBody().getVariants() == null) {
				return null;
			} else {
				return response.getBody().getVariants().get(0).getId().toString();
			}
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throw new ApiException("Something went wrong while getting 1 product from Lightspeed: " + e.getMessage(), e,
					null);
		}
	}

	public void updateStock(int newStock, String ean, String itemId, String ACCESS_TOKEN_KEY) throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", ACCESS_TOKEN_KEY);

			if (itemId == null) {
				itemId = getProductId(ean, ACCESS_TOKEN_KEY);
			}

			if (itemId != null) {
				LightspeedStockUpdateMessage body = new LightspeedStockUpdateMessage(new LightspeedVariant(newStock));
				HttpEntity<LightspeedStockUpdateMessage> entity = new HttpEntity<>(body, headers);
				final String URI = "/nl/variants/" + itemId + ".json";

				try {
					rest.exchange("https://api.webshopapp.com" + URI, HttpMethod.PUT,
							entity, LightspeedResponse.class);
				} catch (HttpClientErrorException | HttpServerErrorException e) {
					throw new ApiException(
							"Something went wrong while updating a product from Lightspeed: ", e, itemId);
				}
			}
	}
	
	//DEPRICATED METHOD
	/*public ResponseEntity<LightspeedResponse> getProducts(int page) throws ApiException {
	RestTemplate rest = new RestTemplate();

	HttpHeaders headers = new HttpHeaders();
	headers.add("Authorization", ACCESS_TOKEN_KEY);

	HttpEntity<?> entity = new HttpEntity<Object>(headers);
	final String URI = "/nl/variants.json?page=" + page;

	try {
		return rest.exchange(String.format("https://%s:%s@%s%s", API_KEYS[0], API_SECRETS[0], DOMAIN, URI), HttpMethod.GET,
				entity, LightspeedResponse.class);
	} catch (HttpClientErrorException | HttpServerErrorException e) {
		throw new ApiException(
				"Something went wrong while getting all the products from Lightspeed: " + e.getMessage(), e);
	}
}*/
}
