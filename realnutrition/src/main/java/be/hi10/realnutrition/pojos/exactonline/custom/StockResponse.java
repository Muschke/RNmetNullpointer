package be.hi10.realnutrition.pojos.exactonline.custom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StockResponse {

    @JsonProperty("d")
    private ProductList content;

    @JsonProperty("d")
    public ProductList getContent() {
        return content;
    }

    @JsonProperty("d")
    public void setContent(ProductList content) {
        this.content = content;
    }
}
