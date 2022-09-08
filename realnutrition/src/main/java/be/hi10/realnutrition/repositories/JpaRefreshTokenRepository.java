package be.hi10.realnutrition.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import be.hi10.realnutrition.entities.RefreshToken;

public interface JpaRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

}
