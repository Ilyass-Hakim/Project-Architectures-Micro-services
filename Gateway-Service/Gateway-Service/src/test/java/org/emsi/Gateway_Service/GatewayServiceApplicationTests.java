package org.emsi.Gateway_Service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Les tests tournent sans Eureka
@SpringBootTest(properties = "DISCOVERY_ENABLED=false")
class GatewayServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
