package be.hi10.realnutrition.beans;

import java.time.LocalDateTime;


import java.time.ZoneId;
import java.util.Optional;


import be.hi10.realnutrition.apis.*;
import be.hi10.realnutrition.apis.amazon.AmazonApiUpdateProduct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import be.hi10.realnutrition.entities.RetryItem;
import be.hi10.realnutrition.entities.Item;
import be.hi10.realnutrition.enums.Website;
import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.retryprocedure.ScheduledRetryProcedure;
import be.hi10.realnutrition.services.RetryItemService;
import be.hi10.realnutrition.services.ItemService;

@Component
public class ShopsUpdateBean {

    // Logger
    private final static Logger LOGGER = LoggerFactory.getLogger(ShopsUpdateBean.class);

    private final Bol1ComApi bol1ComApi;
    private final Bol2ComApi bol2ComApi;
    private final CCVApi ccvApi;
    private final CCVHealthyNutritionApi ccvHealthyNutritionApi;
    private final LightspeedApi lightspeedApi;
    private final WooApi wooApi;
    private final ItemService itemService;
    private final RetryItemService retryItemService;
    private final AmazonApiUpdateProduct amazonApiUpdateProduct;

    @Autowired
    public ShopsUpdateBean(AmazonApiUpdateProduct amazonApiUpdateProduct, Bol1ComApi bol1ComApi, Bol2ComApi bol2ComApi, CCVApi ccvApi, CCVHealthyNutritionApi ccvHealthyNutritionApi, LightspeedApi lightspeedApi,
                           WooApi wooApi, ItemService itemService, RetryItemService retryItemService) {
        this.bol1ComApi = bol1ComApi;
        this.bol2ComApi = bol2ComApi;
        this.ccvApi = ccvApi;
        this.ccvHealthyNutritionApi = ccvHealthyNutritionApi;
        this.lightspeedApi = lightspeedApi;
        this.wooApi = wooApi;
        this.itemService = itemService;
        this.retryItemService = retryItemService;
        this.amazonApiUpdateProduct = amazonApiUpdateProduct;
    }

    public void updateAllShops(int newStock, String ean, boolean retryProcedure) {
        LOGGER.info("ShopsUpdateBean --> Started updating all shops.");
        this.updateBolProductWithoutItemId(newStock, ean, retryProcedure);
        this.updateBol2ProductWithoutItemId(newStock, ean, retryProcedure);
        this.updateCCVProductWithoutItemId(newStock, ean, retryProcedure);
//		this.updateCCVHealthyNutritionProductWithoutItemId(newStock, ean, retryProcedure);
        this.updateLightspeedProductRetailNutrition(null, newStock, ean, retryProcedure);
        this.updateLightspeedProductOlifitshop(null, newStock, ean, retryProcedure);
        this.updateAmazonProductWithoutItemId(newStock, ean, retryProcedure);
        // this.updateWooProductWithoutItemId(newStock, ean, retryProcedure);
        LOGGER.info("ShopsUpdateBean --> Finished updating all shops.");
    }

    public void updateBolProductWithoutItemId(int newStock, String ean, boolean retryProcedure) {

        Optional<Item> item = itemService.findFirstByWebsiteAndEan(Website.BOL.toString(), ean);

        if (item.isPresent()) {
            LOGGER.info("ShopsUpdateBean --> BOL: item is present");
            this.updateBolProductWithItemId(item.get().getItemId(), newStock, ean, retryProcedure);

        } else {
            if (!retryProcedure) {
                saveProductAsRetryItem(Website.BOL, newStock, ean, null);

            } else {
                this.deleteRetryItemFromDB(Website.BOL, ean);
            }
        }
    }

    public void updateBol2ProductWithoutItemId(int newStock, String ean, boolean retryProcedure) {

        Optional<Item> item = itemService.findFirstByWebsiteAndEan(Website.BOL2.toString(), ean);

        if (item.isPresent()) {
            LOGGER.info("ShopsUpdateBean --> BOL2: item is present");
            this.updateBol2ProductWithItemId(item.get().getItemId(), newStock, ean, retryProcedure);

        } else {
            if (!retryProcedure) {
                saveProductAsRetryItem(Website.BOL2, newStock, ean, null);

            } else {
                this.deleteRetryItemFromDB(Website.BOL2, ean);
            }
        }
    }

    public void updateCCVProductWithoutItemId(int newStock, String ean, boolean retryProcedure) {

        Optional<Item> item = itemService.findFirstByWebsiteAndEan(Website.CCV.toString(), ean);

        if (item.isPresent()) {
            LOGGER.info("ShopsUpdateBean --> CCV: item is present");
            this.updateCCVProductWithItemId(item.get().getItemId(), newStock, ean, retryProcedure);

        } else {
            if (!retryProcedure) {
                saveProductAsRetryItem(Website.CCV, newStock, ean, null);

            } else {
                this.deleteRetryItemFromDB(Website.CCV, ean);
            }
        }
    }

    public void updateCCVHealthyNutritionProductWithoutItemId(int newStock, String ean, boolean retryProcedure) {

        Optional<Item> item = itemService.findFirstByWebsiteAndEan(Website.CCVHEALTHYNUTRITION.toString(), ean);

        if (item.isPresent()) {
            LOGGER.info("ShopsUpdateBean --> CCVHEALTHYNUTRITION: item is present");
            this.updateCCVHealthyNutritionProductWithItemId(item.get().getItemId(), newStock, ean, retryProcedure);

        } else {
            if (!retryProcedure) {
                saveProductAsRetryItem(Website.CCVHEALTHYNUTRITION, newStock, ean, null);

            } else {
                this.deleteRetryItemFromDB(Website.CCVHEALTHYNUTRITION, ean);
            }
        }
    }

    public void updateWooProductWithoutItemId(int newStock, String ean, boolean retryProcedure) {

        Optional<Item> item = itemService.findFirstByWebsiteAndEan(Website.WOO.toString(), ean);

        if (item.isPresent()) {
            LOGGER.info("ShopsUpdateBean --> WOO: item is present");
            this.updateWooProductWithItemId(item.get().getItemId(), newStock, ean, retryProcedure);

        } else {
            if (!retryProcedure) {
                saveProductAsRetryItem(Website.WOO, newStock, ean, null);

            } else {
                this.deleteRetryItemFromDB(Website.WOO, ean);
            }
        }
    }
    
    public void updateAmazonProductWithoutItemId(int newStock, String ean, boolean retryProcedure) {
    	 Optional<Item> item = itemService.findFirstByWebsiteAndEan(Website.AMAZON.toString(), ean);
    	  
    	 if (item.isPresent()) {
             LOGGER.info("ShopsUpdateBean --> AMAZON: item is present");
             this.updateAmazonProductWithItemId(item.get().getItemId(), newStock, ean, retryProcedure);

         } else {
             if (!retryProcedure) {
                 saveProductAsRetryItem(Website.AMAZON, newStock, ean, null);

             } else {
                 this.deleteRetryItemFromDB(Website.AMAZON, ean);
             }
         }	 
    }
    
    public void updateBolProductWithItemId(String itemId, int newStock, String ean, boolean retryProcedure) {
        try {
            bol1ComApi.updateStock(newStock, itemId, null);
            this.deleteRetryItem(Website.BOL, ean);
            LOGGER.info("ShopsUpdateBean --> BOL: Stock updated");

        } catch (ApiException e) {
            LOGGER.info("ShopsUpdateBean --> BOL: ApiException");
            saveProductAsRetryItem(Website.BOL, newStock, ean, itemId);
            if (retryProcedure) {
                SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
            }
        }
    }

    public void updateBol2ProductWithItemId(String itemId, int newStock, String ean, boolean retryProcedure) {
        try {
            bol2ComApi.updateStock(newStock, itemId, null);
            this.deleteRetryItem(Website.BOL2, ean);
            LOGGER.info("ShopsUpdateBean --> BOL2: Stock updated");

        } catch (ApiException e) {
            LOGGER.info("ShopsUpdateBean --> BOL2: ApiException");
            saveProductAsRetryItem(Website.BOL2, newStock, ean, itemId);
            if (retryProcedure) {
                SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
            }
        }
    }

    public void updateCCVProductWithItemId(String itemId, int newStock, String ean, boolean retryProcedure) {
        try {
            ccvApi.updateStock(newStock, itemId);
            this.deleteRetryItem(Website.CCV, ean);
            LOGGER.info("ShopsUpdateBean --> CCV: Stock updated");

        } catch (ApiException e) {
            LOGGER.info("ShopsUpdateBean --> CCV: ApiException");

            saveProductAsRetryItem(Website.CCV, newStock, ean, itemId);
            if (retryProcedure) {
                SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
            }
        }
    }

    public void updateCCVHealthyNutritionProductWithItemId(String itemId, int newStock, String ean, boolean retryProcedure) {
        try {
            ccvHealthyNutritionApi.updateStock(newStock, itemId);
            this.deleteRetryItem(Website.CCVHEALTHYNUTRITION, ean);
            LOGGER.info("ShopsUpdateBean --> CCVHEALTHYNUTRITION: Stock updated");

        } catch (ApiException e) {
            LOGGER.info("ShopsUpdateBean --> CCVHEALTHYNUTRITION: ApiException");
            saveProductAsRetryItem(Website.CCVHEALTHYNUTRITION, newStock, ean, itemId);
            if (retryProcedure) {
                SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
            }
        }
    }

    public void updateWooProductWithItemId(String itemId, int newStock, String ean, boolean retryProcedure) {
        try {
            wooApi.updateStock(newStock, itemId);
            this.deleteRetryItem(Website.WOO, ean);
            LOGGER.info("ShopsUpdateBean --> WOO: Stock updated");

        } catch (ApiException e) {
            LOGGER.info("ShopsUpdateBean --> WOO: ApiException");
            saveProductAsRetryItem(Website.WOO, newStock, ean, itemId);
            if (retryProcedure) {
                SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
            }
        }
    }

    public void updateLightspeedProductRetailNutrition(String itemId, int newStock, String ean,
                                                       boolean retryProcedure) {

        final String ACCESS_TOKEN_KEY = "Basic MWZjZWM0MmM2ODliMmQ3ZGE5Mjk2MzViMDc3NGNjNWU6NjMyYzUyY2E5Yzc2YmVlZjIyYmE2MDdhYjE5ZDIwNjQ=";

        try {
            lightspeedApi.updateStock(newStock, ean, itemId, ACCESS_TOKEN_KEY);
            this.deleteRetryItem(Website.LIGHTSPEEDRETAILNUTRITION, ean);
            LOGGER.info("ShopsUpdateBean --> LIGHTSPEEDRETAILNUTRITION: Stock updated");

        } catch (ApiException e) {
            LOGGER.info("ShopsUpdateBean --> LIGHTSPEEDRETAILNUTRITION: ApiException");
            saveProductAsRetryItem(Website.LIGHTSPEEDRETAILNUTRITION, newStock, ean, e.getItemId());
            if (retryProcedure) {
                SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
            }
        }
    }

    public void updateLightspeedProductOlifitshop(String itemId, int newStock, String ean, boolean retryProcedure) {

        final String ACCESS_TOKEN_KEY = "Basic NWUzOGE3NGVlYWNjMDAwMGJmYjI0MzllMWEwYzNiZGY6MGUzOGQ0NWU4MTg2NTU2NmQ0ZWY4YzBjNDUxZTA3NzM=";

        try {
            lightspeedApi.updateStock(newStock, ean, itemId, ACCESS_TOKEN_KEY);
            this.deleteRetryItem(Website.LIGHTSPEEDOLIFITSHOP, ean);
            LOGGER.info("ShopsUpdateBean --> LIGHTSPEEDOLIFITSHOP: Stock updated");

        } catch (ApiException e) {
            LOGGER.info("ShopsUpdateBean --> LIGHTSPEEDOLIFITSHOP: ApiException");
            saveProductAsRetryItem(Website.LIGHTSPEEDOLIFITSHOP, newStock, ean, e.getItemId());
            if (retryProcedure) {
                SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
            }
        }
    }
    
    public void updateAmazonProductWithItemId(String itemId, int newStock, String ean, boolean retryProcedure) {
    	try {
    		amazonApiUpdateProduct.updateStock(itemId, newStock);  
    		this.deleteRetryItem(Website.AMAZON, ean);
    		   LOGGER.info("ShopsUpdateBean --> AMAZON: Stock updated");
    	}catch(ApiException e) {
    		 LOGGER.info("ShopsUpdateBean --> AMAZON: ApiException");
             saveProductAsRetryItem(Website.AMAZON, newStock, ean, e.getItemId());  //e.getItemId???
             if(retryProcedure) {
            	 SynchronizedMethodsBean.setRetryProcedureRanWithoutErrors(false);
             }
    	}
    }
    

    private void deleteRetryItem(Website website, String ean) {
        Optional<RetryItem> retryItem = retryItemService.findFirstByWebsiteAndEan(website.toString(), ean);
        if (retryItem.isPresent()) {
            retryItemService.delete(retryItem.get());
            LOGGER.info("ShopsUpdateBean --> " + website.toString() + ": retryItem deleted, ean = " + ean);
        }
    }

    public void deleteRetryItemFromDB(Website website, String ean) {

        switch (website) {
            case BOL:
                if (!ScheduledRetryProcedure.isDeleteBolRetryItems()) {
                    return;
                }
                break;
            case BOL2:
                if (!ScheduledRetryProcedure.isDeleteBol2RetryItems()) {
                    return;
                }
                break;
            case CCV:
                if (!ScheduledRetryProcedure.isDeleteCCVRetryItems()) {
                    return;
                }
                break;
            case CCVHEALTHYNUTRITION:
                if (!ScheduledRetryProcedure.isDeleteCCVHealthyNutritionRetryItems()) {
                	LOGGER.info("RetryProcedure --> Is delete CCV healthynutritionRetryitems is false.");
                    return;
                }
                break;
            case WOO:
                if (!ScheduledRetryProcedure.isDeleteWOORetryItems()) {
                    return;
                }
                break;
            case AMAZON:
                if (!ScheduledRetryProcedure.isDeleteAmazonRetryItems()) {
                    return;
                }
                break;
            default:
                break;
        }
        this.deleteRetryItem(website, ean);
        LOGGER.info("ShopsUpdateBean --> RetryItem deleted.");
    }

    private void saveProductAsRetryItem(Website website, int newStock, String ean, String id) {
        Optional<RetryItem> retryItem = null;

        retryItem = retryItemService.findFirstByWebsiteAndEan(website.toString(), ean);
        if (retryItem.isPresent()) {
            retryItem.get().setProjectedStock(newStock);
            retryItem.get().setLastChanged(LocalDateTime.now(ZoneId.of("UTC")));
            retryItem.get().setItemId(id);
            retryItemService.save(retryItem.get());
        } else {
            RetryItem r = new RetryItem();
            r.setWebsite(website.toString());
            r.setProjectedStock(newStock);
            r.setItemId(id);
            r.setEan(ean);
            r.setCreatedOn(LocalDateTime.now(ZoneId.of("UTC")));
            retryItemService.save(r);
        }
        LOGGER.info("ShopsUpdateBean --> " + website.toString() + ": product saved as retryItem, newStock = " + newStock
                + ", ean = " + ean + ", id = " + id);
    }
}