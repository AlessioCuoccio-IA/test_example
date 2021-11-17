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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestRegion {
	
	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "Region/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Region/";
	private static final String MUTATIONS_PATH_COUNTRY = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Country/";
	
	private static final int LOCAL_REPOSITORY_SIZE = 40;
	private static final String ID_FIELD = "id";
	private static final String REGION_FIELD = "region";
	private static final String ABBREVIATION_FIELD = "abbreviation";
	private static final String COUNTRIES_FIELD = "countries";
	private static final String ID_FIELD_A = "IdA";
	private static final String ID_FIELD_B = "IdB";
	private static final String REVISION_NUMBER_FILD = "revisionNumber";
	public static final String ISO_CODE_FIELD = "iso_code";
	public static final String DESCRIPTION_FIELD = "description";
	public static final String ISSUENET_MAPPING_FIELD = "issuenet_mapping";
	

	
	@LocalServerPort
	private int port;
	
	private TestManager<Country> testManager;
	private TestManager<Region> testManagerRegion;
	private static List<Integer> revisonNumber = new ArrayList<>(); 
	private static LocalRepositoryTestArray<Region> testArray = new LocalRepositoryTestArray<>();
	private static LocalRepositoryTestArray<Country> testArrayCountry = new LocalRepositoryTestArray<>();
	private static LocalRepositoryTestArray<Region> testArrayHistory = new LocalRepositoryTestArray<>();
	//MUTATIONS
	@Test
	@Order(1)
	void add() throws IOException, ParseException {
		
		
		String graphqlUri = "http://localhost:" + port + "/graphql";
		testManager = new TestManager<Country>("http://localhost:" + port + "/graphql"); 
		testManagerRegion = new TestManager<Region>("http://localhost:" + port + "/graphql");
		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
			
			
			String isoCode = "."+i;
			String description = "."+i;
			String issueNetMapping =  "."+i;
			
			ObjectNode variables = new ObjectMapper().createObjectNode();
			ObjectNode input = variables.putObject("country"); 
			input.put(ISO_CODE_FIELD ,isoCode);
			input.put(DESCRIPTION_FIELD, description); 
			input.put(ISSUENET_MAPPING_FIELD, issueNetMapping); 
			
			
			Response response = testManager.request(MUTATIONS_PATH_COUNTRY + "addCountry.graphql", variables); 
			JsonNode jsonNode = testManager.checkResponse(response, "addCountry"); 
		
			TestCountry.assertsNewRecord(isoCode, description, issueNetMapping,jsonNode);
			Country country = TestCountry.addToLocalList(jsonNode);
			testArrayCountry.add(country);
			
			
			// Region	
			String region = "REGION"+i;
			String abbreviation = "Ab"+i;

			variables = new ObjectMapper().createObjectNode();
			ObjectNode inputRegion = variables.putObject("region");
			inputRegion.put(REGION_FIELD, region);
			inputRegion.put(ABBREVIATION_FIELD, abbreviation);
			
		
			ArrayNode countryArrayObject = inputRegion.putArray("countries"); 
			ObjectNode countryObject = new ObjectMapper().createObjectNode();
			countryObject.put("id", country.getId());
			countryArrayObject.add(countryObject);
			
			response = testManagerRegion.request(MUTATIONS_PATH + "addRegion.graphql", variables);
			jsonNode = testManagerRegion.checkResponse(response, "addRegion");

			Assert.assertEquals(region, jsonNode.get(REGION_FIELD).asText());
			Assert.assertEquals(abbreviation, jsonNode.get(ABBREVIATION_FIELD).asText());

			Region regionCreated = new Region();

			regionCreated.setId(jsonNode.get(ID_FIELD).asLong());
			regionCreated.setAbbreviation(abbreviation);
			regionCreated.setRegion(region);
			testArray.add(regionCreated);
		}		
	}
	

	@Test
	@Order(2)
	public void getByID() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		Region region = testArray.getFirstElement();
		long id = region.getId();
		
		//TEST GET BY ID
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getRegionById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

		String jsonDataChange = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("getRegionById"); 
		System.out.print("GET"+jsonNodeChange); 
		Assert.assertEquals(jsonNodeChange.get(ID_FIELD).asInt(), id);
		assertsNewRecord(region.getRegion(), region.getAbbreviation(), jsonNodeChange);
		 
	}
	

	@Test
	@Order(3)
	public void getByName() throws IOException, ParseException {
	
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Region region = testArray.getFirstElement();
		ObjectNode input = new ObjectMapper().createObjectNode();
		input.put(REGION_FIELD,region.getRegion()); 
		
		Response response = testManager.request(QUERIES_PATH+ "getRegionByName.graphql", input); 
		JsonNode jsonNode = testManager.checkResponse(response, "getRegionByName");
		
		Assert.assertEquals(region.getRegion(),jsonNode.get(REGION_FIELD).asText());
	}
	
	

	@Test
	@Order(3)
	void getAll() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";

		InputStream streamChange = this.getClass().getResourceAsStream(this.QUERIES_PATH + "getRegionAll.graphql");

		ObjectNode var = new ObjectMapper().createObjectNode();
		String payload = GraphqlTemplate.parseGraphql(streamChange, var);
		Response response = TestsUtility.executeGraphqlMethod(payload, graphqlUri);

		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		String jsonData = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonData).get("data").get("getAllRegions"); 
		System.out.print("GET ALL"+jsonNodeChange); 
		
	}
	
	@Test
	@Order(4)
	void getAllFiltered() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";

		InputStream streamChange = this.getClass().getResourceAsStream(this.QUERIES_PATH + "getFilteredRegion.graphql");

		ObjectNode var = new ObjectMapper().createObjectNode();
		String payload = GraphqlTemplate.parseGraphql(streamChange, var);
		Response response = TestsUtility.executeGraphqlMethod(payload, graphqlUri);

		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		String jsonData = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonData).get("data").get("getFilteredRegions"); 
		System.out.print("GET ALL"+jsonNodeChange); 
		
	}
	
	
@Test
@Order(5)
void update() throws IOException, ParseException {
	
	testManagerRegion = new TestManager<Region>("http://localhost:" + port + "/graphql"); 
	Region region = testArray.getFirstElement();
	testArrayHistory.add(TestsUtility.copy(region, Region.class));
	String regionName = "REGION " + "modpro";
	String abbreviation = "RG " + "md";
	ObjectNode input = new ObjectMapper().createObjectNode(); 
	Region newRegion= new Region(); 
	newRegion.setId(region.getId()); 
	newRegion.setAbbreviation(abbreviation); 
	newRegion.setRegion(regionName); 
	
	testArrayHistory.add(TestsUtility.copy(newRegion, Region.class));
	
	// add a nested node object to our current node
	input.put(ID_FIELD, region.getId());
	input.put(REGION_FIELD, regionName);
	input.put(ABBREVIATION_FIELD,abbreviation);
	Country country = testArrayCountry.getElement(2);
	
	ArrayNode countryArrayObject = input.putArray("countries"); 
	ObjectNode countryObject = new ObjectMapper().createObjectNode();
	countryObject.put("id", country.getId());
	countryArrayObject.add(countryObject);
	
	Response response = testManagerRegion.request(MUTATIONS_PATH + "updateRegionById.graphql", input); 
	JsonNode jsonNode = testManagerRegion.checkResponse(response, "updateRegionById");
	
	Assert.assertEquals(regionName,jsonNode.get(REGION_FIELD).asText());
	Assert.assertEquals(abbreviation,jsonNode.get(ABBREVIATION_FIELD).asText());
	
}
	
	
@Test
@Order(6)
void merge() throws IOException, ParseException {
	testManagerRegion = new TestManager<Region>("http://localhost:" + port + "/graphql"); 
	List<Region> listRegion = testArray.getAllElements();
	Region regionA = listRegion.get(4); 
	Region regionB = listRegion.get(5);
	
	ObjectNode input = new ObjectMapper().createObjectNode(); 
	input.put("IdA", regionA.getId());
	input.put("IdB", regionB.getId());

	Response response = testManagerRegion.request(MUTATIONS_PATH + "mergeRegionById.graphql", input); 
	JsonNode jsonNode = testManagerRegion.checkResponse(response, "mergeRegionById");
	
	Assert.assertEquals(regionB.getRegion(),jsonNode.get(REGION_FIELD).asText());
	Assert.assertEquals(regionB.getAbbreviation(),jsonNode.get(ABBREVIATION_FIELD).asText());
	
}



@Test
@Order(7)
void getHistory() throws IOException, ParseException {

	testManager = new TestManager("http://localhost:" + port + "/graphql"); 
	Region region = testArray.getFirstElement();
	ObjectNode variables = new ObjectMapper().createObjectNode();
	variables.put(ID_FIELD,region.getId());

	
	Response response = testManager.request(QUERIES_PATH + "getRegionHistoryById.graphql", variables); 
	JsonNode jsonNode = testManager.checkResponse(response, "getRegionHistoryById");
	
	int i = 0; 
	for(JsonNode object: jsonNode) {

		String regionFild = object.get(REGION_FIELD).asText();
		String abbreviationFild =  object.get(ABBREVIATION_FIELD).asText();
		
		Region regionHistory = testArrayHistory.getElement(i); 
		i++; 
		
		Assert.assertEquals(regionHistory.getRegion(),regionFild);
		Assert.assertEquals(regionHistory.getAbbreviation(),abbreviationFild);
		revisonNumber.add(object.get(REVISION_NUMBER_FILD).asInt()); 
	}	
}


@Test
@Order(8)
void recoverPreviousVersion() throws IOException, ParseException {
	testManager = new TestManager("http://localhost:" + port + "/graphql"); 
	Region region = testArray.getFirstElement();
	Region regionToRecover = testArrayHistory.getFirstElement();
	regionToRecover.setId(region.getId()); 
	ObjectNode variables = new ObjectMapper().createObjectNode();
	variables.put(ID_FIELD,region.getId());
	variables.put(REVISION_NUMBER_FILD, revisonNumber.get(0));
	
	Response response = testManager.request(MUTATIONS_PATH + "recoverRegionByRevisionNumber.graphql", variables); 
	JsonNode jsonNode = testManager.checkResponse(response, "recoverRegionByRevisionNumber");
	
	Assert.assertEquals(regionToRecover.getRegion(),jsonNode.get(REGION_FIELD).asText());
	Assert.assertEquals(regionToRecover.getAbbreviation(),jsonNode.get(ABBREVIATION_FIELD).asText());
}


@Test
@Order(9)
void delete() throws IOException, ParseException {
	
	String graphqlUri = "http://localhost:" + port + "/graphql";
	
	Region region = testArray.getFirstElement();
	
	long id = region.getId();
	
	//TEST DELETE 
	InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "deleteRegionById.graphql");

	ObjectNode node = new ObjectMapper().createObjectNode();
	node.put(ID_FIELD,id); 
	String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
	
	Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
	Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

	String jsonDataChange = response.body().string();
	JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("deleteRegionById"); 
	Assert.assertEquals(id, jsonNodeChange.get(ID_FIELD).asInt());
	
}


  	
private void assertsNewRecord(String region, String abreviation,JsonNode jsonNodeChange) throws ParseException {
	Assert.assertEquals(region,jsonNodeChange.get(REGION_FIELD).asText());
	Assert.assertEquals(abreviation,jsonNodeChange.get(ABBREVIATION_FIELD).asText());
}
  	
  
	
	
	
	
	

}
