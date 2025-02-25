package org.example.paymenttservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.TestPropertySource;


@TestPropertySource(properties = {
		"eureka.client.enabled=false",
		"spring.cloud.config.enabled=false"
})
class PaymenttServiceApplicationTests {

	@Test
	void contextLoads() {
	}
}