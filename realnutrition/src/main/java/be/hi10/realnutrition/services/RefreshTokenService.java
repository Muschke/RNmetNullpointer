package be.hi10.realnutrition.services;

import be.hi10.realnutrition.exceptions.RefreshTokenException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

public interface RefreshTokenService {

	String getAccessToken() throws RefreshTokenException, HttpClientErrorException, HttpServerErrorException;
	//String getNewAccessToken() throws RefreshTokenException, HttpClientErrorException, HttpServerErrorException;
	//String getAccessToken() throws RefreshTokenException;
}
