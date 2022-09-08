package be.hi10.realnutrition.repositories;

import be.hi10.realnutrition.entities.AmazonEanAsin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaAmazonEanAsinRepository  extends JpaRepository<AmazonEanAsin, Long> {
    List<AmazonEanAsin> findAll();
    Optional<AmazonEanAsin> findByAsin(String asin);
}
