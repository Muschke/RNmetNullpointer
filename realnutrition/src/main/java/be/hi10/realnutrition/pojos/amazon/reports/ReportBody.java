package be.hi10.realnutrition.pojos.amazon.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class ReportBody {
    @JsonProperty("reportType")
    public String reportType;

    @JsonProperty("marketplaceIds")
    public ArrayList<String> marketplaceIds;

    public String getReportType() {
        return reportType;
    }

    public ArrayList<String> getMarketplaceIds() {
        return marketplaceIds;
    }

    public ReportBody(String reportType, ArrayList<String> marketplaceIds) {
        this.reportType = reportType;
        this.marketplaceIds = marketplaceIds;
    }
}