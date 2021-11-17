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
import com.gatelab.microservice.bookbuilder.core.bookbuilderconst.PrivilegeType;
import com.gatelab.microservice.bookbuilder.core.persistence.model.privileges.Privilege;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;

import okhttp3.Response;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
 class TestPrivilege {
	

	@LocalServerPort
	private int port;
	private TestManager<Privilege> testManager;
	private static LocalRepositoryTestArray<Privilege> testArray = new LocalRepositoryTestArray<>();
	
	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "Privilege/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Privilege/";
	
	private static final String NAME_FILD = "name"; 
	private static final String VALUE_FILD = "value"; 
	private static final String ORDER_IN_LIST_FILD ="orderInList"; 
	private static final String LIST_ISSUE_VIEW_FILD = "listIssueView"; 
	private static final String DELIVERY_TYPES_FILD = "deliveryTypes"; 
	private static final String PRIVATE_TRANCHE_FILD = "privateTranche";
	private static final String SUBJECT_REGION_FILD = "subjectRegion"; 
	private static final String ALLOWRECONFIRMATION_FILD = "allowReconfirmation"; 
	private static final String DEFAULT_RECONFIRMATION_FILD = "defaultReconfirmation"; 
	private static final String POST_ALLOCATED_FILD = "postAllocated";
	private static final String HIGH_LIGHT_BASE_INFO_TO_SALE_FILD = "highLightBaseInfoToSale"; 
	private static final String TYPE_FILD = "type"; 
	private static final String ID_FIELD = "id";
	private static final String HOST = "http://localhost:"; 
	private static final String ENTRY_POINT = "/graphql"; 
	private static final String GRAPHQL_EXTENTION = ".graphql"; 
	
	private static final int LOCAL_REPOSITORY_SIZE = 100;
	
	@Test
	@Order(1)
	void add() throws IOException {
		testManager = new TestManager<Privilege>("http://localhost:" + port + "/graphql"); 
		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
			
			PrivilegeType type = (i%2==0)? PrivilegeType.ISSUE_NET:  PrivilegeType.PRODUCT_COVERAGE; 
			String  name = "name"+i; 
			String listIssueView = "list"+i; 
			String deliveryTypes ="delovery"+i; 
			boolean privateTranche = (i%2==0)? true:false; 
			boolean subjectRegion = (i%2==0)? true:false; 
			boolean allowReconfirmation = (i%2==0)? true:false; 
			boolean defaultReconfirmation= (i%2==0)? true:false; 
			boolean postAllocated= (i%2==0)? true:false; 
			boolean highLightBaseInfoToSale= (i%2==0)? true:false; 
			boolean value = (i%2==0)? true:false; 
			int orderInList = i; 
			
			ObjectNode variables = new ObjectMapper().createObjectNode();
			ObjectNode input = variables.putObject("privilege");
			input.put(NAME_FILD, name); 
			input.put(LIST_ISSUE_VIEW_FILD,listIssueView); 
			input.put(TYPE_FILD,PrivilegeType.LEGAL_STATUS.getName()); 
			
			Response response = testManager.request(MUTATIONS_PATH + "addPrivilege.graphql", variables); 
			JsonNode jsonNode = testManager.checkResponse(response, "addPrivilege"); 
			
			Assert.assertEquals(name,jsonNode.get(NAME_FILD).asText());
			Assert.assertEquals(listIssueView,jsonNode.get(LIST_ISSUE_VIEW_FILD).asText()); 
			
			Privilege privilege = new Privilege(); 
			privilege.setId(jsonNode.get(ID_FIELD).asLong());
			privilege.setName(name); 
			privilege.setListIssueView(listIssueView);
			testArray.add(privilege); 
		}
	}
	
	@Test
	@Order(2)
	void getByID() throws ParseException, IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Privilege privilege  = testArray.getFirstElement();
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,privilege.getId());
		
		Response response = testManager.request(QUERIES_PATH + "getPrivilegeById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getPrivilegeById");
		
		Assert.assertEquals(privilege.getId(),jsonNode.get(ID_FIELD).asLong());
		
		
	}
	
	@Test
	@Order(3)
	void getAll() throws IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		ObjectNode variables = new ObjectMapper().createObjectNode();
		Response response = testManager.request(QUERIES_PATH + "getAllPrivileges.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "getAllPrivileges");
		Assert.assertEquals(testArray.getSize(),jsonNode.size());	
	}
	
	@Test
	@Order(4)
	void update() throws ParseException, IOException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Privilege privilege  = testArray.getFirstElement();
		String  name = "na211me"; 
		String listIssueView = "l22ist"; 
	
		
		privilege.setName(name); 
		privilege.setListIssueView(listIssueView); 
		
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,privilege.getId());
		variables.put(NAME_FILD, name); 
		variables.put(TYPE_FILD, PrivilegeType.PRODUCT_COVERAGE.getName()); 
		variables.put(LIST_ISSUE_VIEW_FILD,listIssueView);
		
		Response response = testManager.request(MUTATIONS_PATH + "updatePrivilegeById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "updatePrivilegeById");
		
		Assert.assertEquals(privilege.getId(),jsonNode.get(ID_FIELD).asLong());
	}
	
	@Test
	@Order(5)
	void delete() throws IOException, ParseException {
		
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		Privilege privilege  = testArray.getFirstElement();
		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put(ID_FIELD,privilege.getId());
		
		Response response = testManager.request(MUTATIONS_PATH + "deletePrivilegeById.graphql", variables); 
		JsonNode jsonNode = testManager.checkResponse(response, "deletePrivilegeById");
		
		
	}
}
