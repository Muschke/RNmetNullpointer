package be.hi10.realnutrition.webhooks;

import java.util.LinkedHashSet;
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
import be.hi10.realnutrition.entities.RetryItem;
import be.hi10.realnutrition.entities.Item;
import be.hi10.realnutrition.enums.Website;
import be.hi10.realnutrition.exceptions.RefreshTokenException;
import be.hi10.realnutrition.services.ItemService;

@Component
public class ScheduledWebHookModule {

	@Autowired
	ExactOnlineApi exactOnlineApi;

	@Autowired
	ItemService itemService;

	@Autowired
	ShopsUpdateBean shopsUpdateBean;

	// Logger
	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledWebHookModule.class);

	public void checkForExactProjectedstockUpdatesScheduled() {
		Set<RetryItem> exactRetryItemsFromApi = new LinkedHashSet<>();
		Set<Item> exactItemsToBeSaved = new LinkedHashSet<>();

		try {
			exactRetryItemsFromApi = exactOnlineApi.getItems();
		} catch (HttpClientErrorException | HttpServerErrorException | RefreshTokenException e) {

			LOGGER.error("Exact-Api --> Could not get accessToken for ScheduledWebHookModule: " + e.getStackTrace());
			return;
		}

		if (exactRetryItemsFromApi.size() > 0) {
			List<Item> exactItemsFromDatabase = itemService.findByWebsite(Website.EXACT.toString());
			for (RetryItem exactRetryItemFromApi : exactRetryItemsFromApi) {
				Item exactItemFromApi = new Item(exactRetryItemFromApi.getEan(), exactRetryItemFromApi.getItemId(),
						exactRetryItemFromApi.getProjectedStock(), exactRetryItemFromApi.getWebsite());
				Boolean existsInDatabaseAndStockIsSame = false;
				Item exactItemApiExistsInDatabase = null;
				for (Item exactItemFromDatabase : exactItemsFromDatabase) {
					if (exactItemFromApi.equals(exactItemFromDatabase)
							&& exactItemFromApi.getProjectedStock() != exactItemFromDatabase.getProjectedStock()) {

						exactItemApiExistsInDatabase = exactItemFromDatabase;
						break;
					} else if (exactItemFromApi.equals(exactItemFromDatabase)
							&& exactItemFromApi.getProjectedStock() == exactItemFromDatabase.getProjectedStock()) {

						existsInDatabaseAndStockIsSame = true;
						exactItemApiExistsInDatabase = exactItemFromDatabase;
						break;
					}
				}
				if (exactItemApiExistsInDatabase != null) {
					exactItemsFromDatabase.remove(exactItemApiExistsInDatabase);
				}
				if (!existsInDatabaseAndStockIsSame) {
					shopsUpdateBean.updateAllShops(exactItemFromApi.getProjectedStock(), exactItemFromApi.getEan(),
							false);
					Optional<Item> optionalItem = itemService.findFirstByWebsiteAndEan(Website.EXACT.toString(),
							exactItemFromApi.getEan());
					if (optionalItem.isPresent()) {
						Item item = optionalItem.get();
						item.setProjectedStock(exactItemFromApi.getProjectedStock());
						exactItemsToBeSaved.add(item);
					} else {
						exactItemsToBeSaved.add(exactItemFromApi);
					}
				}
			}
			itemService.saveAll(exactItemsToBeSaved);
		}
	}
}

