package be.hi10.realnutrition.controllers;

import be.hi10.realnutrition.pojos.woo.WooWebhookResponse;
import be.hi10.realnutrition.services.custom.EmailService;
import org.springframework.web.bind.annotation.*;


@RestController
public class WooCommerceWebhookController {

    private EmailService emailService;

    public WooCommerceWebhookController(EmailService emailService) {
        this.emailService = emailService;
    }

    @RequestMapping(value = "/woocommerce", method = RequestMethod.POST)
    @ResponseBody
    public void webhook(@RequestBody(required = false) WooWebhookResponse response) {

            emailService.sendOrder(response.getLine_items());
    }
}
