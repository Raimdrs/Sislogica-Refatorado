package com.sislogica.api.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class SilogismoResourceTest {

    @Test
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
}
