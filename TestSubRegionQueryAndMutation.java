package com.gatelab.microservice.bookbuilder.core;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.SubRegion;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;
import com.gatelab.microservices.bookbulder.utils.TestsUtility;
import com.vimalselvam.graphql.GraphqlTemplate;

import okhttp3.Response;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestSubRegionQueryAndMutation {
	
	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "SalesRegion/SubRegion/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "SalesRegion/SubRegion/";
	private static final int LOCAL_REPOSITORY_SIZE = 100;
	private static final String ID_FIELD = "id";
	private static final String SUB_REGION_NAME_FIELD = "subRegion";
	private static final String SUB_REGION_SHORT_NAME_FIELD = "subRegionShortName";
	
	@LocalServerPort
	private int port;
	private static LocalRepositoryTestArray<SubRegion> testArray = new LocalRepositoryTestArray<>();
	private TestManager<SubRegion> testManager;

	
	//MUTATIONS
	@Test
	@Order(1)
	void add() throws IOException, ParseException {
		
		String graphqlUri = "http://localhost:" + port + "/graphql";
		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
			
			String subRegion = "subRegion " + i;
			String subRegionShortName = "shortName" + i;
		
			InputStream streamAdd = this.getClass().getResourceAsStream(MUTATIONS_PATH +"addSubRegion.graphql"); 
			String addPayload = createGraphqlAddTemplate(streamAdd, subRegion, subRegionShortName);
			Response response = TestsUtility.executeGraphqlMethod(addPayload, graphqlUri);
			Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
			String jsonDataAdd = response.body().string();
			JsonNode jsonNodeAdd = new ObjectMapper().readTree(jsonDataAdd).get("data").get("addSubRegion"); 
			assertsNewRecord(subRegion, subRegionShortName, jsonNodeAdd);
			addToLocalList(jsonNodeAdd);
		}
	}
	
	
	@Test
	@Order(2)
	void getByID() throws IOException, ParseException {
		
		String graphqlUri = "http://localhost:" + port + "/graphql";		
		SubRegion subRegion = testArray.getFirstElement();
		long id = subRegion.getId(); 

		//TEST GET BY ID
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getSubRegionById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		String jsonDataChange = response.body().string();
		
		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("getSubRegionById"); 
		System.out.print("GET"+jsonNodeChange); 
		Assert.assertEquals(jsonNodeChange.get(ID_FIELD).asInt(), id);
		assertsNewRecord(subRegion.getSubRegion(),subRegion.getSubRegionShortName(), jsonNodeChange);
	}
	
	
	

	@Test
	@Order(3)
	void getAll() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getAllSubRegion.graphql");
		
		ObjectNode var = new ObjectMapper().createObjectNode();
		String payload = GraphqlTemplate.parseGraphql(streamChange, var);
		Response response = TestsUtility.executeGraphqlMethod(payload, graphqlUri);

		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		String jsonData = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonData).get("data").get("getAllSubRegion"); 
		System.out.print("GET ALL"+jsonNodeChange); 
		
	}
	
	
	@Test
	@Order(4)
	void update() throws IOException, ParseException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql");
		SubRegion subRegion = testArray.getFirstElement();
		
		String subRegionName = "subRegionMod";
		String subRegionShortName = "srMod";
		subRegion.setSubRegion(subRegionName); 
		subRegion.setSubRegionShortName(subRegionShortName); 
		
		ObjectNode input = new ObjectMapper().createObjectNode();
		input.put(ID_FIELD, subRegion.getId());
		input.put(SUB_REGION_NAME_FIELD,subRegionName);
		input.put(SUB_REGION_SHORT_NAME_FIELD,subRegionShortName);
		
		Response response = testManager.request(MUTATIONS_PATH + "updateSubRegionById.graphql",input); 
		JsonNode jsonNode = testManager.checkResponse(response, "updateSubRegionById"); 
		
		Assert.assertEquals(subRegion.getId(),jsonNode.get(ID_FIELD).asLong());
		Assert.assertEquals(subRegion.getSubRegion(),jsonNode.get(SUB_REGION_NAME_FIELD).asText());
		Assert.assertEquals(subRegion.getSubRegionShortName(),jsonNode.get(SUB_REGION_SHORT_NAME_FIELD).asText());
		

		
	}
	
	@Test
	@Order(5)
	void delete() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		SubRegion globalRegion = testArray.getFirstElement();
		long id = globalRegion.getId();
		
		InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "deleteSubRegionById.graphql");
		
		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		
		String jsonDataChange = response.body().string();
		
		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("deleteSubRegionById"); 
		Assert.assertEquals(id, jsonNodeChange.get(ID_FIELD).asInt());			
	}
	
	
	private static String createGraphqlAddTemplate(InputStream iStream, String subRegion, String subRegionShortName) throws IOException {
		// Create a variables to pass to the graphql query		
		ObjectNode variables = new ObjectMapper().createObjectNode();
		ObjectNode input = variables.putObject("region");
		
		input.put(SUB_REGION_NAME_FIELD,subRegion);
		input.put(SUB_REGION_SHORT_NAME_FIELD,subRegionShortName);
		
		String graphqlPayload = GraphqlTemplate.parseGraphql(iStream, variables);
		return graphqlPayload;
	}
	  	
  	private void assertsNewRecord(String globalRegion, String globalRegionShortName, JsonNode jsonNodeChange) throws ParseException {
		
  		Assert.assertEquals(globalRegion,jsonNodeChange.get(SUB_REGION_NAME_FIELD).asText());
		Assert.assertEquals(globalRegionShortName,jsonNodeChange.get(SUB_REGION_SHORT_NAME_FIELD).asText());
  
  	}
  	
  	private void addToLocalList(JsonNode node) throws ParseException { 
  		
		SubRegion subRegion = new SubRegion(); 
		subRegion.setId(node.get(ID_FIELD).asLong());
		subRegion.setSubRegion(node.get(SUB_REGION_NAME_FIELD).asText());
		subRegion.setSubRegionShortName(node.get(SUB_REGION_SHORT_NAME_FIELD).asText());
		testArray.add(subRegion);
		
	}
}
