package be.hi10.realnutrition.pojos.amazon.reports;
import com.fasterxml.jackson.annotation.JsonProperty;


public class DocumentFeedResponse {
	@JsonProperty("feedDocumentId")
    public String feedDocumentId;
    @JsonProperty("url")
    public String url;

    public String getFeedDocumentId() {
        return feedDocumentId;
    }

    public String getUrl() {
        return url;
    }
}
