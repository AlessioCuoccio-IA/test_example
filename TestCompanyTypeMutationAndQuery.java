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
import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.Company;
import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.CompanyType;
import com.gatelab.microservice.bookbuilder.core.persistence.model.role.Role;
import com.gatelab.microservice.bookbuilder.core.persistence.model.users.UserAccount;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;

import okhttp3.Response;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestCompanyTypeMutationAndQuery {

	@LocalServerPort
	private int port;
	private TestManager<CompanyType> testManager;
	private TestManager<Role> testManagerRole;
	
	private static LocalRepositoryTestArray<Role> testArrayRole = new LocalRepositoryTestArray<>();
	private static LocalRepositoryTestArray<CompanyType> testArray = new LocalRepositoryTestArray<>();
	
	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "Company/CompanyType/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Company/CompanyType/";
	
	private static final String QUERIES_PATH_ROLE = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "Role/";
	private static final String MUTATIONS_PATH_ROLE = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Role/";
	
	
	private static final String ID_FIELD = "id";
	private static final String NAME_FIELD = "name";
	private static final String REGION_SALES_MANAGEMENT_FIELD = "regionSalesManagement";
	private static final String TYPE_FIELD = "type";
	private static final String ABBREVIATION_FIELD = "abbreviation";
	
	private static final int LOCAL_REPOSITORY_SIZE = 100;
	
	
	@Test
	@Order(1)
	void add() throws IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		testManagerRole = new TestManager("http://localhost:" + port + "/graphql"); 
		
		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
			
			String roleName = "role"+i+"T"; 
			boolean regionSalesManagement = (i%2==0)? true : false;
			
			ObjectNode variables = new ObjectMapper().createObjectNode();
			ObjectNode input = variables.putObject("role"); 
			input.put(NAME_FIELD, roleName); 
			input.put(REGION_SALES_MANAGEMENT_FIELD, regionSalesManagement); 

			
			Response response = testManagerRole.request(MUTATIONS_PATH_ROLE + "addRole.graphql", variables); 
			JsonNode jsonNode = testManagerRole.checkResponse(response, "addRole"); 
			
			Assert.assertEquals(roleName,jsonNode.get(NAME_FIELD).asText());
			Assert.assertEquals(regionSalesManagement,jsonNode.get(REGION_SALES_MANAGEMENT_FIELD).asBoolean());
			
			
			Role role = new Role();
			role.setId(jsonNode.get(ID_FIELD).asLong()); 
			role.setName(jsonNode.get(NAME_FIELD).asText()); 
			role.setRegionSalesManagement(jsonNode.get(REGION_SALES_MANAGEMENT_FIELD).asBoolean());
			testArrayRole.add(role);

			
			String type = "type"+i; 
			String abbreviation = "abbreviation"+i; 
			
			variables = new ObjectMapper().createObjectNode();
			input = variables.putObject("companyType"); 
			input.put(ABBREVIATION_FIELD, abbreviation); 
			input.put(TYPE_FIELD, type); 
			ObjectNode roleInput = input.putObject("role"); 
			roleInput.put(ID_FIELD, role.getId());
			roleInput.put("name", role.getName());
			
			response = testManager.request(MUTATIONS_PATH + "addCompanyType.graphql", variables); 
			jsonNode = testManager.checkResponse(response, "addCompanyType"); 
			
			Assert.assertEquals(type,jsonNode.get(TYPE_FIELD).asText());
			Assert.assertEquals(abbreviation,jsonNode.get(ABBREVIATION_FIELD).asText());
			
			CompanyType companyType = new CompanyType(); 
			companyType.setId(jsonNode.get(ID_FIELD).asLong()); 
			companyType.setType(jsonNode.get(TYPE_FIELD).asText()); 
			companyType.setAbbreviation(jsonNode.get(ABBREVIATION_FIELD).asText()); 
			testArray.add(companyType); 
		}
	}
	
	@Test
	@Order(2)
    void getByID() throws IOException, ParseException {

		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		CompanyType companyType = testArray.getFirstElement();
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,companyType.getId());
		
		Response response = testManager.request(QUERIES_PATH + "getCompanyTypeById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getCompanyTypeById");
		
		Assert.assertEquals(companyType.getType(),jsonNode.get(TYPE_FIELD).asText());
		Assert.assertEquals(companyType.getAbbreviation(),jsonNode.get(ABBREVIATION_FIELD).asText());	
		
	}
	
	
	@Test
	@Order(3)
	void getAll() throws IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		ObjectNode variables = new ObjectMapper().createObjectNode();
		Response response = testManager.request(QUERIES_PATH + "getAllCompanyType.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getAllCompanyType");
		
		
	}
	
	@Test
	@Order(4)
	void update() throws ParseException, IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		CompanyType companyType = testArray.getFirstElement();
		
		
		String type = "typeMod"; 
		String abbreviation = "abbreviationMod"; 
		
		companyType.setType(type); 
		companyType.setAbbreviation(abbreviation); 
		
		ObjectNode variables = new ObjectMapper().createObjectNode();
		ObjectNode input = variables.putObject("companyType"); 
		variables.put(ID_FIELD,companyType.getId());
		input.put(ABBREVIATION_FIELD, abbreviation); 
		input.put(TYPE_FIELD, type); 
		
		Response response = testManager.request(MUTATIONS_PATH + "changeCompanyTypeById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "changeCompanyTypeById");
		
		Assert.assertEquals(companyType.getId(),jsonNode.get(ID_FIELD).asLong());


		
			
	}
	
	
	
	@Test
	@Order(5)
	void delete() throws ParseException, IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		CompanyType companyType = testArray.getFirstElement();
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,companyType.getId());
		
		Response response = testManager.request(MUTATIONS_PATH + "deleteCompanyTypeById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "deleteCompanyTypeById");
		
		Assert.assertEquals(companyType.getId(),jsonNode.get(ID_FIELD).asLong());
	}
	
	
	
}
