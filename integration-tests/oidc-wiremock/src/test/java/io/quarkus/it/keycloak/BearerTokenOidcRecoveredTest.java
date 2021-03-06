package io.quarkus.it.keycloak;

import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.smallrye.jwt.build.Jwt;

@QuarkusTest
public class BearerTokenOidcRecoveredTest {

    @Test
    public void testOidcRecoveredWithDiscovery() {
        String token = getAccessToken("alice", new HashSet<>(Arrays.asList("user", "admin")));

        // Server has not started
        RestAssured.given().auth().oauth2(token)
                .when().get("/recovered/api/users/preferredUserName")
                .then()
                .statusCode(500);

        // Server is starting now
        WiremockTestResource server = new WiremockTestResource();
        server.start();
        try {
            RestAssured.given().auth().oauth2(token)
                    .when().get("/recovered/api/users/preferredUserName")
                    .then()
                    .statusCode(200)
                    .body("userName", equalTo("alice"));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testOidcRecoveredWithNoDiscovery() {
        String token = getAccessToken("alice", new HashSet<>(Arrays.asList("user", "admin")));

        // Server has not started
        RestAssured.given().auth().oauth2(token)
                .when().get("/recovered-no-discovery/api/users/preferredUserName")
                .then()
                .statusCode(500);

        // Server is starting now
        WiremockTestResource server = new WiremockTestResource();
        server.start();
        try {
            RestAssured.given().auth().oauth2(token)
                    .when().get("/recovered-no-discovery/api/users/preferredUserName")
                    .then()
                    .statusCode(200)
                    .body("userName", equalTo("alice"));
        } finally {
            server.stop();
        }
    }

    private String getAccessToken(String userName, Set<String> groups) {
        return Jwt.preferredUserName(userName)
                .groups(groups)
                .issuer("https://server.example.com")
                .audience("https://service.example.com")
                .jws()
                .keyId("1")
                .sign();
    }
}
