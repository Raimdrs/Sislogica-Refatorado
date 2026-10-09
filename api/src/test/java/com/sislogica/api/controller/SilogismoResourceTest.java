package com.sislogica.api.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class SilogismoResourceTest {

    @Test
    @DisplayName("GET /api/v1/turmas/{turmaId}/silogismos: Rota aninhada deve responder 200")
    public void testListarSilogismosPorTurmaPaginadoEndpoint() {
        Long turmaIdTeste = 1L;

        given()
            .pathParam("turmaId", turmaIdTeste)
            .queryParam("pagina", 0)
            .queryParam("tamanho", 5)
        .when()
            .get("/api/v1/turmas/{turmaId}/silogismos")
        .then()
            .statusCode(200)
            .body(notNullValue());
    }

    @Test
    @DisplayName("GET /api/v1/silogismos: Listagem paginada deve responder 200")
    public void testListarSilogismosPaginadoEndpoint() {
        given()
            .queryParam("pagina", 0)
            .queryParam("tamanho", 10)
        .when()
            .get("/api/v1/silogismos")
        .then()
            .statusCode(200)
            .body(notNullValue());
    }

    @Test
    @DisplayName("GET /api/v1/silogismos/{id}: ID inexistente deve responder 404")
    public void testBuscarSilogismoInexistente() {
        given()
            .pathParam("id", 999999L)
        .when()
            .get("/api/v1/silogismos/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("POST /api/v1/silogismos: Payload vazio deve responder 400 por violacao de Bean Validation")
    public void testCriarSilogismoInvalido() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")
        .when()
            .post("/api/v1/silogismos")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("DELETE /api/v1/silogismos/{id}: ID inexistente deve responder 404")
    public void testDeletarSilogismoInexistente() {
        given()
            .pathParam("id", 999999L)
        .when()
            .delete("/api/v1/silogismos/{id}")
        .then()
            .statusCode(404);
    }
}
