package com.sislogica.api.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class TurmaResourceTest {

    @Test
    @DisplayName("GET /api/v1/turmas: Listagem de turmas deve responder 200")
    public void testListarTurmasEndpoint() {
        given()
            .queryParam("pagina", 0)
            .queryParam("tamanho", 10)
        .when()
            .get("/api/v1/turmas")
        .then()
            .statusCode(200)
            .body(notNullValue());
    }

    @Test
    @DisplayName("GET /api/v1/turmas/{id}: ID inexistente deve responder 404")
    public void testBuscarTurmaInexistente() {
        given()
            .pathParam("id", 999999L)
        .when()
            .get("/api/v1/turmas/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("POST /api/v1/turmas: Payload vazio deve responder 400 por Bean Validation")
    public void testCriarTurmaInvalida() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")
        .when()
            .post("/api/v1/turmas")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("DELETE /api/v1/turmas/{id}: ID inexistente deve responder 404")
    public void testDeletarTurmaInexistente() {
        given()
            .pathParam("id", 999999L)
        .when()
            .delete("/api/v1/turmas/{id}")
        .then()
            .statusCode(404);
    }
}
