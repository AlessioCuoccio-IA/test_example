package com.gatelab.microservice.bookbuilder.core;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Assert;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gatelab.microservice.bookbuilder.core.persistence.model.dealTranche.rating.Rating;
import com.gatelab.microservice.bookbuilder.core.persistence.model.dealTranche.rating.RatingAgency;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;

import okhttp3.Response;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestRatingAgencyMutationAndQuery.class,
})
class TestRatingMutationAndQuery {
	
	@LocalServerPort
	private int port;
	
	private TestManager<Rating> testManager;
	private TestManager<RatingAgency> testManagerRatingAgency;
	private static LocalRepositoryTestArray<Rating> testArray = new LocalRepositoryTestArray<>();

	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "DealTranche/Rating/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "DealTranche/Rating/";
	private static final String MUTATIONS_PATH_RATING_AGENCY = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "DealTranche/RatingAgency/";
	
	private static final String ID_FIELD = "id";
	private static final String SHORT_NAME_FIELD = "shortName";
	private static final String LONG_NAME_FIELD = "longName";
	private static final String IS_DEFAULT_FIELD = "def";
	private static final String TYPE_FIELD = "type";
	
	
	private static final int LOCAL_REPOSITORY_SIZE = 100;
	RatingAgency ratingAgency = new RatingAgency();
	@Test
	@Order(1)
	void addRatingAgency() throws IOException, ParseException {
		
		testManagerRatingAgency = new TestManager<RatingAgency>("http://localhost:" + port + "/graphql"); 
			
		String shortName = "76short"; 
		String longName  = "654longName"; 
		
		ObjectNode variables = new ObjectMapper().createObjectNode();
		ObjectNode input = variables.putObject("ratingAgency");
		input.put(LONG_NAME_FIELD, longName);
		input.put(SHORT_NAME_FIELD, shortName); 
		

		Response response = testManagerRatingAgency.request(MUTATIONS_PATH_RATING_AGENCY + "addRatingAgency.graphql", variables); 
		JsonNode jsonNode = testManagerRatingAgency.checkResponse(response, "addRatingAgency");
		
		Assert.assertEquals(shortName,jsonNode.get(SHORT_NAME_FIELD).asText());
		Assert.assertEquals(longName,jsonNode.get(LONG_NAME_FIELD).asText());
					
		 
		ratingAgency.setId(jsonNode.get(ID_FIELD).asLong()); 
		ratingAgency.setShortName(jsonNode.get(SHORT_NAME_FIELD).asText());
		ratingAgency.setLongName(jsonNode.get(LONG_NAME_FIELD).asText());
			 

		
	}
	
	@Test
	@Order(2)
	void add() throws IOException, ParseException {
		testManager = new TestManager<Rating>("http://localhost:" + port + "/graphql"); 
		
		for (int i = 1;i<LOCAL_REPOSITORY_SIZE;i++) {
			
			String longName = "AAA"+i; 
			String shortName = "A"+i; 
			boolean isDefault = (i%2==0)?true:false;
			String type ="type"+i; 
			ObjectNode variables = new ObjectMapper().createObjectNode();
			ObjectNode input = variables.putObject("rating");
			input.put(LONG_NAME_FIELD, longName);
			input.put(SHORT_NAME_FIELD, shortName);
			input.put(IS_DEFAULT_FIELD, isDefault); 
			input.put(TYPE_FIELD,type); 
		
			ArrayNode ratingAgencyArrayObject = input.putArray("ratingAgency"); 
			ObjectNode ratingObject = new ObjectMapper().createObjectNode();
			ratingObject.put(ID_FIELD,ratingAgency.getId()); 
			ratingAgencyArrayObject.add(ratingObject);
			
			Response response = testManager.request(MUTATIONS_PATH + "addRating.graphql", variables); 
			JsonNode jsonNode = testManager.checkResponse(response, "addRating");
			
			Assert.assertEquals(shortName,jsonNode.get(SHORT_NAME_FIELD).asText());
			Assert.assertEquals(longName,jsonNode.get(LONG_NAME_FIELD).asText());
			Assert.assertEquals(isDefault,jsonNode.get(IS_DEFAULT_FIELD).asBoolean());
			Assert.assertEquals(type,jsonNode.get(TYPE_FIELD).asText());
		
			Rating rating = new Rating(); 
			rating.setId(jsonNode.get(ID_FIELD).asLong()); 
			rating.setDef(jsonNode.get(IS_DEFAULT_FIELD).asBoolean()); 
			rating.setLongName(jsonNode.get(LONG_NAME_FIELD).asText()); 
			rating.setShortName(jsonNode.get(SHORT_NAME_FIELD).asText()); 
			rating.setType(jsonNode.get(TYPE_FIELD).asText()); 
			testArray.add(rating); 
			
		}
	}
	
	
	@Test
	@Order(3)
	void getByID() throws ParseException, IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Rating rating = testArray.getFirstElement();
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,rating.getId());
	
		Response response = testManager.request(QUERIES_PATH + "getRatingById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getRatingById");
		
		Assert.assertEquals(rating.getShortName(),jsonNode.get(SHORT_NAME_FIELD).asText());
		Assert.assertEquals(rating.getLongName(),jsonNode.get(LONG_NAME_FIELD).asText());
		Assert.assertEquals(rating.isDef(),jsonNode.get(IS_DEFAULT_FIELD).asBoolean());
		Assert.assertEquals(rating.getType(),jsonNode.get(TYPE_FIELD).asText());
		
	}
	
	
	@Test
	@Order(4)
	void getAll() throws IOException {
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		ObjectNode variables = new ObjectMapper().createObjectNode();
		Response response = testManager.request(QUERIES_PATH + "getAllRating.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getAllRating");
	}
	
	@Test
	@Order(5)
	void update() throws ParseException, IOException {

		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Rating rating = testArray.getFirstElement();
		
		String longName = "MOD AA"; 
		String shortName = "MAA"; 
		boolean isDefault = false;
		
		rating.setDef(isDefault); 
		rating.setLongName(longName); 
		rating.setLongName(longName); 
		
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,rating.getId());
		variables.put(LONG_NAME_FIELD,longName);
		variables.put(SHORT_NAME_FIELD, shortName); 
		
		ArrayNode ratingAgencyArrayObject = variables.putArray("ratingAgency"); 
		ObjectNode ratingObject = new ObjectMapper().createObjectNode();
		ratingObject.put(ID_FIELD,ratingAgency.getId()); 
		ratingAgencyArrayObject.add(ratingObject);
		
		
		Response response = testManager.request(MUTATIONS_PATH + "changeRatingById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "changeRatingById");
		
		Assert.assertEquals(rating.getId(),jsonNode.get(ID_FIELD).asLong());
		
		
		
	
	}
	
	@Test
	@Order(6)
	void delete() throws IOException, ParseException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Rating rating = testArray.getFirstElement();
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,rating.getId());

		Response response = testManager.request(MUTATIONS_PATH + "deleteRatingById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "deleteRatingById");
		
		Assert.assertEquals(rating.getId(),jsonNode.get(ID_FIELD).asLong());
	}
}
