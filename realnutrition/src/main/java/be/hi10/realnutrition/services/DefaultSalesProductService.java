package be.hi10.realnutrition.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import be.hi10.realnutrition.entities.SalesProduct;
import be.hi10.realnutrition.repositories.JpaSalesProductRepository;

@Service
public class DefaultSalesProductService implements SalesProductService {
	private JpaSalesProductRepository repository;

	@Override
	public Optional<SalesProduct> findByEan(String ean) {
		return repository.findByEan(ean);
	}

	@Override
	public void save(SalesProduct product) {
		repository.save(product);
		
	}


}
