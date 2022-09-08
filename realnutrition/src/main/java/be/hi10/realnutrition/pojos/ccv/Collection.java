
package be.hi10.realnutrition.pojos.ccv;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "product_id",
    "product_number",
    "ean_number",
    "active"
})
public class Collection {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("product_id")
    private Integer productId;
    @JsonProperty("product_number")
    private String productNumber;
    @JsonProperty("ean_number")
    private String eanNumber;
    @JsonProperty("active")
    private Boolean active;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("product_id")
    public Integer getProductId() {
        return productId;
    }

    @JsonProperty("product_id")
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @JsonProperty("product_number")
    public String getProductNumber() {
        return productNumber;
    }

    @JsonProperty("product_number")
    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    @JsonProperty("ean_number")
    public String getEanNumber() {
        return eanNumber;
    }

    @JsonProperty("ean_number")
    public void setEanNumber(String eanNumber) {
        this.eanNumber = eanNumber;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
