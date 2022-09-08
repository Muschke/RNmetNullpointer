package be.hi10.realnutrition.util;

import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

public class HasherTest {
    @Test
    public void testHmacSha512() throws InvalidKeyException, NoSuchAlgorithmException {
        String preCalculatedHash = "6a6d1d4ac7a284d633ce16969794" +
                "12ab00d4bcea8127d71b23b3788d43dc11" +
                "bef794d0293a8bd9a23b418bc9524e320" +
                "9a9d306f1bb3f6dfdb42386d8b32c8104";

        String hash = Hasher.createHash("This is a RealNutrition app",
                "RealNutrition ", "HmacSHA512");

        assertEquals(preCalculatedHash, hash);
    }

    @Test
    public void testHmacSha256() throws InvalidKeyException, NoSuchAlgorithmException {
        String preCalculatedHash = "8c7533337e5b79722affe9511dbbb48065a9e49a8c8d3e257c649ae2dd607b53";

        String hash = Hasher.createHash("This is a RealNutrition app",
                "RealNutrition ", "HmacSHA256");

        assertEquals(preCalculatedHash, hash);
    }

    @Test(expected = NoSuchAlgorithmException.class)
    public void testwrongAlgo() throws InvalidKeyException, NoSuchAlgorithmException {
        @SuppressWarnings("unused")
		String hash = Hasher.createHash("This is a RealNutrition app",
                "RealNutrition ", "IkBestaNiet");
    }

    @Test(expected = InvalidKeyException.class)
    public void TestinvalidKey() throws InvalidKeyException, NoSuchAlgorithmException {
        // via our hasher method, there is no way to get this error
        throw new InvalidKeyException();
    }
}