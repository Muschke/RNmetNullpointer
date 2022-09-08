package be.hi10.realnutrition.pojos.updatemessages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "amount", "managedByRetailer" })
public class BolStockUpdateMessage {

	@JsonProperty("amount")
	private Integer amount;
	@JsonProperty("managedByRetailer")
	private Boolean managedByRetailer;

	public BolStockUpdateMessage(Integer amount, Boolean managedByRetailer) {
		this.amount = amount;
		this.managedByRetailer = managedByRetailer;
	}

	@JsonProperty("amount")
	public Integer getAmount() {
		return amount;
	}

	@JsonProperty("amount")
	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	@JsonProperty("managedByRetailer")
	public Boolean getManagedByRetailer() {
		return managedByRetailer;
	}

	@JsonProperty("managedByRetailer")
	public void setManagedByRetailer(Boolean managedByRetailer) {
		this.managedByRetailer = managedByRetailer;
	}

}