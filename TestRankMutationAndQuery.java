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
import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.Rank;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;
import com.gatelab.microservices.bookbulder.utils.TestsUtility;
import com.vimalselvam.graphql.GraphqlTemplate;

import okhttp3.Response;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestRankMutationAndQuery {

	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "Rank/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Rank/";
	private static final int LOCAL_REPOSITORY_SIZE = 100;
	private static final String ID_FIELD = "id";
	private static final String CATEGORY_FIELD = "category";
	private static final String ALLOCATION_RATIONALE_FIELD = "allocationRationale";
	private static final String HOST = "http://localhost:"; 
	private static final String ENTRY_POINT = "/graphql"; 
	private static final String GRAPHQL_EXTENTION = ".graphql"; 
	private static final String ADD_METHOD = "addRank"; 

	
	@LocalServerPort
	private int port;
	private static LocalRepositoryTestArray<Rank> testArray = new LocalRepositoryTestArray<>();
	private TestManager<Rank> testManager; 
	
	
	//MUTATIONS
	@Test
	@Order(1)
	void add() throws IOException, ParseException {

		String graphqlUri = HOST + port + ENTRY_POINT;

		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
				
			int category =  i;
			String allocationRationale = "allocation " + i;
			
			InputStream streamAdd = this.getClass().getResourceAsStream(MUTATIONS_PATH + ADD_METHOD +GRAPHQL_EXTENTION); 
			String addPayload = createGraphqlAddTemplate(streamAdd, category, allocationRationale);
			Response response = TestsUtility.executeGraphqlMethod(addPayload, graphqlUri);

			Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
			String jsonDataAdd = response.body().string();
			JsonNode jsonNodeAdd = new ObjectMapper().readTree(jsonDataAdd).get("data").get(ADD_METHOD); 
						
			assertsNewRecord(category,allocationRationale, jsonNodeAdd); 		
			addToLocalList(jsonNodeAdd);
		}
	}
	
	
	@Test
	@Order(2)
    void getByID() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		Rank rank = testArray.getFirstElement();
		long id = rank.getId();

		//TEST GET BY ID
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getRankById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		String jsonDataChange = response.body().string();
		
		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("getRankById"); 
		System.out.print("GET"+jsonNodeChange); 
		System.out.println("JSON_NODE> ID > "+jsonNodeChange.get(ID_FIELD).asInt()+"\n"); 
		Assert.assertEquals(jsonNodeChange.get(ID_FIELD).asInt(), id);
		assertsNewRecord(rank.getCategory(), rank.getAllocationRationale(), jsonNodeChange);
		 
	}
	

	@Test
	@Order(3)
	void getAll() throws IOException, ParseException {
		 testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		 ObjectNode var = new ObjectMapper().createObjectNode();
		 Response response = testManager.request(QUERIES_PATH + "getAllRanks.graphql", var); 
		 JsonNode jsonNode = testManager.checkResponse(response, "getAllRank"); 
		
	}

	@Test
	@Order(4)
	void updateById() throws IOException, ParseException {
		
		Rank rank = testArray.getFirstElement();
		testManager = new TestManager("http://localhost:" + port + "/graphql");
		
		String allocation ="AllocationMod";
		int category = 1997;
		
		rank.setAllocationRationale(allocation); 
		rank.setCategory(category); 
		
		ObjectNode var = new ObjectMapper().createObjectNode();  
		var.put(ID_FIELD,rank.getId());
		var.put(CATEGORY_FIELD, category); 
		var.put(ALLOCATION_RATIONALE_FIELD, allocation);


		Response response = testManager.request(MUTATIONS_PATH + "changeRank.graphql", var); 
		JsonNode jsonNode = testManager.checkResponse(response, "changeRankById");
		
		Assert.assertEquals(category,jsonNode.get(CATEGORY_FIELD).asInt());
		Assert.assertEquals(allocation,jsonNode.get(ALLOCATION_RATIONALE_FIELD).asText());
		
	}
	
	@Test
	@Order(5)
	void delete() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		Rank rank = testArray.getFirstElement();
		long id = rank.getId();

		InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "deleteRankById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

		String jsonDataChange = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("deleteRankById"); 
		Assert.assertEquals(id, jsonNodeChange.get(ID_FIELD).asInt());			
	}
	
	
	
	

	private static String createGraphqlAddTemplate(InputStream iStream, int category , String allocationRationale) throws IOException {
		// Create a variables to pass to the graphql query		
		ObjectNode variables = new ObjectMapper().createObjectNode();

		// add a nested node object to our current node
		ObjectNode input = variables.putObject("rank");
		input.put(CATEGORY_FIELD,category);
		input.put(ALLOCATION_RATIONALE_FIELD,allocationRationale);

		String graphqlPayload = GraphqlTemplate.parseGraphql(iStream, variables);
		return graphqlPayload;
	}
	  	
  	private void assertsNewRecord(int  category, String allocationRationale,JsonNode jsonNodeChange) throws ParseException {
  		
  		Assert.assertEquals(category,jsonNodeChange.get(CATEGORY_FIELD).asInt());
		Assert.assertEquals(allocationRationale,jsonNodeChange.get(ALLOCATION_RATIONALE_FIELD).asText());
  	
  	}
  	
  	private void addToLocalList(JsonNode node) throws ParseException { 
  		
  		Rank rank = new Rank(); 
  		rank.setId(node.get(ID_FIELD).asLong());
  		rank.setAllocationRationale(node.get(ALLOCATION_RATIONALE_FIELD).asText());
		rank.setCategory(node.get(CATEGORY_FIELD).asInt());
		testArray.add(rank);
	}
		  
}