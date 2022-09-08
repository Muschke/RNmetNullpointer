package be.hi10.realnutrition.beans;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BlockingBucket;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.TokensInheritanceStrategy;

@Component
public class RateLimitBean {
	private Bandwidth limitPerMinute;
	private Bandwidth limitPerDay;
	private Bucket bucket;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RateLimitBean.class);

	public RateLimitBean() {
		limitPerMinute = Bandwidth.classic(60, Refill.intervally(60, Duration.ofMinutes(1)));
		limitPerDay = Bandwidth.classic(5000, Refill.intervally(5000, Duration.ofDays(1)));
		bucket = Bucket.builder()
				.addLimit(limitPerMinute).addLimit(limitPerDay).build();
	}
	
	public BlockingBucket getBucket() {
		return bucket.asBlocking();
	}
	
	public void updateBucketLimits(HttpHeaders responseHeaders) {
		List<String> dailyLimit = responseHeaders.get("X-RateLimit-Limit");
		List<String> dailyLimitRemaining = responseHeaders.get("X-RateLimit-Remaining");
		List<String> minutelyLimit = responseHeaders.get("X-RateLimit-Minutely-Limit");
		List<String> minutelyLimitRemaining = responseHeaders.get("X-RateLimit-Minutely-Remaining");
		Long dailyLimitValue = null;
		Long dailyLimitRemainingValue = null;
		Long minutelyLimitValue = null;
		Long minutelyLimitRemainingValue = null;
		if(dailyLimit != null) {
			if (dailyLimit.size() == 1) {
				try {
					dailyLimitValue = Long.parseLong(dailyLimit.get(0));
				} catch(NumberFormatException nfe) {
					//TODO
				}
			}
		}
		if(dailyLimitRemaining != null) {
			if (dailyLimitRemaining.size() == 1) {
				try {
					dailyLimitRemainingValue = Long.parseLong(dailyLimitRemaining.get(0));
				} catch(NumberFormatException nfe) {
					//TODO
				}
			}
		}
		if(minutelyLimit != null) {
			if (minutelyLimit.size() == 1) {
				try {
					minutelyLimitValue = Long.parseLong(minutelyLimit.get(0));
				} catch(NumberFormatException nfe) {
					//TODO
				}
			}
		}
		if(minutelyLimitRemaining != null) {
			if (minutelyLimitRemaining.size() == 1) {
				try {
					minutelyLimitRemainingValue = Long.parseLong(minutelyLimitRemaining.get(0));
				} catch(NumberFormatException nfe) {
					//TODO
				}
			}
		}
		
		updateBucketLimits(
				dailyLimitValue, 
				dailyLimitRemainingValue,
				minutelyLimitValue, 
				minutelyLimitRemainingValue);
	}

	private void updateBucketLimits(Long dailyLimitValue, Long dailyLimitRemainingValue,
			Long minutelyLimitValue, Long minutelyLimitRemainingValue) {
		synchronized(bucket) {
			boolean updateRequired = false;
			if(dailyLimitRemainingValue != null && dailyLimitRemainingValue <= 5) {
				updateRequired = true;
				LOGGER.info("Remaining daily tokens almost depleted, finetuning rate limit accuracy");
				if(dailyLimitValue != null && dailyLimitRemainingValue != null) {
					Instant instant = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS).toInstant();
					limitPerDay = Bandwidth.classic(dailyLimitValue, Refill.intervallyAligned(dailyLimitValue, Duration.ofDays(1), instant, false)).withInitialTokens(dailyLimitRemainingValue);
				}
			}
			if(minutelyLimitRemainingValue != null && minutelyLimitRemainingValue <= 5) {
				updateRequired = true;
				LOGGER.info("Remaining minutely tokens almost depleted, finetuning rate limit accuracy");
				if(minutelyLimitValue != null && minutelyLimitRemainingValue != null) {
					Instant instant = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES).plus(1, ChronoUnit.MINUTES).toInstant();
					limitPerMinute = Bandwidth.classic(minutelyLimitValue, Refill.intervallyAligned(minutelyLimitValue, Duration.ofMinutes(1), instant, false)).withInitialTokens(minutelyLimitRemainingValue);
				}
			}	
			if(updateRequired) {
				BucketConfiguration bc = BucketConfiguration.builder().addLimit(limitPerMinute).addLimit(limitPerDay).build();
				bucket.replaceConfiguration(bc, TokensInheritanceStrategy.RESET);
			}
		}
	}
}
