package com.gatelab.microservice.bookbuilder.core;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.Address;
import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.Company;
import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.CompanyType;
import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.IssuerSector;
import com.gatelab.microservice.bookbuilder.core.persistence.model.companies.Rank;
import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.Country;
import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.Desk;
import com.gatelab.microservice.bookbuilder.core.persistence.model.geographical.Region;
import com.gatelab.microservice.bookbuilder.core.persistence.model.role.Role;
import com.gatelab.microservices.bookbulder.utils.LocalRepositoryTestArray;
import com.gatelab.microservices.bookbulder.utils.TestConstants;
import com.gatelab.microservices.bookbulder.utils.TestManager;
import com.gatelab.microservices.bookbulder.utils.TestsUtility;
import okhttp3.Response;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TestCompany {

//	@LocalServerPort
//	private int port;
//	private TestManager<Company> testManager;
//	private TestManager<Region> testManagerRegion;
//	private TestManager<Desk> testManagerDesk;
//	private TestManager<Country> testManagerCountry;
//	private TestManager<CompanyType> testManagerCompanyType;
//	private TestManager<IssuerSector> testManagerIsssuerSerctor;
//	private TestManager<Role> testManagerRole;
//	private TestManager<Rank> testManagerRank;
//	private TestManager<Address> testManagerAddress; 
//	
//	private static List<Integer> revisonNumber = new ArrayList<>(); 
//	private static LocalRepositoryTestArray<Company> testArrayHistory = new LocalRepositoryTestArray<>();
//	private static LocalRepositoryTestArray<Company> testArray = new LocalRepositoryTestArray<>();
//	private static LocalRepositoryTestArray<Rank> testArrayRank = new LocalRepositoryTestArray<>();
//
//	private static final String QUERIES_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_QUERIES
//			+ "Company/Company/";
//	private static final String MUTATIONS_PATH = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "Company/Company/";
//	private static final String MUTATIONS_PATH_REGION = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "Region/";
//	private static final String MUTATIONS_PATH_DESK = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "Desk/";
//	private static final String MUTATIONS_PATH_COUNTRY = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "Country/";
//	private static final String MUTATIONS_PATH_COMPANY_TYPE = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "Company/CompanyType/";
//	private static final String MUTATIONS_PATH_ISSUER_SECTOR = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "IssuerSector/";
//	private static final String MUTATIONS_PATH_ROLE = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION
//			+ "Role/";
//
//	private static final String MUTATIONS_PATH_ADDRESS = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Company/Address/";
//	
//
//	private static final String MUTATIONS_PATH_RANK = TestConstants.GENERAL_PATH_CONFIGURATION_STATIC_DATA_MUTATION + "Rank/";
//	private static final int LOCAL_REPOSITORY_SIZE = 100;
//	private static final String ID_FIELD = "id";
//	private static final String NAME_FILD = "name";
//	private static final String SHORT_NAME_FILD = "shortName";
//	private static final String NDG_AMAGE_FILD = "ndgAnage";
//	private static final String DESCRIPTION_FILD = "description";
//	private static final String BLOOMBERG_TICKER_FILD = "bloombergTicker";
//	private static final String LEGAL_ENTITY_FILD = "legalEntity";
//	private static final String SUB_RECORD_ENTITY_FILD = "subRecordEntity";
//	private static final String MONEY_ENTITY_TYPE_FILD = "moneyEntityType";
//	private static final String CURRENTLY_TRADED_FILD = "currentlyTraded";
//	private static final String EXTERNAL_FILD = "external";
//	private static final String IHS_MARKIT_ADD_INVECT_TYPE_FILD = "ihsMarkitAddInvectType";
//	private static final String IHS_MARKIT_WARE_HOUS_ID_FILD = "ihsMarkitWarehousId";
//	private static final String IHS_MARKIT_COMMON_NAME_FILD = "ihsMarkitCommonName";
//	private static final String PMID_FILD = "pmid";
//	private static final String DEFAULT_INTERNAL_EMPLOYER_FILD = "defaultInternalEmployer";
//
//	private static final String PRIMARY_ADDRESS_FIELD = "address_1";
//	private static final String SECONDARY_ADDRESS_FIELD = "address_2";
//	private static final String STATE_FIELD = "state";
//	private static final String CITY_FIELD = "city";
//	private static final String POSTCODE_FIELD = "postCode";
//	private static final String SWITCH_BOARD_FIELD = "switchBoard"; 
//	private static final String FAX_FIELD = "fax";
//	
//	private static final String REGION_FIELD = "region";
//	private static final String ABBREVIATION_FIELD = "abbreviation";
//
//	private static final String DESK_FIELD = "desk";
//	private static final String SHORT_NAME_FIELD = "shortName";
//
//	public static final String ISO_CODE_FIELD = "iso_code";
//	public static final String DESCRIPTION_FIELD = "description";
//	public static final String ISSUENET_MAPPING_FIELD = "issuenet_mapping";
//
//	private static final String ISSUER_SECTOR_FIELD = "issuerSector";
//
//	private static final String TYPE_FIELD = "type";
//
//	private static final String NAME_FIELD = "name";
//	private static final String REGION_SALES_MANAGEMENT_FIELD = "regionSalesManagement";
//
//	
//	private static final String CATEGORY_FIELD = "category";
//	private static final String ALLOCATION_RATIONALE_FIELD = "allocationRationale";
//	
//	private static final String REVISION_NUMBER_FILD = "revisionNumber";
//	
//	Region regionCreated = new Region();
//	Desk deskCreated = new Desk();
//	Country country = new Country();
//	IssuerSector issuer = new IssuerSector();
//	Role role = new Role();
//	CompanyType companyType = new CompanyType();
//	Address address = new Address(); 
//	
//	@Test
//	@Order(1)
//	void add() throws IOException, ParseException {
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
//		// Country
//		testManagerCountry = new TestManager<Country>("http://localhost:" + port + "/graphql");
//		String isoCode = "mod";
//		String description = "DESCRIPTION Mod";
//		String issueNetMapping = "ISSUENET MAPPING mod";
//
//		variables = new ObjectMapper().createObjectNode();
//		ObjectNode inputCountry = variables.putObject("country");
//		inputCountry.put(ISO_CODE_FIELD, isoCode);
//		inputCountry.put(DESCRIPTION_FIELD, description);
//		inputCountry.put(ISSUENET_MAPPING_FIELD, issueNetMapping);
//
//		response = testManagerCountry.request(MUTATIONS_PATH_COUNTRY + "addCountry.graphql", variables);
//		jsonNode = testManagerCountry.checkResponse(response, "addCountry");
//
//		Assert.assertEquals(isoCode, jsonNode.get(ISO_CODE_FIELD).asText());
//		Assert.assertEquals(description, jsonNode.get(DESCRIPTION_FIELD).asText());
//		Assert.assertEquals(issueNetMapping, jsonNode.get(ISSUENET_MAPPING_FIELD).asText());
//
//		country.setId(jsonNode.get(ID_FIELD).asLong());
//		country.setDescription(description);
//		country.setIssuenet_mapping(issueNetMapping);
//
//	
//		// IssuerSector
//		testManagerIsssuerSerctor = new TestManager("http://localhost:" + port + "/graphql");
//
//		String issuerSector = "ISSUER SECTOR mod";
//		variables = new ObjectMapper().createObjectNode();
//		ObjectNode inputIssuer = variables.putObject("issuerSector");
//		inputIssuer.put(ISSUER_SECTOR_FIELD, issuerSector);
//
//		response = testManagerIsssuerSerctor.request(MUTATIONS_PATH_ISSUER_SECTOR + "addIssuerSector.graphql", variables);
//		jsonNode = testManagerIsssuerSerctor.checkResponse(response, "addIssuerSector");
//
//		Assert.assertEquals(issuerSector, jsonNode.get(ISSUER_SECTOR_FIELD).asText());
//
//		
//		issuer.setId(jsonNode.get(ID_FIELD).asLong());
//		issuer.setIssuerSector(issuerSector);
//
//		// Role
//		testManagerRole = new TestManager("http://localhost:" + port + "/graphql");
//
//		String roleName = "roleMod";
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
//
//		
//		// CompanyType
//		testManagerCompanyType = new TestManager("http://localhost:" + port + "/graphql");
//		String type = "typeMOd";
//		abbreviation = "abbMod";
//
//		variables = new ObjectMapper().createObjectNode();
//		ObjectNode inputCompanyType = variables.putObject("companyType");
//		inputCompanyType.put(ABBREVIATION_FIELD, abbreviation);
//		inputCompanyType.put(TYPE_FIELD, type);
//		ObjectNode roleNode = inputCompanyType.putObject("role");
//		roleNode.put(ID_FIELD, role.getId());
//		
//		
//		response = testManagerCompanyType.request(MUTATIONS_PATH_COMPANY_TYPE + "addCompanyType.graphql", variables);
//		jsonNode = testManagerCompanyType.checkResponse(response, "addCompanyType");
//
//		Assert.assertEquals(type, jsonNode.get(TYPE_FIELD).asText());
//		Assert.assertEquals(abbreviation, jsonNode.get(ABBREVIATION_FIELD).asText());
//
//		
//		companyType.setId(jsonNode.get(ID_FIELD).asLong());
//		companyType.setType(jsonNode.get(TYPE_FIELD).asText());
//		companyType.setAbbreviation(jsonNode.get(ABBREVIATION_FIELD).asText());
//		
//		// Rank List
//		
//		for (int i = 0;i<LOCAL_REPOSITORY_SIZE;i++) {
//			testManagerRank =  new TestManager("http://localhost:" + port + "/graphql");
//			int category =  i+598;
//			String allocationRationale = "allocation  " + i +category; 
//			
//			variables = new ObjectMapper().createObjectNode();
//
//			// add a nested node object to our current node
//			ObjectNode inputRank = variables.putObject("rank");
//			inputRank.put(CATEGORY_FIELD,category);
//			inputRank.put(ALLOCATION_RATIONALE_FIELD,allocationRationale);
//			
//			response = testManagerRank.request(MUTATIONS_PATH_RANK+ "addRank.graphql", variables);
//			jsonNode = testManagerRank.checkResponse(response, "addRank");
//			
//			Assert.assertEquals(category,jsonNode.get(CATEGORY_FIELD).asInt());
//			Assert.assertEquals(allocationRationale,jsonNode.get(ALLOCATION_RATIONALE_FIELD).asText());
//			Rank rankCreated = new Rank(); 
//			rankCreated.setId(jsonNode.get(ID_FIELD).asLong()); 
//			rankCreated.setAllocationRationale(allocationRationale);
//			rankCreated.setCategory(category); 
//			testArrayRank.add(rankCreated); 
//		}
//		
//		
//		
//		
//		testManager = new TestManager<Company>("http://localhost:" + port + "/graphql");
//		for (int i = 0; i < LOCAL_REPOSITORY_SIZE; i++) {
//
//			String name = "name" + i;
//			shortName = "sh" + i;
//			String ndgAnage = "ndgAnage" + i;
//			description = "desc" + i;	
//			String pmid = "pmid" + i;
//			boolean external = (i % 2 == 0) ? true : false;
//
//			variables = new ObjectMapper().createObjectNode();
//			ObjectNode input = variables.putObject("company");
//			input.put(NAME_FILD, name);
//			input.put(SHORT_NAME_FILD, shortName);
//			input.put(NDG_AMAGE_FILD, ndgAnage);
//			input.put(PMID_FILD, pmid);
//			input.put(EXTERNAL_FILD, external);
//			
//			ObjectNode regionNodeObject = input.putObject("region");
//			regionNodeObject.put(ID_FIELD, regionCreated.getId());
//
//			ObjectNode deskNodeObject = input.putObject("desk");
//			deskNodeObject.put(ID_FIELD, deskCreated.getId());
//
//			ObjectNode countryNodeObject = input.putObject("country");
//			countryNodeObject.put(ID_FIELD, country.getId());
//
//			ObjectNode companyTypeNodeObject = input.putObject("companyType");
//			companyTypeNodeObject.put(ID_FIELD, companyType.getId());
//
//			ObjectNode issuerNodeObject = input.putObject("issuerSector");
//			issuerNodeObject.put(ID_FIELD, issuer.getId());
//
//			ObjectNode roleNodeObject = input.putObject("role");
//			roleNodeObject.put(ID_FIELD, role.getId());
//		
//			Rank tempRank = testArrayRank.getFirstElement();
//			ArrayNode rankArrayObject = input.putArray("rank"); 
//			ObjectNode rankObject = new ObjectMapper().createObjectNode();
//			rankObject.put(ID_FIELD,tempRank.getId()); 
//			rankArrayObject.add(rankObject); 
//		
//			response = testManager.request(MUTATIONS_PATH + "addCompany.graphql", variables);
//			jsonNode = testManager.checkResponse(response, "addCompany");
//
//			Assert.assertEquals(name, jsonNode.get(NAME_FILD).asText());
//			Assert.assertEquals(shortName, jsonNode.get(SHORT_NAME_FILD).asText());
//			Assert.assertEquals(ndgAnage, jsonNode.get(NDG_AMAGE_FILD).asText());	
//			Assert.assertEquals(pmid, jsonNode.get(PMID_FILD).asText());	
//			Assert.assertEquals(external, jsonNode.get(EXTERNAL_FILD).asBoolean());
//
//			Company company = new Company();
//			company.setId(jsonNode.get(ID_FIELD).asLong());
//			company.setName(name);
//			company.setShortName(shortName);
//			company.setNdgAnage(ndgAnage);	
//			company.setPmid(pmid);
//			testArray.add(company);
//		}
//	}
//
//	@Test
//	@Order(2)
//	void getByID() throws ParseException, IOException {
//		
//		testManager = new TestManager("http://localhost:" + port + "/graphql");
//		Company company = testArray.getFirstElement();
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD, company.getId());
//
//		Response response = testManager.request(QUERIES_PATH + "getCompanyById.graphql", variables);
//		JsonNode jsonNode = testManager.checkResponse(response, "getCompanyById");
//
//		Assert.assertEquals(company.getId(), jsonNode.get(ID_FIELD).asLong());
//	}
//
//	@Test
//	@Order(3)
//	void getAll() throws IOException {
//
//		testManager = new TestManager("http://localhost:" + port + "/graphql");
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		Response response = testManager.request(QUERIES_PATH + "getAllCompanies.graphql", variables);
//		JsonNode jsonNode = testManager.checkResponse(response, " getAllCompanies");
//
//	}
//
//	@Test
//	@Order(4)
//	void update() throws ParseException, IOException {
//
//		testManager = new TestManager("http://localhost:" + port + "/graphql");
//		Company company = testArray.getFirstElement();
//		testArrayHistory.add(TestsUtility.copy(company, Company.class));
//		String name = "name";
//		String shortName = "sh";
//		String ndgAnage = "ndgAnage";
//		String description = "desc";
//		String bloombergTicker = "bloom";
//		String legalEntity = "legal";
//		String subRecordEntity = "subRecord";
//		String moneyEntityType = "moneyEntity";
//		String ihsMarkitOrionId = "ihsM";
//		String ihsMarkitLei = "ish";
//		String ihsMarkitAddInvectType = "lei";
//		String ihsMarkitWarehousId = "ware";
//		String ihsMarkitCommonName = "commonName";
//		String settleVia = "settleVia";
//		String clientPriority = "client";
//		String pmid = "pmid";
//		String defaultInternalEmployer = "defaultInternal";
//		boolean createUserGroup = false;
//		boolean currentlyTraded = false;
//		boolean external = false;
//
//		company.setName(name);
//		company.setShortName(shortName);
//		company.setNdgAnage(ndgAnage);
//		company.setBloombergTicker(bloombergTicker);
//		company.setMoneyEntityType(moneyEntityType);
//		company.setIhsMarkitAddInvectType(ihsMarkitAddInvectType);
//		company.setIhsMarkitWarehousId(ihsMarkitWarehousId);
//		company.setIhsMarkitCommonName(ihsMarkitCommonName);
//		company.setPmid(pmid);
//		company.setDefaultInternalEmployer(defaultInternalEmployer);
//		company.setDefaultInternalEmployer(defaultInternalEmployer);
//		company.setCurrentlyTraded(currentlyTraded);
//		company.setExternal(external);
//		testArrayHistory.add(TestsUtility.copy(company, Company.class));
//
//		ObjectNode input = new ObjectMapper().createObjectNode();
//		input.put(ID_FIELD, company.getId());
//		input.put(NAME_FILD, name);
//		input.put(SHORT_NAME_FILD, shortName);
//		input.put(NDG_AMAGE_FILD, ndgAnage);
//		input.put(PMID_FILD, pmid);
//		input.put(EXTERNAL_FILD, external);
//	
//		ObjectNode regionNodeObject = input.putObject("region");
//		regionNodeObject.put(ID_FIELD, regionCreated.getId());
//
//		ObjectNode deskNodeObject = input.putObject("desk");
//		deskNodeObject.put(ID_FIELD, deskCreated.getId());
//
//		ObjectNode countryNodeObject = input.putObject("country");
//		countryNodeObject.put(ID_FIELD, country.getId());
//
//		ObjectNode companyTypeNodeObject = input.putObject("companyType");
//		companyTypeNodeObject.put(ID_FIELD, companyType.getId());
//
//		ObjectNode issuerNodeObject = input.putObject("issuerSector");
//		issuerNodeObject.put(ID_FIELD, issuer.getId());
//
//		ObjectNode roleNodeObject = input.putObject("role");
//		roleNodeObject.put(ID_FIELD, role.getId());
//
//		Rank tempRank = testArrayRank.getFirstElement();
//		ArrayNode rankArrayObject = input.putArray("rank"); 
//		ObjectNode rankObject = new ObjectMapper().createObjectNode();
//		rankObject.put(ID_FIELD,tempRank.getId()); 
//		rankArrayObject.add(rankObject); 
//		
//		Response response = testManager.request(MUTATIONS_PATH + "changeCompanyById.graphql", input);
//		JsonNode jsonNode = testManager.checkResponse(response, "changeCompanyById");
//
//		Assert.assertEquals(name, jsonNode.get(NAME_FILD).asText());
//		Assert.assertEquals(shortName, jsonNode.get(SHORT_NAME_FILD).asText());
//		Assert.assertEquals(ndgAnage, jsonNode.get(NDG_AMAGE_FILD).asText());
//		Assert.assertEquals(pmid, jsonNode.get(PMID_FILD).asText());
//		Assert.assertEquals(external, jsonNode.get(EXTERNAL_FILD).asBoolean());
//	}
//
//	@Test
//	@Order(5)
//	void getHistory() throws ParseException, IOException {
//		
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		Company company = testArray.getFirstElement();
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD,company.getId());
//		
//		Response response = testManager.request(QUERIES_PATH + "getCompanyHistoryById.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "getCompanyHistoryById");
//		
//		int i = 0; 
//		for(JsonNode object: jsonNode) {
//	
//			String name = object.get(NAME_FIELD).asText();
//			String shortName =  object.get(SHORT_NAME_FIELD).asText();
//			String pmid = object.get(PMID_FILD).asText();
//
//			
//			Company companyHistory = testArrayHistory.getElement(i); 
//			i++; 
//			
//			Assert.assertEquals(companyHistory.getName(),name);
//			Assert.assertEquals(companyHistory.getShortName(),shortName);
//			revisonNumber.add(object.get(REVISION_NUMBER_FILD).asInt()); 
//		}	
//		
//	}
//	
//	@Test
//	@Order(6)
//	void recover() throws ParseException, IOException {
//		testManager = new TestManager("http://localhost:" + port + "/graphql"); 
//		Company company = testArray.getFirstElement();	
//		Company companyToRecover = testArrayHistory.getFirstElement();
//		companyToRecover.setId(company.getId());
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD,company.getId());
//		variables.put(REVISION_NUMBER_FILD,revisonNumber.get(0));
//		
//		Response response = testManager.request(MUTATIONS_PATH + "recoverCompanyByRevisionNumber.graphql", variables); 
//		JsonNode jsonNode = testManager.checkResponse(response, "recoverCompanyByRevisionNumber");
//
//		Assert.assertEquals(companyToRecover.getId(),jsonNode.get(ID_FIELD).asLong());
//		Assert.assertEquals(companyToRecover.getName(),jsonNode.get(NAME_FILD).asText());
//		Assert.assertEquals(companyToRecover.getShortName(),jsonNode.get(SHORT_NAME_FIELD).asText());
//		
//	}
//	
//	
//	@Test
//	@Order(7)
//	void delete() throws IOException, ParseException {
//
//		testManager = new TestManager("http://localhost:" + port + "/graphql");
//		Company company = testArray.getFirstElement();
//		ObjectNode variables = new ObjectMapper().createObjectNode();
//		variables.put(ID_FIELD, company.getId());
//
//		Response response = testManager.request(MUTATIONS_PATH + "deleteCompanyById.graphql", variables);
//		JsonNode jsonNode = testManager.checkResponse(response, "deleteCompanyById");
//
//		Assert.assertEquals(company.getId(), jsonNode.get(ID_FIELD).asLong());
//	}
}
