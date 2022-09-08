package be.hi10.realnutrition.apis;


import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import be.hi10.realnutrition.beans.RateLimitBean;
import be.hi10.realnutrition.entities.RetryItem;
import be.hi10.realnutrition.enums.Website;
import be.hi10.realnutrition.exceptions.RefreshTokenException;
import be.hi10.realnutrition.pojos.exactonline.custom.ProductList;
import be.hi10.realnutrition.pojos.exactonline.custom.StockResponse;
import be.hi10.realnutrition.pojos.exactonline.item.ItemBulkResponse;
import be.hi10.realnutrition.pojos.exactonline.item.Result;
import be.hi10.realnutrition.pojos.exactonline.stockposition.StockPositionContent;
import be.hi10.realnutrition.pojos.exactonline.stockposition.StockPositionResponse;
import be.hi10.realnutrition.services.RefreshTokenService;

@Component
public class ExactOnlineApi {

	@Autowired
	private RefreshTokenService refreshTokenService;
	
	@Autowired
	private RateLimitBean rateLimitBean;
	
//	private BlockingBucket bucket = rateLimitBean.getBucket();

	/*
	 * *****ATTENTION******* The minute-rate limit of 300 per minute is handled (see
	 * TIME_THREAD_SLEEPS), for the moment the day-limit of 50 000 isn't handled but
	 * doesn't have to be handled. From Exact Online Api Limit Docs you can read
	 * they will probably lower the daily limit to 5000 in the future. If that's the
	 * case, the method will go in error and next launch of scheduled will retry. In
	 * general only 401 Unauthorized exceptions are really handled. All other
	 * exceptions will break the scheduled-method, next launch of scheduled method
	 * will then again retry to make request. If daily limit is set to 5000 or
	 * minutely limit of 300 is lowered, then this method needs appropriate
	 * refactoring.
	 */

	// Logger
	private final static Logger LOGGER = LoggerFactory.getLogger(ExactOnlineApi.class);

	// GENERAL
	private static final int TIME_THREAD_SLEEPS = 500;

	private static final String DIVISION = "354283";

	// URL's
	private static final String GET_ITEMS_URL = "https://start.exactonline.be/api/v1/" + DIVISION
			+ "/bulk/logistics/Items?$select=ID,Barcode";

	private static final String GET_ITEMS_WITH_STOCK_URL = "https://start.exactonline.be/api/v1/354283/bulk/logistics/Items?itemId=guid'616299a8-3f51-4b3d-aed8-0ac45679c84b'&$select=Barcode,Stock";

	private static final String GET_STOCK_POSITION_URL = "https://start.exactonline.be/api/v1/" + DIVISION
			+ "/read/logistics/StockPosition?itemId=guid'";

	private static final String GET_ITEM_URL = "https://start.exactonline.be/api/v1/" + DIVISION
			+ "/logistics/Items?$filter=ID eq guid'";

	public Set<RetryItem> getItems() throws RefreshTokenException, HttpClientErrorException, HttpServerErrorException {

		// Make template + headers + empty items + setBearerAuth + set nextUrl as
		// initial URL for getting items in bulk
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		Set<RetryItem> retryItems = new LinkedHashSet<>();
		String nextUrl = GET_ITEMS_URL;

		String accessToken = refreshTokenService.getAccessToken();

		headers.setBearerAuth(accessToken);
		headers.add("Accept", "application/json");
		HttpEntity<?> request = new HttpEntity<>(headers);

		// Check if unauthorized already launched for getting the items in bulk
		boolean unauthorizedErrorLaunchedOnce = false;

		while (nextUrl != null) {

			try {
				LOGGER.info("Requesting permit from RateLimiter");
				rateLimitBean.getBucket().consume(1);
				LOGGER.info("Permit granted");

				ResponseEntity<ItemBulkResponse> bulkResponse = rest.exchange(nextUrl, HttpMethod.GET, request,
						ItemBulkResponse.class);

				// If above 'GET'-request for getting items in bulk goes in error, the following
				// lines won't be launched. Only get nextUrl if 'GET'-request succeeded, also
				// set unauthorized back to false so that nextUrl can begin with clean
				// unauthorized. If no next is found in bulkResponse, while loop will stop
				
				rateLimitBean.updateBucketLimits(bulkResponse.getHeaders());
								
				nextUrl = bulkResponse.getBody().getContent().getNext();
				unauthorizedErrorLaunchedOnce = false;
				for (Result result : bulkResponse.getBody().getContent().getResults()) {

					// We can't update in shops if barcode/ean == null, so these should be forgotten
					

						// You have ean and id from bulk-items (see URL), you make item with still
						// unknown stock-position, which you will fetch now
						RetryItem retryItem = new RetryItem(result.getBarcode(), result.getId(), null, Website.EXACT.toString());
						retryItems.add(retryItem);

						// Fetch stockPosition
//						try {
//							retryItems.add(this.setStockPosition(retryItem, request, rest));
//						} catch (HttpClientErrorException | HttpServerErrorException e) {
//							if (e.getRawStatusCode() == 401) {
//
//								LOGGER.error("Exact-Api --> Getting stock position error: "
//										+ e.getResponseBodyAsString() + e.getStackTrace());
//
//								// Refresh access_token and retry
//								headers.setBearerAuth(refreshTokenService.getAccessToken());
//								request = new HttpEntity<>(headers);
//								try {
//									retryItems.add(this.setStockPosition(retryItem, request, rest));
//								} catch (HttpClientErrorException | HttpServerErrorException eRetry) {
//
//									LOGGER.error(
//											"Exact-Api --> Something went wrong while trying to get StockPosition (inside 401 unauthorized): "
//													+ eRetry.getResponseBodyAsString() + eRetry.getStackTrace());
//								}
//							} else {
//
//								LOGGER.error("Exact-Api --> Something went wrong while trying to get StockPosition: ", e);
//							}
//						}
					
				}
			} catch (HttpClientErrorException | HttpServerErrorException e) {
				if (e.getRawStatusCode() == 401) {
					if (unauthorizedErrorLaunchedOnce) {

						LOGGER.error("Exact-Api --> Something went wrong while trying to get Items (unauthorizedErrorLaunchedOnce): ", e);

						// Following line will stop the while-loop
						nextUrl = null;
					} else {

						LOGGER.error("Exact-Api --> Getting bulk items error: ", e);
						// Refresh token and retry with same nextUrl
						headers.setBearerAuth(refreshTokenService.getAccessToken());
						request = new HttpEntity<>(headers);
						unauthorizedErrorLaunchedOnce = true;
					}
				} else {
					rateLimitBean.updateBucketLimits(e.getResponseHeaders());

					LOGGER.error("Exact-Api --> Something went wrong while getting next bulk items: ", e);
					// Following line will stop the while-loop
					nextUrl = null;
				}
			} catch (InterruptedException e) {
				LOGGER.error("Exact-Api --> Rate limit blocking interrupted: ", e);
			}
		}
		return retryItems;
	}

	public RetryItem getItem(RetryItem retryItem) throws RefreshTokenException, HttpClientErrorException, HttpServerErrorException {

		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.setBearerAuth(refreshTokenService.getAccessToken());
		HttpEntity<?> request = new HttpEntity<>(headers);

		// Get EAN
		if (retryItem.getEan() == null) {
			try {
				retryItem = this.setEan(retryItem, request, rest);
			} catch (HttpClientErrorException | HttpServerErrorException e) {
//				StringWriter sw = new StringWriter();
//				PrintWriter pw = new PrintWriter(sw);
//				e.printStackTrace(pw);
//				LOGGER.error("Exact-Api --> setEan failed, resulting error: " + sw.toString());
				rateLimitBean.updateBucketLimits(e.getResponseHeaders());
				LOGGER.error("Exact-Api --> setEan failed, resulting error: ", e);
				if (e.getRawStatusCode() == 401) {

					// Refresh access_token and retry
					headers.setBearerAuth(refreshTokenService.getAccessToken());
					request = new HttpEntity<>(headers);
					try {
						retryItem = this.setEan(retryItem, request, rest);
					} catch (HttpClientErrorException | HttpServerErrorException eRetry) {
//						sw.getBuffer().setLength(0);
//						eRetry.printStackTrace(pw);
//
//						LOGGER.error(
//								"Exact-Api --> Getting single item for ean in WebHookNotification handler after unauthorized retry: "
//										+ sw.toString());
						rateLimitBean.updateBucketLimits(eRetry.getResponseHeaders());
						LOGGER.error("Exact-Api --> Getting single item for ean in WebHookNotification handler after unauthorized retry: ", eRetry);
					}
				}
			}
		}

		// Get Projected Stock
		if (retryItem.getProjectedStock() == null && retryItem.getEan() != null) {
			try {
				retryItem = this.setStockPosition(retryItem, request, rest);
			} catch (HttpClientErrorException | HttpServerErrorException e) {
				rateLimitBean.updateBucketLimits(e.getResponseHeaders());
				if (e.getRawStatusCode() == 401) {

					LOGGER.error("Exact-Api --> Getting stockPosition in WebHookNotification handler: ", e);

					headers.setBearerAuth(refreshTokenService.getAccessToken());
					request = new HttpEntity<>(headers);
					try {
						retryItem = this.setStockPosition(retryItem, request, rest);
					} catch (HttpClientErrorException | HttpServerErrorException eRetry) {
						rateLimitBean.updateBucketLimits(eRetry.getResponseHeaders());
						LOGGER.error(
								"Exact-Api --> Getting stockPosition in WebHookNotification handler after unauthorized retry: ", e);
					}
				} else {

					LOGGER.error("Exact-Api --> Something went wrong while trying to get StockPosition in getItem: ", e);
				}
			}
		}

		return retryItem;
	}
	
	/*function to only set ean for realnutrition website*/
	public RetryItem getItemEan(RetryItem retryItem) throws RefreshTokenException, HttpClientErrorException, HttpServerErrorException {

		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.setBearerAuth(refreshTokenService.getAccessToken());
		HttpEntity<?> request = new HttpEntity<>(headers);

		if (retryItem.getEan() == null) {
			try {
				retryItem = this.setEan(retryItem, request, rest);
			} catch (HttpClientErrorException | HttpServerErrorException e) {

				rateLimitBean.updateBucketLimits(e.getResponseHeaders());
				LOGGER.error("Exact-Api --> setEan failed, resulting error: ", e);
				if (e.getRawStatusCode() == 401) {

					// Refresh access_token and retry
					headers.setBearerAuth(refreshTokenService.getAccessToken());
					request = new HttpEntity<>(headers);
					try {
						retryItem = this.setEan(retryItem, request, rest);
					} catch (HttpClientErrorException | HttpServerErrorException eRetry) {
						rateLimitBean.updateBucketLimits(eRetry.getResponseHeaders());
						LOGGER.error("Exact-Api --> Getting single item for ean in WebHookNotification handler after unauthorized retry: ", eRetry);
					}
				}
			}
		}

		return retryItem;
	}
	
	/*Endfunction to only set ean for realnutrtion website*/
	
	/*function to only set stock for realnutrition website*/
	public RetryItem getItemStock(RetryItem retryItem) throws RefreshTokenException, HttpClientErrorException, HttpServerErrorException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.setBearerAuth(refreshTokenService.getAccessToken());
		HttpEntity<?> request = new HttpEntity<>(headers);

		if (retryItem.getProjectedStock() == null && retryItem.getEan() != null) {
			try {
				retryItem = this.setStockPosition(retryItem, request, rest);
			} catch (HttpClientErrorException | HttpServerErrorException e) {
				rateLimitBean.updateBucketLimits(e.getResponseHeaders());
				if (e.getRawStatusCode() == 401) {

					LOGGER.error("Exact-Api --> Getting stockPosition for realnutritionwebsite in WebHookNotification handler: ", e);

					headers.setBearerAuth(refreshTokenService.getAccessToken());
					request = new HttpEntity<>(headers);
					try {
						retryItem = this.setStockPosition(retryItem, request, rest);
					} catch (HttpClientErrorException | HttpServerErrorException eRetry) {
						rateLimitBean.updateBucketLimits(eRetry.getResponseHeaders());
						LOGGER.error(
								"Exact-Api --> Getting stockPosition for realnutritionwebsite in WebHookNotification handler after unauthorized retry: ", e);
					}
				} else {

					LOGGER.error("Exact-Api --> Something went wrong while trying to get StockPosition for realnutritionwebsite in getItem: ", e);
				}
			}
		}

		return retryItem;
	}
	/*Endfunction to only set stock for realnutrition website*/
	

	private RetryItem setEan(RetryItem retryItem, HttpEntity<?> request, RestTemplate rest)
			throws HttpClientErrorException, HttpServerErrorException {

		String url = GET_ITEM_URL + retryItem.getItemId() + "'&$select=ID,Barcode";
		ResponseEntity<ItemBulkResponse> itemResponse = null;
		try {
			LOGGER.info("Requesting permit from RateLimiter");
			rateLimitBean.getBucket().consume(1);
			LOGGER.info("Permit granted");
			itemResponse = rest.exchange(url, HttpMethod.GET, request,
					ItemBulkResponse.class);
			rateLimitBean.updateBucketLimits(itemResponse.getHeaders());
		} catch (InterruptedException e) {
			LOGGER.error("Exact-Api --> Rate limit blocking interrupted: ", e);
		}
		

		if (itemResponse != null && itemResponse.getBody().getContent().getResults().size() > 0) {
			retryItem.setEan(itemResponse.getBody().getContent().getResults().get(0).getBarcode());
		}
		else
		{
			LOGGER.warn("Exact-Api --> No item found with id: " + retryItem.getItemId());
		}
		return retryItem;
	}

	private RetryItem setStockPosition(RetryItem retryItem, HttpEntity<?> request, RestTemplate rest)
			throws HttpClientErrorException, HttpServerErrorException {

		String url = GET_STOCK_POSITION_URL + retryItem.getItemId() + "'";
		this.sleep(TIME_THREAD_SLEEPS);

		// Following line throws HttpClientErrorException
		try {
			LOGGER.info("Requesting permit from RateLimiter");
			rateLimitBean.getBucket().consume(1);
			LOGGER.info("Permit granted");
			ResponseEntity<StockPositionResponse> stockResponse = rest.exchange(url, HttpMethod.GET, request,
			StockPositionResponse.class);
			rateLimitBean.updateBucketLimits(stockResponse.getHeaders());
			StockPositionContent stockPositionContent = stockResponse.getBody().getContent().get(0);
			int projectedStock = stockPositionContent.getInStock() + stockPositionContent.getPlanningIn()
					- stockPositionContent.getPlanningOut();
			if (projectedStock < 0) {
				projectedStock = 0;
			}
			retryItem.setProjectedStock(projectedStock);
		} catch (InterruptedException e) {
			LOGGER.error("Exact-Api --> Rate limit blocking interrupted: ", e);
		}
		return retryItem;
	}

	public ProductList getItemsWithStock() throws RefreshTokenException {

		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		String accessToken = refreshTokenService.getAccessToken();

		headers.setBearerAuth(accessToken);
		headers.add("Accept", "application/json");
		HttpEntity<?> request = new HttpEntity<>(headers);

		try {
			LOGGER.info("Requesting permit from RateLimiter");
			rateLimitBean.getBucket().consume(1);
			LOGGER.info("Permit granted");
			ResponseEntity<StockResponse> stockResponse = rest.exchange(GET_ITEMS_WITH_STOCK_URL, HttpMethod.GET, request, StockResponse.class);
			rateLimitBean.updateBucketLimits(stockResponse.getHeaders());
			return stockResponse.getBody().getContent();
		} catch (InterruptedException e) {
			LOGGER.error("Exact-Api --> Rate limit blocking interrupted: ", e);
		}
		return null;
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
	}
