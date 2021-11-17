package com.gatelab.microservice.bookbuilder.core;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CoreApplicationTests {

	@Test
	public void testSize1() {
		List<Integer> list = new ArrayList<>();
		list.add(42);
		list.add(-3);
		list.add(17);
		list.add(99);
		assertTrue(true);
	    assertTrue(list.contains(-3));

	}
	
	 


}
