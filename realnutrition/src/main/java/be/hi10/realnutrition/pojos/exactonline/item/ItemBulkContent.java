package be.hi10.realnutrition.pojos.exactonline.item;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemBulkContent {

	@JsonProperty("results")
	private List<Result> results = null;
	@JsonProperty("__next")
	private String next;

	@JsonProperty("results")
	public List<Result> getResults() {
		return results;
	}

	@JsonProperty("results")
	public void setResults(List<Result> results) {
		this.results = results;
	}

	@JsonProperty("__next")
	public String getNext() {
		return next;
	}

	@JsonProperty("__next")
	public void setNext(String next) {
		this.next = next;
	}
}