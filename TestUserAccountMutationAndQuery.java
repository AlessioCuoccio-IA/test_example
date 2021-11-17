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
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.Company;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.Desk;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.Region;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.role.Role;
//import com.gatelab.microservice.bookbuilder.core.persistence.model.users.UserAccount;
//import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
//import com.gatelab.microservices.bookbulder.utils.TestConstants;
//import com.gatelab.microservices.bookbulder.utils.TestManager;
//
//
//import okhttp3.Response;
//
//@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
//@TestMethodOrder(OrderAnnotation.class)
//public class TestUserAccountMutationAndQuery {
//
//	@LocalServerPort
//	private int port;
//	private TestManager<UserAccount> testManager;
//	private TestManager<Company> testManagerCompany;
//	private TestManager<Role> testManagerRole;
//	private TestManager<Desk> testManagerDesk;
//	
//	private static LocalRepositoryTestArray<UserAccount> testArray = new LocalRepositoryTestArray<>();
//	private static LocalRepositoryTestArray<UserAccount> testArrayHistory = new LocalRepositoryTestArray<>();
//	private static List<Integer> revisonNumber = new ArrayList<>(); 
//	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "User/UserAccount/";
//	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "User/UserAccount/";
//	
//	
//	private static final String ID_FIELD = "id";
//	private static final String FIRST_NAME_FIELD = "firstName";
//	private static final String SECOND_NAME_FIELD = "secondName";
//	private static final String EMAIL_FIELD = "email";
//	private static final String USER_NAME_FIELD = "userName";
//	private static final String PASSWORD_FIELD = "password";
//	private static final String LOCK_FIELD = "lock"; 
//	private static final String OVVERIDE_INVESTOR_TYPE_FILD = "ovverideInvestorType";
//	private static final String REVISION_NUMBER_FILD = "revisionNumber";
//	
//	
//	private static final String ALIAS_FILD = "alias";
//	private static final String JOB_DESCRIPTION_FILD = "jobDescription";
//	private static final String ADDRESS_FILD = "address";
//	private static final String PHONE_NUMBER_FILD = "phoneNumber";
//	private static final String MOBILE_NUMBER_FILD = "mobileNumber";
//	private static final String FAX_NUMBER_FILD = "faxNumber";
//	private static final String EMAIL_ADDRESS_SECOND_FILD = "emailAddressSecond";
//	private static final String EXTERNAL_ID_FILD = "externalId";
//	private static final String MIDDLE_INITIAL_FILD = "middleInitial";
//	
//	private static final String DEAL_LIST_REFRESH_FILD = "dealListRefreshInterval";
//	private static final String ORDER_BOOK_REFRESH_FILD = "orderBookRefreshInterval";
//	private static final String ORDER_BOOK_TICKER_FILD = "orderBookTickerRefreshInterval";
//	private static final String MODIFIED_ORDER_DURATION_FILD = "modifiedOrderHighlightDuration";
//	private static final String DEFAULT_TREMSHEET_FILD = "defaultTermsheetType";
//	private static final String LAST_FULL_HIGHT_FILD = "lastFullHighlight";
//
//	private static final String DEFAULT_PRODUCT_COVERAGE_FILD = "defaultProductCoverageView";
//	private static final String SHOW_MAX_TOTAL_FILD = "showMaxTotal";
//	private static final String DEFAULT_REC_DEALS_FILD = "defaultRecDealsStartPage";
//
//
//	private static final String NAME_FIELD = "name";
//	private static final String REGION_SALES_MANAGEMENT_FIELD = "regionSalesManagement";
//	
//	private static final String REGION_FIELD = "region";
//	private static final String ABBREVIATION_FIELD = "abbreviation";
//
//	private static final String DESK_FIELD = "desk";
//	private static final String SHORT_NAME_FIELD = "shortName";
//	
//	private static final String COMPANY_FILD = "company";
//	private static final String SUBSIDIARY_FILD = "subsidiary";
//	private static final String ROLE_FILD = "role";
//	private static final String DESK_FILD = "desk";
//	private static final String USERGROUP_FILD = "userGroup";
//	private static final int LOCAL_REPOSITORY_SIZE = 100;
//	
//	private static final String MUTATIONS_PATH_COMPANY = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "Company/Company/";
//	private static final String MUTATIONS_PATH_DESK = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "Desk/";
//	private static final String MUTATIONS_PATH_ROLE = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "Role/";
//	
//	Role role = new Role();
//	Desk deskCreated = new Desk();
//	
//	@Test
//	@Order(1)
//	void addData() throws IOException {
//
//		// Desk
//		testManagerDesk = new TestManager<Desk>("http://localhost:" + port + "/graphql");
//		String desk = "DeskMod76543";
//		String shortName = "dsn32";
//
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		ObjectNode inputDesk = variables.putObject("desk");
//		inputDesk.put(DESK_FIELD, desk);
//		inputDesk.put(SHORT_NAME_FIELD, shortName);
//
//		Response response = testManagerDesk.request(MUTATIONS_PATH_DESK + "addDesk.graphql", variables);
//		JsonNode jsonNode = testManagerDesk.checkResponse(response, "addDesk");
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
//		String roleName = "roleMod876543";
//		boolean regionSalesManagement = false;
//
//		variables = new ObjectMapper().createObjectNode();
//		ObjectNode inputRole = variables.putObject("role");
//		inputRole.put(NAME_FIELD, roleName);
//		inputRole.put(REGION_SALES_MANAGEMENT_FIELD, regionSalesManagement);
//
//		response = testManagerRole.request(MUTATIONS_PATH_ROLE + "addRole.graphql", variables);
//		jsonNode = testManagerRole.checkResponse(response, "addRole");
//
//		Assert.assertEquals(roleName, jsonNode.get(NAME_FIELD).asText());
//		Assert.assertEquals(regionSalesManagement, jsonNode.get(REGION_SALES_MANAGEMENT_FIELD).asBoolean());
//
//		
//		role.setId(jsonNode.get(ID_FIELD).asLong());
//		role.setName(jsonNode.get(NAME_FIELD).asText());
//		role.setRegionSalesManagement(jsonNode.get(REGION_SALES_MANAGEMENT_FIELD).asBoolean());
//	}
//	
//	@Test
//	@Order(1)
//	void add() throws IOException {
//		testManager = new TestManager<UserAccount>("http://localhost:" + port + "/graphql"); 
//		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
//			
//			String fistName = "firstName"+i; 
//			String secondName = "secondName"+i; 
//			String email = "email"+i+"@gmail.com"; 
//			String userName = "userName"+i; 
//			String password = "password"+i; 
//			String alias = "alisa"+i; 
//			String jobDescrtiption ="jobDescrtiption"+i; 
//			String address ="address"+i; 
//			String phoneNumber = "phoneNumber"+i; 
//			String mobileNumber = "mobileNumber"+i; 
//			String faxNumber = "faxNumber"+i; 
//			String emailAddressSecond ="emailAddressSecond"+i; 
//			String externalId ="externalId"+i; 
//			String middleInitial = "middleInitial"; 
//			String defaultRecDealsStartPage = "Orderbook";
//			String defaultProductCoverageView = "All Products";
//			String defaultTremsheetType = "Standard"; 
//			int dealListRefreshInternal = i; 
//			int orderBookRefreshInterval = i; 
//			int orderBookTickerRefreshInterval = i; 
//			int modifiedOrderHighLishtDuration = i; 
//			boolean lastFullHighLight =  (i%2==0)?true:false;
//			boolean showMaxTotal = (i%2==0)?true:false;
//			boolean lock = (i%2==0)?true:false;
//			boolean ovverideInvestorType = (i%2==0)?true:false;	
//						
//			ObjectNode variables = new ObjectMapper().createObjectNode();
//			ObjectNode input = variables.putObject("user"); 
//			input.put(FIRST_NAME_FIELD, fistName);
//			input.put(SECOND_NAME_FIELD, secondName); 
//			input.put(EMAIL_FIELD, email); 
//			input.put(USER_NAME_FIELD, userName);
//			input.put(PASSWORD_FIELD, password); 
//			input.put(LOCK_FIELD, lock); 
//			input.put(OVVERIDE_INVESTOR_TYPE_FILD, ovverideInvestorType); 
//			
//			input.put(ALIAS_FILD, alias); 	
//			input.put(JOB_DESCRIPTION_FILD, jobDescrtiption); 
//			input.put(ADDRESS_FILD, address);
//			input.put(PHONE_NUMBER_FILD , phoneNumber); 
//			input.put(MOBILE_NUMBER_FILD , mobileNumber); 
//			input.put(FAX_NUMBER_FILD , faxNumber);
//			input.put(EMAIL_ADDRESS_SECOND_FILD , emailAddressSecond); 
//			input.put(MIDDLE_INITIAL_FILD , middleInitial); 
//			input.put(EXTERNAL_ID_FILD, externalId);
//			
//			input.put(DEAL_LIST_REFRESH_FILD , dealListRefreshInternal); 
//			input.put(ORDER_BOOK_REFRESH_FILD , orderBookRefreshInterval); 	
//			input.put(ORDER_BOOK_TICKER_FILD , orderBookTickerRefreshInterval); 
//			input.put(MODIFIED_ORDER_DURATION_FILD  , modifiedOrderHighLishtDuration); 
//			input.put(DEFAULT_TREMSHEET_FILD  , defaultTremsheetType); 	
//			input.put(LAST_FULL_HIGHT_FILD  , lastFullHighLight); 
//			
//			input.put(DEFAULT_PRODUCT_COVERAGE_FILD   , defaultProductCoverageView); 
//			input.put(SHOW_MAX_TOTAL_FILD   , showMaxTotal); 	
//			input.put(DEFAULT_REC_DEALS_FILD   , defaultRecDealsStartPage); 
//
//			ObjectNode deskNodeObject = input.putObject("desk");
//			deskNodeObject.put(ID_FIELD, deskCreated.getId());
//			
//			ObjectNode roleNodeObject = input.putObject("role");
//			roleNodeObject.put(ID_FIELD, role.getId());
//			
//			Response response = testManager.request(MUTATIONS_PATH + "addUserAccount.graphql", variables); 
//			JsonNode jsonNode = testManager.checkResponse(response, "addUserAccount"); 
//			
//			Assert.assertEquals(fistName,jsonNode.get(FIRST_NAME_FIELD).asText());
//			Assert.assertEquals(secondName,jsonNode.get(SECOND_NAME_FIELD).asText());
//			Assert.assertEquals(email,jsonNode.get(EMAIL_FIELD).asText());
//			Assert.assertEquals(userName,jsonNode.get(USER_NAME_FIELD).asText());
//			Assert.assertEquals(password,jsonNode.get(PASSWORD_FIELD).asText());
//			Assert.assertEquals(lock,jsonNode.get(LOCK_FIELD).asBoolean());
//			Assert.assertEquals(ovverideInvestorType,jsonNode.get(OVVERIDE_INVESTOR_TYPE_FILD).asBoolean());
//			
//			UserAccount userAccount = new UserAccount(); 
//			userAccount.setId(jsonNode.get(ID_FIELD).asLong()); 
//			userAccount.setFirstName(fistName); 
//			userAccount.setSecondName(secondName); 
//			userAccount.setEmail(email); 
//			userAccount.setPassword(password); 
//			userAccount.setUserName(userName); 
//			userAccount.setLock(lock);
//			userAccount.setOvverideInvestorType(ovverideInvestorType); 
//			testArray.add(userAccount); 	
//			
//		}
//		
//	}
//	
//	@Test
//	@Order(2)
//	void getByID() throws ParseException, IOException {	
//		
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		
//		UserAccount userAccount = testArray.getFirstElement();
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD,userAccount.getId());
//		Response response = testManager.request(QUERIES_PATH + "getUserAccountById.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getUserAccountById");
//		
//		Assert.assertEquals(userAccount.getId(),jsonNode.get(ID_FIELD).asLong());
//		Assert.assertEquals(userAccount.getFirstName(),jsonNode.get(FIRST_NAME_FIELD).asText());
//		Assert.assertEquals(userAccount.getSecondName(),jsonNode.get(SECOND_NAME_FIELD).asText());
//		Assert.assertEquals(userAccount.getEmail(),jsonNode.get(EMAIL_FIELD).asText());
//		Assert.assertEquals(userAccount.getPassword(),jsonNode.get(PASSWORD_FIELD).asText());
//		Assert.assertEquals(userAccount.getUserName(),jsonNode.get(USER_NAME_FIELD).asText());
//		Assert.assertEquals(userAccount.isLock(),jsonNode.get(LOCK_FIELD).asBoolean());
//		Assert.assertEquals(userAccount.isOvverideInvestorType(),jsonNode.get(OVVERIDE_INVESTOR_TYPE_FILD).asBoolean());
//	}
//	
//	@Test
//	@Order(3)
//	void getAll() throws IOException {
//		
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		Response response = testManager.request(QUERIES_PATH + "getAllUserAccount.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getAllUserAccounts");
//		Assert.assertEquals(testArray.getSize(),jsonNode.size());	
//
//	}
//	
//	@Test
//	@Order(4)
//	void update() throws ParseException, IOException {
//		
//		UserAccount userAccount = testArray.getFirstElement();
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		
//		UserAccount historyUserAccount = new UserAccount(); 
//		historyUserAccount.setFirstName(userAccount.getFirstName()); 
//		historyUserAccount.setSecondName(userAccount.getSecondName()); 
//		historyUserAccount.setEmail(userAccount.getEmail()); 
//		historyUserAccount.setUserName(userAccount.getUserName()); 
//		historyUserAccount.setPassword(userAccount.getPassword());
//		historyUserAccount.setLock(userAccount.isLock()); 
//		historyUserAccount.setOvverideInvestorType(userAccount.isOvverideInvestorType());
//		
//		testArrayHistory.add(historyUserAccount); 
//		
//		String fistName = "firstNameMod"; 
//		String secondName = "secondNameMode"; 
//		String email = "email"+"@gmail.com"; 
//		String userName = "userNameMod"; 
//		String password = "passwordMod"; 
//		boolean lock = false;
//		boolean ovverideInvestorType=false;
//		
//		userAccount.setFirstName(fistName);
//		userAccount.setSecondName(secondName);
//		userAccount.setEmail(email);
//		userAccount.setUserName(userName);
//		userAccount.setPassword(password);
//		userAccount.setLock(lock);
//		userAccount.setOvverideInvestorType(ovverideInvestorType);
//		
//		testArrayHistory.add(userAccount); 
//		
//		ObjectNode inputObject = new ObjectMapper().createObjectNode();
//		ObjectNode input = inputObject.putObject("user");
//		input.put(ID_FIELD, userAccount.getId());
//		input.put(FIRST_NAME_FIELD, fistName);
//		input.put(SECOND_NAME_FIELD, secondName); 
//		input.put(EMAIL_FIELD, email); 
//		input.put(USER_NAME_FIELD, userName);
//		input.put(PASSWORD_FIELD, password); 
//		input.put(LOCK_FIELD, lock); 
//		input.put(OVVERIDE_INVESTOR_TYPE_FILD, ovverideInvestorType); 
//		
//		ObjectNode deskNodeObject = input.putObject("desk");
//		deskNodeObject.put(ID_FIELD, deskCreated.getId());
//
//		ObjectNode roleNodeObject = input.putObject("role");
//		roleNodeObject.put(ID_FIELD, role.getId());
//		
//		Response response = testManager.request(MUTATIONS_PATH + "updateUserAccountById.graphql", inputObject); 
//		JsonNode jsonNode = testManager.checkResponse(response, "updateUserAccountById");
//		
//		Assert.assertEquals(userAccount.getId(),jsonNode.get(ID_FIELD).asLong());
//		Assert.assertEquals(fistName,jsonNode.get(FIRST_NAME_FIELD).asText());
//		Assert.assertEquals(secondName,jsonNode.get(SECOND_NAME_FIELD).asText());
//		Assert.assertEquals(email,jsonNode.get(EMAIL_FIELD).asText());
//		Assert.assertEquals(userName,jsonNode.get(USER_NAME_FIELD).asText());
//		Assert.assertEquals(password,jsonNode.get(PASSWORD_FIELD).asText());
//		Assert.assertEquals(lock,jsonNode.get(LOCK_FIELD).asBoolean());
//		Assert.assertEquals(ovverideInvestorType,jsonNode.get(OVVERIDE_INVESTOR_TYPE_FILD).asBoolean());
//
//	}
//	
//	@Test
//	@Order(5)
//	void getUserAccountHistory() throws IOException, ParseException {
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		UserAccount userAccount = testArray.getFirstElement();
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD,userAccount.getId());
//		
//		Response response = testManager.request(QUERIES_PATH + "getUserAccountHistoryById.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getUserAccountHistoryById");
//		
//		int i = 0; 
//		for(JsonNode object: jsonNode) {
//	
//			String name = object.get(FIRST_NAME_FIELD).asText();
//			String seconName =  object.get(SECOND_NAME_FIELD).asText();
//			String email = object.get(EMAIL_FIELD).asText();
//			String userName =  object.get(USER_NAME_FIELD).asText(); 
//			String password = object.get(PASSWORD_FIELD).asText(); 
//			boolean lock = object.get(LOCK_FIELD).asBoolean(); 
//			boolean ovverideInvestorType= object.get(OVVERIDE_INVESTOR_TYPE_FILD).asBoolean(); 
//			UserAccount userAccountHistory = testArrayHistory.getElement(i); 
//			i++; 
//			
//			Assert.assertEquals(userAccountHistory.getFirstName(),name);
//			Assert.assertEquals(userAccountHistory.getSecondName(),seconName);
//			Assert.assertEquals(userAccountHistory.getEmail(),email);
//			Assert.assertEquals(userAccountHistory.getUserName(),userName);
//			Assert.assertEquals(userAccountHistory.getPassword(),password);
//			Assert.assertEquals(userAccountHistory.isLock(),lock);
//			Assert.assertEquals(userAccountHistory.isOvverideInvestorType(),ovverideInvestorType);
//			revisonNumber.add(object.get(REVISION_NUMBER_FILD).asInt()); 
//		}	
//	}
//	
//	
//	@Test
//	@Order(6)
//	void recoverUserAccount() throws IOException, ParseException {
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		UserAccount userAccount = testArray.getFirstElement();
//		UserAccount userAccountToRecover = testArrayHistory.getFirstElement(); 
//		userAccountToRecover.setId(userAccount.getId()); 
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD,userAccountToRecover.getId());
//		variables.put(REVISION_NUMBER_FILD,revisonNumber.get(0));
//		
//		Response response = testManager.request(MUTATIONS_PATH + "recoverUserAccountByRevisionNumber.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "recoverUserAccountByRevisionNumber");
//		
//		Assert.assertEquals(userAccountToRecover.getId(),jsonNode.get(ID_FIELD).asLong());
//		Assert.assertEquals(userAccountToRecover.getFirstName(),jsonNode.get(FIRST_NAME_FIELD).asText());
//		Assert.assertEquals(userAccountToRecover.getSecondName(),jsonNode.get(SECOND_NAME_FIELD).asText());
//		Assert.assertEquals(userAccountToRecover.getEmail(),jsonNode.get(EMAIL_FIELD).asText());
//		Assert.assertEquals(userAccountToRecover.getUserName(),jsonNode.get(USER_NAME_FIELD).asText());
//		Assert.assertEquals(userAccountToRecover.getPassword(),jsonNode.get(PASSWORD_FIELD).asText());
//		Assert.assertEquals(userAccountToRecover.isLock(),jsonNode.get(LOCK_FIELD).asBoolean());
//		Assert.assertEquals(userAccountToRecover.isOvverideInvestorType(),jsonNode.get(OVVERIDE_INVESTOR_TYPE_FILD).asBoolean());
//	}
//	
//	@Test
//	@Order(7)
//	void delete() throws IOException, ParseException {
//		
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		UserAccount userAccount  = testArrayHistory.getFirstElement();
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD,userAccount.getId());
//
//		Response response = testManager.request(MUTATIONS_PATH + "deleteUserAccountById.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "deleteUserAccountById");
//		
//		Assert.assertEquals(userAccount.getId(),jsonNode.get(ID_FIELD).asLong());
//		Assert.assertEquals(userAccount.getFirstName(),jsonNode.get(FIRST_NAME_FIELD).asText());
//		Assert.assertEquals(userAccount.getSecondName(),jsonNode.get(SECOND_NAME_FIELD).asText());
//		Assert.assertEquals(userAccount.getEmail(),jsonNode.get(EMAIL_FIELD).asText());
//		Assert.assertEquals(userAccount.getPassword(),jsonNode.get(PASSWORD_FIELD).asText());
//		Assert.assertEquals(userAccount.getUserName(),jsonNode.get(USER_NAME_FIELD).asText());
//		Assert.assertEquals(userAccount.isLock(),jsonNode.get(LOCK_FIELD).asBoolean());
//		Assert.assertEquals(userAccount.isOvverideInvestorType(),jsonNode.get(OVVERIDE_INVESTOR_TYPE_FILD).asBoolean());
//		
//	}
//}
