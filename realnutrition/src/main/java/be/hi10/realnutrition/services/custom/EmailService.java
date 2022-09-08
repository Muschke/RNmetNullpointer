package be.hi10.realnutrition.services.custom;

import be.hi10.realnutrition.pojos.woo.WooWebhookOrder;

import java.util.List;

public interface EmailService {
    void sendOrder(List<WooWebhookOrder> orderList);
}
