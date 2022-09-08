
package be.hi10.realnutrition.pojos.lightspeed;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "createdAt", "updatedAt", "isDefault", "sortOrder", "articleCode", "ean", "sku", "hs",
		"unitPrice", "unitUnit", "priceExcl", "priceIncl", "priceCost", "oldPriceExcl", "oldPriceIncl", "stockTracking",
		"stockLevel", "stockAlert", "stockMinimum", "stockSold", "stockBuyMininum", "stockBuyMinimum",
		"stockBuyMaximum", "weight", "weightValue", "weightUnit", "volume", "volumeValue", "volumeUnit", "colli",
		"sizeX", "sizeY", "sizeZ", "sizeXValue", "sizeYValue", "sizeZValue", "sizeUnit", "matrix", "title", "taxType",
		"image", "tax", "product", "movements", "metafields", "additionalcost", "options" })
public class Variant {

	@JsonProperty("id")
	private Integer id;
	@JsonProperty("articleCode")
	private String articleCode;
	@JsonProperty("ean")
	private String ean;
	@JsonProperty("stockLevel")
	private Integer stockLevel;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("id")
	public Integer getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(Integer id) {
		this.id = id;
	}

	@JsonProperty("ean")
	public String getEan() {
		return ean;
	}

	@JsonProperty("ean")
	public void setEan(String ean) {
		this.ean = ean;
	}

	@JsonProperty("stockLevel")
	public Integer getStockLevel() {
		return stockLevel;
	}

	@JsonProperty("stockLevel")
	public void setStockLevel(Integer stockLevel) {
		this.stockLevel = stockLevel;
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
