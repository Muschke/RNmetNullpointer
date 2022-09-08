package be.hi10.realnutrition.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import be.hi10.realnutrition.entities.RetryItem;

public interface JpaRetryItemRepository extends JpaRepository<RetryItem, Long> {
	Optional<RetryItem> findFirstByWebsiteAndItemId(String website, String itemId);
	Optional<RetryItem> findFirstByWebsiteAndEan(String website, String ean);
	List<RetryItem> findByWebsiteIsNotOrderByCreatedOnAsc(String website);
}
