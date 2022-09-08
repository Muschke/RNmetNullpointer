package be.hi10.realnutrition.pojos.updatemessages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "stockLevel" })
public class LightspeedVariant {

	@JsonProperty("stockLevel")
	private Integer stockLevel;

	public LightspeedVariant(Integer stockLevel) {
		this.stockLevel = stockLevel;
	}

	@JsonProperty("stockLevel")
	public Integer getStockLevel() {
		return stockLevel;
	}

	@JsonProperty("stockLevel")
	public void setStockLevel(Integer stockLevel) {
		this.stockLevel = stockLevel;
	}

}