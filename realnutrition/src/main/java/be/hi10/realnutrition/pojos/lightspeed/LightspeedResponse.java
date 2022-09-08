
package be.hi10.realnutrition.pojos.lightspeed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "variants"
})
public class LightspeedResponse {

    @JsonProperty("variants")
    private List<Variant> variants = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("variants")
    public List<Variant> getVariants() {
        return variants;
    }

    @JsonProperty("variants")
    public void setVariants(List<Variant> variants) {
        this.variants = variants;
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
