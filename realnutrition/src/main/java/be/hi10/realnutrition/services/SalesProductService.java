package be.hi10.realnutrition.services;

import java.util.Optional;

import be.hi10.realnutrition.entities.SalesProduct;

public interface SalesProductService {
	Optional<SalesProduct> findByEan(String ean);
	void save(SalesProduct product);
}
