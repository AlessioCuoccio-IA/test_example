package com.gatelab.microservice.bookbuilder.core;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SampleGraphqlErrorHandlingApplicationTests {

    @Test
    public void contextLoads() {
    	assertTrue(true);
    }

}