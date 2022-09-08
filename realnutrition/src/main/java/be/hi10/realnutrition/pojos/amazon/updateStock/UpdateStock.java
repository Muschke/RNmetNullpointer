package be.hi10.realnutrition.pojos.amazon.updateStock;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateStock {
		@JsonProperty("feedType")
	  	public String feedType;
		@JsonProperty("marketplaceIds")
	    public ArrayList<String> marketplaceIds;
		@JsonProperty("inputFeedDocumentId")
	    public String inputFeedDocumentId;
		
		
		
		public UpdateStock(String feedType, ArrayList<String> marketplaceIds, String inputFeedDocumentId) {

			this.feedType = feedType;
			this.marketplaceIds = marketplaceIds;
			this.inputFeedDocumentId = inputFeedDocumentId;
		}
		public String getFeedType() {
			return feedType;
		}
		public ArrayList<String> getMarketplaceIds() {
			return marketplaceIds;
		}
		public String getInputFeedDocumentId() {
			return inputFeedDocumentId;
		}
		
		
}
