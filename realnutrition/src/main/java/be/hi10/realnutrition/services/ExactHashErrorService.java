package be.hi10.realnutrition.services;

import java.util.List;

import be.hi10.realnutrition.entities.ExactHashError;

public interface ExactHashErrorService {

	List<ExactHashError> findAll();

	ExactHashError save(ExactHashError exactHashError);

	void delete(ExactHashError exactHashError);

	void deleteAll(List<ExactHashError> exactHashErrors);

	List<ExactHashError> findByKey(String key);

}
