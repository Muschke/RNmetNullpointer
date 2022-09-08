package be.hi10.realnutrition.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import be.hi10.realnutrition.beans.SynchronizedMethodsBean;
import be.hi10.realnutrition.pojos.exactonline.webhooknotification.WebHookNotificationResponse;

@RestController
public class WebhookController {

	@Autowired
	SynchronizedMethodsBean synchronizedMethodsBean;

	/*
	 * ************************* WEBHOOK SUBSCRIPTION ******************************
	 * { "d": { "__metadata": { "uri":
	 * "https://start.exactonline.be/api/v1/354283/webhooks/WebhookSubscriptions(guid'd12096de-86d0-4ac1-bcde-68aa62cd1e34')",
	 * "type": "Exact.Web.Api.Models.Webhooks.WebhookSubscription" }, "ClientID":
	 * null, "Created": "/Date(1567606725060)/", "Creator":
	 * "4a8e8a11-cc00-4edb-ae84-aa0ffe9fc8b8", "CreatorFullName": "Bart Mestdagh",
	 * "Description": "Bol_Lightspeed_CCV_Stock_Update", "Division": 354283, "ID":
	 * "d12096de-86d0-4ac1-bcde-68aa62cd1e34", "CallbackURL":
	 * "https://realnutrition.nl/WebHooks", "Topic": "StockPositions" } }
	 */

	@RequestMapping(value = "/WebHooks", method = RequestMethod.POST)
	@ResponseBody
	public void webhook(@RequestBody(required = false) WebHookNotificationResponse webHookNotificationResponse) {
		if (webHookNotificationResponse != null) {
			synchronizedMethodsBean.exactWebHookSynchronized(webHookNotificationResponse);
		}
	}
}
