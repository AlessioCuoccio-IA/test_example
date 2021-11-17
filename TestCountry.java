package com.gatelab.microservice.bookbuilder.core;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.Country;
import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.Desk;
import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.Region;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;
import com.gatelab.microservices.bookbulder.utils.TestsUtility;
import com.vimalselvam.graphql.GraphqlTemplate;
import okhttp3.Response;


@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestCountry {
	
	private TestManager<Region> testManagerRegion;
	private TestManager<Desk> testManagerDesk;
	
	private static LocalRepositoryTestArray<Country> testArrayHistory = new LocalRepositoryTestArray<>();
	private static List<Integer> revisonNumber = new ArrayList<>(); 
	
	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "Country/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Country/";

	private static final String MUTATIONS_PATH_REGION = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
			+ "Region/";
	private static final String MUTATIONS_PATH_DESK = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
			+ "Desk/";
	
	private static final int LOCAL_REPOSITORY_SIZE = 2;
	public static final String ID_FIELD = "id";
	public static final String ISO_CODE_FIELD = "iso_code";
	public static final String DESCRIPTION_FIELD = "description";
	public static final String ISSUENET_MAPPING_FIELD = "issuenet_mapping";
	public static final String COUNTRY_FK_ID_FIELD = "id";
	private static final String REVISION_NUMBER_FILD = "revisionNumber"; 
	

	private static final String REGION_FIELD = "region";
	private static final String ABBREVIATION_FIELD = "abbreviation";

	private static final String DESK_FIELD = "desk";
	private static final String SHORT_NAME_FIELD = "shortName";
	
	private TestManager<Country> testManager; 
	
	@LocalServerPort
	private int port;
	
	private static LocalRepositoryTestArray<Country> testArray = new LocalRepositoryTestArray<>();
	

	
	//MUTATIONS 
	@Test
	@Order(1)
	void add() throws IOException, ParseException {
		
		// Region
		testManagerRegion = new TestManager<Region>("http://localhost:" + port + "/graphql");
		String region = "REGION MOD2";
		String abbreviation = "RGM2";

		ObjectNode variables = new ObjectMapper().createObjectNode();
		ObjectNode inputRegion = variables.putObject("region");
		inputRegion.put(REGION_FIELD, region);
		inputRegion.put(ABBREVIATION_FIELD, abbreviation);
		
		
		Response response = testManagerRegion.request(MUTATIONS_PATH_REGION + "addRegion.graphql", variables);
		JsonNode jsonNode = testManagerRegion.checkResponse(response, "addRegion");

		Assert.assertEquals(region, jsonNode.get(REGION_FIELD).asText());
		Assert.assertEquals(abbreviation, jsonNode.get(ABBREVIATION_FIELD).asText());

		Region regionCreated = new Region();

		regionCreated.setId(jsonNode.get(ID_FIELD).asLong());
		regionCreated.setAbbreviation(abbreviation);
		regionCreated.setRegion(region);

		// Desk
		testManagerDesk = new TestManager<Desk>("http://localhost:" + port + "/graphql");
		String desk = "DeskMod2";
		String shortName = "dsn2";

		variables = new ObjectMapper().createObjectNode();
		ObjectNode inputDesk = variables.putObject("desk");
		inputDesk.put(DESK_FIELD, desk);
		inputDesk.put(SHORT_NAME_FIELD, shortName);

		response = testManagerDesk.request(MUTATIONS_PATH_DESK + "addDesk.graphql", variables);
		jsonNode = testManagerDesk.checkResponse(response, "addDesk");

		Assert.assertEquals(desk, jsonNode.get(DESK_FIELD).asText());
		Assert.assertEquals(shortName, jsonNode.get(SHORT_NAME_FIELD).asText());

		Desk deskCreated = new Desk();
		deskCreated.setId(jsonNode.get(ID_FIELD).asLong());
		deskCreated.setDesk(desk);
		deskCreated.setShortName(shortName); 
		
		
		testManager = new TestManager<Country>("http://localhost:" + port + "/graphql"); 
		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
			
			String isoCode = "ISO"+ i;
			String description = "DESCRIPTION "+ i ;
			String issueNetMapping = "ISSUENET MAPPING " +  i;
			
		    variables = new ObjectMapper().createObjectNode();
			ObjectNode input = variables.putObject("country"); 
			input.put(ISO_CODE_FIELD ,isoCode);
			input.put(DESCRIPTION_FIELD, description); 
			input.put(ISSUENET_MAPPING_FIELD, issueNetMapping); 
			
			ObjectNode regionNodeObject = input.putObject("region");
			regionNodeObject.put(ID_FIELD, regionCreated.getId());

			ObjectNode deskNodeObject = input.putObject("desk");
			deskNodeObject.put(ID_FIELD, deskCreated.getId());

			
			response = testManager.request(TestCountry.MUTATIONS_PATH + "addCountry.graphql", variables); 
			jsonNode = testManager.checkResponse(response, "addCountry"); 
		
			this.assertsNewRecord(isoCode, description, issueNetMapping,jsonNode);
			Country country = this.addToLocalList(jsonNode);
		}
	}
	
	@Test
	@Order(2)
	void getByID() throws IOException, ParseException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Country country = testArray.getFirstElement(); 
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,country.getId());
		
		Response response = testManager.request(QUERIES_PATH + "getCountryById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getCountryById");
		
		Assert.assertEquals(country.getId(),jsonNode.get(ID_FIELD).asLong());
		
	}
	
	
	@Test
	@Order(3)
	void getAll() throws IOException, ParseException {
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		ObjectNode variables = new ObjectMapper().createObjectNode();
		Response response = testManager.request(QUERIES_PATH + "getAllCountries.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getAllCountries");
	
	}
	

		
	@Test
	@Order(4)
	void getCountriesUndefinedRegion() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		 
		//TEST GET BY DATE
		InputStream stream = this.getClass().getResourceAsStream(QUERIES_PATH + "getCountriesUndefinedRegion.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		String payload = GraphqlTemplate.parseGraphql(stream, node);
		
		Response getDataResponse = TestsUtility.executeGraphqlMethod(payload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,getDataResponse.code());

		String jsonDataGetByDate = getDataResponse.body().string();
		JsonNode jsonNodeGetByDate = new ObjectMapper().readTree(jsonDataGetByDate).get("data").get("getCountriesUndefinedRegion"); 
		
	}
	
	@Test
	@Order(5)
	void getCountriesUndefinedDesk() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		 
		//TEST GET BY DATE
		InputStream stream = this.getClass().getResourceAsStream(QUERIES_PATH + "getCountriesUndefinedDesk.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		String payload = GraphqlTemplate.parseGraphql(stream, node);
		
		Response getDataResponse = TestsUtility.executeGraphqlMethod(payload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,getDataResponse.code());

		String jsonDataGetByDate = getDataResponse.body().string();
		JsonNode jsonNodeGetByDate = new ObjectMapper().readTree(jsonDataGetByDate).get("data").get("getCountriesUndefinedDesk"); 		
	 
	}
	
	
	@Test
	@Order(6)
	void updateById() throws IOException, ParseException {
	
		// Region
		testManagerRegion = new TestManager<Region>("http://localhost:" + port + "/graphql");
		String region = "UpdateMOD2";
		String abbreviation = "RGM2";

		ObjectNode variables = new ObjectMapper().createObjectNode();
		ObjectNode inputRegion = variables.putObject("region");
		inputRegion.put(REGION_FIELD, region);
		inputRegion.put(ABBREVIATION_FIELD, abbreviation);

		Response response = testManagerRegion.request(MUTATIONS_PATH_REGION + "addRegion.graphql", variables);
		JsonNode jsonNode = testManagerRegion.checkResponse(response, "addRegion");

		Assert.assertEquals(region, jsonNode.get(REGION_FIELD).asText());
		Assert.assertEquals(abbreviation, jsonNode.get(ABBREVIATION_FIELD).asText());

		Region regionCreated = new Region();

		regionCreated.setId(jsonNode.get(ID_FIELD).asLong());
		regionCreated.setAbbreviation(abbreviation);
		regionCreated.setRegion(region);

		// Desk
		testManagerDesk = new TestManager<Desk>("http://localhost:" + port + "/graphql");
		String desk = "UpdateDesk";
		String shortName = "dsn2";

		variables = new ObjectMapper().createObjectNode();
		ObjectNode inputDesk = variables.putObject("desk");
		inputDesk.put(DESK_FIELD, desk);
		inputDesk.put(SHORT_NAME_FIELD, shortName);

		response = testManagerDesk.request(MUTATIONS_PATH_DESK + "addDesk.graphql", variables);
		jsonNode = testManagerDesk.checkResponse(response, "addDesk");

		Assert.assertEquals(desk, jsonNode.get(DESK_FIELD).asText());
		Assert.assertEquals(shortName, jsonNode.get(SHORT_NAME_FIELD).asText());

		Desk deskCreated = new Desk();
		deskCreated.setId(jsonNode.get(ID_FIELD).asLong());
		deskCreated.setDesk(desk);
		deskCreated.setShortName(shortName); 
		
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Country country = testArray.getFirstElement();
		
		Country countryHistory = new Country();
		
		//UPDATE
		String isoCode = "ISO";
		String description = "DESCRIPTION UPDATE";
		String issueNetMapping = "ISSUENET MAPPING UPDATE";
		countryHistory.setId(country.getId());
		countryHistory.setIso_code(country.getIso_code()); 
		countryHistory.setDescription(country.getDescription()); 
		countryHistory.setIssuenet_mapping(country.getIssuenet_mapping()); 
		
		testArrayHistory.add(countryHistory); 
		
		country.setIso_code(isoCode);
		country.setDescription(description);
		country.setIssuenet_mapping(issueNetMapping);
		
		testArrayHistory.add(country); 
		
		ObjectNode input = new ObjectMapper().createObjectNode();
		input.put(ID_FIELD,country.getId()); 
		input.put(ISO_CODE_FIELD,isoCode); 
		input.put(DESCRIPTION_FIELD,description);
		input.put(ISSUENET_MAPPING_FIELD,issueNetMapping);
		
		
		ObjectNode regionNodeObject = input.putObject("region");
		regionNodeObject.put(ID_FIELD, regionCreated.getId());

		ObjectNode deskNodeObject = input.putObject("desk");
		deskNodeObject.put(ID_FIELD, deskCreated.getId());

		response = testManager.request(MUTATIONS_PATH + "changeCountryById.graphql", input); 
		jsonNode = testManager.checkResponse(response, "changeCountryById"); 

		Assert.assertEquals(country.getDescription(),jsonNode.get(DESCRIPTION_FIELD).asText());
	}
	
	@Test
	@Order(7)
	void getCountryHistory() throws IOException, ParseException {
		int i = 0;
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Country country = testArray.getFirstElement(); 
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,country.getId());
		Response response = testManager.request(QUERIES_PATH + "getCountryHistoryById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getCountryHistoryById");
		for(JsonNode object: jsonNode) {
			
			Country countryObject = testArrayHistory.getElement(i); 
			i++; 
			Assert.assertEquals(countryObject.getId(), object.get(ID_FIELD).asLong());
			revisonNumber.add(object.get(REVISION_NUMBER_FILD).asInt()); 
		}		
	}
	
	@Test
	@Order(8)
	void recoverCountry() throws IOException, ParseException {
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Country country  = testArrayHistory.getElement(0);
		
		
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,country.getId());
		variables.put(REVISION_NUMBER_FILD,revisonNumber.get(0));
		
		Response response = testManager.request(MUTATIONS_PATH + "recoverCountryByRevisionNumber.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "recoverCountryByRevisionNumber");
		Assert.assertEquals(country.getId(), jsonNode.get(ID_FIELD).asLong());
		
	}
	
	@Test
	@Order(9)
	void delete() throws IOException, ParseException {
		
		String graphqlUri = "http://localhost:" + port + "/graphql";
		Country country = testArray.getFirstElement(); 
		long id = country.getId();
		
		//TEST DELETE 
		InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "deleteCountryById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

		String jsonDataChange = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("deleteCountryById"); 
		Assert.assertEquals(id, jsonNodeChange.get(ID_FIELD).asInt());	
	}
	

  	public static void assertsNewRecord(String isoCode, String description,String issueNetMapping,JsonNode jsonNodeChange) throws ParseException {
  		
		Assert.assertEquals(isoCode,jsonNodeChange.get(ISO_CODE_FIELD).asText());
		Assert.assertEquals(description,jsonNodeChange.get(DESCRIPTION_FIELD).asText());
		Assert.assertEquals(issueNetMapping,jsonNodeChange.get(ISSUENET_MAPPING_FIELD).asText());
	
  	}
	 
  	
  	public static Country addToLocalList(JsonNode node) throws ParseException { 

		Country country = new Country();
		country.setId(node.get(ID_FIELD).asLong());
		country.setIso_code(node.get(ISO_CODE_FIELD).asText());
		country.setDescription(node.get(DESCRIPTION_FIELD).asText());
		country.setIssuenet_mapping(node.get(ISSUENET_MAPPING_FIELD).asText());
		testArray.add(country);
		return country;
		
	}

}