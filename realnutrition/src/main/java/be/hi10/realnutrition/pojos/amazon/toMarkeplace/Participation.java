package be.hi10.realnutrition.pojos.amazon.toMarkeplace;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Participation {
	@JsonProperty("isParticipating")
	public String isParticipating;
	@JsonProperty("hasSuspendedListings")
	public String hasSuspendedListings;
	
	public String getIsParticipating() {
		return isParticipating;
	}
	public String getHasSuspendedListings() {
		return hasSuspendedListings;
	}
	
	
}
