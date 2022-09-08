package be.hi10.realnutrition.pojos.amazon.getCatalog;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Catalog {
		@JsonProperty("asin")
	   public String asin;
		@JsonProperty("identifiers")
	    public ArrayList<Identifier> identifiers;
		
		
		public String getAsin() {
			return asin;
		}
		public ArrayList<Identifier> getIdentifiers() {
			return identifiers;
		}
		
		
}
