package com.gatelab.microservice.bookbuilder.core;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
public class TestRatingAgencyMutationAndQuery {

	@LocalServerPort
	private int port;
	private TestManager<RatingAgency> testManager;
	private static LocalRepositoryTestArray<RatingAgency> testArray = new LocalRepositoryTestArray<>();
	
	private TestManager<Rating> testManagerRating;
	
	private static LocalRepositoryTestArray<Rating> testArrayRating = new LocalRepositoryTestArray<>();
	private static final String MUTATIONS_PATH_RATING = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "DealTranche/Rating/";
	

	
	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "DealTranche/RatingAgency/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "DealTranche/RatingAgency/";
	
	
	private static final String ID_FIELD = "id";
	private static final String LONG_NAME = "longName"; 
	private static final String SHORT_NAME = "shortName";
	private static final String LONG_NAME_FIELD = "longName";
	private static final String SHORT_NAME_FIELD = "shortName";
	private static final String IS_DEFAULT_FIELD = "def";
	private static final String TYPE_FIELD = "type";

	
	private static final int LOCAL_REPOSITORY_SIZE = 100;
	
	
	@Test
	@Order(1)
	void addRating() throws IOException, ParseException {
		testManagerRating = new TestManager<Rating>("http://localhost:" + port + "/graphql"); 
		
		for (int i = 1;i<LOCAL_REPOSITORY_SIZE;i++) {
			
			String longName = "56AAAwe"+i; 
			String shortName = "65Awe"+i; 
			boolean isDefault = (i%2==0)?true:false;
			String type ="t44ypewe"+i; 
			ObjectNode variables = new ObjectMapper().createObjectNode();
			ObjectNode input = variables.putObject("rating");
			input.put(LONG_NAME_FIELD, longName);
			input.put(SHORT_NAME_FIELD, shortName);
			input.put(IS_DEFAULT_FIELD, isDefault); 
			input.put(TYPE_FIELD,type); 
		
			Response response = testManagerRating.request(MUTATIONS_PATH_RATING+ "addRating.graphql", variables); 
			JsonNode jsonNode = testManagerRating.checkResponse(response, "addRating");
			
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
			testArrayRating.add(rating); 
			
		}
	}

	@Test
	@Order(2)
	void add() throws IOException, ParseException {
		
		testManager = new TestManager<RatingAgency>("http://localhost:" + port + "/graphql"); 
		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {	
		
			String shortName = "short"+i; 
			String longName  = "longName"+i; 
			
			ObjectNode variables = new ObjectMapper().createObjectNode();
			ObjectNode input = variables.putObject("ratingAgency");
			input.put(LONG_NAME, longName);
			input.put(SHORT_NAME, shortName); 
			
			Rating rating = testArrayRating.getFirstElement();
			ArrayNode ratingArrayObject = input.putArray("rating"); 
			ObjectNode ratingObject = new ObjectMapper().createObjectNode();
			ratingObject.put(ID_FIELD,rating.getId()); 
			ratingArrayObject.add(ratingObject);
			
			Response response = testManager.request(MUTATIONS_PATH + "addRatingAgency.graphql", variables); 
			JsonNode jsonNode = testManager.checkResponse(response, "addRatingAgency");
			
			Assert.assertEquals(shortName,jsonNode.get(SHORT_NAME).asText());
			Assert.assertEquals(longName,jsonNode.get(LONG_NAME).asText());
						
			RatingAgency ratingAgency = new RatingAgency(); 
			ratingAgency.setId(jsonNode.get(ID_FIELD).asLong()); 
			ratingAgency.setShortName(jsonNode.get(SHORT_NAME).asText());
			ratingAgency.setLongName(jsonNode.get(LONG_NAME).asText());
			testArray.add(ratingAgency); 

		}
	}
	
	@Test
	@Order(3)
	void getByID() throws ParseException, IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		RatingAgency ratingAgency = testArray.getFirstElement();
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,ratingAgency.getId());
		
		Response response = testManager.request(QUERIES_PATH + "getRatingAgencyById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getRatingAgencyById");
		
		Assert.assertEquals(ratingAgency.getId(),jsonNode.get(ID_FIELD).asLong());
		Assert.assertEquals(ratingAgency.getLongName(),jsonNode.get(LONG_NAME).asText());
		Assert.assertEquals(ratingAgency.getShortName(),jsonNode.get(SHORT_NAME).asText());
	}
	
	
	@Test
	@Order(4)
	void getAll() throws IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		ObjectNode variables = new ObjectMapper().createObjectNode();
		Response response = testManager.request(QUERIES_PATH + "getAllRatingAgency.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getAllRatingAgencies");
	}
	
	@Test
	@Order(5)
	void update() throws ParseException, IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		RatingAgency ratingAgency = testArray.getFirstElement();
	
		
		String shortName = "ratwqingA3"; 
		String longName  = "rA3wq"; 
		
		ratingAgency.setShortName(shortName); 
		ratingAgency.setLongName(longName); 
		
		
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,ratingAgency.getId());
		variables.put(LONG_NAME, longName); 
		variables.put(SHORT_NAME, shortName); 
		
		Rating rating = testArrayRating.getFirstElement();
		ArrayNode ratingArrayObject = variables.putArray("rating"); 
		ObjectNode ratingObject = new ObjectMapper().createObjectNode();
		ratingObject.put(ID_FIELD,rating.getId()); 
		ratingArrayObject.add(ratingObject);
		
		Response response = testManager.request(MUTATIONS_PATH + "changeRatingAgencyById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "changeRatingAgencyById");
		
		Assert.assertEquals(ratingAgency.getId(),jsonNode.get(ID_FIELD).asLong());
		Assert.assertEquals(ratingAgency.getLongName(),jsonNode.get(LONG_NAME).asText());
		Assert.assertEquals(ratingAgency.getShortName(),jsonNode.get(SHORT_NAME).asText());
		
	}
	
	@Test
	@Order(6)
	void delete() throws IOException, ParseException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		RatingAgency ratingAgency = testArray.getFirstElement();
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,ratingAgency.getId());

		Response response = testManager.request(MUTATIONS_PATH + "deleteRatingAgencyById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "deleteRatingAgencyById");
		
		Assert.assertEquals(ratingAgency.getId(),jsonNode.get(ID_FIELD).asLong());
		Assert.assertEquals(ratingAgency.getLongName(),jsonNode.get(LONG_NAME).asText());
		Assert.assertEquals(ratingAgency.getShortName(),jsonNode.get(SHORT_NAME).asText());
		
	}
	
	public static List<RatingAgency> getCreatedRatingAgency(){
		return testArray.getAllElements(); 
	}
}
