package com.gatelab.microservice.bookbuilder.core;

import java.io.IOException;
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
import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.Address;
import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.CompanyType;
import com.gatelab.microservice.bookbuilder.core.persistence.model.role.Role;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;

import okhttp3.Response;


@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestAdressMutationAndQuery {

	@LocalServerPort
	private int port;
	private TestManager<Address> testManager;
	private static LocalRepositoryTestArray<Address> testArray = new LocalRepositoryTestArray<>();
	
	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "Company/Address/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Company/Address/";
	
	private static final String ID_FIELD = "id";
	private static final String PRIMARY_ADDRESS_FIELD = "address_1";
	private static final String SECONDARY_ADDRESS_FIELD = "address_2";
	private static final String STATE_FIELD = "state";
	private static final String CITY_FIELD = "city";
	private static final String POSTCODE_FIELD = "postCode";
	private static final String SWITCH_BOARD_FIELD = "switchBoard"; 
	private static final String FAX_FIELD = "fax";

	private static final int LOCAL_REPOSITORY_SIZE = 100;
	
	@Test
	@Order(1)
	void add() throws IOException {
		
	
		testManager = new TestManager<Address>("http://localhost:" + port + "/graphql"); 
		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
			
			String primaryAddress = "address1"+i; 
			String secondaryAddress = "address2"+i; 
			String state = "state"+i; 
			String city = "city"+i; 
			String postCode = "postCode"+i; 
			String switchBoard = "switchBoard"+i; 
			String fax = "fax"+i; 			

			ObjectNode variables = new ObjectMapper().createObjectNode();
			ObjectNode input = variables.putObject("address"); 
			input.put(PRIMARY_ADDRESS_FIELD, primaryAddress);
			input.put(SECONDARY_ADDRESS_FIELD, secondaryAddress); 
			input.put(STATE_FIELD, state); 
			input.put(CITY_FIELD,city);
			input.put(POSTCODE_FIELD, postCode); 
			input.put(SWITCH_BOARD_FIELD, switchBoard); 
			input.put(FAX_FIELD, fax); 
			
			Response response = testManager.request(MUTATIONS_PATH + "addAddress.graphql", variables); 
			JsonNode jsonNode = testManager.checkResponse(response, "addAddress"); 
			
			Assert.assertEquals(primaryAddress,jsonNode.get(PRIMARY_ADDRESS_FIELD).asText());
			Assert.assertEquals(secondaryAddress,jsonNode.get(SECONDARY_ADDRESS_FIELD).asText());
			Assert.assertEquals(state,jsonNode.get(STATE_FIELD).asText());
			Assert.assertEquals(city,jsonNode.get(CITY_FIELD).asText());
			Assert.assertEquals(postCode,jsonNode.get(POSTCODE_FIELD).asText());
			Assert.assertEquals(switchBoard,jsonNode.get(SWITCH_BOARD_FIELD).asText());
			Assert.assertEquals(fax,jsonNode.get(FAX_FIELD).asText());
			
			Address address = new Address(); 
			address.setId(jsonNode.get(ID_FIELD).asLong()); 
			address.setAddress_1(primaryAddress); 
			address.setAddress_2(secondaryAddress); 
			address.setState(state);
			address.setCity(city); 
			address.setSwitchBoard(switchBoard); 
			address.setFax(fax); 
			address.setPostCode(postCode); 
			
			testArray.add(address); 	
		}
	}

	@Test
	@Order(2)
	void getByID() throws ParseException, IOException {
	
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Address address = testArray.getFirstElement();
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,address.getId());
		
		Response response = testManager.request(QUERIES_PATH + "getAddressById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getAddressById");
		
		Assert.assertEquals(address.getId(),jsonNode.get(ID_FIELD).asLong());
		Assert.assertEquals(address.getAddress_1(),jsonNode.get(PRIMARY_ADDRESS_FIELD).asText());
		Assert.assertEquals(address.getAddress_2(),jsonNode.get(SECONDARY_ADDRESS_FIELD).asText());
		Assert.assertEquals(address.getState(),jsonNode.get(STATE_FIELD).asText());
		Assert.assertEquals(address.getCity(),jsonNode.get(CITY_FIELD).asText());
		Assert.assertEquals(address.getPostCode(),jsonNode.get(POSTCODE_FIELD).asText());
		Assert.assertEquals(address.getSwitchBoard(),jsonNode.get(SWITCH_BOARD_FIELD).asText());
		Assert.assertEquals(address.getFax(),jsonNode.get(FAX_FIELD).asText());
		
	}
	
	@Test
	@Order(3)
	void getAll() throws IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		ObjectNode variables = new ObjectMapper().createObjectNode();
		Response response = testManager.request(QUERIES_PATH + "getAllAddress.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getAllAddress");
		Assert.assertEquals(testArray.getSize(),jsonNode.size());	
	
	}
	
	
	@Test
	@Order(4)
	void update() throws ParseException, IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Address address = testArray.getFirstElement();
	
		String primaryAddress = "address1"; 
		String secondaryAddress = "address2"; 
		String state = "state"; 
		String city = "city"; 
		String postCode = "postCode"; 
		String switchBoard = "switchBoard"; 
		String fax = "fax"; 
	
		address.setAddress_1(primaryAddress); 
		address.setAddress_2(secondaryAddress); 
		address.setState(state);
		address.setCity(city); 
		address.setSwitchBoard(switchBoard); 
		address.setFax(fax); 
		address.setPostCode(postCode);
		
		ObjectNode input = new ObjectMapper().createObjectNode();
		input.put(ID_FIELD, address.getId()); 
		input.put(PRIMARY_ADDRESS_FIELD, primaryAddress);
		input.put(SECONDARY_ADDRESS_FIELD, secondaryAddress); 
		input.put(STATE_FIELD, state); 
		input.put(CITY_FIELD,city);
		input.put(POSTCODE_FIELD, postCode); 
		input.put(SWITCH_BOARD_FIELD, switchBoard); 
		input.put(FAX_FIELD, fax);
		
	
		Response response = testManager.request(MUTATIONS_PATH + "changeAddressById.graphql", input); 
		JsonNode jsonNode = testManager.checkResponse(response, "changeAddressById");
		
		Assert.assertEquals(address.getId(),jsonNode.get(ID_FIELD).asLong());
		Assert.assertEquals(address.getAddress_1(),jsonNode.get(PRIMARY_ADDRESS_FIELD).asText());
		Assert.assertEquals(address.getAddress_2(),jsonNode.get(SECONDARY_ADDRESS_FIELD).asText());
		Assert.assertEquals(address.getState(),jsonNode.get(STATE_FIELD).asText());
		Assert.assertEquals(address.getCity(),jsonNode.get(CITY_FIELD).asText());
		Assert.assertEquals(address.getPostCode(),jsonNode.get(POSTCODE_FIELD).asText());
		Assert.assertEquals(address.getSwitchBoard(),jsonNode.get(SWITCH_BOARD_FIELD).asText());
		Assert.assertEquals(address.getFax(),jsonNode.get(FAX_FIELD).asText());
		
		
	}
	
	@Test
	@Order(5)
	void delete() throws IOException, ParseException {
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Address address = testArray.getFirstElement();
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,address.getId());

		Response response = testManager.request(MUTATIONS_PATH + "deleteAddressById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "deleteAddressById");
		
		Assert.assertEquals(address.getId(),jsonNode.get(ID_FIELD).asLong());
	}
}
