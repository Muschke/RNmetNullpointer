package be.hi10.realnutrition.apis;

import be.hi10.realnutrition.apis.amazon.CredentialsAndToken;
import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.pojos.amazon.accestoken.AccessToken;
import be.hi10.realnutrition.pojos.amazon.assumeRole.Credentials;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CredentialsAndTokenTest extends AbstractTransactionalJUnit4SpringContextTests{
    //private CredentialsAndToken credentialsAndToken;
    @BeforeEach
    void beforeEach() {

    }

    @Test
    public void getAccessToken() throws ApiException {
        CredentialsAndToken credentialsAndToken= new CredentialsAndToken();
        ResponseEntity<AccessToken> accessToken = credentialsAndToken.getAccessToken();
        assertThat(accessToken.getBody()).isNotNull();
        assertThat(accessToken.getBody()).isInstanceOf(AccessToken.class);
        int statusCode = accessToken.getStatusCodeValue();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void getAssumeRole() throws ApiException {
        CredentialsAndToken credentialsAndToken= new CredentialsAndToken();
        Credentials creds = credentialsAndToken.getAssumeRolCredentials();
        assertThat(creds).isNotNull();
        assertThat(creds).isInstanceOf(Credentials.class);
    }
}
