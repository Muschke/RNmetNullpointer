package be.hi10.realnutrition.pojos.exactonline.custom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResult {

    @JsonProperty("Barcode")
    private String ean;

    @JsonProperty("Stock")
    private int stock;

    public ProductResult()
    {}

    public ProductResult(String ean, int stock) {
        this.ean = ean;
        this.stock = stock;
    }

    @JsonProperty("Barcode")
    public String getEan() {
        return ean;
    }

    @JsonProperty("Stock")
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
