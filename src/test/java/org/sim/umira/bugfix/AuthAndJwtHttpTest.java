package org.sim.umira.bugfix;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

/**
 * Test HTTP yang tidak butuh database: memastikan error input tidak lagi menjadi HTTP 500.
 */
@QuarkusTest
class AuthAndJwtHttpTest {

    @Test
    void securedEndpointTanpaTokenMengembalikan401() {
        given().when().get("/role/get-role")
                .then().statusCode(401).body("success", is(false));
    }

    @Test
    void bearerKosongMengembalikan401BukanError500() {
        given().header("Authorization", "Bearer ")
                .when().get("/role/get-role")
                .then().statusCode(401);
    }

    @Test
    void tokenSampahMengembalikan401() {
        given().header("Authorization", "Bearer ini.bukan.jwt")
                .when().get("/role/get-role")
                .then().statusCode(401);
    }

    @Test
    void verifyTokenRusakDianggapExpiredBukan500() {
        given().queryParam("token", "token-rusak")
                .when().get("/auth/verify-token")
                .then().statusCode(200).body("data", is(true));
    }

    @Test
    void resetPasswordTokenRusakMengembalikan400() {
        given().contentType(ContentType.JSON)
                .body("{\"token\":\"token-rusak\",\"password\":\"Rahasia123\"}")
                .when().post("/auth/reset-password")
                .then().statusCode(400);
    }
}
