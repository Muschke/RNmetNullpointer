package be.hi10.realnutrition.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import be.hi10.realnutrition.entities.RetryItem;
import be.hi10.realnutrition.repositories.JpaRetryItemRepository;

@Service
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
public class DefaultRetryItemService implements RetryItemService {

	@Autowired
	JpaRetryItemRepository retryItemRepository;

	@Override
	public Optional<RetryItem> findFirstByWebsiteAndItemId(String website, String itemId) {
		return retryItemRepository.findFirstByWebsiteAndItemId(website, itemId);

	}

	@Override
	public Optional<RetryItem> findFirstByWebsiteAndEan(String website, String ean) {
		return retryItemRepository.findFirstByWebsiteAndEan(website, ean);
	}

	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	@Override
	public RetryItem save(RetryItem retryItem) {
		return retryItemRepository.save(retryItem);
	}

	@Override
	public List<RetryItem> findByWebsiteIsNotOrderByCreatedOnAsc(String website) {
		return retryItemRepository.findByWebsiteIsNotOrderByCreatedOnAsc(website);
	}

	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	@Override
	public void delete(RetryItem retryItem) {
		retryItemRepository.delete(retryItem);
	}

	@Override
	public List<RetryItem> findAll() {
		return retryItemRepository.findAll();
	}
}
