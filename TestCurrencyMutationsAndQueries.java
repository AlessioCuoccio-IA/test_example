package com.gatelab.microservice.bookbuilder.core;



import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
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
import com.gatelab.microservice.bookbuilder.core.persistence.model.currencies.Currency;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;
import com.gatelab.microservices.bookbulder.utils.TestsUtility;
import com.vimalselvam.graphql.GraphqlTemplate;
import okhttp3.Response;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:testdb","spring.jpa.hibernate.ddl-auto=create-drop"}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestCurrencyMutationsAndQueries {
	
	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES + "Currency/";
	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Currency/";

	private static final int LOCAL_REPOSITORY_SIZE = 100;
	private static final String ID_FIELD = "id";
	private static final String CCYD_FIELD = "ccyId";
	private static final String CURRENCY_NAME_FIELD = "currencyName";
	private static final String PRINCIPAL_FIELD = "principal";



	@LocalServerPort
	private int port;
	private static LocalRepositoryTestArray<Currency> currencyTestArray = new LocalRepositoryTestArray<>();
	private TestManager<Currency> testManager;
	
	//MUTATIONS
	@Test
	@Order(1)
	public void addMutation_getCurrencyByCcyId_Test() throws IOException, ParseException {

		String graphqlUri = "http://localhost:" + port + "/graphql";
		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
			String ccyd = "CY " + i;
			String currencyName = "CURRENCY_NAME " + i;
			boolean principal = false;
			InputStream streamAdd = this.getClass().getResourceAsStream(MUTATIONS_PATH +"addCurrency.graphql"); 
			String addPayload = createGraphqlAddTemplate(streamAdd, ccyd,currencyName,principal);
			Response response = TestsUtility.executeGraphqlMethod(addPayload, graphqlUri);
			Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
			String jsonDataAdd = response.body().string();
			JsonNode jsonNodeAdd = new ObjectMapper().readTree(jsonDataAdd).get("data").get("addCurrency"); 
			assertsNewCurrency(ccyd, currencyName, principal, jsonNodeAdd);
			addCurrencyToLocalList(jsonNodeAdd);
			
			
		}
	}
	
	@Test
	@Order(2)
	public void getCurrencyByID() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";
		
		Currency currency = currencyTestArray.getFirstElement();
		long id = currency.getId();

		//TEST GET BY ID
		InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getCurrencyById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());

		String jsonDataChange = response.body().string();

		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("getCurrencyById"); 
		System.out.print("GET"+jsonNodeChange); 
		Assert.assertEquals(jsonNodeChange.get(ID_FIELD).asInt(), id);
		assertsNewCurrency(currency.getCcyId(), currency.getCurrencyName(), currency.isPrincipal(), jsonNodeChange);
		 
	}
	
	//Restituisce tutti i record salavti a db, anche quelli "scaduti"
	@Test
	@Order(3)
	void getAllCurrencies() throws IOException, ParseException {
			String graphqlUri = "http://localhost:" + port + "/graphql";

			//TEST GET ALL
			InputStream streamChange = this.getClass().getResourceAsStream(QUERIES_PATH + "getAllCurrencies.graphql");

			ObjectNode var = new ObjectMapper().createObjectNode();
			String payload = GraphqlTemplate.parseGraphql(streamChange, var);
			Response response = TestsUtility.executeGraphqlMethod(payload, graphqlUri);

			Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
			String jsonData = response.body().string();

			JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonData).get("data").get("getAllCurrencies"); 
			System.out.print("GET ALL"+jsonNodeChange); 
		    int x = jsonNodeChange.size(); 
			int y = currencyTestArray.getSize(); 
			Assert.assertEquals(currencyTestArray.getSize() ,jsonNodeChange.size());
	}
		
	@Test
	@Order(4)
	void updateCurrencyById() throws IOException, ParseException {
		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
		
		Currency currency = currencyTestArray.getFirstElement();
		long oldId = currency.getId();
		
		//UPDATE
		InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "updateCurrencyById.graphql");
		String ccyd = "UPD";
		String currencyName = "CURRENCY_NAME_UPDATE";
		boolean principal = false;
		ObjectNode input = createCurrencyObjectNode(ccyd, currencyName, principal, currency.getId());
		
		Response response = testManager.request(MUTATIONS_PATH + "updateCurrencyById.graphql", input); 
		JsonNode jsonNode = testManager.checkResponse(response, "changeCurrencyById");
	
		assertsNewCurrency(ccyd, currencyName, principal, jsonNode);

	}


	@Test
	@Order(5)
	public void deleteCurrencyByID() throws IOException, ParseException {
		String graphqlUri = "http://localhost:" + port + "/graphql";	
		Currency currency = currencyTestArray.getFirstElement();
		long id = currency.getId();
	
		//TEST DELETE 
		InputStream streamChange = this.getClass().getResourceAsStream(MUTATIONS_PATH + "deleteCurrencyById.graphql");

		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put(ID_FIELD,id); 
		String changePayload = GraphqlTemplate.parseGraphql(streamChange, node);
		
		Response response = TestsUtility.executeGraphqlMethod(changePayload, graphqlUri);
		Assert.assertEquals(TestConstants.RESPONSE_OK_CODE,response.code());
		String jsonDataChange = response.body().string();
		JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange).get("data").get("deleteCurrencyById"); 
		currencyTestArray.deleteFirstElement();
		Assert.assertEquals(id, jsonNodeChange.get(ID_FIELD).asInt());
		
	}

	
	private ObjectNode createCurrencyObjectNode(String ccyd, String currencyName, boolean principal,Long id) {
		ObjectNode var = new ObjectMapper().createObjectNode();  
		var.put(ID_FIELD,id);
		var.put(CCYD_FIELD,ccyd); 
		var.put(CURRENCY_NAME_FIELD,currencyName);
		return var;
	}


	private static String createGraphqlAddTemplate(InputStream iStream, String ccyd, String currencyName, boolean principal) throws IOException {
		// Create a variables to pass to the graphql query		
		ObjectNode variables = new ObjectMapper().createObjectNode();

		// add a nested node object to our current node
		ObjectNode input = variables.putObject("currency");
			input.put(CCYD_FIELD,ccyd);
			input.put(CURRENCY_NAME_FIELD,currencyName);
			input.put(PRINCIPAL_FIELD,principal);
			
			// Now parse the graphql file to a request payload string
			String graphqlPayload = GraphqlTemplate.parseGraphql(iStream, variables);
			return graphqlPayload;
		}
	  	
	  	private void assertsNewCurrency(String ccyd, String currencyName, Boolean principal, JsonNode jsonNodeChange) throws ParseException {
			Assert.assertEquals(ccyd,jsonNodeChange.get(CCYD_FIELD).asText());
			Assert.assertEquals(currencyName,jsonNodeChange.get(CURRENCY_NAME_FIELD).asText());
			Assert.assertEquals(principal,jsonNodeChange.get(PRINCIPAL_FIELD).asBoolean());
		}
	  	
	  	private void addCurrencyToLocalList(JsonNode node) throws ParseException { 

			Currency currency = new Currency();
			currency.setId(node.get(ID_FIELD).asLong());
			currency.setCcyId(node.get(CCYD_FIELD).asText());
			currency.setCurrencyName(node.get(CURRENCY_NAME_FIELD).asText());
			currency.setPrincipal(node.get(PRINCIPAL_FIELD).asBoolean());			
			currencyTestArray.add(currency);
		}
	  
	  
	

//
////    @Test
////    public void addCurrencyException1Test() throws IOException {
////    	// preapare the uri
////    	String graphqlUri = "http://localhost:" + port + "/graphql";
////    	
////    	//Read a graphql file as an input stream
////        InputStream streamAdd = this.getClass().getResourceAsStream("/graphql/mutations/addCurrencyTest.graphql");
////    	
////        String ccyId = null;
////        String currencyName = "inutile";
////        String currencyDate = "20210604 00:00:00";
////        String expectedMessage = "CCYD must be not empty";
////        
////        //the commented way of proceed is quite difficult to follow, as the exception is thrown in the server
//////        Exception exception = assertThrows(CurrencyException.class, () -> {
//////        	String addPayload = add(streamAdd, ccyId, currencyName, currencyDate);
//////        	// Build and trigger the request
//////            Response addResponse = prepareResponse(addPayload, graphqlUri);
//////            String jsonDataAdd = addResponse.body().string();
//////            System.out.println("PAPPOLO " + jsonDataAdd);
//////        });
//////        String actualMessage = exception.getMessage();
//////         assertTrue(actualMessage.contains(expectedMessage));
////         
////         
////        String addPayload = add(streamAdd, ccyId, currencyName, currencyDate);
////    	// Build and trigger the request
////        Response addResponse = prepareResponse(addPayload, graphqlUri);
////        String jsonDataAdd = addResponse.body().string();
//////        System.out.println("PAPPOLO " + jsonDataAdd);
////        
////        JsonNode jsonNodeAdd = new ObjectMapper().readTree(jsonDataAdd);
//////        System.out.println("PAPPOLOne " + jsonNodeAdd.get("errors").get(0));
////        Assert.assertEquals(jsonNodeAdd.get("errors").get(0).get("message").asText(), expectedMessage);
////       
////    }
////    
////    @Test
////    public void addCurrencyException2Test() throws IOException {
////    	// preapare the uri
////    	String graphqlUri = "http://localhost:" + port + "/graphql";
////    	
////    	//Read a graphql file as an input stream
////        InputStream streamAdd = this.getClass().getResourceAsStream("/graphql/mutations/addCurrencyTest.graphql");
////    	
////        String ccyId = "";
////        String currencyName = "inutile";
////        String currencyDate = "20210604";
////        String expectedMessage = "CCYD must be not empty";
////
////        String addPayload = add(streamAdd, ccyId, currencyName, currencyDate);
////    	// Build and trigger the request
////        Response addResponse = prepareResponse(addPayload, graphqlUri);
////        String jsonDataAdd = addResponse.body().string();
//////        System.out.println("PAPPOLO " + jsonDataAdd);
////        
////        JsonNode jsonNodeAdd = new ObjectMapper().readTree(jsonDataAdd);
//////        System.out.println("PAPPOLOne " + jsonNodeAdd.get("errors").get(0));
////        Assert.assertEquals(jsonNodeAdd.get("errors").get(0).get("message").asText(), expectedMessage);
////       
////    }
//    /*
//  
//    */
////    @Test
////	public void sameCurrencyDoubleAdd() throws IOException, ParseException {
////
////    	// preapare the uri
////    	String graphqlUri = "http://localhost:" + port + "/graphql";
////    	
////    	//Read a graphql file as an input stream
////        InputStream streamAdd = this.getClass().getResourceAsStream("/graphql/mutations/addCurrencyTest.graphql"),
////        		streamAdd2 = this.getClass().getResourceAsStream("/graphql/mutations/addCurrencyTest.graphql");
////        
////        String ccyId = "ADD";
////        String currencyName = "currency";
////        Date currencyDate = DateUtils.getDateFromString("20210604 00:00:00",  BookBuilderConst.dateFormat);
////        String currencyDateString = DateUtils.getStringFromDate(currencyDate, BookBuilderConst.dateFormat);
////        String nullDate = "null";
////        String todayDate = DateUtils.getStringFromDate(new Date(), BookBuilderConst.dateFormat);
////        
////        String addPayload = add(streamAdd, ccyId, currencyName, currencyDateString);
////        Response addResponse = prepareResponse(addPayload, graphqlUri);
////        //assert add response code
////        Assert.assertEquals(addResponse.code(), 200);
////        
////        String jsonDataAdd = addResponse.body().string();
////        
////        JsonNode jsonNodeAdd = new ObjectMapper().readTree(jsonDataAdd).get("data").get("addCurrency");
////        Assert.assertEquals(jsonNodeAdd.get("ccyId").asText(), ccyId);
////        Assert.assertEquals(jsonNodeAdd.get("currencyName").asText(), currencyName);
//////        Assert.assertEquals(jsonNodeAdd.get("referenceDate").asText(), currencyDate);
//////        Assert.assertEquals(jsonNodeAdd.get("insertDate").asText(), nullDate);
//////        Assert.assertEquals(jsonNodeAdd.get("updateDate").asText(), nullDate);
//////        Assert.assertEquals(jsonNodeAdd.get("startDate").asText(), currencyDate);
//////        Assert.assertEquals(jsonNodeAdd.get("endDate").asText(), BookBuilderConst.DefaultStringEndDate);
////        
////        String addPayload2 = add(streamAdd2, ccyId, currencyName, currencyDateString);
////        Response addResponse2 = prepareResponse(addPayload2, graphqlUri);
////        //assert add response code
////        Assert.assertEquals(addResponse2.code(), 200);
////        
////        String jsonDataAdd2 = addResponse2.body().string();
//////        System.out.println("CICCIO " + jsonDataAdd2);
////		
////        JsonNode jsonNodeError = new ObjectMapper().readTree(jsonDataAdd2).get("errors");
////        Assert.assertTrue(jsonNodeError.size() > 0);
////	}
//    /*

//    */
////    @Test
////    public void changeCcyIdException1Test() throws IOException, ParseException { 
////    	
////		///////////////////
////		// PREP SCECTION //
////		///////////////////
////		String graphqlUri = "http://localhost:" + port + "/graphql";
////		
////		String ccyId = "OLD";
////		String ccyToChange = null;
////		String newCcyId = ""; //"NEW";
////		String currencyName = "currencyName";
////		
////		Date currencyDate = DateUtils.getDateFromString("20210604 00:00:00",  BookBuilderConst.dateFormat);
////	    String currencyDateString = DateUtils.getStringFromDate(currencyDate, BookBuilderConst.dateFormat);
////	     
////		String nullDate = "null";
////		String toCheck = "the CCY_IDs must not be null";
//////		String todayDate = DateUtils.getStringFromDate(new Date(), BookBuilderConst.dateFormat);
////		
////		/////////////////
////		// ADD SECTION //
////		/////////////////
////		InputStream streamAdd = this.getClass().getResourceAsStream("/graphql/mutations/addCurrencyTest.graphql");
////		
////		String addPayload = add(streamAdd, ccyId, currencyName, currencyDateString);
////		Response addResponse = prepareResponse(addPayload, graphqlUri);
////		//assert add response code
////		Assert.assertEquals(addResponse.code(), 200);
////		
////		String jsonDataAdd = addResponse.body().string();
////		
////		JsonNode jsonNodeAdd = new ObjectMapper().readTree(jsonDataAdd).get("data").get("addCurrency");
////		Assert.assertEquals(jsonNodeAdd.get("ccyId").asText(), ccyId);
////		Assert.assertEquals(jsonNodeAdd.get("currencyName").asText(), currencyName);
//////		Assert.assertEquals(jsonNodeAdd.get("referenceDate").asText(), currencyDate);
//////		Assert.assertEquals(jsonNodeAdd.get("insertDate").asText(), nullDate);
//////		Assert.assertEquals(jsonNodeAdd.get("updateDate").asText(), nullDate);
//////		Assert.assertEquals(jsonNodeAdd.get("startDate").asText(), currencyDate);
//////		Assert.assertEquals(jsonNodeAdd.get("endDate").asText(), BookBuilderConst.DefaultStringEndDate);
////    	
////		////////////////////
////		// UPDATE SECTION //
////		////////////////////
////    	
////    	//Read a graphql file as an input stream
////        InputStream streamChange = this.getClass().getResourceAsStream("/graphql/mutations/changeCcyIdTest.graphql");
////    	
////        ObjectNode var = new ObjectMapper().createObjectNode();
////		var.put("oldCcyId", ccyId);
////		var.put("newCcyId", newCcyId);
////	
////		
////		
////		String changePayload = GraphqlTemplate.parseGraphql(streamChange, var);
////        Response changeResponse = prepareResponse(changePayload, graphqlUri);
////        //assert add response code
////        Assert.assertEquals(changeResponse.code(), 200);
////        
////        String jsonDataChange = changeResponse.body().string();
//////        System.out.println("PIPPO " + jsonDataChange);
////		
////        JsonNode jsonNodeChange = new ObjectMapper().readTree(jsonDataChange);
////        Assert.assertEquals(jsonNodeChange.get("errors").get(0).get("message").asText(), toCheck);
////       
////    }
//    
//	/*




	

	

}
