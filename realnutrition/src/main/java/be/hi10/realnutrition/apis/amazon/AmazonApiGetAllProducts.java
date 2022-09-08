package be.hi10.realnutrition.apis.amazon;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;
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
import be.hi10.realnutrition.pojos.amazon.getCatalog.Catalog;
import be.hi10.realnutrition.pojos.amazon.reports.Report;
import be.hi10.realnutrition.pojos.amazon.reports.ReportBody;
import be.hi10.realnutrition.pojos.amazon.reports.ReportDocument;
import be.hi10.realnutrition.pojos.amazon.reports.ReportId;
import be.hi10.realnutrition.pojos.amazon.toMarkeplace.MarketplacesArray;

@Component
public class AmazonApiGetAllProducts {
	CredentialsAndToken credentialsAndToken = new CredentialsAndToken();
	NecessaryFunctions necessaryFunctions = new NecessaryFunctions();
	private final static Logger LOGGER = LoggerFactory.getLogger(AmazonApiGetAllProducts.class);

	public MarketplacesArray getToMarketplace() throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		ResponseEntity<AccessToken> accesToken = credentialsAndToken.getAccessToken();
		Credentials credentials = credentialsAndToken.getAssumeRolCredentials();

		/* generate signature key - variables */
		String accesKey = credentials.accessKeyId;
		String secretKey = credentials.secretAccessKey;
		String sessionToken = credentials.sessionToken;
		String accesTokenToInsert = accesToken.getBody().getAccessToken();
		
		LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
		String dateStamp = NecessaryFunctions.getDate(now);
		String dateTimeStamp = NecessaryFunctions.getTimeStamp(now);
		String regionName = "eu-west-1";
		String serviceName = "execute-api";
		String URI = "https://sellingpartnerapi-eu.amazon.com/sellers/v1/marketplaceParticipations";

		// 1. create canonical header
		StringBuilder canonicalURL = new StringBuilder("");
		// 1.1 set httpmethod
		canonicalURL.append("GET").append("\n");
		// 1.2 set canonicalURL --if empty, use forward slash - encoded twice? everything between host and query parameters, excluding the last /
		canonicalURL.append("/sellers/v1/marketplaceParticipations").append("\n");
		// 1.3 canonical querystring -- If the request does not include a query string, use an empty string (essentially, a blank line).
		canonicalURL.append("").append("\n");
		// 1.4 set canonical headers (obligated: usually host & x-amz-date, possibly more depending on the request)
		canonicalURL.append(("host").toLowerCase() + ":" + ("sellingpartnerapi-eu.amazon.com").trim() + "\n");
		canonicalURL.append(("x-amz-access-Token").toLowerCase() + ":" + (accesTokenToInsert).trim() + "\n");
		canonicalURL.append(("x-amz-Date").toLowerCase() + ":" + (dateTimeStamp).trim() + "\n");
		canonicalURL.append(("x-amz-Security-Token").toLowerCase() + ":" + (sessionToken).trim() + "\n");
		canonicalURL.append("\n");
		// 1.5 set signed headers !signedHeaders must correspond to the list of headers
		// in the canonical headers -- ex. SignedHeaders=host;x-amz-date; ! the
		// headersname must be in lowercase"
		canonicalURL.append("host;x-amz-access-token;x-amz-date;x-amz-security-token").append("\n");
		// 1.6 append a hashed payload, if u use getrequest, use hashed empty string
		var hashedPayload = DigestUtils.sha256Hex("");
		canonicalURL.append(hashedPayload);
		// print to check result:
		// System.out.println("canonicalUrl: \n"+ canonicalURL.toString());

		// 2. create stringToSign
		StringBuilder stringToSign = new StringBuilder("");
		// 2..1 contacenate algoritme (always AWS4-HMAC-SHA256)
		stringToSign.append("AWS4-HMAC-SHA256").append("\n");
		// 2.2 contacenate timestamp (ex 20220728Z081106T) note ! GMT time
		stringToSign.append(dateTimeStamp).append("\n");
		// 2.3 add credential information (ex 20220728/eu-west-1/sts/aws4_request)
		stringToSign.append(dateStamp + "/" + regionName + "/" + serviceName + "/aws4_request").append("\n");
		// 2.4 add the hash of canonical request
		stringToSign.append(DigestUtils.sha256Hex(canonicalURL.toString()));

		/* 3.1 generate signing key */
		byte[] signature = null;
		try {
			signature = NecessaryFunctions.getSignatureKey(secretKey, dateStamp, regionName, serviceName);
		} catch (Exception e) {
			LOGGER.error("AmazonApiGetAllProducts --> Error while getting the signature key, step 3.1: " + e.getMessage());
		}
		// 3.2 calculate signature: byte[] signature = HmacSHA256(signature,stringToSign);
		try {
			signature = NecessaryFunctions.HmacSHA256(stringToSign.toString(), signature);
		} catch (Exception e1) {
			LOGGER.error("AmazonApiGetAllProducts --> Error while getting the signature key, step 3.2: " + e1.getMessage());
		}
		// 3.3 encode signature
		String signatureString = NecessaryFunctions.bytesToHex(signature);

		/* log in headers */
		String AuthorizationHeader = "AWS4-HMAC-SHA256 Credential=" + accesKey + "/" + dateStamp + "/" + regionName
				+ "/" + serviceName
				+ "/aws4_request, SignedHeaders=host;x-amz-access-token;x-amz-date;x-amz-security-token, Signature="
				+ signatureString;
		headers.add("authorization", AuthorizationHeader);
		headers.add("X-Amz-Date", dateTimeStamp);
		headers.add("x-amz-access-token", accesTokenToInsert);
		headers.add("x-amz-security-token", sessionToken);
		headers.add("Accept", "application/json");

		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<MarketplacesArray> marketplacesArray = rest.exchange(URI, HttpMethod.GET, entity, MarketplacesArray.class);
			return marketplacesArray.getBody();
		} catch (HttpClientErrorException e) {
			LOGGER.error("AmazonApiGetAllProducts --> Something went wrong while getting marketplaces from Amazon.com: " + e.getResponseBodyAsString());
			throw new ApiException("Something went wrong while getting marketplaces from Amazon.com: " + e.getResponseBodyAsString());
		}
	}

	public String getReportId(String marketplace) throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		ResponseEntity<AccessToken> accesToken = credentialsAndToken.getAccessToken();
		Credentials credentials = credentialsAndToken.getAssumeRolCredentials();

		String accesKey = credentials.getAccessKeyId();
		String secretKey = credentials.getSecretAccessKey();
		String securityToken = credentials.getSessionToken();

		LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
		String dateStamp = NecessaryFunctions.getDate(now);
		String dateTimeStamp = NecessaryFunctions.getTimeStamp(now);
		String regionName = "eu-west-1";
		String serviceName = "execute-api";

		String REPORT_TYPE = "GET_FLAT_FILE_OPEN_LISTINGS_DATA";
		// String REPORT_TYPE = "GET_MERCHANT_LISTINGS_ALL_DATA";
		ArrayList<String> countries = new ArrayList<>();
		countries.add(marketplace);

		ReportBody reportBody = new ReportBody(REPORT_TYPE, countries);
		String reportToJson = null;
		try {
			reportToJson = new ObjectMapper().writeValueAsString(reportBody); //converts objects into a json string
		} catch (JsonProcessingException e2) {
			LOGGER.error("AmazonApiGetAllProducts --> Something went wrong while creating reportBody for getReportId, " + e2.getMessage());
		}

		String hashedPayload = DigestUtils.sha256Hex(reportToJson);

		StringBuilder canonicalURL = new StringBuilder("");
		canonicalURL.append("POST").append("\n");
		canonicalURL.append("/reports/2021-06-30/reports").append("\n");
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
		stringToSign.append(dateStamp + "/" + regionName + "/" + serviceName /* or s3 */ + "/aws4_request").append("\n");
		stringToSign.append(DigestUtils.sha256Hex(canonicalURL.toString()));

		byte[] signature = null;
		try {
			signature = NecessaryFunctions.getSignatureKey(secretKey, dateStamp, regionName, serviceName);
		} catch (Exception e) {
			LOGGER.error("AmazonApiGetAllProducts --> Something went wrong with the signature, " + e.getMessage());
		}

		try {
			signature = NecessaryFunctions.HmacSHA256(stringToSign.toString(), signature);
		} catch (Exception e1) {
			LOGGER.error("AmazonApiGetAllProducts --> Something went wrong with the second signature, " + e1.getMessage());
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
		String DATA = reportToJson; //kinda unnecessary

		HttpEntity<String> entity = new HttpEntity<>(DATA, headers);
		String URI = "https://sellingpartnerapi-eu.amazon.com/reports/2021-06-30/reports";

		try {
			return rest.exchange(URI, HttpMethod.POST, entity, ReportId.class).getBody().getReportId();
		} catch (HttpClientErrorException e) {
			LOGGER.error("AmazonApiGetAllProducts --> Something went wrong while getting reportId from Amazon:" + e.getResponseBodyAsString());
			throw new ApiException("AmazonApiGetAllProducts --> Something went wrong while getting reportId from Amazon:" + e.getResponseBodyAsString());
		}
	}

	public ResponseEntity<Report> getReport(String reportIdParam, String marketplace) throws ApiException {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		ResponseEntity<AccessToken> accesToken = credentialsAndToken.getAccessToken();
		Credentials credentials = credentialsAndToken.getAssumeRolCredentials();

		String reportId;

		if (reportIdParam == null) {
			reportId = getReportId(marketplace);
		} else {
			reportId = reportIdParam;
		}

		String accesKey = credentials.accessKeyId;
		String secretKey = credentials.secretAccessKey;
		String sessionToken = credentials.sessionToken;
		String accesTokenToInsert = accesToken.getBody().getAccessToken();
		LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
		String dateStamp = NecessaryFunctions.getDate(now);
		String dateTimeStamp = NecessaryFunctions.getTimeStamp(now);
		String regionName = "eu-west-1";
		String serviceName = "execute-api";
		String URI = "https://sellingpartnerapi-eu.amazon.com/reports/2021-06-30/reports/" + reportId;

		// 1. canonical header
		StringBuilder canonicalURL = new StringBuilder("");
		canonicalURL.append("GET").append("\n");
		canonicalURL.append("/reports/2021-06-30/reports/" + reportId).append("\n");
		canonicalURL.append("").append("\n");
		canonicalURL.append(("host").toLowerCase() + ":" + ("sellingpartnerapi-eu.amazon.com").trim() + "\n");
		canonicalURL.append(("x-amz-access-Token").toLowerCase() + ":" + (accesTokenToInsert).trim() + "\n");
		canonicalURL.append(("x-amz-Date").toLowerCase() + ":" + (dateTimeStamp).trim() + "\n");
		canonicalURL.append(("x-amz-Security-Token").toLowerCase() + ":" + (sessionToken).trim() + "\n");
		canonicalURL.append("\n");
		canonicalURL.append("host;x-amz-access-token;x-amz-date;x-amz-security-token").append("\n");
		String hashedPayload = DigestUtils.sha256Hex("");
		canonicalURL.append(hashedPayload);

		// 2. create stringToSign
		StringBuilder stringToSign = new StringBuilder("");
		stringToSign.append("AWS4-HMAC-SHA256").append("\n");
		stringToSign.append(dateTimeStamp).append("\n");
		stringToSign.append(dateStamp + "/" + regionName + "/" + serviceName + "/aws4_request").append("\n");
		stringToSign.append(DigestUtils.sha256Hex(canonicalURL.toString()));

		/* 3.1 generate signing key */
		byte[] signature = null;
		try {
			signature = NecessaryFunctions.getSignatureKey(secretKey, dateStamp, regionName, serviceName);
		} catch (Exception e) {
			LOGGER.error("AmazonApiGetAllProducts --> Something went wrong with the signature 273, " + e.getMessage());
		}

		try {
			signature = NecessaryFunctions.HmacSHA256(stringToSign.toString(), signature);
		} catch (Exception e1) {
			LOGGER.error("AmazonApiGetAllProducts --> Something went wrong with the signature, 279 " + e1.getMessage());
		}

		String signatureString = NecessaryFunctions.bytesToHex(signature);

		String AuthorizationHeader = "AWS4-HMAC-SHA256 Credential=" + accesKey + "/" + dateStamp + "/" + regionName
				+ "/" + serviceName
				+ "/aws4_request, SignedHeaders=host;x-amz-access-token;x-amz-date;x-amz-security-token, Signature="
				+ signatureString;
		headers.add("authorization", AuthorizationHeader);
		headers.add("X-Amz-Date", dateTimeStamp);
		headers.add("x-amz-access-token", accesTokenToInsert);
		headers.add("x-amz-security-token", sessionToken);
		headers.add("Accept", "application/json");
		HttpEntity<String> entity = new HttpEntity<>(headers);
		try {
			return rest.exchange(URI, HttpMethod.GET, entity, Report.class);
		} catch (HttpClientErrorException e) {
			LOGGER.error("AmazonApiGetAllProducts --> RestExchange error at getReport, " + e.getResponseBodyAsString());
			throw new ApiException("Something went wrong while the reportlink from Amazon.com: " + e.getResponseBodyAsString());
		}
	}

	public ReportDocument getReportDocument(String marketplace) throws ApiException {
		String reportId = getReportId(marketplace);
		Report report = getReport(reportId, marketplace).getBody();
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			LOGGER.error("AmazonApiGetAllProducts --> ThreadSleep error");
		}

		while (loopThrough(report, marketplace) == null) {
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				LOGGER.error("AmazonApiGetAllProducts --> ThreadSleep error 2");
			}
		}
		return loopThrough(report, marketplace);
	}

	public ReportDocument loopThrough(Report reportParam, String marketplace) throws ApiException {
		String reportId = reportParam.reportId;
		Report report = getReport(reportId, marketplace).getBody();
		ReportDocument reportDocument = null;
		switch (report.processingStatus) {
		case "IN_QUEUE":
			try {
				Thread.sleep(15000);
			} catch (Exception e) {
				LOGGER.error("AmazonApiGetAllProducts --> ThreadSleep error 3");
			}
			break;
		case "IN_PROGRESS":
			try {
				Thread.sleep(15000);
			} catch (Exception e) {
				LOGGER.error("AmazonApiGetAllProducts --> ThreadSleep error 4");
			}
			break;
		case "DONE":
			reportDocument = getReportDocumentURL(report).getBody();
			break;
		}
		return reportDocument;
	}

	public ResponseEntity<ReportDocument> getReportDocumentURL(Report reportParam) throws ApiException {
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
		String URI = "https://sellingpartnerapi-eu.amazon.com/reports/2021-06-30/documents/" + reportParam.getReportDocumentId();

		StringBuilder canonicalURL = new StringBuilder("");
		canonicalURL.append("GET").append("\n");
		canonicalURL.append("/reports/2021-06-30/documents/" + reportParam.getReportDocumentId()).append("\n");
		canonicalURL.append("").append("\n");
		canonicalURL.append(("host").toLowerCase() + ":" + ("sellingpartnerapi-eu.amazon.com").trim() + "\n");
		canonicalURL.append(("x-amz-access-Token").toLowerCase() + ":" + (accesTokenToInsert).trim() + "\n");
		canonicalURL.append(("x-amz-Date").toLowerCase() + ":" + (dateTimeStamp).trim() + "\n");
		canonicalURL.append(("x-amz-Security-Token").toLowerCase() + ":" + (sessionToken).trim() + "\n");
		canonicalURL.append("\n");
		canonicalURL.append("host;x-amz-access-token;x-amz-date;x-amz-security-token").append("\n");
		String hashedPayload = DigestUtils.sha256Hex("");
		canonicalURL.append(hashedPayload);

		StringBuilder stringToSign = new StringBuilder("");
		stringToSign.append("AWS4-HMAC-SHA256").append("\n");
		stringToSign.append(dateTimeStamp).append("\n");
		stringToSign.append(dateStamp + "/" + regionName + "/" + serviceName + "/aws4_request").append("\n");
		stringToSign.append(DigestUtils.sha256Hex(canonicalURL.toString()));

		/* 3.1 generate signing key */
		byte[] signature = null;
		try {
			signature = NecessaryFunctions.getSignatureKey(secretKey, dateStamp, regionName, serviceName);
		} catch (Exception e) {
			LOGGER.error("AmazonApiGetAllProducts --> Signature error 1 e:" + e.getMessage());
		}

		try {
			signature = NecessaryFunctions.HmacSHA256(stringToSign.toString(), signature);
		} catch (Exception e1) {
			LOGGER.error("AmazonApiGetAllProducts --> Signature error e1:" + e1.getMessage());
		}
		String signatureString = NecessaryFunctions.bytesToHex(signature);

		/*----------------------------------------------------------------------------------------------------------------------------*/
		/* log in headers */
		String AuthorizationHeader = "AWS4-HMAC-SHA256 Credential=" + accesKey + "/" + dateStamp + "/" + regionName
				+ "/" + serviceName
				+ "/aws4_request, SignedHeaders=host;x-amz-access-token;x-amz-date;x-amz-security-token, Signature="
				+ signatureString;
		headers.add("authorization", AuthorizationHeader);
		headers.add("X-Amz-Date", dateTimeStamp);
		headers.add("x-amz-access-token", accesTokenToInsert);
		headers.add("x-amz-security-token", sessionToken);
		headers.add("Accept", "application/json");
		HttpEntity<String> entity = new HttpEntity<>(headers);
		try {
			return rest.exchange(URI, HttpMethod.GET, entity, ReportDocument.class);
		} catch (HttpClientErrorException e) {
			LOGGER.error("AmazonApiGetAllProducts --> RestExchange error with getReportDocumentURL:" + e.getMessage());
			throw new ApiException("Something went wrong while the reportURl from Amazon.com: " + e.getResponseBodyAsString());
		}
	}

	/*-----------------------------------------------*/

	public Catalog getEancode(String asin, String marketplaceId) throws ApiException {
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
		String marketplaces = marketplaceId;
		String URI = "https://sellingpartnerapi-eu.amazon.com/catalog/2020-12-01/items/" + asin + "?marketplaceIds="
				+ marketplaces + "&includedData=identifiers";

		
		StringBuilder canonicalURL = new StringBuilder("");
		canonicalURL.append("GET").append("\n");
		canonicalURL.append("/catalog/2020-12-01/items/" + asin).append("\n");
		try {
			canonicalURL
					.append(URLEncoder.encode("includedData", "UTF-8").replaceAll("\\+", "%20") + "="
							+ URLEncoder.encode("identifiers", "UTF-8").replaceAll("\\+", "%20") + "&")
					.append(URLEncoder.encode("marketplaceIds", "UTF-8").replaceAll("\\+", "%20") + "="
							+ URLEncoder.encode(marketplaces, "UTF-8").replaceAll("\\+", "%20"))
					.append("\n");
		} catch (UnsupportedEncodingException e2) {
			LOGGER.error("AmazonApiGetAllProducts --> Failure to merge canonicalURL");
		}

		canonicalURL.append(("host").toLowerCase() + ":" + ("sellingpartnerapi-eu.amazon.com").trim() + "\n");
		canonicalURL.append(("x-amz-access-Token").toLowerCase() + ":" + (accesTokenToInsert).trim() + "\n");
		canonicalURL.append(("x-amz-Date").toLowerCase() + ":" + (dateTimeStamp).trim() + "\n");
		canonicalURL.append(("x-amz-Security-Token").toLowerCase() + ":" + (sessionToken).trim() + "\n");
		canonicalURL.append("\n");
		canonicalURL.append("host;x-amz-access-token;x-amz-date;x-amz-security-token").append("\n");
		String hashedPayload = DigestUtils.sha256Hex("");
		canonicalURL.append(hashedPayload);

		// 2. create stringToSign
		StringBuilder stringToSign = new StringBuilder("");
		stringToSign.append("AWS4-HMAC-SHA256").append("\n");
		stringToSign.append(dateTimeStamp).append("\n");
		stringToSign.append(dateStamp + "/" + regionName + "/" + serviceName + "/aws4_request").append("\n");
		stringToSign.append(DigestUtils.sha256Hex(canonicalURL.toString()));

		/* 3.1 generate signing key */
		byte[] signature = null;
		try {
			signature = NecessaryFunctions.getSignatureKey(secretKey, dateStamp, regionName, serviceName);
		} catch (Exception e) {
			LOGGER.error("AmazonApiGetAllProducts --> Failed to signature at 477: " + e.getMessage());
		}
		
		try {
			signature = NecessaryFunctions.HmacSHA256(stringToSign.toString(), signature);
		} catch (Exception e1) {
			LOGGER.error("AmazonApiGetAllProducts --> Failed the second signature: " + e1.getMessage());
		}

		String signatureString = NecessaryFunctions.bytesToHex(signature);

		/* log in headers */
		String AuthorizationHeader = "AWS4-HMAC-SHA256 Credential=" + accesKey + "/" + dateStamp + "/" + regionName
				+ "/" + serviceName
				+ "/aws4_request, SignedHeaders=host;x-amz-access-token;x-amz-date;x-amz-security-token, Signature="
				+ signatureString;
		headers.add("authorization", AuthorizationHeader);
		headers.add("X-Amz-Date", dateTimeStamp);
		headers.add("x-amz-access-token", accesTokenToInsert);
		headers.add("x-amz-security-token", sessionToken);
		headers.add("Accept", "application/json");

		HttpEntity<String> entity = new HttpEntity<>(headers);
		try {
			ResponseEntity<Catalog> catalogWithEan = rest.exchange(URI, HttpMethod.GET, entity, Catalog.class);
			return catalogWithEan.getBody();
		} catch (HttpClientErrorException e) {
			LOGGER.error("AmazonApiGetAllProducts --> Rest exchange error at getEanCode:" + e.getMessage());
			throw new ApiException("Something went wrong while getting ean code from Amazon.com: " + e.getResponseBodyAsString());
		}
	}

}
