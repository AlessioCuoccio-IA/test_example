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
import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.Desk;
import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.GlobalRegion;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;
import com.gatelab.microservices.bookbulder.utils.TestsUtility;
import com.vimalselvam.graphql.GraphqlTemplate;

import okhttp3.Response;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestGlobalRegionMutationAndQueries {

	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "SalesRegion/GlobalRegion/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "SalesRegion/GlobalRegion/";
	private static final int LOCAL_REPOSITORY_SIZE = 100;
	private static final String ID_FIELD = "id";
	private static final String GLOBAL_REGION_NAME_FIELD = "globalRegion";
	private static final String GLOBAL_REGION_SHORT_NAME_FIELD = "globalRegionShortName";
	private static final String GLOBAL_REGION_SUBREGION_FIELD = "subRegion";
	
	
	@LocalServerPort
	private int port;
	private static LocalRepositoryTestArray<GlobalRegion> testArray = new LocalRepositoryTestArray<>();
	private TestManager<GlobalRegion> testManager;

	//MUTATIONS
	@Test
	@Order(1)
	void add() throws IOException, ParseException {

		String graphqlUri = "http://localhost:" + port + "/graphql";

		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
			String globalRegion = "globalRegion " + i;
			String globalRegionShortName = "shortName" + i;
		
			InputStream streamAdd = this.getClass().getResourceAsStream(MUTATIONS_PATH +"addGlobalRegion.graphql"); 
			String addPayload = createGraphqlAddTemplate(streamAdd, globalRegion, globalRegionShortName);
			Response response = TestsUtility.executeGraphqlMethod(addPayload, graphqlUri);
			Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
			String jsonDataAdd = response.body().string();
			JsonNode jsonNodeAdd = new ObjectMapper().readTree(jsonDataAdd).get("data").get("addGlobalRegion"); 
			assertsNewRecord(globalRegion, globalRegionShortName, jsonNodeAdd);
			addToLocalList(jsonNodeAdd);
		}
	}
	
	@Test
	@Order(2)
	void addGlobalAndSub() throws IOException, ParseException {

		String graphqlUri = "http://localhost:" + port + "/graphql";

		
		String globalRegion = "globalRegion_3221211";
		String globalRegionShortName = "ssad31";
	
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		ObjectNode input = new ObjectMapper().createObjectNode();
		
		ObjectNode globalR = input.putObject("global");
		globalR.put(GLOBAL_REGION_NAME_FIELD, globalRegion);
		globalR.put(GLOBAL_REGION_SHORT_NAME_FIELD, globalRegionShortName);

		ObjectNode subR = input.putObject("sub");
		subR.put("subRegion", "subRegion2134r");
		subR.put("subRegionShortName", "7dsa");
		
		Response response = testManager.request(MUTATIONS_PATH+ "addGlobalRegionAndSubRegion.graphql", input); 
		JsonNode jsonNode = testManager.checkResponse(response, "addGlobalRegionAndSubRegion");

		
	}
	
	
	
	@Test
	@Order(3)
	void getByID() throws IOException, ParseException {
		
		String graphqlUri = "http://localhost:" + port + "/graphql";		
		GlobalRegion globalRegion = testArray.getFirstElement();
		long id =globalRegion.getId();

		//TEST GET BY ID
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getGlobalRegionById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		String jsonDataChange = response.body().string();
		
		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("getGlobalRegionById"); 
		System.out.print("GET"+jsonNodeChange); 
		Assert.assertEquals(jsonNodeChange.get(ID_FIELD).asInt(), id);
		assertsNewRecord(globalRegion.getGlobalRegion(),globalRegion.getGlobalRegionShortName(), jsonNodeChange);
	}
	

	@Test
	@Order(4)
	void getAll() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getAllGlobalRegion.graphql");
		
		ObjectNode var = new ObjectMapper().createObjectNode();
		String payload = GraphqlTemplate.parseGraphql(streamChange, var);
		Response response = TestsUtility.executeGraphqlMethod(payload, graphqlUri);
	
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		String jsonData = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonData).get("data").get("getAllGlobalRegion"); 
		System.out.print("GET ALL"+jsonNodeChange); 
		
	}
	
	@Test
	@Order(5)
	void  update() throws IOException, ParseException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		GlobalRegion globalRegion = testArray.getFirstElement();
		String name = "hello name";
		String shortName =" hello";
		ObjectNode node = new ObjectMapper().createObjectNode();
		globalRegion.setGlobalRegionShortName(shortName);
		globalRegion.setGlobalRegion(name); 
		
		node.put(ID_FIELD,globalRegion.getId()); 
		node.put(GLOBAL_REGION_NAME_FIELD,name );
		node.put(GLOBAL_REGION_SHORT_NAME_FIELD,shortName); 

		Response response = testManager.request(MUTATIONS_PATH + "updateGlobalRegion.graphql", node); 
		JsonNode jsonNode = testManager.checkResponse(response, "updateGlobalRegionById");
	
		Assert.assertEquals(globalRegion.getId(), jsonNode.get(ID_FIELD).asInt());			
	}
	
	@Test
	@Order(6)
	void delete() throws IOException, ParseException {
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 

		
		GlobalRegion globalRegion = testArray.getFirstElement();
		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,globalRegion.getId()); 

		Response response = testManager.request(MUTATIONS_PATH + "deleteGlobalRegion.graphql", node); 
		JsonNode jsonNode = testManager.checkResponse(response, "deleteGlobalRegionById");
	
		Assert.assertEquals(globalRegion.getId(), jsonNode.get(ID_FIELD).asInt());			
	}
	
	
	private static String createGraphqlAddTemplate(InputStream iStream, String globalRegion, String globalRegionShortName) throws IOException {
		// Create a variables to pass to the graphql query		
		ObjectNode variables = new ObjectMapper().createObjectNode();
		
		ObjectNode input = variables.putObject("region");
		input.put(GLOBAL_REGION_NAME_FIELD,globalRegion);
		input.put(GLOBAL_REGION_SHORT_NAME_FIELD,globalRegionShortName);


		String graphqlPayload = GraphqlTemplate.parseGraphql(iStream, variables);
		return graphqlPayload;
	}
	  	
  	private void assertsNewRecord(String globalRegion, String globalRegionShortName, JsonNode jsonNodeChange) throws ParseException {
		
  		Assert.assertEquals(globalRegion,jsonNodeChange.get(GLOBAL_REGION_NAME_FIELD).asText());
		Assert.assertEquals(globalRegionShortName,jsonNodeChange.get(GLOBAL_REGION_SHORT_NAME_FIELD).asText());
  
  	}
  	
  	private void addToLocalList(JsonNode node) throws ParseException { 
  		
		GlobalRegion globalRegion = new GlobalRegion();
		globalRegion.setId(node.get(ID_FIELD).asLong());
		globalRegion.setGlobalRegion(node.get(GLOBAL_REGION_NAME_FIELD).asText());
		globalRegion.setGlobalRegionShortName(node.get(GLOBAL_REGION_SHORT_NAME_FIELD).asText());
		testArray.add(globalRegion);
		
	}
}
