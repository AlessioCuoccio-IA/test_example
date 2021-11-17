package com.gatelab.microservice.bookbuilder.core;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.Country;
import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.Desk;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;
import com.gatelab.microservices.bookbulder.utils.TestsUtility;
import com.vimalselvam.graphql.GraphqlTemplate;

import okhttp3.Response;
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestDesk {

	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "Desk/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Desk/";
	private static final String MUTATIONS_PATH_COUNTRY = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Country/";

	private static final int LOCAL_REPOSITORY_SIZE = 100;
	private static final String ID_FIELD = "id";
	private static final String DESK_FIELD = "desk";
	private static final String SHORT_NAME_FIELD = "shortName";

	public static final String ISO_CODE_FIELD = "iso_code";
	public static final String DESCRIPTION_FIELD = "description";
	public static final String ISSUENET_MAPPING_FIELD = "issuenet_mapping";
	private TestManager<Desk> testManager;
	private TestManager<Country> testManagerCountry;
	
	@LocalServerPort
	private int port;
	private static LocalRepositoryTestArray<Desk> testArray = new LocalRepositoryTestArray<>();
	private static LocalRepositoryTestArray<Country> testArrayCountry = new LocalRepositoryTestArray<>();
	//MUTATIONS
	@Test
	@Order(1)
	void add() throws IOException, ParseException {

		String graphqlUri = "http://localhost:" + port + "/graphql";
		testManagerCountry = new TestManager<Country>("http://localhost:" + port + "/graphql"); 
		
		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
			
			String isoCode = ":"+i;
			String description = ":"+i;
			String issueNetMapping =  ":"+i;
			
			ObjectNode variables = new ObjectMapper().createObjectNode();
			ObjectNode input = variables.putObject("country"); 
			input.put(ISO_CODE_FIELD ,isoCode);
			input.put(DESCRIPTION_FIELD, description); 
			input.put(ISSUENET_MAPPING_FIELD, issueNetMapping); 
			
			
			Response response = testManagerCountry.request(MUTATIONS_PATH_COUNTRY + "addCountry.graphql", variables); 
			JsonNode jsonNode = testManagerCountry.checkResponse(response, "addCountry"); 
		
			TestCountry.assertsNewRecord(isoCode, description, issueNetMapping,jsonNode);
			Country country = TestCountry.addToLocalList(jsonNode);
			testArrayCountry.add(country);
			
			
			String desk = "DESK " + i;
			String shortName = "SN " + i;
			
			InputStream streamAdd = this.getClass().getResourceAsStream(MUTATIONS_PATH +"addDesk.graphql"); 
			String addPayload = createGraphqlAddTemplateMod(streamAdd, desk,shortName,country);
			response = TestsUtility.executeGraphqlMethod(addPayload, graphqlUri);
			Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
			String jsonDataAdd = response.body().string();
			JsonNode jsonNodeAdd = new ObjectMapper().readTree(jsonDataAdd).get("data").get("addDesk"); 
			assertsNewRecord(desk, shortName, jsonNodeAdd);
			addToLocalList(jsonNodeAdd);
		}
	}
	
	@Test
	@Order(2)
	public void getByID() throws IOException, ParseException {
	
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Desk desk = testArray.getFirstElement();
		ObjectNode input = new ObjectMapper().createObjectNode();
		input.put(ID_FIELD,desk.getId()); 
		
		Response response = testManager.request(QUERIES_PATH+ "getDeskById.graphql", input); 
		JsonNode jsonNode = testManager.checkResponse(response, "getDeskById");
		
		Assert.assertEquals(desk.getId(),jsonNode.get(ID_FIELD).asLong());
		Assert.assertEquals(desk.getDesk(),jsonNode.get(DESK_FIELD).asText());
		Assert.assertEquals(desk.getShortName(),jsonNode.get(SHORT_NAME_FIELD).asText());
		 
	}
	
	@Test
	@Order(3)
	public void getByName() throws IOException, ParseException {
	
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Desk desk = testArray.getFirstElement();
		ObjectNode input = new ObjectMapper().createObjectNode();
		input.put(DESK_FIELD,desk.getDesk()); 
		
		Response response = testManager.request(QUERIES_PATH+ "getDeskByName.graphql", input); 
		JsonNode jsonNode = testManager.checkResponse(response, "getDeskByName");
		
		Assert.assertEquals(desk.getId(),jsonNode.get(ID_FIELD).asLong());
		Assert.assertEquals(desk.getDesk(),jsonNode.get(DESK_FIELD).asText());
		Assert.assertEquals(desk.getShortName(),jsonNode.get(SHORT_NAME_FIELD).asText());
		 
	}
	
	
	//Restituisce tutti i record salavti a db, anche quelli "scaduti"
	@Test
	@Order(4)
	void getAll() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";

		//TEST GET ALL
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getDeskAll.graphql");

		ObjectNode var = new ObjectMapper().createObjectNode();
		String payload = GraphqlTemplate.parseGraphql(streamChange, var);
		Response response = TestsUtility.executeGraphqlMethod(payload, graphqlUri);

		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		String jsonData = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonData).get("data").get("getAllDesks"); 
		System.out.print("GET ALL"+jsonNodeChange); 
		
		
	}
	
	@Test
	@Order(5)
	void getAllFiltered() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";

		//TEST GET ALL
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getFilteredDesks.graphql");

		ObjectNode var = new ObjectMapper().createObjectNode();
		String payload = GraphqlTemplate.parseGraphql(streamChange, var);
		Response response = TestsUtility.executeGraphqlMethod(payload, graphqlUri);

		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		String jsonData = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonData).get("data").get("getFilteredDesks"); 
		System.out.print("GET ALL"+jsonNodeChange); 
		
		
	}
	@Test
	@Order(6)
	void updateByIdException() throws IOException, ParseException {
		
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Desk desk = testArray.getFirstElement(); 
		//UPDATE
		if(desk!=null) {

			String deskName = "DESK " + 10;
			String deskShortName = "SN " + 10;
			desk.setShortName(deskName);
			desk.setDesk(deskShortName); 
			
			ObjectNode input = new ObjectMapper().createObjectNode();
			input.put(ID_FIELD, desk.getId());
			input.put(DESK_FIELD, deskName);
			input.put(SHORT_NAME_FIELD, deskShortName);
			
			Response response = testManager.request(MUTATIONS_PATH + "updateDeskById.graphql", input); 
			Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

			String jsonDataChange = response.body().string();

			String error  = new ObjectMapper().readTree(jsonDataChange).get("errors").get(0).get("message").asText(); 
			Assert.assertEquals("The record already exist!", error);	
		}
	}
	
	@Test
	@Order(7)
	void updateById() throws IOException, ParseException {
		
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Desk desk = testArray.getFirstElement(); 
		//UPDATE
		if(desk!=null) {

			String deskUpdate = "DESK 100 UPDATE";
			String shortName = "SH1";
			desk.setShortName(shortName);
			desk.setDesk(deskUpdate); 
			
			ObjectNode input = new ObjectMapper().createObjectNode();
			input.put(ID_FIELD, desk.getId());
			input.put(DESK_FIELD, deskUpdate);
			input.put(SHORT_NAME_FIELD, shortName);
			Country country = testArrayCountry.getElement(2);
			ArrayNode countryArrayObject = input.putArray("countries"); 
			ObjectNode countryObject = new ObjectMapper().createObjectNode();
			countryObject.put("id", country.getId());
			countryArrayObject.add(countryObject);
			
			Response response = testManager.request(MUTATIONS_PATH + "updateDeskById.graphql", input); 
			JsonNode jsonNode = testManager.checkResponse(response, "changeDeskById");
		
			assertsNewRecord(deskUpdate, shortName, jsonNode);
		}
	}
	
	@Test
	@Order(8)
	void delete() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		Desk desk = testArray.getFirstElement();
		
		long id = desk.getId();
		
		//TEST DELETE 
		InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "deleteDeskById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

		String jsonDataChange = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("deleteDeskById"); 
		Assert.assertEquals(id, jsonNodeChange.get(ID_FIELD).asInt());			
	}
		
	@Test
	@Order(9)
	public void deleteException() throws IOException, ParseException {
	
		String graphqlUri = "http://localhost:" + port + "/graphql";		
		long id =1997;
		
		//TEST DELETE 	
		InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "deleteDeskById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

		String jsonDataChange = response.body().string();

		String error  = new ObjectMapper().readTree(jsonDataChange).get("errors").get(0).get("message").asText(); 
		Assert.assertEquals("Server Error!", error);			
		 
	}
	
	private static String createGraphqlAddTemplateMod(InputStream iStream, String desk, String shortName,Country country) throws IOException {
		// Create a variables to pass to the graphql query		
		ObjectNode variables = new ObjectMapper().createObjectNode();

		// add a nested node object to our current node
		ObjectNode input = variables.putObject("desk");
		input.put(DESK_FIELD,desk);
		input.put(SHORT_NAME_FIELD,shortName);

		ArrayNode countryArrayObject = input.putArray("countries"); 
		ObjectNode countryObject = new ObjectMapper().createObjectNode();
		countryObject.put("id", country.getId());
		countryArrayObject.add(countryObject);
		
		// Now parse the graphql file to a request payload string
		String graphqlPayload = GraphqlTemplate.parseGraphql(iStream, variables);
		return graphqlPayload;
	}
	
	
	  	
  	private void assertsNewRecord(String desk, String shortName,JsonNode jsonNodeChange) throws ParseException {
  		Assert.assertEquals(desk,jsonNodeChange.get(DESK_FIELD).asText());
		Assert.assertEquals(shortName,jsonNodeChange.get(SHORT_NAME_FIELD).asText());
  	}
  	
  	private void addToLocalList(JsonNode node) throws ParseException { 
  		
		Desk desk = new Desk();
		desk.setId(node.get(ID_FIELD).asLong());
		desk.setDesk(node.get(DESK_FIELD).asText());
		desk.setShortName(node.get(SHORT_NAME_FIELD).asText());
		testArray.add(desk);
	}
		  		
}
