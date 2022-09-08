package be.hi10.realnutrition.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import be.hi10.realnutrition.entities.Item;
import be.hi10.realnutrition.repositories.JpaItemRepository;

@Service
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
public class DefaultItemService implements ItemService {

	@Autowired
	JpaItemRepository itemRepository;

	// Yes
	@Override
	public List<Item> findByWebsite(String website) {
		return itemRepository.findByWebsite(website);
	}

	@Override
	public List<Item> findByEan(String ean) {
		return itemRepository.findByEan(ean);
	}

	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	@Override
	public Set<Item> saveAll(Set<Item> items) {
		return Set.copyOf(itemRepository.saveAll(items));
	}

	@Override
	public Optional<Item> findFirstByWebsiteAndEan(String website, String ean) {
		return itemRepository.findFirstByWebsiteAndEan(website, ean);
	}

	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	@Override
	public void deleteAll(Set<Item> items) {
		itemRepository.deleteAll(items);
	}

	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	@Override
	public void deleteAll() {
		itemRepository.deleteAllInBatch();
	}

	@Override
	public int count() {
		return (int) itemRepository.count();
	}
}
