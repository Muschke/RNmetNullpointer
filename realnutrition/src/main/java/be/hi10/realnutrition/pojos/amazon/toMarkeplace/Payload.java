package be.hi10.realnutrition.pojos.amazon.toMarkeplace;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payload {
	@JsonProperty("marketplace")
	public Marketplace marketplace;
	@JsonProperty("participation")
	public Participation participation;
	public Marketplace getMarketplace() {
		return marketplace;
	}
	public Participation getParticipation() {
		return participation;
	}
}
