
package be.hi10.realnutrition.pojos.bol;

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
    "amount",
    "correctedStock",
    "managedByRetailer"
})
public class Stock {

    @JsonProperty("amount")
    private Integer amount;
    @JsonProperty("correctedStock")
    private Integer correctedStock;
    @JsonProperty("managedByRetailer")
    private Boolean managedByRetailer;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("amount")
    public Integer getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @JsonProperty("correctedStock")
    public Integer getCorrectedStock() {
        return correctedStock;
    }

    @JsonProperty("correctedStock")
    public void setCorrectedStock(Integer correctedStock) {
        this.correctedStock = correctedStock;
    }

    @JsonProperty("managedByRetailer")
    public Boolean getManagedByRetailer() {
        return managedByRetailer;
    }

    @JsonProperty("managedByRetailer")
    public void setManagedByRetailer(Boolean managedByRetailer) {
        this.managedByRetailer = managedByRetailer;
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
