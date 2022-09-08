package be.hi10.realnutrition.pojos.exactonline.webhooknotification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "Topic", "ClientId", "Division", "Action", "Key", "ExactOnlineEndpoint", "EventCreatedOn" })
public class WebHookContent {

	@JsonProperty("Topic")
	private String topic;
	@JsonProperty("Action")
	private String action;
	@JsonProperty("Division")
	private String division;
	@JsonProperty("Key")
	private String key;
	@JsonProperty("ExactOnlineEndpoint")
	private String exactOnlineEndpoint;
	@JsonProperty("EventCreatedOn")
	private String eventCreatedOn;
	@JsonProperty("ClientId")
	private String clientId;

	public WebHookContent(String topic, String action, String division, String key, String exactOnlineEndpoint,
			String eventCreatedOn, String clientId) {
		this.topic = topic;
		this.action = action;
		this.division = division;
		this.key = key;
		this.exactOnlineEndpoint = exactOnlineEndpoint;
		this.eventCreatedOn = eventCreatedOn;
		this.clientId = clientId;
	}

	@JsonProperty("Topic")
	public String getTopic() {
		return topic;
	}

	@JsonProperty("Topic")
	public void setTopic(String topic) {
		this.topic = topic;
	}

	@JsonProperty("Action")
	public String getAction() {
		return action;
	}

	@JsonProperty("Action")
	public void setAction(String action) {
		this.action = action;
	}

	@JsonProperty("Division")
	public String getDivision() {
		return division;
	}

	@JsonProperty("Division")
	public void setDivision(String division) {
		this.division = division;
	}

	@JsonProperty("Key")
	public String getKey() {
		return key;
	}

	@JsonProperty("Key")
	public void setKey(String key) {
		this.key = key;
	}

	@JsonProperty("ExactOnlineEndpoint")
	public String getExactOnlineEndpoint() {
		return exactOnlineEndpoint;
	}

	@JsonProperty("ExactOnlineEndpoint")
	public void setExactOnlineEndpoint(String exactOnlineEndpoint) {
		this.exactOnlineEndpoint = exactOnlineEndpoint;
	}

	@JsonProperty("EventCreatedOn")
	public String getEventCreatedOn() {
		return eventCreatedOn;
	}

	@JsonProperty("EventCreatedOn")
	public void setEventCreatedOn(String eventCreatedOn) {
		this.eventCreatedOn = eventCreatedOn;
	}

	@JsonProperty("ClientId")
	public String getClientId() {
		return clientId;
	}

	@JsonProperty("ClientId")
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}