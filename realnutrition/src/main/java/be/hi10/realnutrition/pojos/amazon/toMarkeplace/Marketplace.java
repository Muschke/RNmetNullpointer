package be.hi10.realnutrition.pojos.amazon.toMarkeplace;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Marketplace {
	
	@JsonProperty("id")
	public String id;
	@JsonProperty("countryCode")
	public String countryCode;
	@JsonProperty("name")
	public String name;
	@JsonProperty("defaultCurrencyCode")
	public String defaultCurrencyCode;
	@JsonProperty("defaultLanguageCode")
	public String defaultLanguageCode;
	@JsonProperty("domainName")
	public String domainName;
	
	public String getId() {
		return id;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public String getName() {
		return name;
	}
	public String getDefaultCurrencyCode() {
		return defaultCurrencyCode;
	}
	public String getDefaultLanguageCode() {
		return defaultLanguageCode;
	}
	public String getDomainName() {
		return domainName;
	}
	
	
	

}
