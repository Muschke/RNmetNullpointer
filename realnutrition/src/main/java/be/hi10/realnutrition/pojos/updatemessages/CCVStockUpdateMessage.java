package be.hi10.realnutrition.pojos.updatemessages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "stock" })
public class CCVStockUpdateMessage {

	@JsonProperty("stock")
	private Integer stock;

	public CCVStockUpdateMessage(Integer stock) {
		this.stock = stock;
	}

	@JsonProperty("stock")
	public Integer getStock() {
		return stock;
	}

	@JsonProperty("stock")
	public void setStock(Integer stock) {
		this.stock = stock;
	}

	@Override
	public String toString() {
		return "{\"stock\": " + getStock() + "}";
	}
}
