package com.gatelab.microservice.bookbuilder.core;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import com.gatelab.microservice.bookbuilder.core.bookbuilderconst.BookBuilderConst;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BookBuilderConstTest {
	
	BookBuilderConst bc; 
	
	 public BookBuilderConstTest() {
		// TODO Auto-generated constructor stub
		bc = new BookBuilderConst();
	}
	
	@Test
	public void dateFormat() {
		assertEquals("yyyyMMdd HH:mm:ss",BookBuilderConst.getDateformat());
	}
	
	@Test
	public void  DefaultStringEndDate() {
		assertEquals("99991231 00:00:00",BookBuilderConst.getDefaultstringenddate());
	}
}