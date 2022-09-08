package be.hi10.realnutrition.pojos.exactonline.stockposition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StockPositionContent {

	@JsonProperty("ItemId")
	private String itemId;
	@JsonProperty("InStock")
	private int inStock;
	@JsonProperty("PlanningIn")
	private int planningIn;
	@JsonProperty("PlanningOut")
	private int planningOut;

	@JsonProperty("ItemId")
	public String getItemId() {
		return itemId;
	}

	@JsonProperty("ItemId")
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	@JsonProperty("InStock")
	public int getInStock() {
		return inStock;
	}

	@JsonProperty("InStock")
	public void setInStock(int inStock) {
		this.inStock = inStock;
	}

	@JsonProperty("PlanningIn")
	public int getPlanningIn() {
		return planningIn;
	}

	@JsonProperty("PlanningIn")
	public void setPlanningIn(int planningIn) {
		this.planningIn = planningIn;
	}

	@JsonProperty("PlanningOut")
	public int getPlanningOut() {
		return planningOut;
	}

	@JsonProperty("PlanningOut")
	public void setPlanningOut(int planningOut) {
		this.planningOut = planningOut;
	}

}