package com.gatelab.microservice.bookbuilder.core;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.IssuerSector;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestsUtility;
import com.vimalselvam.graphql.GraphqlTemplate;

import okhttp3.Response;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
class TestIssuerSectorMutationsAndQueries {

	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "IssuerSector/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "IssuerSector/";

	private static final int LOCAL_REPOSITORY_SIZE = 100;
	private static final String ID_FIELD = "id";
	private static final String FID_FIELD = "fid";
	private static final String ISSUER_SECTOR_FIELD = "issuerSector";

	private static final String END_DATE_FIELD = "endDate";


	@LocalServerPort
	private int port;
	private static LocalRepositoryTestArray<IssuerSector> testArray = new LocalRepositoryTestArray<>();
	
	@Test
	@Order(1)
	public void add() throws IOException, ParseException {

		String graphqlUri = "http://localhost:" + port + "/graphql";

		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
			String issuerSector = "ISSUER SECTOR " + i;			
			InputStream streamAdd = this.getClass().getResourceAsStream(MUTATIONS_PATH +"addIssuerSector.graphql"); 
			String addPayload = createGraphqlAddTemplate(streamAdd, issuerSector);
			Response response = TestsUtility.executeGraphqlMethod(addPayload, graphqlUri);
			Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
			String jsonDataAdd = response.body().string();
			JsonNode jsonNodeAdd = new ObjectMapper().readTree(jsonDataAdd).get("data").get("addIssuerSector"); 
			assertsNewRecord(issuerSector, jsonNodeAdd);
			addToLocalList(jsonNodeAdd);
		}
	}
	
	
	@Test
	@Order(2)
	public void getByID() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		IssuerSector issuerSector = testArray.getFirstElement();
		long id = issuerSector.getId();

		//TEST GET BY ID
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getIssuerSectorById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

		String jsonDataChange = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("getIssuerSectorById"); 
		System.out.print("GET"+jsonNodeChange); 
		Assert.assertEquals(jsonNodeChange.get(ID_FIELD).asInt(), id);
		assertsNewRecord(issuerSector.getIssuerSector(), jsonNodeChange);
		 
	}
	
	@Test
	@Order(3)
	void getAll() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";

		//TEST GET ALL
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getIssuerSectorAll.graphql");

		ObjectNode var = new ObjectMapper().createObjectNode();
		String payload = GraphqlTemplate.parseGraphql(streamChange, var);
		Response response = TestsUtility.executeGraphqlMethod(payload, graphqlUri);

		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		String jsonData = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonData).get("data").get("getAllIssuerSectors"); 
		System.out.print("GET ALL"+jsonNodeChange); 

	}
	
	@Test
	@Order(4)
	void updateById() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		IssuerSector issuerSector = testArray.getFirstElement();
		long oldId = issuerSector.getId();
		
		//UPDATE
		InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "updateIssuerSectorById.graphql");
		String issuerSectorUpdate = "ISSUER SECTOR UPDATE";
		
		ObjectNode node = createObjectNode(issuerSectorUpdate, oldId);
		
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		Response changeResponse = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,changeResponse.code());
		String jsonDataChange = changeResponse.body().string();
		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("changeIssuerSectorById"); 
	
		addToLocalList(jsonNodeChange); 

		assertsNewRecord(issuerSectorUpdate, jsonNodeChange);

	}
	
	@Test
	@Order(5)
	public void delete() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		IssuerSector issuerSector = testArray.getFirstElement();
		
		long id = issuerSector.getId();
		
		//TEST DELETE 
		InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "deleteIssuerSectorById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

		String jsonDataChange = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("deleteIssuerSectorById"); 
		Assert.assertEquals(id, jsonNodeChange.get(ID_FIELD).asInt());
		testArray.deleteFirstElement();
		
	}
	
	

	
	
	
	private ObjectNode createObjectNode(String issuerSector,long id) {
		ObjectNode var = new ObjectMapper().createObjectNode();  
		var.put(ID_FIELD,id);
		var.put(ISSUER_SECTOR_FIELD,issuerSector); 
		return var;
	}


	private static String createGraphqlAddTemplate(InputStream iStream, String issuerSector) throws IOException {
		// Create a variables to pass to the graphql query		
		ObjectNode variables = new ObjectMapper().createObjectNode();

		// add a nested node object to our current node
		ObjectNode input = variables.putObject("issuerSector");
		input.put(ISSUER_SECTOR_FIELD,issuerSector);
		

		// Now parse the graphql file to a request payload string
		String graphqlPayload = GraphqlTemplate.parseGraphql(iStream, variables);
		return graphqlPayload;
	}
	  	
	  	private void assertsNewRecord(String issuerSector,JsonNode jsonNodeChange) throws ParseException {
			Assert.assertEquals(issuerSector,jsonNodeChange.get(ISSUER_SECTOR_FIELD).asText());
		}
	  	
	  	private void addToLocalList(JsonNode node) throws ParseException { 

			IssuerSector issuerSector = new IssuerSector();
			issuerSector.setId(node.get(ID_FIELD).asLong());
			issuerSector.setIssuerSector(node.get(ISSUER_SECTOR_FIELD).asText());
			testArray.add(issuerSector);
		}

}