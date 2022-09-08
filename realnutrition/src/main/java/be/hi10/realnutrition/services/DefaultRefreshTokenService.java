package be.hi10.realnutrition.services;

import be.hi10.realnutrition.beans.RateLimitBean;
import be.hi10.realnutrition.entities.RefreshToken;
import be.hi10.realnutrition.exceptions.RefreshTokenException;
import be.hi10.realnutrition.pojos.exactonline.refreshtoken.TokenResponse;
import be.hi10.realnutrition.repositories.JpaRefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;

@Service
public class DefaultRefreshTokenService implements RefreshTokenService {
	
	@Autowired
	private RateLimitBean rateLimitBean;
	
//	private BlockingBucket bucket = rateLimitBean.getBucket();
	
	// Logger
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRefreshTokenService.class);

	@Autowired
	private JpaRefreshTokenRepository refreshTokenRepository;

	// GENERAL
	private static final String CLIENT_ID = "fae55e70-2048-4a45-93fe-3abec4d7a885";
	private static final String CLIENT_SECRET = "guXWZkxU4wcz";

	// URL
	private static final String REFRESH_TOKEN_URL = "https://start.exactonline.be/api/oauth2/token";

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	public String getAccessToken() throws RefreshTokenException, HttpClientErrorException, HttpServerErrorException {
		Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findById(1L);
		RefreshToken refreshToken = getRefreshToken(refreshTokenOptional);

		Long timestamp = refreshToken.getTimestamp();
		Long expiresIn = refreshToken.getExpiresIn();
		long currentTime = System.currentTimeMillis() / 1000;
		
		String accessToken = "";
		boolean newRequestRequired = false;
		if (timestamp != null) {
			if (timestamp + expiresIn - 30 > currentTime) {
				accessToken = refreshToken.getAccessToken();
			} else {
				LOGGER.info("Stored Access Token is expired, requesting new one");
				newRequestRequired = true;
			}
		} else {
			LOGGER.info("Stored Access Token has no timestamp, requesting new one");
			newRequestRequired = true;
		}
		
		if (newRequestRequired) {
			RestTemplate rest = new RestTemplate();
			HttpEntity<MultiValueMap<String, String>> request = createRequest(refreshToken);

			try {
				LOGGER.info("Requesting permit from RateLimiter");
				rateLimitBean.getBucket().consume(1);
				LOGGER.info("Permit granted");
				ResponseEntity<TokenResponse> response = rest.postForEntity(REFRESH_TOKEN_URL, request, TokenResponse.class);
				rateLimitBean.updateBucketLimits(response.getHeaders());

				accessToken = response.getBody().getAccessToken();
				refreshToken.setRefreshToken(response.getBody().getRefreshToken());
				refreshToken.setAccessToken(accessToken);
				refreshToken.setExpiresIn(response.getBody().getExpiresIn());
				refreshToken.setTimestamp(currentTime);
				refreshTokenRepository.save(refreshToken);
			} catch (InterruptedException e) {
				LOGGER.error("DefaultRefreshTokenService --> failed to refresh token: ", e);
			}			
		}		
		return accessToken;
	}

	/*@Override
	public String getAccessToken() throws RefreshTokenException {

		Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findById(1L);
		RefreshToken refreshToken = getRefreshToken(refreshTokenOptional);
		return refreshToken.getAccessToken();
	}*/

	private HttpEntity<MultiValueMap<String, String>> createRequest(RefreshToken refreshToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Accept", "application/json");


		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("refresh_token", refreshToken.getRefreshToken());
		body.add("grant_type", "refresh_token");
		body.add("client_id", CLIENT_ID);
		body.add("client_secret", CLIENT_SECRET);

		return new HttpEntity<MultiValueMap<String, String>>(body, headers);
	}

	private RefreshToken getRefreshToken(Optional<RefreshToken> refreshTokenOptional) throws RefreshTokenException {
		RefreshToken refreshToken;
		if (refreshTokenOptional.isPresent()) {
			refreshToken = refreshTokenOptional.get();
		} else {
			throw new RefreshTokenException(
					"Exact-Api --> No refreshToken found in database on id == 1, website == Exact Online");
		}
		return refreshToken;
	}
}
