package com.gatelab.microservice.bookbuilder.core;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;

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
import com.gatelab.microservice.bookbuilder.core.bookbuilderconst.BookBuilderConst;
import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.TimeZones;
import com.gatelab.microservice.bookbuilder.core.utils.DateUtils;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestsUtility;
import com.vimalselvam.graphql.GraphqlTemplate;

import okhttp3.Response;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestTimeZoneMutationAndQueries {
	
	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "TimeZone/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "TimeZone/";

	private static final int LOCAL_REPOSITORY_SIZE = 100;
	private static final String ID_FIELD = "id";
	private static final String LONG_NAME_FIELD = "longName";
	private static final String SHORT_NAME_FIELD = "shortName";
	private static final String GMTOFFSET_NAME_FIELD = "gmtOffset";
	private static final String USER_SELECTABLE_NAME_FIELD = "userSelectable";
	private static final String USER_SELECTABLE_NAME_FIELD_QUERY = "userSectable";
	private static final String DEF_FIELD = "def";
	


	@LocalServerPort
	private int port;
	private static LocalRepositoryTestArray<TimeZones> testArray = new LocalRepositoryTestArray<>();
	
	//MUTATIONS
	@Test
	@Order(1)
	public void add() throws IOException, ParseException {

		String graphqlUri = "http://localhost:" + port + "/graphql";

		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
			String longName = "LONG_NAME " + i;
			String shortName = "SHORT_NAME " + i;
			float gmtOffset = new Float(i + 0.1);
			boolean userSelectable = false;
			boolean def = false;
			
			InputStream streamAdd = this.getClass().getResourceAsStream(MUTATIONS_PATH +"addTimeZone.graphql"); 
			String addPayload = createGraphqlAddTemplate(streamAdd, longName,shortName,gmtOffset,userSelectable,def);
			Response response = TestsUtility.executeGraphqlMethod(addPayload, graphqlUri);
			Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
			String jsonDataAdd = response.body().string();
			JsonNode jsonNodeAdd = new ObjectMapper().readTree(jsonDataAdd).get("data").get("addTimeZone"); 
			assertsNewRecord(longName, shortName,gmtOffset,userSelectable,def, jsonNodeAdd);
			addToLocalList(jsonNodeAdd);
		}
	}
	
	@Test
	@Order(2)
	public void getByID() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		TimeZones timeZone = testArray.getFirstElement();
		long id = timeZone.getId();

		//TEST GET BY ID
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getTimeZoneById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

		String jsonDataChange = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("getTimeZoneById"); 
		System.out.print("GET"+jsonNodeChange); 
		Assert.assertEquals(jsonNodeChange.get(ID_FIELD).asInt(), id);
		assertsNewRecord(timeZone.getLongName(), timeZone.getShortName(),timeZone.getGmtOffset(),timeZone.getUserSelectable(),timeZone.getDef(), jsonNodeChange);
		 
	}
	
	@Test
	@Order(3)
	void getAll() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";

		//TEST GET ALL
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getTimeZoneAll.graphql");

		ObjectNode var = new ObjectMapper().createObjectNode();
		String payload = GraphqlTemplate.parseGraphql(streamChange, var);
		Response response = TestsUtility.executeGraphqlMethod(payload, graphqlUri);

		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		String jsonData = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonData).get("data").get("getAllTimeZones"); 
		System.out.print("GET ALL"+jsonNodeChange); 
		Assert.assertEquals(testArray.getSize() ,jsonNodeChange.size());
	}
	
	@Test
	@Order(4)
	void updateById() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		TimeZones timeZone = testArray.getFirstElement();
		long oldId = timeZone.getId();
		
		//UPDATE
		InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "updateTimeZoneById.graphql");
		String longName = "LONG NAME UPDATE";
		String shortName = "SHORT NAME UPDATE";
		float gmtOffset = 1.1f;
		boolean userSelectable = true;
		boolean def = true;
		
		ObjectNode node = createObjectNode(longName, shortName,gmtOffset,userSelectable,def, oldId);
		
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		Response changeResponse = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,changeResponse.code());
		String jsonDataChange = changeResponse.body().string();
		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("updateTimeZoneById"); 
			addToLocalList(jsonNodeChange); 
 
			assertsNewRecord(longName, shortName,gmtOffset,userSelectable,def, jsonNodeChange);

		}
	
	@Test
	@Order(5)
	public void delete() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		TimeZones timeZone = testArray.getFirstElement(); 
		
		long id = timeZone.getId();
		
		//TEST DELETE 
		InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "deleteTimeZoneById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

		String jsonDataChange = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("deleteTimeZoneById"); 
		Assert.assertEquals(id, jsonNodeChange.get(ID_FIELD).asInt());				
	}
	
	private ObjectNode createObjectNode(String longName, String shortName,float gmtOffset,boolean userSelectable,boolean def,long id) {
		ObjectNode var = new ObjectMapper().createObjectNode();  
		var.put(ID_FIELD,id);
		var.put(LONG_NAME_FIELD,longName); 
		var.put(SHORT_NAME_FIELD,shortName);
		var.put(GMTOFFSET_NAME_FIELD,gmtOffset);
		var.put(USER_SELECTABLE_NAME_FIELD_QUERY,userSelectable);
		var.put(DEF_FIELD,def);
		return var;
	}


	private static String createGraphqlAddTemplate(InputStream iStream, String longName, String shortName,float gmtOffset,boolean userSelectable,boolean def) throws IOException {
		// Create a variables to pass to the graphql query		
		ObjectNode variables = new ObjectMapper().createObjectNode();

		// add a nested node object to our current node
		ObjectNode input = variables.putObject("timeZone");
		input.put(LONG_NAME_FIELD,longName); 
		input.put(SHORT_NAME_FIELD,shortName);
		input.put(GMTOFFSET_NAME_FIELD,gmtOffset);
		input.put(USER_SELECTABLE_NAME_FIELD,userSelectable);
		input.put(DEF_FIELD,def);

		// Now parse the graphql file to a request payload string
		String graphqlPayload = GraphqlTemplate.parseGraphql(iStream, variables);
		return graphqlPayload;
	}
	  	
  	private void assertsNewRecord(String longName, String shortName,float gmtOffset,boolean userSelectable,boolean def,JsonNode jsonNodeChange) throws ParseException {
		Assert.assertEquals(longName,jsonNodeChange.get(LONG_NAME_FIELD).asText());
		Assert.assertEquals(shortName,jsonNodeChange.get(SHORT_NAME_FIELD).asText());
		Assert.assertEquals(gmtOffset,jsonNodeChange.get(GMTOFFSET_NAME_FIELD).floatValue(),0);
		Assert.assertEquals(userSelectable,jsonNodeChange.get(USER_SELECTABLE_NAME_FIELD).asBoolean());
		Assert.assertEquals(def,jsonNodeChange.get(DEF_FIELD).asBoolean());
	}
  	
  	private void addToLocalList(JsonNode node) throws ParseException { 

		TimeZones timeZone = new TimeZones();
		timeZone.setId(node.get(ID_FIELD).asLong());
		timeZone.setLongName(node.get(LONG_NAME_FIELD).asText()); 
		timeZone.setShortName(node.get(SHORT_NAME_FIELD).asText());
		timeZone.setGmtOffset(new Float(node.get(GMTOFFSET_NAME_FIELD).asDouble())); 
		timeZone.setUserSelectable(node.get(USER_SELECTABLE_NAME_FIELD).asBoolean());
		timeZone.setDef(node.get(DEF_FIELD).asBoolean());
		testArray.add(timeZone);
	}

	

}
