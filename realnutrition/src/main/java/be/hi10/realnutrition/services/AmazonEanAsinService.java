package be.hi10.realnutrition.services;

import java.util.List;
import java.util.Optional;

import be.hi10.realnutrition.entities.AmazonEanAsin;

public interface AmazonEanAsinService {
    Optional<AmazonEanAsin> findByAsin(String asin);
    void save(AmazonEanAsin a);
    void deleteAll();
    List<AmazonEanAsin> findAll();
}
