package be.hi10.realnutrition.pojos.woo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class WooWebhookResponse {
    @JsonProperty("line_items")
    List<WooWebhookOrder> line_items;

    @JsonProperty("line_items")
    public List<WooWebhookOrder> getLine_items() {
        return line_items;
    }
    @JsonProperty("line_items")
    public void setLine_items(List<WooWebhookOrder> line_items) {
        this.line_items = line_items;
    }
}
