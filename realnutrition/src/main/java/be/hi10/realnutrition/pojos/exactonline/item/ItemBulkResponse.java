package be.hi10.realnutrition.pojos.exactonline.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemBulkResponse {

	@JsonProperty("d")
	private ItemBulkContent content;

	@JsonProperty("d")
	public ItemBulkContent getContent() {
		return content;
	}

	@JsonProperty("d")
	public void setContent(ItemBulkContent content) {
		this.content = content;
	}
}