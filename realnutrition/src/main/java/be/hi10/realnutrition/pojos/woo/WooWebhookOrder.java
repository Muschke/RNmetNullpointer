package be.hi10.realnutrition.pojos.woo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WooWebhookOrder {
    private String name;

    @JsonProperty("sku")
    private String ean;

    private int quantity;

    public WooWebhookOrder(String name, String ean, int quantity) {
        this.name = name;
        this.ean = ean;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("sku")
    public String getEan() {
        return ean;
    }
    @JsonProperty("sku")
    public void setEan(String ean) {
        this.ean = ean;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
