package be.hi10.realnutrition.pojos.amazon.reports;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportDocument {
	@JsonProperty("reportDocumentId")
    public String reportDocumentId;
	@JsonProperty("url")
    public String url;
	public String getReportDocumentId() {
		return reportDocumentId;
	}
	public String getUrl() {
		return url;
	}
	
	
}
