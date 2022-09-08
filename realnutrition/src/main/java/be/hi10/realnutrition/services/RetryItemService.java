package be.hi10.realnutrition.services;

import java.util.List;
import java.util.Optional;

import be.hi10.realnutrition.entities.RetryItem;

public interface RetryItemService {
	Optional<RetryItem> findFirstByWebsiteAndItemId(String website, String itemId);

	Optional<RetryItem> findFirstByWebsiteAndEan(String website, String ean);

	RetryItem save(RetryItem retryItem);

	List<RetryItem> findByWebsiteIsNotOrderByCreatedOnAsc(String website);

	void delete(RetryItem retryItem);

	List<RetryItem> findAll();
}
