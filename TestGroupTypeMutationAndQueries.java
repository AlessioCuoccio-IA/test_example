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
import com.gatelab.microservice.bookbuilder.core.persistence.model.users.GroupType;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestsUtility;
import com.vimalselvam.graphql.GraphqlTemplate;
import okhttp3.Response;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestGroupTypeMutationAndQueries {

	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "GroupType/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "GroupType/";
	private static final int LOCAL_REPOSITORY_SIZE = 100;
	private static final String ID_FIELD = "id";
	private static final String TYPE_FIELD = "type";


	@LocalServerPort
	private int port;
	private static LocalRepositoryTestArray<GroupType> testArray = new LocalRepositoryTestArray<>();

	//MUTATIONS
	@Test
	@Order(1)
	void add() throws IOException, ParseException {

		String graphqlUri = "http://localhost:" + port + "/graphql";

		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
			String type = "TYPE " + i;

			InputStream streamAdd = this.getClass().getResourceAsStream(MUTATIONS_PATH +"addGroupType.graphql"); 
			String addPayload = createGraphqlAddTemplate(streamAdd, type);
			Response response = TestsUtility.executeGraphqlMethod(addPayload, graphqlUri);
			Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
			String jsonDataAdd = response.body().string();
			JsonNode jsonNodeAdd = new ObjectMapper().readTree(jsonDataAdd).get("data").get("addGroupType"); 
			assertsNewRecord(type, jsonNodeAdd);
			addToLocalList(jsonNodeAdd);
		}
	}
	
	@Test
	@Order(2)
	void getByID() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		GroupType groupType = testArray.getFirstElement();
		long id = groupType.getId();

		//TEST GET BY ID
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getGroupTypeById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

		String jsonDataChange = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("getGroupTypeById"); 
		System.out.print("GET"+jsonNodeChange); 
		Assert.assertEquals(jsonNodeChange.get(ID_FIELD).asInt(), id);
		assertsNewRecord(groupType.getType(), jsonNodeChange);
		 
	}	
	
	@Test
	@Order(3)
	void getAll() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";

		//TEST GET ALL
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getGroupTypeAll.graphql");

		ObjectNode var = new ObjectMapper().createObjectNode();
		String payload = GraphqlTemplate.parseGraphql(streamChange, var);
		Response response = TestsUtility.executeGraphqlMethod(payload, graphqlUri);

		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		String jsonData = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonData).get("data").get("getAllGroupTypes"); 
		System.out.print("GET ALL"+jsonNodeChange); 
	
	}
	

	@Test
	@Order(4)
	void updateById() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		GroupType desk = testArray.getFirstElement(); 
		long oldId = desk.getId();
		
		//UPDATE
		InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "updateGroupTypeById.graphql");
		String type = "TYPE UPDATE";
		
		ObjectNode node = createObjectNode(type, oldId);
		
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		Response changeResponse = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		 
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,changeResponse.code());
		String jsonDataChange = changeResponse.body().string();
		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("changeGroupTypeById"); 
		addToLocalList(jsonNodeChange); 
		assertsNewRecord(type, jsonNodeChange);
	}
	
	@Test
	@Order(5)
	void delete() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		GroupType groupType = testArray.getFirstElement();
		
		long id = groupType.getId();
		
		//TEST DELETE 
		InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "deleteGroupTypeById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

		String jsonDataChange = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("deleteGroupTypeById"); 
		Assert.assertEquals(id, jsonNodeChange.get(ID_FIELD).asInt());
	}
				
	private ObjectNode createObjectNode(String type,long id) {
		ObjectNode var = new ObjectMapper().createObjectNode();  
		var.put(ID_FIELD,id);
		var.put(TYPE_FIELD,type); 
		return var;
	}


	private static String createGraphqlAddTemplate(InputStream iStream, String type) throws IOException {
		// Create a variables to pass to the graphql query		
		ObjectNode variables = new ObjectMapper().createObjectNode();

		// add a nested node object to our current node
		ObjectNode input = variables.putObject("groupType");
		input.put(TYPE_FIELD,type);

		// Now parse the graphql file to a request payload string
		String graphqlPayload = GraphqlTemplate.parseGraphql(iStream, variables);
		return graphqlPayload;
	}
	  	
  	private void assertsNewRecord(String type,JsonNode jsonNodeChange) throws ParseException {
		Assert.assertEquals(type,jsonNodeChange.get(TYPE_FIELD).asText());
	}
  	
  	private void addToLocalList(JsonNode node) throws ParseException { 

		GroupType groupType = new GroupType();
		groupType.setId(node.get(ID_FIELD).asLong());
		groupType.setType(node.get(TYPE_FIELD).asText());
		testArray.add(groupType);
	}
}


