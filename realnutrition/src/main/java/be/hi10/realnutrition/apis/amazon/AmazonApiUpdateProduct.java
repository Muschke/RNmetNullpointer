package be.hi10.realnutrition.apis.amazon;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.pojos.amazon.accestoken.AccessToken;
import be.hi10.realnutrition.pojos.amazon.assumeRole.Credentials;
import be.hi10.realnutrition.pojos.amazon.reports.DocumentFeedResponse;
import be.hi10.realnutrition.pojos.amazon.updateStock.UpdateStock;
@Component
public class AmazonApiUpdateProduct {
	CredentialsAndToken credentialsAndToken = new CredentialsAndToken();
	NecessaryFunctions necessaryFunctions = new NecessaryFunctions();
	private final static Logger LOGGER = LoggerFactory.getLogger(AmazonApiUpdateProduct.class);

	// post request - getfeeddocument
	public DocumentFeedResponse getFeedDocument() throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		ResponseEntity<AccessToken> accesToken = credentialsAndToken.getAccessToken();
		Credentials credentials = credentialsAndToken.getAssumeRolCredentials();

		String accesKey = credentials.accessKeyId;
		String secretKey = credentials.secretAccessKey;
		String sessionToken = credentials.sessionToken;
		String accesTokenToInsert = accesToken.getBody().getAccessToken();

		LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
		String dateStamp = NecessaryFunctions.getDate(now);
		String dateTimeStamp = NecessaryFunctions.getTimeStamp(now);
		String regionName = "eu-west-1";
		String serviceName = "execute-api";
		String URI = "https://sellingpartnerapi-eu.amazon.com/feeds/2021-06-30/documents";

		StringBuilder canonicalURL = new StringBuilder("");
		canonicalURL.append("POST").append("\n");
		canonicalURL.append("/feeds/2021-06-30/documents").append("\n");
		canonicalURL.append("").append("\n");
		canonicalURL.append(("host").toLowerCase() + ":" + ("sellingpartnerapi-eu.amazon.com").trim() + "\n");
		canonicalURL.append(("x-amz-access-Token").toLowerCase() + ":" + (accesTokenToInsert).trim() + "\n");
		canonicalURL.append(("x-amz-Date").toLowerCase() + ":" + (dateTimeStamp).trim() + "\n");
		canonicalURL.append(("x-amz-Security-Token").toLowerCase() + ":" + (sessionToken).trim() + "\n");
		canonicalURL.append("\n");
		canonicalURL.append("host;x-amz-access-token;x-amz-date;x-amz-security-token").append("\n");

		StringBuilder builder1 = new StringBuilder();
		builder1.append("{").append("\n");
		builder1.append("    \"contentType\":\"application/xml\"").append("\n");
		builder1.append("}");
		String documentToJson = builder1.toString(); //niet de beste naam maar OK

		String hashedPayload = DigestUtils.sha256Hex(documentToJson);
		canonicalURL.append(hashedPayload);

		StringBuilder stringToSign = new StringBuilder("");
		stringToSign.append("AWS4-HMAC-SHA256").append("\n");
		stringToSign.append(dateTimeStamp).append("\n");
		stringToSign.append(dateStamp + "/" + regionName + "/" + serviceName /* or s3 */ + "/aws4_request").append("\n");
		stringToSign.append(DigestUtils.sha256Hex(canonicalURL.toString()));

		byte[] signature = null;
		try {
			signature = NecessaryFunctions.getSignatureKey(secretKey, dateStamp, regionName, serviceName);
		} catch (Exception e) {
			LOGGER.error("AmazonApiUpdateProduct --> First signature error:"+e.getMessage());
		}

		try {
			signature = NecessaryFunctions.HmacSHA256(stringToSign.toString(), signature);
		} catch (Exception e1) {
			LOGGER.error("AmazonApiUpdateProduct --> Second signature error:"+e1.getMessage());
		}
		
		String signatureString = NecessaryFunctions.bytesToHex(signature);

		String AuthorizationHeader = "AWS4-HMAC-SHA256 Credential=" + accesKey + "/" + dateStamp + "/" + regionName
				+ "/" + serviceName
				+ "/aws4_request, SignedHeaders=host;x-amz-access-token;x-amz-date;x-amz-security-token, Signature="
				+ signatureString;

		headers.add("Authorization", AuthorizationHeader);
		headers.add("x-amz-access-token", accesTokenToInsert);
		headers.add("X-Amz-Date", dateTimeStamp);
		headers.add("X-Amz-Security-Token", sessionToken);
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		String DATA = documentToJson;

		HttpEntity<String> entity = new HttpEntity<>(DATA, headers);

		try {
			return rest.exchange(URI, HttpMethod.POST, entity, DocumentFeedResponse.class).getBody();
		} catch (HttpClientErrorException e) {
			LOGGER.error("AmazonApiUpdateProduct --> RestExchange error at getFeedDocument():" + e.getMessage());
			throw new ApiException("something went wrong whilst getting DocumentFeedResponse from amazon"+ e.getResponseBodyAsString());
		}
	}

	// put request - feeddocument

	public void putFeedDocument(String sku, int quantity, String URIEncoded) throws ApiException {

		StringBuilder stringBuilder = new StringBuilder(); //everything is hardcoded except sku & quantity
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
		stringBuilder.append("<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\">").append("\n");
		stringBuilder.append("<Header>").append("\n");
		stringBuilder.append("<DocumentVersion>1.01</DocumentVersion>").append("\n");
		stringBuilder.append("<MerchantIdentifier>A24AHHJQD1JJQL</MerchantIdentifier>").append("\n");
		stringBuilder.append("</Header>").append("\n");
		stringBuilder.append("<MessageType>Inventory</MessageType>").append("\n");
		stringBuilder.append("<Message>").append("\n");
		stringBuilder.append("<MessageID>1</MessageID>").append("\n");
		stringBuilder.append("<Inventory>").append("\n");
		stringBuilder.append("<SKU>").append(sku).append("</SKU>").append("\n");                          //------>> SKU
		stringBuilder.append("<Quantity>").append(quantity).append("</Quantity>").append("\n");           //------>> Quantity
		stringBuilder.append("</Inventory>").append("\n");
		stringBuilder.append("</Message>").append("\n");
		stringBuilder.append("</AmazonEnvelope>");
		String DATA = stringBuilder.toString();

		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPut httpPut = new HttpPut(URIEncoded);
			httpPut.setHeader("Host", "tortuga-prod-eu.s3-eu-west-1.amazonaws.com");
			httpPut.setHeader("Content-Type", "application/xml");
			org.apache.http.HttpEntity entity1 = new ByteArrayEntity(DATA.getBytes("UTF-8"));
			httpPut.setEntity(entity1);
			httpclient.execute(httpPut);
		} catch (IOException e) {
			LOGGER.error("AmazonApiUpdateProduct --> HttpClient IOException at putFeedDocument");
		}
	}

	// post request update stock - werkt
	public String updateStock(String sku, int quantity) throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		ResponseEntity<AccessToken> accesToken = credentialsAndToken.getAccessToken();
		Credentials credentials = credentialsAndToken.getAssumeRolCredentials();

		DocumentFeedResponse documentFeedResponse = getFeedDocument();
		String inputFeedDocumentId = documentFeedResponse.getFeedDocumentId();
		String URL = documentFeedResponse.getUrl();
		putFeedDocument(sku, quantity, URL);

		String accesKey = credentials.getAccessKeyId();
		String secretKey = credentials.getSecretAccessKey();
		String securityToken = credentials.getSessionToken();

		LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
		String dateStamp = NecessaryFunctions.getDate(now);
		String dateTimeStamp = NecessaryFunctions.getTimeStamp(now);
		String regionName = "eu-west-1";
		String serviceName = "execute-api";

		String feedType = "POST_INVENTORY_AVAILABILITY_DATA";
		ArrayList<String> marketplaceIds = new ArrayList<>();
		marketplaceIds.add("A13V1IB3VIYZZH");// france

		/*
		 * countries.add("A1805IZSGTT6HS");//netherlands
		 * countries.add("A1C3SOZRARQ6R3");//poland
		 * countries.add("A1F83G8C2ARO7P");//greatbritain
		 * countries.add("A1PA6795UKMFR9");//germany
		 * countries.add("A1RKKUPIHCS9HS");//spain
		 * countries.add("A2NODRKZP88ZB9");//sweden
		 * countries.add("APJ6JRA9NG5V4");//italy
		 */

		UpdateStock updateStock = new UpdateStock(feedType, marketplaceIds, inputFeedDocumentId);
		String updateStockToJson = null;
		try {
			updateStockToJson = new ObjectMapper().writeValueAsString(updateStock);
		} catch (JsonProcessingException e2) {
			LOGGER.error("AmazonApiUpdateProduct --> Something went wrong while creating updateStockObject for updateStockMethod: "+ e2.getMessage());
		}

		String hashedPayload = DigestUtils.sha256Hex(updateStockToJson);

		StringBuilder canonicalURL = new StringBuilder("");
		canonicalURL.append("POST").append("\n");
		canonicalURL.append("/feeds/2020-09-04/feeds").append("\n");
		canonicalURL.append("\n"); // no query string
		canonicalURL.append(("host").toLowerCase() + ":" + ("sellingpartnerapi-eu.amazon.com").trim() + "\n");
		canonicalURL.append(("x-amz-access-token").toLowerCase() + ":" + (accesToken.getBody().getAccessToken()).trim() + "\n");
		canonicalURL.append(("x-amz-date").toLowerCase() + ":" + (dateTimeStamp).trim() + "\n");
		canonicalURL.append(("x-amz-security-token").toLowerCase() + ":" + (securityToken).trim() + "\n");
		canonicalURL.append("\n");
		canonicalURL.append("host;x-amz-access-token;x-amz-date;x-amz-security-token").append("\n");
		canonicalURL.append(hashedPayload.toLowerCase());

		StringBuilder stringToSign = new StringBuilder("");
		stringToSign.append("AWS4-HMAC-SHA256").append("\n");
		stringToSign.append(dateTimeStamp).append("\n");
		stringToSign.append(dateStamp + "/" + regionName + "/" + serviceName /* or s3 */ + "/aws4_request")
				.append("\n");
		stringToSign.append(DigestUtils.sha256Hex(canonicalURL.toString()));

		byte[] signature = null;
		try {
			signature = NecessaryFunctions.getSignatureKey(secretKey, dateStamp, regionName, serviceName);
		} catch (Exception e) {
			LOGGER.error("AmazonApiUpdateProduct --> Signature error the first: "+ e.getMessage());
		}

		try {
			signature = NecessaryFunctions.HmacSHA256(stringToSign.toString(), signature);
		} catch (Exception e1) {
			LOGGER.error("AmazonApiUpdateProduct --> Signature error the second: "+ e1.getMessage());
		}

		String signatureString = NecessaryFunctions.bytesToHex(signature);

		String AuthorizationHeader = "AWS4-HMAC-SHA256 Credential=" + accesKey + "/" + dateStamp + "/" + regionName
				+ "/" + serviceName
				+ "/aws4_request, SignedHeaders=host;x-amz-access-token;x-amz-date;x-amz-security-token, Signature="
				+ signatureString;

		headers.add("Authorization", AuthorizationHeader);
		headers.add("x-amz-access-token", accesToken.getBody().getAccessToken());
		headers.add("X-Amz-Date", dateTimeStamp);
		headers.add("X-Amz-Security-Token", securityToken);
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		String DATA = updateStockToJson;

		HttpEntity<String> entity = new HttpEntity<>(DATA, headers);
		String URI = "https://sellingpartnerapi-eu.amazon.com/feeds/2020-09-04/feeds";

		try {
			ResponseEntity<String> response = rest.exchange(URI, HttpMethod.POST, entity, String.class);
			return response.getBody().toString();
		} catch (HttpClientErrorException e) {
			LOGGER.error("AmazonApiUpdateProduct --> RestExchange error at updateStock: "+e.getResponseBodyAsString());
			throw new ApiException("something went wrong whilst getting reportId from amazon" + e.getResponseBodyAsString());
		}
	}

}
