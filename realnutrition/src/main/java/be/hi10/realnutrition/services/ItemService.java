package be.hi10.realnutrition.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import be.hi10.realnutrition.entities.Item;

public interface ItemService {

	List<Item> findByWebsite(String website);

	List<Item> findByEan(String ean);

	Set<Item> saveAll(Set<Item> items);

	Optional<Item> findFirstByWebsiteAndEan(String website, String ean);

	void deleteAll(Set<Item> items);
	
	void deleteAll();
	
	int count();

}
