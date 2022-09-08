package be.hi10.realnutrition.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import be.hi10.realnutrition.entities.Item;

public interface JpaItemRepository extends JpaRepository<Item, Long> {

	List<Item> findByWebsite(String website);

	List<Item> findByEan(String ean);

	Optional<Item> findFirstByWebsiteAndEan(String website, String ean);
}
