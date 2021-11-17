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
import com.gatelab.microservice.bookbuilder.core.bookbuilderconst.PrivilegeType;
import com.gatelab.microservice.bookbuilder.core.persistence.model.permission.Permission;
import com.gatelab.microservice.bookbuilder.core.persistence.model.privileges.Privilege;
import com.gatelab.microservice.bookbuilder.core.persistence.model.role.Role;
import com.gatelab.microservice.bookbuilder.core.persistence.model.users.UserGroup;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;

import okhttp3.Response;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
class TestPermission  {
//		
//	
//	@LocalServerPort
//	private int port;
//	
//
//	private static final String NAME_FILD = "name";  
//	private static final String LIST_ISSUE_VIEW_FILD = "listIssueView"; 
//	private static final String TYPE_FILD = "type"; 
//	private static final String ID_FIELD = "id";
//	private static final String REGION_SALES_MANAGEMENT_FIELD = "regionSalesManagement";
//	private static final String VIEW_FILD = "view"; 
//	private static final String CREATE_FILD = "create"; 
//	private static final String DELETE_FILD = "delete"; 
//	private static final String EDIT_FILD = "edit"; 
//	private static final String PERMISSION_TYPE_FILD = "type"; 
//	private static final String SESSION_TIMEOUT_FILD = "sessionTimeoutHours"; 
//	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "Permission/";
//	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Permission/";
//	private static final String MUTATIONS_PATH_ROLE = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Role/";
//	private static final String MUTATIONS_PATH_PRIVILEGE = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Privilege/";
//	private static final String MUTATIONS_PATH_USER_GROUP = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "User/UserGroup/";
//	private TestManager<Role> testManagerRole;
//	private TestManager<Permission> testManager;
//	private TestManager<Privilege> testManagerPrivilege;
//	private TestManager<UserGroup> testManagerUserGroup;
//	
//	private static LocalRepositoryTestArray<Permission> testArray = new LocalRepositoryTestArray<>();
//	private static LocalRepositoryTestArray<Role> testArrayRole = new LocalRepositoryTestArray<>();
//	private static LocalRepositoryTestArray<UserGroup> testArrayUserGroup = new LocalRepositoryTestArray<>();
//	
//	
//	@Test
//	@Order(1)
//	void addPermission() throws IOException {
//		Privilege privilege = new Privilege(); 
//		Role role = new Role(); 
//
//		String name = "LLL"; 
//		int sessionTimeOut = 100; 
//		testManagerUserGroup = new TestManager<UserGroup>("http://localhost:" + port + "/graphql"); 
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		ObjectNode input = variables.putObject("userGroup"); 
//		input.put(NAME_FILD, name); 
//		input.put(SESSION_TIMEOUT_FILD, sessionTimeOut); 
//		
//		Response response = testManagerUserGroup.request(MUTATIONS_PATH_USER_GROUP + "addUserGroup.graphql", variables); 
//		JsonNode jsonNode = testManagerUserGroup.checkResponse(response, "addUserGroup"); 
//		
//		Assert.assertEquals(name,jsonNode.get(NAME_FILD).asText());
//		Assert.assertEquals(sessionTimeOut,jsonNode.get(SESSION_TIMEOUT_FILD).asInt());
//		
//		UserGroup userGroup = new UserGroup();
//		userGroup.setId(jsonNode.get(ID_FIELD).asLong()); 
//		userGroup.setName(jsonNode.get(NAME_FILD).asText()); 
//		userGroup.setSessionTimeoutHours(sessionTimeOut); 
//		testArrayUserGroup.add(userGroup); 	
//		
//		testManagerRole = new TestManager<Role>("http://localhost:" + port + "/graphql"); 
//		String roleName = "oosaoa"; 
//		boolean regionSalesManagement = false;
//		
//		
//		variables = new ObjectMapper().createObjectNode();
//		input = variables.putObject("role"); 
//		input.put(NAME_FILD, roleName); 
//		input.put(REGION_SALES_MANAGEMENT_FIELD, regionSalesManagement); 
//
//		
//		response = testManagerRole.request(MUTATIONS_PATH_ROLE + "addRole.graphql", variables); 
//		jsonNode = testManagerRole.checkResponse(response, "addRole"); 
//		
//		Assert.assertEquals(roleName,jsonNode.get(NAME_FILD).asText());
//		Assert.assertEquals(regionSalesManagement,jsonNode.get(REGION_SALES_MANAGEMENT_FIELD).asBoolean());
//		
//		role.setId(jsonNode.get(ID_FIELD).asLong()); 
//		role.setName(roleName); 
//		role.setRegionSalesManagement(regionSalesManagement); 
//		testArrayRole.add(role); 
//		
//		testManagerPrivilege = new TestManager<Privilege>("http://localhost:" + port + "/graphql"); 
//		name = "name"+"m2"; 
//		String listIssueView = "list"+"m2"; 
//	
//		variables = new ObjectMapper().createObjectNode();
//		input = variables.putObject("privilege");
//		input.put(NAME_FILD, name); 
//		input.put(LIST_ISSUE_VIEW_FILD,listIssueView); 
//		input.put(TYPE_FILD,PrivilegeType.LEGAL_STATUS.getName()); 
//		
//		response = testManagerPrivilege.request(MUTATIONS_PATH_PRIVILEGE + "addPrivilege.graphql", variables); 
//		jsonNode = testManagerPrivilege.checkResponse(response, "addPrivilege"); 
//		
//		Assert.assertEquals(name,jsonNode.get(NAME_FILD).asText());
//		Assert.assertEquals(listIssueView,jsonNode.get(LIST_ISSUE_VIEW_FILD).asText()); 
//
//		privilege.setId(jsonNode.get(ID_FIELD).asLong());
//		privilege.setName(name); 
//		privilege.setListIssueView(listIssueView);
//		
//		testManager = new TestManager<Permission>("http://localhost:" + port + "/graphql"); 
//		int i = 13; 
//		int view =0;
//		int create=1; 
//		int delete=0; 
//		int edit = 1; 
//		
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
//		ObjectNode inputu_privilege = input_permission.putObject("privilege"); 
//		inputu_privilege.put(ID_FIELD,privilege.getId());
//		
//		ObjectNode inputu_userGroup = input_permission.putObject("userGroup"); 
//		inputu_userGroup.put(ID_FIELD,userGroup.getId());
//		
//		
//		response = testManager.request(MUTATIONS_PATH + "addPermission.graphql", variables_permission); 
//		jsonNode = testManager.checkResponse(response, "addPermission"); 
//		
//		Assert.assertEquals(create,jsonNode.get(CREATE_FILD).asBoolean());
//		Assert.assertEquals(edit,jsonNode.get(EDIT_FILD).asBoolean());
//		Assert.assertEquals(view,jsonNode.get(VIEW_FILD).asBoolean());
//		Assert.assertEquals(delete,jsonNode.get(DELETE_FILD).asBoolean());
//		Permission globalPermission= new Permission();
//		globalPermission.setId(jsonNode.get(ID_FIELD).asLong());
//		globalPermission.setEdit(edit);
//		globalPermission.setCreate(create);
//		globalPermission.setView(view);
//		globalPermission.setDelete(delete); 
//		
//		testArray.add(globalPermission);
//	}
//	
//	@Test
//	@Order(2)
//	void getByID() throws ParseException, IOException {
//		
//		Permission permission  = testArray.getFirstElement();
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD, permission.getId());
//		
//		Response response = testManager.request(QUERIES_PATH + "getPermissionById.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getPermissionById");
//		
//		Assert.assertEquals(permission.getId(),jsonNode.get(ID_FIELD).asLong());
//	}
//	
//	@Test
//	@Order(3)
//	void getAll() throws IOException {
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		
//		Response response = testManager.request(QUERIES_PATH + "getAllPermission.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getAllPermissions");
//		
//		//Assert.assertEquals(testArray.getSize(),jsonNode.size());
//	}
//	
//	@Test
//	@Order(4)
//	void getPermissionByRole() throws IOException, ParseException {
//		
//		Permission permission  = testArray.getFirstElement();
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		Role role = testArrayRole.getFirstElement();
//		variables.put("roleName", role.getName());
//		
//		Response response = testManager.request(QUERIES_PATH + "getAllPermissionsByRole.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getAllPermissionsByRole");
//		
////		Assert.assertEquals(true,jsonNode.get(0).get(CREATE_FILD).asBoolean());
////		Assert.assertEquals(true,jsonNode.get(0).get(DELETE_FILD).asBoolean());
////		Assert.assertEquals(true,jsonNode.get(0).get(EDIT_FILD).asBoolean());
////		Assert.assertEquals(true,jsonNode.get(0).get(VIEW_FILD).asBoolean());
//		
//	}
//	
//	
//	
//	@Test
//	@Order(5)
//	void getPermissionByUserGroup() throws IOException, ParseException {
//		
//		UserGroup userGroup = testArrayUserGroup.getFirstElement(); 
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD, userGroup.getId());
//		
//		Response response = testManager.request(QUERIES_PATH + "getAllPermissionsByUserGroupId.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getAllPermissionsByUserGroupId");
//		Permission permission = testArray.getFirstElement();
//	//	Assert.assertEquals(permission.getId(),jsonNode.get(0).get(ID_FIELD).asLong());
//	}
//	
//	@Test
//	@Order(6)
//	void update() throws IOException, ParseException {
//		
//		//Permission permission  = testArray.getFirstElement();
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		Permission permission = testArray.getFirstElement();
//		int i = 12; 
//		int view =0;
//		int create=1; 
//		int delete=0; 
//		int edit = 1; 
//		
//		permission.setCreate(create); 
//		permission.setView(view); 
//		permission.setDelete(delete); 
//		permission.setEdit(edit); 
//		
//		ObjectNode input_permission = new ObjectMapper().createObjectNode();
//		input_permission.put(VIEW_FILD,view); 
//		input_permission.put(PERMISSION_TYPE_FILD, PermissionType.ORDER.getName()); 
//		input_permission.put(CREATE_FILD,create); 
//		input_permission.put(DELETE_FILD,delete); 
//		input_permission.put(EDIT_FILD,edit); 
//		input_permission.put(ID_FIELD,permission.getId());
//		Response response = testManager.request(MUTATIONS_PATH + "updatePermissionById.graphql", input_permission); 
//		JsonNode jsonNode = testManager.checkResponse(response, "updatePermissionById");
//		
//		Assert.assertEquals(permission.getId(),jsonNode.get(ID_FIELD).asLong());
//		Assert.assertEquals(create,jsonNode.get(CREATE_FILD).asBoolean());
//		Assert.assertEquals(edit,jsonNode.get(EDIT_FILD).asBoolean());
//		Assert.assertEquals(view,jsonNode.get(VIEW_FILD).asBoolean());
//		Assert.assertEquals(delete,jsonNode.get(DELETE_FILD).asBoolean());		
//	
//	}
//	
//	@Test
//	@Order(7)
//	void delete() throws IOException, ParseException {
//		Permission permission  = testArray.getFirstElement();
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD, permission.getId());
//		
//		Response response = testManager.request(MUTATIONS_PATH + "deletePermissionById.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "deletePermissionById");
//		Assert.assertEquals(permission.getId(),jsonNode.get(ID_FIELD).asLong());
//	}

}
