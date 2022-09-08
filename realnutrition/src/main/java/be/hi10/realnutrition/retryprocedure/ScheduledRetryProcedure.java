package be.hi10.realnutrition.retryprocedure;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import be.hi10.realnutrition.apis.*;
import be.hi10.realnutrition.apis.amazon.AllProducts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import be.hi10.realnutrition.beans.ShopsUpdateBean;
import be.hi10.realnutrition.beans.SynchronizedMethodsBean;
import be.hi10.realnutrition.domain.Product;
import be.hi10.realnutrition.entities.ExactHashError;
import be.hi10.realnutrition.entities.Item;
import be.hi10.realnutrition.entities.RetryItem;
import be.hi10.realnutrition.entities.SalesProduct;
import be.hi10.realnutrition.enums.Website;
import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.exceptions.RefreshTokenException;
import be.hi10.realnutrition.pojos.ccv.CCVResponse;
import be.hi10.realnutrition.pojos.ccv.Collection;
import be.hi10.realnutrition.services.ExactHashErrorService;
import be.hi10.realnutrition.services.ItemService;
import be.hi10.realnutrition.services.RetryItemService;
import be.hi10.realnutrition.services.SalesProductService;
import be.hi10.realnutrition.webhooks.ExactWebHook;

@Component
public class ScheduledRetryProcedure {
    private final ItemService itemService;
    private final AllProducts allProducts;
    private final Bol1ComApi bol1ComApi;
    private final Bol2ComApi bol2ComApi;
    private final CCVApi ccvApi;
    @SuppressWarnings("unused")
	private final CCVHealthyNutritionApi ccvHealthyNutritionApi;
    @SuppressWarnings("unused")
    private final WooApi wooApi;
    private final ExactHashErrorService exactHashErrorService;
    private final ExactWebHook exactWebHook;
    private final RetryItemService retryItemService;
    private final ShopsUpdateBean shopsUpdateBean;
    private static boolean deleteAmazonRetryItems = false;  
    private static boolean deleteBolRetryItems = false;
    private static boolean deleteBol2RetryItems = false;
    private static boolean deleteCCVRetryItems = false;
    private static boolean deleteCCVHealthyNutritionRetryItems = false;
    private static boolean deleteWooRetryItems = false;
    
    @Autowired
    private SalesProductService salesProductService;

    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduledRetryProcedure.class);
    
    @Autowired
    ExactOnlineApi exactOnlineApi;

    @Autowired
    public ScheduledRetryProcedure(ItemService itemService,  AllProducts allProducts, Bol1ComApi bol1ComApi, Bol2ComApi bol2ComApi, CCVApi ccvApi,
                                   CCVHealthyNutritionApi ccvHealthyNutritionApi, WooApi wooApi, ExactHashErrorService exactHashErrorService, ExactWebHook exactWebHook,
                                   RetryItemService retryItemService, ShopsUpdateBean shopsUpdateBean) {
    	this.allProducts = allProducts;
        this.itemService = itemService;
        this.bol1ComApi = bol1ComApi;
        this.bol2ComApi = bol2ComApi;
        this.ccvApi = ccvApi;
        this.ccvHealthyNutritionApi = ccvHealthyNutritionApi;
        this.wooApi = wooApi;
        this.exactHashErrorService = exactHashErrorService;
        this.exactWebHook = exactWebHook;
        this.retryItemService = retryItemService;
        this.shopsUpdateBean = shopsUpdateBean;
    }

    public void retryProcedure() {

    	System.out.println("--------------------------IK BEN DE RETRY---------------------------");
        var deletedItems = itemService.count();
        itemService.deleteAll();
        LOGGER.info("RetryProcedure --> Deleted " + deletedItems + " items from the 'items' table in the database.");
        Set<Item> newItems = new HashSet<>();

        this.getBolItemsAndSaveInDB(newItems);
        this.getBol2ItemsAndSaveInDB(newItems);
        this.getCcvItemsAndSaveInDB(newItems);
        this.getAmazonItemsAndSaveInDb(newItems);
        //this.getCcvHealthyNutritionItemsAndSaveInDB(newItems);
        // this.getWooItemsAndSaveInDB(newItems);

        itemService.saveAll(newItems);
        LOGGER.info("RetryProcedure --> Saved " + itemService.count() + " items to the 'items' table in the database.");

        // Retry Errors
        LOGGER.info("RetryProcedure --> Started checking HashErrors.");
        this.checkHashErrors();
        LOGGER.info("RetryProcedure --> Finished checking HashErrors.");
        LOGGER.info("RetryProcedure --> Started checking RetryItems.");
        this.checkRetryItems();
        LOGGER.info("RetryProcedure --> Finished checking RetryItems.");
    }

//    private void getWooItemsAndSaveInDB(Set<Item> newItems) {
//        try {
//            ResponseEntity<List<WooItem>> response;
//
//            response = wooApi.getProducts();
//
//            if (response.getBody().size() != 0) {
//                for (WooItem item : response.getBody()) {
//
//                    if (item.getEan() != null && !item.getEan().trim().equals("") && item.getId() != null) {
//                        newItems.add(new Item(item.getEan(), item.getId(), Website.WOO.toString()));
////							LOGGER.info("RetryProcedure --> WOO: Added new item with EAN: " + item.getEan());
//                    }
//                }
//            }
//            setDeleteWooRetryItems(true);
//        } catch (ApiException e) {
//            setDeleteWooRetryItems(false);
//            SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
//            LOGGER.error("RetryProcedure --> WOO: Something went wrong while getting next bulk items: " + e.getMessage()
//                    + e.getStackTrace());
//        }
//    }

    private void checkHashErrors() {

        Set<String> keysNotToCheckAnymore = new HashSet<>();
        List<ExactHashError> exactHashErrors = exactHashErrorService.findAll();

        if (exactHashErrors.size() > 0) {
            for (ExactHashError exactHashError : exactHashErrors) {
                if (!keysNotToCheckAnymore.contains(exactHashError.getKey())) {
                    if (exactWebHook.checkHashEquality(exactHashError, true)) {
                        String key = exactHashError.getKey();
                        keysNotToCheckAnymore.add(key);
                        RetryItem retryItem = new RetryItem(null, key, null, Website.EXACT.toString());

                        exactWebHook.getExactItemAndUpdateShops(retryItem, true);
                    }
                }
            }
        }
    }

    private void checkRetryItems() {
        List<RetryItem> retryItems = retryItemService.findByWebsiteIsNotOrderByCreatedOnAsc(Website.OTHER.toString());
        Set<RetryItem> exactRetryItemsFromApi = null;
        try {
			exactRetryItemsFromApi = exactOnlineApi.getItems();
		} catch (RefreshTokenException e) {
			LOGGER.error("ScheduledRetryProcedure --> Something went wrong while getting items from API: " , e);
		};
		
		
		 /*code to update stock on realnutritition website*/
		for (RetryItem itemFromApi: exactRetryItemsFromApi) {
			LOGGER.info("in loop of items from apie - item id: " + itemFromApi.getItemId() + "/ ean: " + itemFromApi.getEan());//ean al aanwezig
			
       	try {
       		LOGGER.error("started to get ean for item");
       		itemFromApi = exactOnlineApi.getItemEan(itemFromApi);
       		LOGGER.error("received ean for item");
       	} catch (HttpClientErrorException |HttpServerErrorException |RefreshTokenException e) {
       			LOGGER.error("something went wrong in api to fetch ean for product to update realnutritionwebsite " + e.getStackTrace());
			}
       	
       	LOGGER.info("after adding ean - item id: " + itemFromApi.getItemId() + "/ ean: " +itemFromApi.getEan());
      	 
       	if(itemFromApi.getEan() != null) {
           
       	
       		
   		if(salesProductService.findByEan(itemFromApi.getEan()).isPresent()) {
   			try {
   				itemFromApi = exactOnlineApi.getItemStock(itemFromApi);
				} catch (HttpClientErrorException |HttpServerErrorException |RefreshTokenException e) {
					System.out.print("something went wrong in api to fetch stock for product to update realnutritionwebsite " + e.getStackTrace());
				} 
   			
   			SalesProduct product = salesProductService.findByEan(itemFromApi.getEan()).get();
   			product.setStock(itemFromApi.getProjectedStock());
   			salesProductService.save(product);
   			
   			}    
       	}
   		 /*Endcode to update stock on realnutritition website*/
		}
		
		
		
		
		

        for (RetryItem retryItem : retryItems) {
            LOGGER.info("RetryProcedure --> RetryItem, ean = " + retryItem.getEan() + ", itemId = "
                    + retryItem.getItemId() + ", website = " + retryItem.getWebsite());

            switch (retryItem.getWebsite()) {
                case "EXACT":
                    LOGGER.info("RetryProcedure --> Website is EXACT.");
                    exactWebHook.getExactItemAndUpdateShops(retryItem, exactRetryItemsFromApi, true);
                    break;
                case "LIGHTSPEEDRETAILNUTRITION":
                    LOGGER.info("RetryProcedure --> Shop is LIGHTSPEEDRETAILNUTRITION.");
                    shopsUpdateBean.updateLightspeedProductRetailNutrition(retryItem.getItemId(),
                            retryItem.getProjectedStock(), retryItem.getEan(), true);
                    break;
                case "LIGHTSPEEDOLIFITSHOP":
                    LOGGER.info("RetryProcedure --> Shop is LIGHTSPEEDOLIFITSHOP.");
                    shopsUpdateBean.updateLightspeedProductOlifitshop(retryItem.getItemId(), retryItem.getProjectedStock(),
                            retryItem.getEan(), true);
                    break;
                case "BOL":
                    LOGGER.info("RetryProcedure --> Website is BOL.");
                    if (retryItem.getItemId() == null) {
                        shopsUpdateBean.updateBolProductWithoutItemId(retryItem.getProjectedStock(), retryItem.getEan(),
                                true);
                    } else {
                        shopsUpdateBean.updateBolProductWithItemId(retryItem.getItemId(), retryItem.getProjectedStock(),
                                retryItem.getEan(), true);
                    }
                    break;
                case "BOL2":
                    LOGGER.info("RetryProcedure --> Website is BOL2.");
                    if (retryItem.getItemId() == null) {
                        shopsUpdateBean.updateBol2ProductWithoutItemId(retryItem.getProjectedStock(), retryItem.getEan(),
                                true);
                    } else {
                        shopsUpdateBean.updateBol2ProductWithItemId(retryItem.getItemId(), retryItem.getProjectedStock(),
                                retryItem.getEan(), true);
                    }
                    break;
                case "CCV":
                    LOGGER.info("RetryProcedure --> Website is CCV.");
                    if (retryItem.getItemId() == null) {
                        shopsUpdateBean.updateCCVProductWithoutItemId(retryItem.getProjectedStock(), retryItem.getEan(),
                                true);
                    } else {
                        shopsUpdateBean.updateCCVProductWithItemId(retryItem.getItemId(), retryItem.getProjectedStock(),
                                retryItem.getEan(), true);
                    }
                    break;
                case "CCVHEALTHYNUTRITION":
                    LOGGER.info("RetryProcedure --> Website is CCVHEALTHYNUTRITION.");
                    if (itemExistsOnWebsite(Website.CCVHEALTHYNUTRITION, retryItem.getEan())) {
                        if (retryItem.getItemId() == null) {
                            shopsUpdateBean.updateCCVHealthyNutritionProductWithoutItemId(retryItem.getProjectedStock(), retryItem.getEan(),
                                    true);
                        } else {
                            shopsUpdateBean.updateCCVHealthyNutritionProductWithItemId(retryItem.getItemId(), retryItem.getProjectedStock(),
                                    retryItem.getEan(), true);
                        }
                    } else {
                    	setDeleteCCVHealthyNutritionRetryItems(true);
                        shopsUpdateBean.deleteRetryItemFromDB(Website.CCVHEALTHYNUTRITION, retryItem.getEan());
                    }
                    break;
                case "AMAZON":
                    LOGGER.info("RetryProcedure --> Website is AMAZON.");
                    if (retryItem.getItemId() == null) {
                        shopsUpdateBean.updateAmazonProductWithoutItemId(retryItem.getProjectedStock(), retryItem.getEan(),
                                true);
                    } else {
                        shopsUpdateBean.updateAmazonProductWithItemId(retryItem.getItemId(), retryItem.getProjectedStock(),
                                retryItem.getEan(), true);
                    }
                    break;
                // TODO
                /*
                 * case "WOO": if (error.getItemId() == null) {
                 * shopsUpdateBean.updateWooProductWithoutItemId(error.getProjectedStock(),
                 * error.getEan(), true); } else {
                 * shopsUpdateBean.updateWooProductWithItemId(error.getItemId(),
                 * error.getProjectedStock(), error.getEan(), true); } break;
                 */
                default:
                    break;

            }
        }
    }

    
    
    private void getCcvItemsAndSaveInDB(Set<Item> newItems) {

        try {
            ResponseEntity<CCVResponse> response;
            int page = 0;
            while (true) {
                response = ccvApi.getProductsinc(page);
                page += 99;

                if (response.getBody().getItems().size() == 0) {
                    break;
                }

                for (be.hi10.realnutrition.pojos.ccv.Item cCVItem : response.getBody().getItems()) {
                    for (Collection attributecombination : cCVItem.getAttributecombinations().getCollection()) {

                        String eanFromCCV = attributecombination.getEanNumber();
                        String itemIdFromCCV = attributecombination.getId().toString();

                        if (eanFromCCV != null && !eanFromCCV.trim().equals("")) {
                            newItems.add(new Item(eanFromCCV, itemIdFromCCV, Website.CCV.toString()));
//							LOGGER.info("RetryProcedure --> CCV: Added new item with EAN: " + eanFromCCV);
                        }

                    }
                }
            }
            setDeleteCCVRetryItems(true);
        } catch (ApiException e) {
            setDeleteCCVRetryItems(false);
            SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
            LOGGER.error("RetryProcedure --> CCV: Something went wrong while getting next bulk items: " + e.getMessage()
                    + e.getStackTrace());
        }
    }

//    private void getCcvHealthyNutritionItemsAndSaveInDB(Set<Item> newItems) {
//
//        try {
//            ResponseEntity<CCVResponse> response;
//            int page = 0;
//            while (true) {
//                response = ccvHealthyNutritionApi.getProductsinc(page);
//                page += 99;
//
//                if (response.getBody().getItems().size() == 0) {
//                    break;
//                }
//
//                for (be.hi10.realnutrition.pojos.ccv.Item cCVItem : response.getBody().getItems()) {
//                    for (Collection attributecombination : cCVItem.getAttributecombinations().getCollection()) {
//
//                        String eanFromCCV = attributecombination.getEanNumber();
//                        String itemIdFromCCV = attributecombination.getId().toString();
//
//                        if (eanFromCCV != null && !eanFromCCV.trim().equals("")) {
//                            newItems.add(new Item(eanFromCCV, itemIdFromCCV, Website.CCVHEALTHYNUTRITION.toString()));
////							LOGGER.info("RetryProcedure --> CCVHEALTHYNUTRITION: Added new item with EAN: " + eanFromCCV);
//                        }
//
//                    }
//                }
//            }
//            setDeleteCCVHealthyNutritionRetryItems(true);
//        } catch (ApiException e) {
//            setDeleteCCVHealthyNutritionRetryItems(false);
//            SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
//            LOGGER.error("RetryProcedure --> CCVHEALTHYNUTRITION: Something went wrong while getting next bulk items: " + e.getMessage()
//                    + e.getStackTrace());
//        }
//    }

    private void getBolItemsAndSaveInDB(Set<Item> newItems) {
        try {
            boolean firstLine = true;
            List<String> csv = bol1ComApi.requestCSVOfferList(null);
            int offerIdPosition = 0;
            int eanPosition = 0;

            for (String s : csv) {
                String[] line = s.split(",");
                if (firstLine) {
                    for (int i = 0; i < line.length; i++) {
                        if (line[i].toUpperCase().equals("EAN"))
                            eanPosition = i;
                        if (line[i].toUpperCase().equals("OFFERID"))
                            offerIdPosition = i;
                    }
                    firstLine = false;
                } else {

                    String eanFromBol = line[eanPosition];
                    String itemIdFromBol = line[offerIdPosition];

                    if (eanFromBol != null && !eanFromBol.trim().equals("")) {
                        newItems.add(new Item(eanFromBol, itemIdFromBol, Website.BOL.toString()));
//						LOGGER.info("RetryProcedure --> BOL: Added new item with EAN: " + eanFromBol);
                    }
                }
            }
            setDeleteBolRetryItems(true);
        } catch (ApiException e) {
            setDeleteBolRetryItems(false);
            SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
            LOGGER.error("RetryProcedure --> BOL: Something went wrong while getting next bulk items: " + e.getMessage()
                    + e.getStackTrace());
        }
    }

    private void getBol2ItemsAndSaveInDB(Set<Item> newItems) {
        try {
            boolean firstLine = true;
            List<String> csv = bol2ComApi.requestCSVOfferList(null);
            int offerIdPosition = 0;
            int eanPosition = 0;

            for (String s : csv) {
                String[] line = s.split(",");
                if (firstLine) {
                    for (int i = 0; i < line.length; i++) {
                        if (line[i].toUpperCase().equals("EAN"))
                            eanPosition = i;
                        if (line[i].toUpperCase().equals("OFFERID"))
                            offerIdPosition = i;
                    }
                    firstLine = false;
                } else {

                    String eanFromBol = line[eanPosition];
                    String itemIdFromBol = line[offerIdPosition];

                    if (eanFromBol != null && !eanFromBol.trim().equals("")) {
                        newItems.add(new Item(eanFromBol, itemIdFromBol, Website.BOL2.toString()));
//						LOGGER.info("RetryProcedure --> BOL2: Added new item with EAN: " + eanFromBol);
                    }
                }
            }

            setDeleteBol2RetryItems(true);
        } catch (ApiException e) {
            setDeleteBol2RetryItems(false);
            SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
            LOGGER.error("RetryProcedure --> BOL2: Something went wrong while getting next bulk items: "
                    + e.getMessage() + e.getStackTrace());
        }
    }
    
    
    private void getAmazonItemsAndSaveInDb(Set<Item> newItems) {
    	try {
    	List<Product> amazonList = allProducts.getAllProductsFromReportwithEan();
    	
    	for(var product: amazonList) {
    		newItems.add(new Item(product.getEan(), product.getSku(), Website.AMAZON.toString()));
    	}
    	setDeleteAmazonRetryItems(true);
 
    		}catch(Exception e) {
    	setDeleteAmazonRetryItems(false);
    	LOGGER.error("RetryProcedure --> AMAZON: Something went wrong while getting bulk items from amazon: " + e.getMessage());
    		}
       	LOGGER.info("RetryProcedure --> AMAZON: Getting bulk items from amazon succedeed.");
    }
    
    
    public static boolean isDeleteBolRetryItems() {
        return deleteBolRetryItems;
    }

    public static void setDeleteBolRetryItems(boolean deleteBolRetryItems) {
        ScheduledRetryProcedure.deleteBolRetryItems = deleteBolRetryItems;
    }

    public static boolean isDeleteBol2RetryItems() {
        return deleteBol2RetryItems;
    }

    public static void setDeleteBol2RetryItems(boolean deleteBolRetryItems) {
        ScheduledRetryProcedure.deleteBol2RetryItems = deleteBolRetryItems;
    }
    
    public static boolean isDeleteAmazonRetryItems() {
    	return deleteAmazonRetryItems;
    }
    
    public static void setDeleteAmazonRetryItems(boolean deleteAmazonRetryItems) {
        ScheduledRetryProcedure.deleteAmazonRetryItems = deleteAmazonRetryItems;
    }
    public static boolean isDeleteCCVRetryItems() {
        return deleteCCVRetryItems;
    }

    public static void setDeleteCCVRetryItems(boolean deleteCCVRetryItems) {
        ScheduledRetryProcedure.deleteCCVRetryItems = deleteCCVRetryItems;
    }

    public static boolean isDeleteCCVHealthyNutritionRetryItems() {
        return deleteCCVHealthyNutritionRetryItems;
    }

    public static void setDeleteCCVHealthyNutritionRetryItems(boolean deleteCCVHealthyNutritionRetryItems) {
        ScheduledRetryProcedure.deleteCCVHealthyNutritionRetryItems = deleteCCVHealthyNutritionRetryItems;
    }

    public static boolean isDeleteWOORetryItems() {
        return deleteWooRetryItems;
    }

    public static void setDeleteWooRetryItems(boolean deleteWooRetryItems) {
        ScheduledRetryProcedure.deleteWooRetryItems = deleteWooRetryItems;
    }

    private boolean itemExistsOnWebsite(Website website, String ean) {
        var item = itemService.findFirstByWebsiteAndEan(website.toString(), ean);
        if (item.isPresent()) {
            return true;
        }
        return false;
    }
}