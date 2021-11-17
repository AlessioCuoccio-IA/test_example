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
import com.gatelab.microservice.bookbuilder.core.bookbuilderconst.PermissionType;
import com.gatelab.microservice.bookbuilder.core.persistence.model.permission.SpecialPermission;
import com.gatelab.microservice.bookbuilder.core.persistence.model.role.Role;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;


import okhttp3.Response;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestSpecialPermission {

//	
//	
//	@LocalServerPort
//	private int port;
//	
//	private static final String MUTATIONS_PATH_ROLE = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Role/";
//
//	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "SpecialPermission/";
//	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "SpecialPermission/";
//	
//	private static final String ID_FIELD = "id";
//	private static final String NAME_FIELD = "name";  
//	private static final String DESCRIPTION_FIELD = "description"; 
//	private static final String VALUE_FIELD = "value"; 
//	private static final String TYPE_FIELD = "type"; 
//	private static final String ROLE_FIELD = "role"; 
//	private static final String USER_GROUP_FIELD = "userGroup"; 
//	private static final String REGION_SALES_MANAGEMENT_FIELD = "regionSalesManagement";
//	
//	private TestManager<SpecialPermission> testManager;
//	private static LocalRepositoryTestArray<SpecialPermission> testArray = new LocalRepositoryTestArray<>();
//	private TestManager<Role> testManagerRole;
//	private static LocalRepositoryTestArray<Role> testArrayRole = new LocalRepositoryTestArray<>();
//	@Test
//	@Order(1)
//	void add() throws IOException {
//	
//		//add Role 
//		testManagerRole = new TestManager<Role>("http://localhost:" + port + "/graphql"); 
//		String roleName = "EXTERNAL_SYNDICATE"; 
//		boolean regionSalesManagement = false;
//		
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		ObjectNode input = variables.putObject("role"); 
//		input.put(NAME_FIELD, roleName); 
//		input.put(REGION_SALES_MANAGEMENT_FIELD, regionSalesManagement); 
//
//		
//		Response response = testManagerRole.request(MUTATIONS_PATH_ROLE + "addRole.graphql", variables); 
//		JsonNode jsonNode = testManagerRole.checkResponse(response, "addRole"); 
//		
//		Assert.assertEquals(roleName,jsonNode.get(NAME_FIELD).asText());
//		Assert.assertEquals(regionSalesManagement,jsonNode.get(REGION_SALES_MANAGEMENT_FIELD).asBoolean());
//		
//		
//		Role role = new Role();
//		role.setId(jsonNode.get(ID_FIELD).asLong()); 
//		role.setName(jsonNode.get(NAME_FIELD).asText()); 
//		role.setRegionSalesManagement(jsonNode.get(REGION_SALES_MANAGEMENT_FIELD).asBoolean());
//		testArrayRole.add(role);
//		
//		
//		testManager = new TestManager<SpecialPermission>("http://localhost:" + port + "/graphql"); 
//		int i = 13; 
//		String name = "name"+i; 
//		String description = "value"+i; 
//		String permissionTypeString = PermissionType.CONTACT.getName(); 
//		int value = 1;
//	 
//		ObjectNode variablesPermission = new ObjectMapper().createObjectNode();
//		ObjectNode inputPermission = variablesPermission.putObject("specialPermission"); 
//		
//		inputPermission.put(NAME_FIELD,name); 
//		inputPermission.put(DESCRIPTION_FIELD, description); 
//		inputPermission.put(TYPE_FIELD,permissionTypeString); 
//		inputPermission.put(VALUE_FIELD,value); 
// 
//		ObjectNode roleInput = inputPermission.putObject("role"); 
//		roleInput.put(ID_FIELD, role.getId()); 
//		
//		response = testManager.request(MUTATIONS_PATH + "addSpecialPermission.graphql", variablesPermission); 
//		jsonNode = testManager.checkResponse(response, "addSpecialPermission"); 
//		
//		Assert.assertEquals(name,jsonNode.get(NAME_FIELD).asText());
//		Assert.assertEquals(description,jsonNode.get(DESCRIPTION_FIELD).asText());
//		Assert.assertEquals(permissionTypeString,jsonNode.get(TYPE_FIELD).asText());
//		Assert.assertEquals(value,jsonNode.get(VALUE_FIELD).asBoolean());
//		
//		SpecialPermission specialPermission = new SpecialPermission(); 
//		specialPermission.setId(jsonNode.get(ID_FIELD).asLong());
//		specialPermission.setDescription(description);
//		specialPermission.setName(name);
//		specialPermission.setValue(value); 
//		specialPermission.setType(PermissionType.CONTACT);
//		testArray.add(specialPermission);
//	}
//	
//	@Test
//	@Order(2)
//	void getByRole() throws ParseException, IOException {
//		testManager = new TestManager<SpecialPermission>("http://localhost:" + port + "/graphql"); 
//		SpecialPermission specialPermission = testArray.getFirstElement();
//		
//		Role role = testArrayRole.getFirstElement();
//		ObjectNode input = new ObjectMapper().createObjectNode();
//		input.put("roleName",role.getName()); 
//		
//		Response response = testManager.request(QUERIES_PATH + "getAllSpecialPermissionsByRole.graphql", input); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getAllSpecialPermissionsByRole"); 
//		
//	   //	Assert.assertEquals(specialPermission.getId(),jsonNode.get(ID_FIELD).asLong());		
//	}
//	
//	@Test
//	@Order(3)
//	void getByID() throws ParseException, IOException {
//		testManager = new TestManager<SpecialPermission>("http://localhost:" + port + "/graphql"); 
//		SpecialPermission specialPermission = testArray.getFirstElement();
//		
//		ObjectNode input = new ObjectMapper().createObjectNode();
//		input.put(ID_FIELD,specialPermission.getId()); 
//		
//		Response response = testManager.request(QUERIES_PATH + "getSpecialPermissionById.graphql", input); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getSpecialPermissionById"); 
//		
//		Assert.assertEquals(specialPermission.getId(),jsonNode.get(ID_FIELD).asLong());		
//	}
//	
//	
//	@Test
//	@Order(4)
//	void getAll() throws IOException {
//		
//		testManager = new TestManager<SpecialPermission>("http://localhost:" + port + "/graphql"); 
//		ObjectNode variablesPermission = new ObjectMapper().createObjectNode();
//		Response response = testManager.request(QUERIES_PATH + "getAllSpecialPermissions.graphql", variablesPermission); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getAllSpecialPermissions"); 
//		Assert.assertEquals(testArray.getSize(),jsonNode.size());		
//	}
//	
//	@Test
//	@Order(5)
//	void update() throws IOException, ParseException {
//		
//		
//		testManager = new TestManager<SpecialPermission>("http://localhost:" + port + "/graphql"); 
//		
//		String name = "nameMOD"; 
//		String description = "valueMOD"; 
//		String permissionTypeString = PermissionType.SPECIAL_CASE.getName(); 
//		int value = 0; 
//	 
//		SpecialPermission specialPermission = testArray.getFirstElement();
//		ObjectNode inputPermission = new ObjectMapper().createObjectNode();
//		inputPermission.put(ID_FIELD,specialPermission.getId()); 
//		inputPermission.put(NAME_FIELD,name); 
//		inputPermission.put(DESCRIPTION_FIELD, description); 
//		inputPermission.put(TYPE_FIELD,permissionTypeString); 
//		inputPermission.put(VALUE_FIELD,value); 
// 
//		Response response = testManager.request(MUTATIONS_PATH + "updateSpecialPermissionById.graphql", inputPermission); 
//		JsonNode jsonNode = testManager.checkResponse(response, "updateSpecialPermissionById"); 
//		
//		Assert.assertEquals(specialPermission.getId(),jsonNode.get(ID_FIELD).asLong());
//		Assert.assertEquals(name,jsonNode.get(NAME_FIELD).asText());
//		Assert.assertEquals(description,jsonNode.get(DESCRIPTION_FIELD).asText());
//		Assert.assertEquals(permissionTypeString,jsonNode.get(TYPE_FIELD).asText());
//		Assert.assertEquals(value,jsonNode.get(VALUE_FIELD).asBoolean());
//		
//		specialPermission.setDescription(description);
//		specialPermission.setName(name);
//		specialPermission.setValue(value); 
//		specialPermission.setType(PermissionType.CONTACT);
//
//	}
//	
//	@Test
//	@Order(6)
//	void delete() throws IOException, ParseException {
//		
//		testManager = new TestManager<SpecialPermission>("http://localhost:" + port + "/graphql"); 
//		SpecialPermission specialPermission = testArray.getFirstElement();
//		
//		ObjectNode input = new ObjectMapper().createObjectNode();
//		input.put(ID_FIELD,specialPermission.getId()); 
//		
//		Response response = testManager.request(MUTATIONS_PATH + "deleteSpecialPermissionById.graphql", input); 
//		JsonNode jsonNode = testManager.checkResponse(response, "deleteSpecialPermissionById"); 
//		
//		Assert.assertEquals(specialPermission.getId(),jsonNode.get(ID_FIELD).asLong());		
//		
//	}
}
