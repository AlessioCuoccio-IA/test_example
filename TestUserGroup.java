//package com.gatelab.microservice.bookbuilder.core;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Assert;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.boot.web.server.LocalServerPort;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.gatelab.microservice.bookbuilder.core.bookbuilderconst.PermissionType;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.Company;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.IssuerSector;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.Desk;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.Region;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.permission.Permission;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.permission.SpecialPermission;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.role.Role;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.users.GroupType;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.users.UserGroup;
//import com.gatelab.microservice.bookbuilder.core.service.implementations.permission.SpecialPermissionServiceImpl;
//import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
//import com.gatelab.microservices.bookbulder.utils.TestConstants;
//import com.gatelab.microservices.bookbulder.utils.TestManager;
//import com.google.gson.JsonObject;
//
//import okhttp3.Response;
//
//@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
//@TestMethodOrder(OrderAnnotation.class)
//public class TestUserGroup {
//
//	@LocalServerPort
//	private int port;
//	private TestManager<UserGroup> testManager;
//	private static LocalRepositoryTestArray<UserGroup> testArray = new LocalRepositoryTestArray<>();
//	private static LocalRepositoryTestArray<UserGroup> testArrayHistory = new LocalRepositoryTestArray<>();
//	private static List<SpecialPermission> testArraySpecialPermission = new ArrayList<>(); 
//	
//	private static List<Integer> revisonNumber = new ArrayList<>(); 
//	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "User/UserGroup/";
//	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "User/UserGroup/";
//	private static final String MUTATIONS_PATH_COMPANY = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "Company/Company/";
//	private static final String MUTATIONS_PATH_REGION = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "Region/";
//	private static final String MUTATIONS_PATH_DESK = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "Desk/";
//	private static final String MUTATIONS_PATH_ROLE = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "Role/";
//	
//	private static final String MUTATIONS_PATH_GROUPTYPE = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "GroupType/";
//	private static final String MUTATIONS_PATH_PERMISSION = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Permission/";
//	private static final String ID_FIELD = "id";
//	private static final String NAME_FILD = "name"; 
//	private static final String SESSION_TIMEOUT_FILD = "sessionTimeoutHours"; 
//	private static final String REVISION_NUMBER_FILD = "revisionNumber"; 
//	private static final String REGION_FIELD = "region";
//	private static final String ABBREVIATION_FIELD = "abbreviation";
//	private static final String REGION_SALES_MANAGEMENT_FIELD = "regionSalesManagement";
//	private static final String DESK_FIELD = "desk";
//	private static final String SHORT_NAME_FIELD = "shortName";
//	private static final String VIEW_FILD = "view"; 
//	private static final String CREATE_FILD = "create"; 
//	private static final String DELETE_FILD = "delete"; 
//	private static final String EDIT_FILD = "edit";
//	private static final String PERMISSION_TYPE_FILD = "type"; 
//	private static final int LOCAL_REPOSITORY_SIZE = 100;
//	private String name = "mod"; 
//	private int sessionTimeOut = 1992; 
//	
//	private TestManager<Region> testManagerRegion;
//	private TestManager<Desk> testManagerDesk;
//	private TestManager<IssuerSector> testManagerIsssuerSerctor;
//	private TestManager<Role> testManagerRole;
//	private TestManager<Company> testManagerCompany;
//	private TestManager<Permission> testManagerPermission;
//	private TestManager<GroupType> testManagerGroupType;
//	private Role role = new Role();
//	private Desk deskCreated = new Desk();
//	private Region regionCreated = new Region();
//	private GroupType groupType = new GroupType(); 
//	
//	@Test
//	@Order(1)
//	void add() throws IOException {
//		
//		
//		// Region
//		testManagerRegion = new TestManager<Region>("http://localhost:" + port + "/graphql");
//		String region = "REGION MOD";
//		String abbreviation = "RGM";
//
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		ObjectNode inputRegion = variables.putObject("region");
//		inputRegion.put(REGION_FIELD, region);
//		inputRegion.put(ABBREVIATION_FIELD, abbreviation);
//
//		Response response = testManagerRegion.request(MUTATIONS_PATH_REGION + "addRegion.graphql", variables);
//		JsonNode jsonNode = testManagerRegion.checkResponse(response, "addRegion");
//
//		Assert.assertEquals(region, jsonNode.get(REGION_FIELD).asText());
//		Assert.assertEquals(abbreviation, jsonNode.get(ABBREVIATION_FIELD).asText());
//
//	
//
//		regionCreated.setId(jsonNode.get(ID_FIELD).asLong());
//		regionCreated.setAbbreviation(abbreviation);
//		regionCreated.setRegion(region);
//
//		// Desk
//		testManagerDesk = new TestManager<Desk>("http://localhost:" + port + "/graphql");
//		String desk = "DeskMod";
//		String shortName = "dsn";
//
//		variables = new ObjectMapper().createObjectNode();
//		ObjectNode inputDesk = variables.putObject("desk");
//		inputDesk.put(DESK_FIELD, desk);
//		inputDesk.put(SHORT_NAME_FIELD, shortName);
//
//		response = testManagerDesk.request(MUTATIONS_PATH_DESK + "addDesk.graphql", variables);
//		jsonNode = testManagerDesk.checkResponse(response, "addDesk");
//
//		Assert.assertEquals(desk, jsonNode.get(DESK_FIELD).asText());
//		Assert.assertEquals(shortName, jsonNode.get(SHORT_NAME_FIELD).asText());
//
//	
//		deskCreated.setId(jsonNode.get(ID_FIELD).asLong());
//		deskCreated.setDesk(desk);
//		deskCreated.setShortName(shortName);
//		
//		// Role
//		testManagerRole = new TestManager("http://localhost:" + port + "/graphql");
//
//		String roleName = "roleMod";
//		boolean regionSalesManagement = false;
//
//		variables = new ObjectMapper().createObjectNode();
//		ObjectNode inputRole = variables.putObject("role");
//		inputRole.put(NAME_FILD, roleName);
//		inputRole.put(REGION_SALES_MANAGEMENT_FIELD, regionSalesManagement);
//
//		response = testManagerRole.request(MUTATIONS_PATH_ROLE + "addRole.graphql", variables);
//		jsonNode = testManagerRole.checkResponse(response, "addRole");
//
//		Assert.assertEquals(roleName, jsonNode.get(NAME_FILD).asText());
//		Assert.assertEquals(regionSalesManagement, jsonNode.get(REGION_SALES_MANAGEMENT_FIELD).asBoolean());
//
//	
//		role.setId(jsonNode.get(ID_FIELD).asLong());
//		role.setName(jsonNode.get(NAME_FILD).asText());
//		role.setRegionSalesManagement(jsonNode.get(REGION_SALES_MANAGEMENT_FIELD).asBoolean());
//	
//		//GroupType
//		
//		testManagerGroupType = new TestManager("http://localhost:" + port + "/graphql");
//		String type = "gtype100";
//	
//		variables = new ObjectMapper().createObjectNode();
//		ObjectNode inputGroupType = variables.putObject("groupType");
//		inputGroupType.put("type", type);
//
//		response = testManagerGroupType.request(MUTATIONS_PATH_GROUPTYPE+ "addGroupType.graphql", variables);
//		jsonNode = testManagerGroupType.checkResponse(response, "addGroupType");
//
//		Assert.assertEquals(type, jsonNode.get("type").asText());
//		
//		groupType.setId(jsonNode.get(ID_FIELD).asLong());
//		groupType.setType(jsonNode.get("type").asText());
//		
//		
//		
//		testManager = new TestManager<UserGroup>("http://localhost:" + port + "/graphql"); 
//		
//				
//		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {	
//			String name = "group"+i; 
//			int sessionTimeOut = 100+i; 
//			
//			variables = new ObjectMapper().createObjectNode();
//			ObjectNode input = variables.putObject("userGroup"); 
//			input.put(NAME_FILD, name); 
//			input.put(SESSION_TIMEOUT_FILD, sessionTimeOut); 
//			
//			ObjectNode regionNodeObject = input.putObject("region");
//			regionNodeObject.put(ID_FIELD, regionCreated.getId());
//
//			ObjectNode deskNodeObject = input.putObject("desk");
//			deskNodeObject.put(ID_FIELD, deskCreated.getId());
//
//			ObjectNode roleNodeObject = input.putObject("role");
//			roleNodeObject.put(ID_FIELD, role.getId());
//			
//			ObjectNode groupNodeObject = input.putObject("groupType");
//			groupNodeObject.put(ID_FIELD, groupType.getId());
//			
//			ArrayNode permissionArrayObject = input.putArray("permissions"); 
//			ObjectNode permissionObject = new ObjectMapper().createObjectNode();
//			
//			permissionObject.put(VIEW_FILD,false); 
//			permissionObject.put(PERMISSION_TYPE_FILD, PermissionType.ORDER.getName()); 
//			permissionObject.put(CREATE_FILD,false); 
//			permissionObject.put(DELETE_FILD,false); 
//			permissionObject.put(EDIT_FILD,false);
//			
//			ObjectNode permissionObjectRole = permissionObject.putObject("role");
//			permissionObjectRole.put(ID_FIELD, role.getId());
//			permissionArrayObject.add(permissionObject);
//			
//			ArrayNode specialPermissionArrayObject = input.putArray("specialPermission"); 
//			ObjectNode specialPermissionObject = new ObjectMapper().createObjectNode();
//			
//			specialPermissionObject.put(NAME_FILD,"SpecialPermission"+i); 
//			specialPermissionObject.put(PERMISSION_TYPE_FILD, PermissionType.SPECIAL_CASE.getName()); 
//			specialPermissionObject.put("value",false);
//			specialPermissionArrayObject.add(specialPermissionObject);
//						
//			response = testManager.request(MUTATIONS_PATH + "addUserGroup.graphql", variables); 
//			jsonNode = testManager.checkResponse(response, "addUserGroup"); 
//			
//			Assert.assertEquals(name,jsonNode.get(NAME_FILD).asText());
//			Assert.assertEquals(sessionTimeOut,jsonNode.get(SESSION_TIMEOUT_FILD).asInt());
//			
//			UserGroup userGroup = new UserGroup();
//			userGroup.setId(jsonNode.get(ID_FIELD).asLong()); 
//			userGroup.setName(jsonNode.get(NAME_FILD).asText()); 
//			userGroup.setSessionTimeoutHours(sessionTimeOut); 
//			testArray.add(userGroup); 	
//		}
//	}
//	
//	
//	@Test
//	@Order(2)
//	void getByID() throws ParseException, IOException {
//
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		UserGroup userGroup = testArray.getFirstElement();
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD,userGroup.getId());
//		Response response = testManager.request(QUERIES_PATH + "getUserGroupById.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getUserGroupById");
//		
//		Assert.assertEquals(userGroup.getId(),jsonNode.get(ID_FIELD).asLong());
//		Assert.assertEquals(userGroup.getName(),jsonNode.get(NAME_FILD).asText());
//		Assert.assertEquals(userGroup.getSessionTimeoutHours(),jsonNode.get(SESSION_TIMEOUT_FILD).asInt());
//		
//		for(JsonNode element:jsonNode.get("specialPermission")) {
//			SpecialPermission specialPermission = new SpecialPermission(); 
//			specialPermission.setId(element.get(ID_FIELD).asLong()); 
//			specialPermission.setType(PermissionType.SPECIAL_CASE); 
//			testArraySpecialPermission.add(specialPermission); 
//		}	
//	}
//	
//	
//	@Test
//	@Order(3)
//	void getAll() throws IOException {
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		Response response = testManager.request(QUERIES_PATH + "getAllUserGroup.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getAllUserGroup");
//	}
//	
//	
//	@Test
//	@Order(4)
//	void update() throws ParseException, IOException {
//		
//		testManagerPermission = new TestManager<Permission>("http://localhost:" + port + "/graphql"); 
//		
//		boolean view = false; 
//		boolean create = false; 
//		boolean delete = false; 
//		boolean edit = false; 
//		ObjectNode variables_permission = new ObjectMapper().createObjectNode();
//		ObjectNode input_permission = variables_permission.putObject("permission"); 
//		
//		input_permission.put(VIEW_FILD,view); 
//		input_permission.put(PERMISSION_TYPE_FILD, PermissionType.ORDER.getName()); 
//		input_permission.put(CREATE_FILD,create); 
//		input_permission.put(DELETE_FILD,delete); 
//		input_permission.put(EDIT_FILD,edit); 
//	
//		ObjectNode inputu_role = input_permission.putObject("role"); 
//		inputu_role.put(ID_FIELD,role.getId());
//		
//
//		Response response = testManagerPermission.request(MUTATIONS_PATH_PERMISSION + "addPermission.graphql", variables_permission); 
//		JsonNode jsonNode  = testManagerPermission.checkResponse(response, "addPermission"); 
//		
//		Permission permission= new Permission();
//		permission.setId(jsonNode.get(ID_FIELD).asLong());
//		
//		
//		testManager = new TestManager("http://localhost:" + port + "/graphql");
//		
//		UserGroup userGroup = testArray.getFirstElement(); 
//		UserGroup historyUserGroup = new UserGroup(); 
//		historyUserGroup.setId(userGroup.getId()); 
//		historyUserGroup.setName(userGroup.getName()); 
//		historyUserGroup.setSessionTimeoutHours(userGroup.getSessionTimeoutHours()); 
//		testArrayHistory.add(historyUserGroup); 
//		
//		userGroup.setName(name); 
//		userGroup.setSessionTimeoutHours(sessionTimeOut); 
//		testArrayHistory.add(userGroup);
//		ObjectNode input = new ObjectMapper().createObjectNode();
//		input.put(ID_FIELD, userGroup.getId()); 
//		input.put(NAME_FILD, name); 
//		input.put(SESSION_TIMEOUT_FILD, sessionTimeOut);
//		
//		ObjectNode permissionObjectRole = input.putObject("role");
//		permissionObjectRole.put(ID_FIELD, role.getId());
//		
//		ObjectNode regionNodeObject = input.putObject("region");
//		regionNodeObject.put(ID_FIELD, regionCreated.getId());
//
//		ObjectNode deskNodeObject = input.putObject("desk");
//		deskNodeObject.put(ID_FIELD, deskCreated.getId());
//		
//		
//		ObjectNode groupTypeNodeObject = input.putObject("groupType");
//		groupTypeNodeObject.put(ID_FIELD, groupType.getId());
//
//		ArrayNode permissionArrayObject = input.putArray("permissions"); 
//		ObjectNode permissionObject = new ObjectMapper().createObjectNode();
//		permissionObject.put(ID_FIELD, permission.getId());
//		permissionObject.put(VIEW_FILD,false); 
//		permissionObject.put(PERMISSION_TYPE_FILD, PermissionType.ORDER.getName()); 
//		permissionObject.put(CREATE_FILD,false); 
//		permissionObject.put(DELETE_FILD,false); 
//		permissionObject.put(EDIT_FILD,false);
//
//		permissionObjectRole = permissionObject.putObject("role");
//		permissionObjectRole.put(ID_FIELD, role.getId());
//		permissionArrayObject.add(permissionObject);
//		
//		ArrayNode specialPermissionArrayObject = input.putArray("specialPermission"); 
//		SpecialPermission specialPermission = testArraySpecialPermission.get(0); 
//		
//		ObjectNode specialPermissionObject = new ObjectMapper().createObjectNode();
//		specialPermissionObject.put(ID_FIELD, specialPermission.getId()); 
//		specialPermissionObject.put(PERMISSION_TYPE_FILD, specialPermission.getType().toString()); 
//		specialPermissionArrayObject.add(specialPermissionObject);
//		
//	    response = testManager.request(MUTATIONS_PATH + "changeUserGroupById.graphql", input); 
//	    jsonNode = testManager.checkResponse(response, "changeUserGroupById");
//		
//		Assert.assertEquals(userGroup.getId(),jsonNode.get(ID_FIELD).asLong());
//		Assert.assertEquals(userGroup.getName(),jsonNode.get(NAME_FILD).asText());
//		Assert.assertEquals(userGroup.getSessionTimeoutHours(),jsonNode.get(SESSION_TIMEOUT_FILD).asInt());
//		
//	}
//	
//
//	@Test
//	@Order(5)
//	void getHistory() throws IOException, ParseException {
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		UserGroup userGroup = testArray.getFirstElement();
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD,userGroup.getId());
//		
//		Response response = testManager.request(QUERIES_PATH + "getUserGroupHistoryById.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getUserGroupHistoryById");
//		
//		int i = 0; 
//		for(JsonNode object: jsonNode) {
//	
//			long id = object.get(ID_FIELD).asLong();
//			String name =  object.get(NAME_FILD).asText();
//			int sessionTimeOut = object.get(SESSION_TIMEOUT_FILD).asInt();
//			UserGroup userGroupHistory = testArrayHistory.getElement(i%testArrayHistory.getSize()); 
//			i++; 
//			
//			Assert.assertEquals(userGroupHistory.getId(),id);
//			revisonNumber.add(object.get(REVISION_NUMBER_FILD).asInt()); 
//		}	
//
//	}
//	
//
//	@Test
//	@Order(6)
//	void recoverFromHistory() throws IOException, ParseException {
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		UserGroup userGroup = testArray.getFirstElement();	
//		UserGroup userGroupToRecover = testArrayHistory.getFirstElement();
//		userGroupToRecover.setId(userGroup.getId());
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD,userGroup.getId());
//		variables.put(REVISION_NUMBER_FILD,revisonNumber.get(0));
//		
//		Response response = testManager.request(MUTATIONS_PATH + "recoverUserGroupByRevisionNumber.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "recoverUserGroupByRevisionNumber");
//
//		Assert.assertEquals(userGroupToRecover.getId(),jsonNode.get(ID_FIELD).asLong());
//		Assert.assertEquals(userGroupToRecover.getName(),jsonNode.get(NAME_FILD).asText());
//		Assert.assertEquals(userGroupToRecover.getSessionTimeoutHours(),jsonNode.get(SESSION_TIMEOUT_FILD).asInt());
//	}
//	
//	@Test
//	@Order(7)
//	void delete() throws IOException, ParseException {
//		
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		UserGroup userGroup = testArrayHistory.getElement(1);
//		
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD,userGroup.getId());
//		
//		Response response = testManager.request(MUTATIONS_PATH + "deleteUserGroupById.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "deleteUserGroupById");
//		
//		Assert.assertEquals(userGroup.getId(),jsonNode.get(ID_FIELD).asLong());
//	}
//}
