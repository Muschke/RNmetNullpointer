package be.hi10.realnutrition.pojos.amazon.assumeRole;

import java.util.Date;
//the pojos in the same package as Credentials (assumeroleuser,...) can be deleted if no problems arised later, 02.09.2022
public class Credentials{
    public String accessKeyId;
    public String secretAccessKey;
    public String sessionToken;
    public Date expiration;

    public Credentials(String accessKeyId, String secretAccessKey, String sessionToken, Date expiration) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.sessionToken = sessionToken;
        this.expiration = expiration;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public Date getExpiration() {
        return expiration;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "accessKeyId='" + accessKeyId + '\'' +
                ", secretAccessKey='" + secretAccessKey + '\'' +
                ", sessionToken='" + sessionToken + '\'' +
                ", expiration=" + expiration +
                '}';
    }
}