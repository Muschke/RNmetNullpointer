package be.hi10.realnutrition.webhooks;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import be.hi10.realnutrition.apis.ExactOnlineApi;
import be.hi10.realnutrition.beans.ShopsUpdateBean;
import be.hi10.realnutrition.beans.SynchronizedMethodsBean;
import be.hi10.realnutrition.entities.RetryItem;
import be.hi10.realnutrition.entities.ExactHashError;
import be.hi10.realnutrition.enums.NotAProduct;
import be.hi10.realnutrition.enums.Website;
import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.exceptions.RefreshTokenException;
import be.hi10.realnutrition.pojos.exactonline.webhooknotification.WebHookNotificationResponse;
import be.hi10.realnutrition.services.RetryItemService;
import be.hi10.realnutrition.services.ExactHashErrorService;

@Component
public class ExactWebHook {
	@Autowired
	RetryItemService retryItemService;

	@Autowired
	ExactHashErrorService exactHashErrorService;

	@Autowired
	ExactOnlineApi exactOnlineApi;

	@Autowired
	ShopsUpdateBean shopsUpdateBean;

	// Logger
	private static final Logger LOGGER = LoggerFactory.getLogger(ExactWebHook.class);

	// GENERAL
	
	@SuppressWarnings("unused")
	private static final String WEBHOOK_SECRET = "Ysc824ezYPq4hC0e";
	@SuppressWarnings("unused")
	private static final String HASH_ALGORITM = "HmacSHA256";

	public void handleNotification(WebHookNotificationResponse webHookNotificationResponse) throws ApiException {

		ExactHashError exactHashError = new ExactHashError(webHookNotificationResponse.getContent().getAction(),
				webHookNotificationResponse.getContent().getDivision(),
				webHookNotificationResponse.getContent().getExactOnlineEndpoint(),
				webHookNotificationResponse.getContent().getEventCreatedOn(), webHookNotificationResponse.getHashCode(),
				webHookNotificationResponse.getContent().getKey(), webHookNotificationResponse.getContent().getTopic());

		RetryItem retryItem = new RetryItem(null, exactHashError.getKey(), null, Website.EXACT.toString());
		this.getExactItemAndUpdateShops(retryItem, false);
	}

	public void getExactItemAndUpdateShops(RetryItem retryItem, boolean retryProcedure) {
		try {
			retryItem = exactOnlineApi.getItem(retryItem);
			LOGGER.info("ExactWebHook --> getItem () from Exact API succeeded.");
		} catch (HttpClientErrorException | HttpServerErrorException | RefreshTokenException e) {
			if (retryProcedure) {
				SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
			}
			LOGGER.error(
					"Exact-Api --> Could not get accessToken for WebHookNotification handler: " + e.getStackTrace());
		}

		if(isAProduct(retryItem)) {
			if (retryItem.getProjectedStock() == null || retryItem.getEan() == null) {
					this.saveRetryItemToDatabase(retryItem);
				
			} else {
//					this.deleteRetryItemFromDatabase(key);
					retryItemService.delete(retryItem);
					LOGGER.info("UPDATING PRODUCT WITH EAN: " + retryItem.getEan() + " AND STOCK: "
							+ retryItem.getProjectedStock());
					shopsUpdateBean.updateAllShops(retryItem.getProjectedStock(), retryItem.getEan(), retryProcedure);
			}
		}
		

	}
	
	public void getExactItemAndUpdateShops(RetryItem retryItem, Set<RetryItem> exactRetryItemsFromApi, boolean retryProcedure) {
		for(RetryItem itemFromApi : exactRetryItemsFromApi) {
			if(itemFromApi.getItemId().equals(retryItem.getItemId())) {
				if(itemFromApi.getEan() == null) {
					LOGGER.info("RetryItem with itemId: " + retryItem.getItemId() + " has no EAN");
					return;
				}
				if(!isAProduct(itemFromApi)) {
					retryItemService.delete(retryItem);
					return;
				} else {
					retryItem.setEan(itemFromApi.getEan());
				}
			}
		}
		
		getExactItemAndUpdateShops(retryItem, retryProcedure);
		
//		consider this as alternative implementation of method
//		long match = exactRetryItemsFromApi.stream()
//				.filter(itemFromApi -> itemFromApi.getItemId().equals(retryItem.getItemId()))
//				.filter(itemFromApi -> !isAProduct(itemFromApi)).count();
//		if (match > 0) {
//			retryItemService.delete(retryItem);
//		} else {
//			getExactItemAndUpdateShops(retryItem, retryProcedure);
//		}
	}

	public boolean checkHashEquality(ExactHashError exactHashError, boolean retryProcedure) {

		return true;
//
//		/*
//		 * 1 ObjectMapper Obj = new ObjectMapper(); String contentJsonStr = ""; boolean
//		 * hashWorked = false;
//		 * 
//		 * try { contentJsonStr = Obj.writeValueAsString(exactHashError); String
//		 * hashFromHasher = Hasher.createHash(contentJsonStr, WEBHOOK_SECRET,
//		 * HASH_ALGORITM).toUpperCase(); String hashFromError =
//		 * exactHashError.getHashCode();
//		 * 
//		 * System.out.println("Hash from Hasher: " + hashFromHasher);
//		 * System.out.println("Hash from error: " + hashFromError);
//		 * 
//		 * 
//		 * if (Hasher.createHash(contentJsonStr, WEBHOOK_SECRET,
//		 * HASH_ALGORITM).equals(exactHashError.getHashCode())) { hashWorked = true;
//		 * this.deleteHashErrorsFromDatabase(exactHashError.getKey()); } else {
//		 * System.out.println("komt hier"); if (retryProcedure) {
//		 * exactHashErrorService.delete(exactHashError); } }
//		 * 
//		 * } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
//		 * 
//		 * if (!retryProcedure) { exactHashErrorService.save(exactHashError); } }
//		 * 
//		 * // return hashWorked; return true;
//		 */
	}

	private void saveRetryItemToDatabase(RetryItem retryItem) {
		if (retryItem.getCreatedOn() == null) {
			retryItem.setCreatedOn(LocalDateTime.now(ZoneId.of("UTC")));
		} else {
			retryItem.setLastChanged(LocalDateTime.now(ZoneId.of("UTC")));
		}
		retryItemService.save(retryItem);
		LOGGER.info("ExactWebHook --> Saved RetryItem, itemId = " + retryItem.getItemId()
				+ " to the 'retryitems' table in the database.");
	}
	@SuppressWarnings("unused")
	private void deleteRetryItemFromDatabase(String key) {
		Optional<RetryItem> optionalRetryItem = retryItemService.findFirstByWebsiteAndItemId(Website.EXACT.toString(),
				key);
		if (optionalRetryItem.isPresent()) {
			retryItemService.delete(optionalRetryItem.get());
			LOGGER.info("ExactWebHook --> Deleted RetryItem, itemId = " + optionalRetryItem.get().getItemId()
					+ " from 'retryitems' table in the database.");
		}
	}
	@SuppressWarnings("unused")
	private void deleteHashErrorsFromDatabase(String key) {
		List<ExactHashError> exactHashErrors = exactHashErrorService.findByKey(key);
		if (exactHashErrors.size() > 0) {
			exactHashErrorService.deleteAll(exactHashErrors);
		}
	}

	public boolean isAProduct(RetryItem retryItem) {
		LOGGER.info("ExactWebHook --> isAProduct () method called.");
		for (NotAProduct notAProduct : NotAProduct.values()) {
			if (notAProduct.ean.equals(retryItem.getEan())) {
				LOGGER.info("ExactWebHook --> Product with ean = " + retryItem.getEan() + " is not a product.");
				return false;
			}
		}
		LOGGER.info("ExactWebHook --> Product with ean = " + retryItem.getEan() + " is a product.");
		return true;
	}
}
