package com.gatelab.microservice.bookbuilder.core;

import org.junit.Assert;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.text.ParseException;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gatelab.microservice.bookbuilder.core.persistence.model.role.Role;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;
import okhttp3.Response;


@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestRoleMutationAndQuery {

	
	@LocalServerPort
	private int port;
	private TestManager<Role> testManager;
	private static LocalRepositoryTestArray<Role> testArray = new LocalRepositoryTestArray<>();
	
	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "Role/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Role/";
	
	private static final String ID_FIELD = "id";
	private static final String NAME_FIELD = "name";
	private static final String REGION_SALES_MANAGEMENT_FIELD = "regionSalesManagement";
	
	private static final String HOST = "http://localhost:"; 
	private static final String ENTRY_POINT = "/graphql"; 
	private static final String GRAPHQL_EXTENTION = ".graphql"; 
	
	private static final int LOCAL_REPOSITORY_SIZE = 100;
	
	@Test
	@Order(1)
	void add() throws IOException {
		
		testManager = new TestManager<Role>("http://localhost:" + port + "/graphql"); 
		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
			
			String roleName = "role"+i; 
			boolean regionSalesManagement = (i%2==0)? true : false;
			
			ObjectNode variables = new ObjectMapper().createObjectNode();
			ObjectNode input = variables.putObject("role"); 
			input.put(NAME_FIELD, roleName); 
			input.put(REGION_SALES_MANAGEMENT_FIELD, regionSalesManagement); 

			
			Response response = testManager.request(MUTATIONS_PATH + "addRole.graphql", variables); 
			JsonNode jsonNode = testManager.checkResponse(response, "addRole"); 
			
			Assert.assertEquals(roleName,jsonNode.get(NAME_FIELD).asText());
			Assert.assertEquals(regionSalesManagement,jsonNode.get(REGION_SALES_MANAGEMENT_FIELD).asBoolean());
			
			
			Role role = new Role();
			role.setId(jsonNode.get(ID_FIELD).asLong()); 
			role.setName(jsonNode.get(NAME_FIELD).asText()); 
			role.setRegionSalesManagement(jsonNode.get(REGION_SALES_MANAGEMENT_FIELD).asBoolean());
			testArray.add(role);
		}
	}
	
	@Test
	@Order(2)
    void getByID() throws ParseException, IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Role role = testArray.getFirstElement();
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,role.getId());
		
		Response response = testManager.request(QUERIES_PATH + "getRoleById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getRoleById");
		
		Assert.assertEquals(role.getName(),jsonNode.get(NAME_FIELD).asText());
		Assert.assertEquals(role.isRegionSalesManagement(),jsonNode.get(REGION_SALES_MANAGEMENT_FIELD).asBoolean());
		
	}
	
	@Test
	@Order(3)
	void getAll() throws IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		ObjectNode variables = new ObjectMapper().createObjectNode();
		Response response = testManager.request(QUERIES_PATH + "getAllRole.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getAllRoles");
		
	}
	
	@Test
	@Order(4)
	void update() {
		
	}
	
	@Test
	@Order(5)
	void delete() throws IOException, ParseException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Role role = testArray.getFirstElement();
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,role.getId());
		
		Response response = testManager.request(MUTATIONS_PATH + "deleteRoleById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "deleteRoleById");
		
		Assert.assertEquals(role.getId(),jsonNode.get(ID_FIELD).asLong());
	}
}
