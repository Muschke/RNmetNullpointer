package be.hi10.realnutrition.pojos.exactonline.webhooknotification;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebHookNotificationResponse {

	@JsonProperty("Content")
	private WebHookContent content;
	@JsonProperty("HashCode")
	private String hashCode;

	public WebHookNotificationResponse(WebHookContent content, String hashCode) {
		this.content = content;
		this.hashCode = hashCode;
	}

	@JsonProperty("Content")
	public WebHookContent getContent() {
		return content;
	}

	@JsonProperty("Content")
	public void setContent(WebHookContent content) {
		this.content = content;
	}

	@JsonProperty("HashCode")
	public String getHashCode() {
		return hashCode;
	}

	@JsonProperty("HashCode")
	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}

	@Override
	public String toString() {
		return "HashCode: " + this.hashCode + "\n ***Content*** \n" + "Topic: " + this.getContent().getTopic()
				+ "\n Action: " + this.getContent().getAction() + "\n Division: " + this.getContent().getDivision()
				+ "\n Key: " + this.getContent().getKey() + "\n Endpoint: " + this.getContent().getExactOnlineEndpoint()
				+ "\n EventCreatedOn: " + this.getContent().getEventCreatedOn();
	}
}