package be.hi10.realnutrition.webhooks;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import be.hi10.realnutrition.entities.RetryItem;
import be.hi10.realnutrition.enums.Website;

class ExactWebHookTest {

	RetryItem retryItem1, retryItem2;

	ExactWebHook exactWebHook = new ExactWebHook();
	
	@Test
	void retryItem1IsNotAProduct() {
		retryItem1 = new RetryItem("7424904586560", "testItemId1", 10, Website.EXACT.toString());
		assertThat(exactWebHook.isAProduct(retryItem1)).isFalse();
	}

	@Test
	void retryItem2IsAProduct() {
		retryItem2 = new RetryItem("4562845478545", "testItemId2", 10, Website.BOL.toString());
		assertThat(exactWebHook.isAProduct(retryItem2)).isTrue();
	}
}
