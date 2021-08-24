package com.isfp.webdemo.ws.restassuredtest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static io.restassured.RestAssured.given;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import io.restassured.RestAssured;
import io.restassured.response.Response;


class TestCreateUser {
	
	private final String CONTEXT_PATH ="/webdemo"; 

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI="http://localhost";
		RestAssured.port= 8080;
	}

	@Test
	void testCreateUser() {
		
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
		userDetails.put("email", "shorouk@mail.com");
		userDetails.put("password", "123");
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

}
