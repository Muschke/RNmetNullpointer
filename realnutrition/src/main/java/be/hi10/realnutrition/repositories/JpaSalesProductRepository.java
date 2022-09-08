package be.hi10.realnutrition.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import be.hi10.realnutrition.entities.SalesProduct;


public interface JpaSalesProductRepository extends JpaRepository<SalesProduct, Long> {
	Optional<SalesProduct> findByEan(String ean);
}
