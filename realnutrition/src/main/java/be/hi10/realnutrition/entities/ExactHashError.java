package be.hi10.realnutrition.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The persistent class for the exact_hash_errors database table.
 * 
 */
@Entity
@Table(name = "exact_hash_errors")
@JsonPropertyOrder({ "Topic", "Action", "Division", "Key", "Endpoint", "EventCreatedOn" })
public class ExactHashError implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Long id;

	@JsonProperty("Action")
	private String action;

	@JsonProperty("Division")
	private String division;

	@JsonProperty("Endpoint")
	private String endpoint;

	@Column(name = "event_created_on")
	@JsonProperty("EventCreatedOn")
	private String eventCreatedOn;

	@Column(name = "hash_code")
	@JsonIgnore
	private String hashCode;

	@JsonProperty("Key")
	private String key;

	@JsonProperty("Topic")
	private String topic;

	public ExactHashError() {
	}

	public ExactHashError(String action, String division, String endpoint, String eventCreatedOn, String hashCode,
			String key, String topic) {
		this.action = action;
		this.division = division;
		this.endpoint = endpoint;
		this.eventCreatedOn = eventCreatedOn;
		this.hashCode = hashCode;
		this.key = key;
		this.topic = topic;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty("Action")
	public String getAction() {
		return this.action;
	}

	@JsonProperty("Action")
	public void setAction(String action) {
		this.action = action;
	}

	@JsonProperty("Division")
	public String getDivision() {
		return this.division;
	}

	@JsonProperty("Division")
	public void setDivision(String division) {
		this.division = division;
	}

	@JsonProperty("Endpoint")
	public String getEndpoint() {
		return this.endpoint;
	}

	@JsonProperty("Endpoint")
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	@JsonProperty("EventCreatedOn")
	public String getEventCreatedOn() {
		return this.eventCreatedOn;
	}

	@JsonProperty("EventCreatedOn")
	public void setEventCreatedOn(String eventCreatedOn) {
		this.eventCreatedOn = eventCreatedOn;
	}

	public String getHashCode() {
		return this.hashCode;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}

	@JsonProperty("Key")
	public String getKey() {
		return this.key;
	}

	@JsonProperty("Key")
	public void setKey(String key) {
		this.key = key;
	}

	@JsonProperty("Topic")
	public String getTopic() {
		return this.topic;
	}

	@JsonProperty("Topic")
	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventCreatedOn == null) ? 0 : eventCreatedOn.hashCode());
		result = prime * result + ((hashCode == null) ? 0 : hashCode.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExactHashError other = (ExactHashError) obj;
		if (eventCreatedOn == null) {
			if (other.eventCreatedOn != null)
				return false;
		} else if (!eventCreatedOn.equals(other.eventCreatedOn))
			return false;
		if (hashCode == null) {
			if (other.hashCode != null)
				return false;
		} else if (!hashCode.equals(other.hashCode))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
}