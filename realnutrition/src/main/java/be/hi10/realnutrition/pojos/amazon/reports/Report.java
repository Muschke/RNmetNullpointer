package be.hi10.realnutrition.pojos.amazon.reports;
import java.sql.Date;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Report {
		@JsonProperty("reportType")
		public String reportType;
		@JsonProperty("processingEndTime")
		public Date processingEndTime;
		@JsonProperty("processingStatus")
		public String processingStatus;
		@JsonProperty("marketplaceIds")
		public ArrayList<String> marketplaceIds;
		@JsonProperty("reportDocumentId")
		public String reportDocumentId;
		@JsonProperty("reportId")
		public String reportId;
		@JsonProperty("dataEndTime")
		public Date dataEndTime;
		@JsonProperty("createdTime")
		public Date createdTime;
		@JsonProperty("processingStartTime")
		public Date processingStartTime;
		@JsonProperty("dataStartTime")
		public Date dataStartTime;
		public String getReportType() {
			return reportType;
		}
		public Date getProcessingEndTime() {
			return processingEndTime;
		}
		public String getProcessingStatus() {
			return processingStatus;
		}
		public ArrayList<String> getMarketplaceIds() {
			return marketplaceIds;
		}
		public String getReportDocumentId() {
			return reportDocumentId;
		}
		public String getReportId() {
			return reportId;
		}
		public Date getDataEndTime() {
			return dataEndTime;
		}
		public Date getCreatedTime() {
			return createdTime;
		}
		public Date getProcessingStartTime() {
			return processingStartTime;
		}
		public Date getDataStartTime() {
			return dataStartTime;
		}
		
		
		
}
