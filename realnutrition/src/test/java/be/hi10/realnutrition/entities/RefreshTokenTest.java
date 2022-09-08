package be.hi10.realnutrition.entities;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenTest {
    private RefreshToken refreshToken, refreshToken2, refreshToken3;

    @BeforeEach
    void init() throws NumberFormatException {
        refreshToken = new RefreshToken();
        refreshToken.setRefreshToken("refreshToken");
        refreshToken.setAccessToken("accessToken");
        refreshToken.setTimestamp(1L);
        refreshToken.setExpiresIn(1L);
        refreshToken.setWebsite("website");
        refreshToken2 = new RefreshToken();
        refreshToken2.setRefreshToken("refreshToken2");
        refreshToken2.setAccessToken("accessToken2");
        refreshToken2.setTimestamp(1L);
        refreshToken2.setExpiresIn(1L);
        refreshToken2.setWebsite("website2");
        refreshToken3 = new RefreshToken();
        refreshToken3.setRefreshToken("refreshToken");
        refreshToken3.setAccessToken("accessToken");
        refreshToken3.setTimestamp(1L);
        refreshToken3.setExpiresIn(1L);
        refreshToken3.setWebsite("website");

    }

    @Test
    void refreshTokenDoesNotEqualRefreshToken2() {
        assertThat(refreshToken.equals(refreshToken2)).isFalse();
    }

    @Test
    void refreshTokenEqualsRefreshToken3() {
        assertThat(refreshToken.equals(refreshToken3)).isTrue();
    }

    @Test
    void refreshTokenAndRefreshToken2HaveDifferentHashCodes() {
        assertThat(refreshToken.hashCode()).isNotEqualTo(refreshToken2.hashCode());
    }

    @Test
    void refreshTokenAndRefreshToken3HaveTheSameHashCode() {
        assertThat(refreshToken.hashCode()).isEqualTo(refreshToken3.hashCode());
    }
}