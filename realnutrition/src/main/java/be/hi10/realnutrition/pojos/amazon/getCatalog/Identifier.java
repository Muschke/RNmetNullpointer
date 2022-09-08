package be.hi10.realnutrition.pojos.amazon.getCatalog;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Identifier {
		@JsonProperty("marketplaceId")
	  	public String marketplaceId;
		@JsonProperty("identifiers")
	    public ArrayList<Identifier> identifiers;
		@JsonProperty("identifier")
	    public String identifier;
		@JsonProperty("identifierType")
	    public String identifierType;
		
		
		public String getMarketplaceId() {
			return marketplaceId;
		}
		public ArrayList<Identifier> getIdentifiers() {
			return identifiers;
		}
		public String getIdentifier() {
			return identifier;
		}
		public String getIdentifierType() {
			return identifierType;
		}
		
		
		
}
