package com.gatelab.microservice.bookbuilder.core;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import com.gatelab.microservice.bookbuilder.core.controller.CoreController;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CoreControllerTest {
	@Test
	public void t() {
		
		CoreController coreController = new CoreController();
		assertEquals("Hello World",coreController.gethelloWorld().getBody());
	}
}
