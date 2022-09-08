package be.hi10.realnutrition.apis.amazon;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.lang3.StringUtils;
import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.pojos.amazon.accestoken.AccessToken;
import be.hi10.realnutrition.pojos.amazon.assumeRole.Credentials;

public class CredentialsAndToken {
	private static final String DOMAIN = "https://api.amazon.com";
	private static final String CLIENT_ID = "amzn1.application-oa2-client.64adf05a66e744ebac8f6342e92032fc";
	private static final String CLIENT_SECRET = "befb4d5c52f6596d2e18237218f1b6e2a6f8b796cf175312659623903c91a5e3";
	private static final String REFRESH_TOKEN = "Atzr|IwEBIJHSJZDrDB7K4SWvc36EUh18lojdz73HNHjTq74ILFGY1nP3uM64AVbB2WHj"
			+ "zDjrtYcfXbqHO9fy7RB9afK4-VOuT3JGpcTurlumaDI4oA3jjoDkIokKWVUGJ8mq5N8AfohSTqutMfx55TX5TKmO7tpdxBzNdf1v1uZmT"
			+ "b9gACtDo-60Umqdn8wwMEyHQUe-GknMYQEwQndd0JCOeukivZlwdt2xUG-7ZFq0mIQYfTCmV6LvWeX7E7lQJJa6WPj922Q-pDrlN4MlfQp"
			+ "X1ZVyWG8FpY4Z1G0HCuw5alaaTQWG9BZ_w8kvhHpIF050uU6sNnLJ3l4U7VVoEmZ5_XPerRLq";

	private final static Logger LOGGER = LoggerFactory.getLogger(CredentialsAndToken.class);

	public ResponseEntity<AccessToken> getAccessToken() throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		final String URI = "/auth/o2/token";
		final String DATA = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET
				+ "&grant_type=refresh_token&refresh_token=" + REFRESH_TOKEN;

		headers.add("Content-Type", "application/x-www-form-urlencoded");
		headers.add("Accept", "application/json");

		HttpEntity<String> entity = new HttpEntity<>(DATA, headers);

		try {
			return rest.exchange(DOMAIN + URI, HttpMethod.POST, entity, AccessToken.class);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			LOGGER.error("CredentialsAndToken --> Something went wrong while getting a bearer token from Amazon.com: " + e.getResponseBodyAsString());
			throw new ApiException("Something went wrong while getting a bearer token from Amazon.com: " + e.getResponseBodyAsString(), e);
		}
	}

	public Credentials getAssumeRolCredentials() throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		/* params - variables */
		final String VERSION = "2011-06-15";
		final String ACTION = "AssumeRole";
		final String ROLESESSIONNAME = "Test1500";
		final String ROLEARN = "arn:aws:iam::485075469560:role/MYRN";
		final String DURATIONSECONDS = "3600";
		final String URI = "https://sts.amazonaws.com/?Version=" + VERSION + "&Action=" + ACTION + "&RoleSessionName="
				+ ROLESESSIONNAME + "&RoleArn=" + ROLEARN + "&DurationSeconds=" + DURATIONSECONDS;

		/* generate signature key - variables */
		String accesKey = "AKIAXB4GAIT4D5QJXILN";
		String secretKey = "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";
		LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
		String dateStamp = NecessaryFunctions.getDate(now);
		String dateTimeStamp = NecessaryFunctions.getTimeStamp(now);
		String regionName = "us-east-1";
		String serviceName = "sts";

		// 1. create canonical header
		StringBuilder canonicalURL = new StringBuilder("");
		// 1.1 set httpmethod
		canonicalURL.append("GET").append("\n");
		// 1.2 set canonicalURL --if empty, use forward slash
		canonicalURL.append("/").append("\n");
		// 1.3 canonical querystring -- alphabetic on key name ascending + URL encode
		// name & values
		try {
			canonicalURL
					.append(URLEncoder.encode("Action", "UTF-8").replaceAll("\\+", "%20") + "="
							+ URLEncoder.encode(ACTION, "UTF-8").replaceAll("\\+", "%20") + "&")
					.append(URLEncoder.encode("DurationSeconds", "UTF-8").replaceAll("\\+", "%20") + "="
							+ URLEncoder.encode(DURATIONSECONDS, "UTF-8").replaceAll("\\+", "%20") + "&")
					.append(URLEncoder.encode("RoleArn", "UTF-8").replaceAll("\\+", "%20") + "="
							+ URLEncoder.encode(ROLEARN, "UTF-8").replaceAll("\\+", "%20") + "&")
					.append(URLEncoder.encode("RoleSessionName", "UTF-8").replaceAll("\\+", "%20") + "="
							+ URLEncoder.encode(ROLESESSIONNAME, "UTF-8").replaceAll("\\+", "%20") + "&")
					.append(URLEncoder.encode("Version", "UTF-8").replaceAll("\\+", "%20") + "="
							+ URLEncoder.encode(VERSION, "UTF-8").replaceAll("\\+", "%20"))
					.append("\n");
		} catch (UnsupportedEncodingException e2) {
			LOGGER.error("CredentialsAndToken --> Failed to append canonical URL: " + e2.getMessage());
		}

		// 1.4 set canonical headers
		canonicalURL.append(("host").toLowerCase() + ":" + ("sts.amazonaws.com").trim() + "\n");
		canonicalURL.append(("x-amz-Date").toLowerCase() + ":" + (dateTimeStamp).trim() + "\n");
		canonicalURL.append("\n");

		// 1.5 set signed headers !signedHeaders must correspond to the list of headers
		// in the canonical headers -- ex. SignedHeaders=host;x-amz-date; ! the
		// headersname must be in lowercase "
		canonicalURL.append("host;x-amz-date").append("\n");
		// 1.6 append a hashed payload, if u use getrequest, use hashed empty string
		String hashedPayload = DigestUtils.sha256Hex("");
		canonicalURL.append(hashedPayload);
		// print to check result:
		// System.out.println("canonicalUrl: \n"+ canonicalURL.toString());

		// 2. create stringToSign
		StringBuilder stringToSign = new StringBuilder("");
		// 2..1 contacenate algoritme
		stringToSign.append("AWS4-HMAC-SHA256").append("\n");
		// 2.2 contacenate timestamp
		stringToSign.append(dateTimeStamp).append("\n");
		// 2.3 add credential information
		stringToSign.append(dateStamp + "/" + regionName + "/" + serviceName /* or s3 */ + "/aws4_request")
				.append("\n");
		// 2.4 add the hash of canonical request
		stringToSign.append(DigestUtils.sha256Hex(canonicalURL.toString()));
		// System.out.println("stringTosign: "+"\n" + stringToSign);

		/* 3.1 generate signing key */
		byte[] signature = null;
		try {
			signature = NecessaryFunctions.getSignatureKey(secretKey, dateStamp, regionName, serviceName);
		} catch (Exception e) {
			LOGGER.error("CredentialsAndToken --> Signature error the first at Credentials: " + e.getMessage());
		}

		// 3.2 calculate signature: byte[] signature = HmacSHA256(signature,
		// stringToSign);
		try {
			signature = NecessaryFunctions.HmacSHA256(stringToSign.toString(), signature);
		} catch (Exception e1) {
			LOGGER.error("CredentialsAndToken --> Signature error the second at Credentials: " + e1.getMessage());
		}

		// 3.3 encode signature
		String signatureString = NecessaryFunctions.bytesToHex(signature);

		/* 4. log in headers */
		String AuthorizationHeader = "AWS4-HMAC-SHA256 Credential=" + accesKey + "/" + dateStamp + "/" + regionName
				+ "/" + serviceName + "/aws4_request, SignedHeaders=host;x-amz-date, Signature=" + signatureString;

		headers.add("authorization", AuthorizationHeader);
		headers.add("X-Amz-Date", dateTimeStamp);
		headers.add("Accept", "application/xml");
		HttpEntity<String> entity = new HttpEntity<>(headers);
		try {
			String xmlToConvert = rest.exchange(URI, HttpMethod.GET, entity, String.class).getBody();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            
            String accessKeyId = StringUtils.substringBetween(xmlToConvert, "<AccessKeyId>", "</AccessKeyId>");
            String secretAccessKey = StringUtils.substringBetween(xmlToConvert, "<SecretAccessKey>", "</SecretAccessKey>");
            String sessionToken = StringUtils.substringBetween(xmlToConvert, "<SessionToken>", "</SessionToken>");
            Date expiration = sdf.parse(StringUtils.substringBetween(xmlToConvert, "<Expiration>", "</Expiration>")) ;
			
            return new Credentials(accessKeyId, secretAccessKey, sessionToken, expiration);
            
		} catch (HttpClientErrorException | HttpServerErrorException | ParseException e) {
			LOGGER.error("CredentialsAndToken --> Something went wrong with the xmlToConvert");
			throw new ApiException("Something went wrong while receiving assumeRoleCredentials from Amazon.com: "
					+ ((RestClientResponseException) e).getResponseBodyAsString(), e);
		}
	}
}

