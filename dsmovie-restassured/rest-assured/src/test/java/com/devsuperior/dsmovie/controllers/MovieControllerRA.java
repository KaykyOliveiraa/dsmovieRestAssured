package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class MovieControllerRA {
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String clientToken, adminToken, invalidToken;
	private Map<String, Object> postMovies;

	@BeforeEach
	public void setUp() throws Exception{
		baseURI = "http://localhost:8080";

		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";

		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		invalidToken = adminToken + "xpto";

		postMovies = new HashMap<>();
		postMovies.put("title", "Test Movie");
		postMovies.put("score", 0.0);
		postMovies.put("count", 0);
		postMovies.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given()
				.get("/movies")
		.then()
				.statusCode(200)
				.body("content.id[0]", is(1))
				.body("content.title[0]", equalTo("The Witcher"))
				.body("content.score[0]", is(4.5F))
				.body("content.count[0]", is(2))
				.body("content.image[0]", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
		String titleName = "venom";

		given()
				.get("/movies?title={titleName}", titleName)
		.then()
				.statusCode(200)
				.body("content.id[0]", is(2))
				.body("content.title[0]", equalTo("Venom: Tempo de Carnificina"))
				.body("content.score[0]", is(3.3F))
				.body("content.count[0]", is(3))
				.body("content.image[0]", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/vIgyYkXkg6NC2whRbYjBD7eb3Er.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
		Long movieId = 1L;

		given()
				.get("/movies/{id}", movieId)
		.then()
				.statusCode(200)
				.body("id", is(1))
				.body("title", equalTo("The Witcher"))
				.body("score", is(4.5F))
				.body("count", is(2))
				.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		Long nonExistsMovieId = 1000L;

		given().get("/movies/{id}", nonExistsMovieId)
				.then()
				.statusCode(404)
				.body("error", equalTo("Recurso não encontrado"));
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		postMovies.put("title", null);
		JSONObject jsonObject = new JSONObject(postMovies);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(jsonObject)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.log()
				.all()
		.when()
				.post("/movies")
		.then()
				.statusCode(422);

	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		JSONObject jsonObject = new JSONObject(postMovies);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(jsonObject)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.log()
				.all()
		.when()
				.post("/movies")
		.then()
				.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject jsonObject = new JSONObject(postMovies);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + invalidToken)
				.body(jsonObject)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.log()
				.all()
		.when()
				.post("/movies")
		.then()
				.statusCode(401);
	}
}
