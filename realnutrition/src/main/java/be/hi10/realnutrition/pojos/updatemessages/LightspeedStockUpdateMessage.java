package be.hi10.realnutrition.pojos.updatemessages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "variant" })
public class LightspeedStockUpdateMessage {

	@JsonProperty("variant")
	private LightspeedVariant variant;

	public LightspeedStockUpdateMessage(LightspeedVariant variant) {
		this.variant = variant;
	}

	@JsonProperty("variant")
	public LightspeedVariant getVariant() {
		return variant;
	}

	@JsonProperty("variant")
	public void setVariant(LightspeedVariant variant) {
		this.variant = variant;
	}
}
