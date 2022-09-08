package be.hi10.realnutrition.pojos.amazon.accestoken;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessToken {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private Integer expiresIn;

    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }
    @JsonProperty("refresh_token")
    public String getRefreshToken() {
        return refreshToken;
    }
    @JsonProperty("token_type")
    public String getTokenType() {
        return tokenType;
    }
    @JsonProperty("expires_in")
    public Integer getExpiresIn() {
        return expiresIn;
    }



}