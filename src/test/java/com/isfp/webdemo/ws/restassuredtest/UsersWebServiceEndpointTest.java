package com.isfp.webdemo.ws.restassuredtest;

//import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;

import io.restassured.RestAssured;
import io.restassured.response.Response;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UsersWebServiceEndpointTest {
	
	private final String CONTEXT_PATH ="/webdemo"; 
	private final String EMAIL_ADDRESS = "shorouk@mail.com";
	private final String PASSWORD = "123";
	private final String JSON = "application/json";
	
	private static String authorizationHeader ;
	private static String userId ;
	private static List<Map<String , String>> addresses;

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI="http://localhost";
		RestAssured.port= 8080;
	}
	
	/*
	 * 
	 * testCreateUser()
	 */
	@Test
	void a() {
		
		List<Map<String,Object>> userAdresses = new ArrayList<>();
		
		Map<String,Object> shippingAddress = new HashMap<>();
		shippingAddress.put("city", "Vancouver");
		shippingAddress.put("country", "Canada");
		shippingAddress.put("streetName", "123 street name");
		shippingAddress.put("postalCode", "123456");
		shippingAddress.put("type", "Shipping");

		userAdresses.add(shippingAddress);
		
		Map<String,Object> userDetails = new HashMap<>();
		userDetails.put("firstName", "shorouk");
		userDetails.put("lastName", "Abdelaziz");
		userDetails.put("email", EMAIL_ADDRESS);
		userDetails.put("password", PASSWORD);
		userDetails.put("addresses", userAdresses);
		
		Response response = given().contentType("application/json").
		accept("application/json").
		body(userDetails).
		when().
		post(CONTEXT_PATH + "/users").
		then().
		statusCode(200).
		contentType("application/json").
		extract().
		response();
		
		String userId = response.jsonPath().getString("userId");
		assertNotNull(userId);
		assertTrue(userId.length()==30);
		
		String bodyString = response.body().asString();
		try {
			JSONObject responseBodyJson = new JSONObject(bodyString);
			JSONArray addresses = responseBodyJson.getJSONArray("addresses");
			assertNotNull(addresses);
			assertTrue(addresses.length()==1); 
			String addressId = addresses.getJSONObject(0).getString("addressId");
			assertNotNull(addressId);
			assertTrue(addressId.length()==30);
			
		} catch (JSONException e) {
			fail(e.getMessage());
			}
		
		
	}
	
	

    /*
     * testUserLogin()
     * 
     * */
	@Test
	final void b() {
		
		Map<String,String> loginDetails = new HashMap<>();
		loginDetails.put("email", EMAIL_ADDRESS);
		loginDetails.put("password", PASSWORD);
		
		Response response = given().contentType(JSON).
		accept(JSON).
		body(loginDetails).
		when().
		post(CONTEXT_PATH + "/users/login").
		then().
		statusCode(200).
		extract().
		response();
		
		authorizationHeader = response.header("Authorization");
		userId = response.header("UserId");
				
		assertNotNull(authorizationHeader);
		assertNotNull(userId);
	}
	
	
	  /*
     * testGetUserDetails()
     * 
     * */
	@Test
	final void c() {
		
		Response response = given()
				.pathParam("id", userId)
		.header("Authorization",authorizationHeader)
		.accept(JSON)
		.when()
		.get(CONTEXT_PATH + "/users/{id}")
		.then()
		.statusCode(200)
		.contentType(JSON)
		.extract()
		.response();

		String userPublicId = response.jsonPath().getString("userId");
		String userEmail = response.jsonPath().getString("email");
		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");
		addresses =  response.jsonPath().getList("addresses");
		String addressId = addresses.get(0).get("addressId");
		
		assertNotNull(userPublicId);
		assertNotNull(userEmail);
		assertNotNull(firstName);
		assertNotNull(lastName);
		assertEquals(EMAIL_ADDRESS,userEmail);
		assertTrue(addresses.size()==1);
		assertTrue(addressId.length()==30);
	}
	
	
	  /*
     * testUpdateUserDetails()
     * 
     * */
	@Test
	
	final void d() {
		
		Map<String,String> userDetails = new HashMap<>();
		userDetails.put("firstName", "Shorouk Said");
		userDetails.put("lastName", "Abdalla Abdelaziz");
		

		 Response response = given()
		 .contentType(JSON)
		 .accept(JSON)
		 .header("Authorization",authorizationHeader)
		 .pathParam("id", userId)
		 .body(userDetails)
		 .when()
		 .put(CONTEXT_PATH + "/users/{id}")
		 .then()
		 .statusCode(200)
		 .contentType(JSON)
		 .extract()
		 .response();
		 
		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");
		List<Map<String , String>> storedAddresses =  response.jsonPath().getList("addresses");
		
		assertEquals(firstName,"Shorouk Said");
		assertEquals(lastName,"Abdalla Abdelaziz");
		
		assertNotNull(storedAddresses);
		assertTrue(addresses.size() == storedAddresses.size());
		assertEquals(addresses.get(0).get("streetName") , storedAddresses.get(0).get("streetName"));

		
		

	}
	
	
	  /*
     * testDeleteUserDetails()
     * 
     * */
	@Test
	
	final void e() {
		
		 Response response = given()
		 .contentType(JSON)
		 .accept(JSON)
		 .header("Authorization",authorizationHeader)
		 .pathParam("id", userId)
		 .when()
		 .delete(CONTEXT_PATH + "/users/{id}")
		 .then()
		 .statusCode(200)
		 .contentType(JSON)
		 .extract()
		 .response();
		
		 
		 String operationResult = response.jsonPath().getString("operationResult");
		 assertEquals("SUCCESS",operationResult);
	}
	
	
}
