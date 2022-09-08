package be.hi10.realnutrition.pojos.exactonline.custom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductList {

    @JsonProperty("results")
    private List<ProductResult> results = null;

    @JsonProperty("results")
    public List<ProductResult> getResults() {
        return results;
    }

    @JsonProperty("results")
    public void setResults(List<ProductResult> results) {
        this.results = results;
    }
}
