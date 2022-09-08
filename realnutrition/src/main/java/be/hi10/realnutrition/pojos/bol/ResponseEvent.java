
package be.hi10.realnutrition.pojos.bol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.hi10.realnutrition.util.BolCSVStatus;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "entityId",
    "eventType",
    "description",
    "status",
    "errorMessage",
    "createTimestamp",
    "links"
})
public class ResponseEvent {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("entityId")
    private String entityId;
    @JsonProperty("eventType")
    private String eventType;
    @JsonProperty("description")
    private String description;
    @JsonProperty("status")
    private BolCSVStatus status;
    @JsonProperty("errorMessage")
    private String errorMessage;
    @JsonProperty("createTimestamp")
    private String createTimestamp;
    @JsonProperty("links")
    private List<Link> links = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("entityId")
    public String getEntityId() {
        return entityId;
    }

    @JsonProperty("entityId")
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @JsonProperty("eventType")
    public String getEventType() {
        return eventType;
    }

    @JsonProperty("eventType")
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("status")
    public BolCSVStatus getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(BolCSVStatus status) {
        this.status = status;
    }

    @JsonProperty("errorMessage")
    public String getErrorMessage() {
        return errorMessage;
    }

    @JsonProperty("errorMessage")
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @JsonProperty("createTimestamp")
    public String getCreateTimestamp() {
        return createTimestamp;
    }

    @JsonProperty("createTimestamp")
    public void setCreateTimestamp(String createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    @JsonProperty("links")
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(List<Link> links) {
        this.links = links;
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
