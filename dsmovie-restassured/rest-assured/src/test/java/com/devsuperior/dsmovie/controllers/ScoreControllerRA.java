package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class ScoreControllerRA {

	private String clientUsername, clientPassword;
	private String clientToken;
	private Map<String, Object> putScore;


	@BeforeEach
	public void setUp() throws Exception{
		baseURI = "http://localhost:8080";

		clientUsername = "alex@gmail.com";
		clientPassword = "123456";

		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);

		putScore = new HashMap<>();
		putScore.put("movieId", 1);
		putScore.put("score", 4);
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		Long nonExistsMovieId = 1000L;
		putScore.put("movieId", nonExistsMovieId);
		JSONObject jsonObject = new JSONObject(putScore);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(jsonObject)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.log()
				.all()
		.when()
				.put("/scores")
		.then()
				.statusCode(404);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		putScore.put("movieId", null);
		JSONObject jsonObject = new JSONObject(putScore);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(jsonObject)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.log()
				.all()
		.when()
				.put("/scores")
		.then()
				.statusCode(422);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		putScore.put("score", -4.0);
		JSONObject jsonObject = new JSONObject(putScore);

		given().header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(jsonObject)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.log()
				.all()
		.when()
				.put("/scores")
		.then()
				.statusCode(422);
	}
}
