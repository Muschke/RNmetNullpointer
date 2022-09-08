
package be.hi10.realnutrition.pojos.bol;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "offerId", "ean", "referenceCode", "onHoldByRetailer", "pricing", "stock", "fulfilment", "store",
		"condition", "notPublishableReasons" })
public class Product {

	@JsonProperty("offerId")
	private String offerId;
	@JsonProperty("ean")
	private String ean;
	@JsonProperty("stock")
	private Stock stock;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("offerId")
	public String getOfferId() {
		return offerId;
	}

	@JsonProperty("offerId")
	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	@JsonProperty("ean")
	public String getEan() {
		return ean;
	}

	@JsonProperty("ean")
	public void setEan(String ean) {
		this.ean = ean;
	}

	@JsonProperty("stock")
	public Stock getStock() {
		return stock;
	}

	@JsonProperty("stock")
	public void setStock(Stock stock) {
		this.stock = stock;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
