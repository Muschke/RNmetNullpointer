package be.hi10.realnutrition.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import be.hi10.realnutrition.entities.ExactHashError;

public interface JpaExactHashErrorRepository extends JpaRepository<ExactHashError, Long> {
	List<ExactHashError> findByKey(String key);
}
