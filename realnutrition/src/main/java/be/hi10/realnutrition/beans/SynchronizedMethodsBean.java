package be.hi10.realnutrition.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import be.hi10.realnutrition.exceptions.ApiException;
import be.hi10.realnutrition.exceptions.RefreshTokenException;
import be.hi10.realnutrition.pojos.exactonline.webhooknotification.WebHookNotificationResponse;
import be.hi10.realnutrition.retryprocedure.ScheduledRetryProcedure;
import be.hi10.realnutrition.webhooks.ExactWebHook;

@Configuration
@EnableScheduling
@EnableAutoConfiguration
public class SynchronizedMethodsBean {

	// Logger
	private final static Logger LOGGER = LoggerFactory.getLogger(SynchronizedMethodsBean.class);

	// Lock
	private static final Object LOCK = new Object();

	// General
	private static boolean runRetryProcedure = true;
	private static boolean retryProcedureRanWithoutErrors = true;

	@Autowired
	ScheduledRetryProcedure scheduledRetryProcedure;

	@Autowired
	ExactWebHook exactWebHook;

//	@Autowired
//	ScheduledWebHookModule scheduledWebHookModule;

	public void exactWebHookSynchronized(WebHookNotificationResponse webHookNotificationResponse) {
		synchronized (LOCK) {
			LOGGER.info("webHook received");
			try {
				exactWebHook.handleNotification(webHookNotificationResponse);
			} catch (ApiException e) {
				e.printStackTrace();
			}
			LOGGER.info("webHook handled");
			setRunRetryProcedure(true);
		}
	}

	@Scheduled(fixedDelayString = "${scheduledRetryProcedure.delay}")
	public void retryProcedureSynchronized() throws RefreshTokenException {
		synchronized (LOCK) {

			if (runRetryProcedure) {

				LOGGER.info("RetryProcedure started");
				scheduledRetryProcedure.retryProcedure();
				LOGGER.info("RetryProcedure finished");

				if (retryProcedureRanWithoutErrors) {
					setRunRetryProcedure(false);
				}
				setRetryProcedureRanWithoutErrors(true);
			} else {
				LOGGER.info(
						"Previous retryProcedure ran without errors and there was no Exact-notification received between the previous retry procedure and this one");
			}
		}
	}

	public static void setRetryProcedureRanWithoutErrors(boolean retryProcedureRanWithoutErrors) {
		SynchronizedMethodsBean.retryProcedureRanWithoutErrors = retryProcedureRanWithoutErrors;
	}

	public static void setRunRetryProcedure(boolean runRetryProcedure) {
		SynchronizedMethodsBean.runRetryProcedure = runRetryProcedure;
	}

	public static boolean isRunRetryProcedure() {
		return runRetryProcedure;
	}

	public static boolean isRetryProcedureRanWithoutErrors() {
		return retryProcedureRanWithoutErrors;
	}

	/*
	 * To activate this method, put exactWebHookSynchronized in comment and
	 * deactivate the WebHookController
	 *
	 * @Scheduled(fixedDelayString = "${scheduledWebHookModule.delay}") public void
	 * scheduledWebHookModuleSynchronized() { synchronized (LOCK) {
	 * scheduledWebHookModule.checkForExactProjectedstockUpdatesScheduled(); } }
	 */

}
