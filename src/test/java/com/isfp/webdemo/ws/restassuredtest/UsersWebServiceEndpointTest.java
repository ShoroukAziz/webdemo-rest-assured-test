package com.isfp.webdemo.ws.restassuredtest;

//import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;



import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
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

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI="http://localhost";
		RestAssured.port= 8080;
	}

    /*
     * testUserLogin()
     * 
     * */
	@Test
	final void a() {
		
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
		
		String authorizationHeader = response.header("Authorization");
		String userId = response.header("UserId");
				
		assertNotNull(authorizationHeader);
		assertNotNull(userId);
	}

}
