package be.hi10.realnutrition.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import be.hi10.realnutrition.entities.ExactHashError;
import be.hi10.realnutrition.repositories.JpaExactHashErrorRepository;

@Service
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
public class DefaultExactHashErrorService implements ExactHashErrorService {

	private final JpaExactHashErrorRepository exactHashErrorRepository;

	public DefaultExactHashErrorService(JpaExactHashErrorRepository repository) {
		this.exactHashErrorRepository = repository;
	}

	@Override
	public List<ExactHashError> findAll() {
		return exactHashErrorRepository.findAll();
	}

	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	@Override
	public ExactHashError save(ExactHashError exactHashError) {
		return exactHashErrorRepository.save(exactHashError);
	}

	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	@Override
	public void delete(ExactHashError exactHashError) {
		exactHashErrorRepository.delete(exactHashError);
	}

	@Override
	public List<ExactHashError> findByKey(String key) {
		return exactHashErrorRepository.findByKey(key);
	}

	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	@Override
	public void deleteAll(List<ExactHashError> exactHashErrors) {
		exactHashErrorRepository.deleteAll(exactHashErrors);
	}

}
