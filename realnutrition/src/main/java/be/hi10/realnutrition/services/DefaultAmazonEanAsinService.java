package be.hi10.realnutrition.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.hi10.realnutrition.entities.AmazonEanAsin;
import be.hi10.realnutrition.repositories.JpaAmazonEanAsinRepository;

@Service
@Transactional
public class DefaultAmazonEanAsinService implements AmazonEanAsinService{
    private final JpaAmazonEanAsinRepository repository;

    public DefaultAmazonEanAsinService(JpaAmazonEanAsinRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<AmazonEanAsin> findByAsin(String asin) {
        return repository.findByAsin(asin);
    }
    @Override
    public void save(AmazonEanAsin amazonEanAsin){
        repository.save(amazonEanAsin);
    }
    @Override
    public void deleteAll(){
        repository.deleteAll();
    }
    @Override
    public List<AmazonEanAsin> findAll() {
        return repository.findAll();
    }
}
