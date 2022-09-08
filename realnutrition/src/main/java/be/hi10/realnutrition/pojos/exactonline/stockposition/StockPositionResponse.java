package be.hi10.realnutrition.pojos.exactonline.stockposition;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockPositionResponse {

	@JsonProperty("d")
	private List<StockPositionContent> content = null;

	@JsonProperty("d")
	public List<StockPositionContent> getContent() {
		return content;
	}

	@JsonProperty("d")
	public void setContent(List<StockPositionContent> content) {
		this.content = content;
	}
}