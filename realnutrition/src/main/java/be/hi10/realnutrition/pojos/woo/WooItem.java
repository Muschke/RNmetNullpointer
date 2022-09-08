package be.hi10.realnutrition.pojos.woo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "items"
})
public class WooItem {

    @JsonProperty("id")
    private String id;

    @JsonProperty("sku")
    private String ean;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("sku")
    public String getEan() {
        return ean;
    }

    @JsonProperty("sku")
    public void setEan(String ean) {
        this.ean = ean;
    }

}