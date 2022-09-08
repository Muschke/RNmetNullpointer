package be.hi10.realnutrition.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class Hasher {
	/**
	 * This method to create a hash digest can use different algorithms, the ones we
	 * need are "HmacSHA256" and "HmacSHA512".
	 *
	 * @param msg       the message to hash
	 * @param keyString the key used fo calculate the hash
	 * @param algo      the specific algorithm used to calculate the hash
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 */
	public static String createHash(String msg, String keyString, String algo)
			throws NoSuchAlgorithmException, InvalidKeyException {

		SecretKeySpec key = new SecretKeySpec((keyString).getBytes(StandardCharsets.UTF_8), algo);
		Mac mac = Mac.getInstance(algo);
		mac.init(key);

		byte[] bytes = mac.doFinal(msg.getBytes(StandardCharsets.US_ASCII));

		StringBuffer hash = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				hash.append('0');
			}
			hash.append(hex);
		}
		return hash.toString();
	}
}
