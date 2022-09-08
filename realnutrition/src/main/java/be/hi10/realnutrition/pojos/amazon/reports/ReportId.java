package be.hi10.realnutrition.pojos.amazon.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportId {
    @JsonProperty("reportId")
    public String reportId;

	public String getReportId() {
		return reportId;
	}
    
    
}