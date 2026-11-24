package com.sislogica.api.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class EquivalenciaResourceTest {

    @Test
    @DisplayName("POST /api/v1/equivalencia/validar: Lei de De Morgan deve retornar 200 e equivalentes=true")
    public void testValidarEquivalenciaDeMorgan() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "formulaA", "~(P ^ Q)",
                        "formulaB", "~P v ~Q"
                ))
        .when()
                .post("/api/v1/equivalencia/validar")
        .then()
                .statusCode(200)
                .body("equivalentes", equalTo(true))
                .body("contraexemplo", nullValue())
                .body("variaveis", contains("P", "Q"))
                .body("tabelaVerdade", hasSize(4));
    }

    @Test
    @DisplayName("POST /api/v1/equivalencia/validar: Fórmulas não equivalentes devem retornar 200 com contraexemplo")
    public void testValidarEquivalenciaFalsaComContraexemplo() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "formulaA", "P -> Q",
                        "formulaB", "Q -> P"
                ))
        .when()
                .post("/api/v1/equivalencia/validar")
        .then()
                .statusCode(200)
                .body("equivalentes", equalTo(false))
                .body("contraexemplo.P", equalTo(true))
                .body("contraexemplo.Q", equalTo(false))
                .body("tabelaVerdade", hasSize(4));
    }

    @Test
    @DisplayName("POST /api/v1/equivalencia/validar: Fórmula sintaticamente inválida deve responder 400 em Problem Details")
    public void testValidarFormulaMalformada() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "formulaA", "(P -> Q",
                        "formulaB", "~P v Q"
                ))
        .when()
                .post("/api/v1/equivalencia/validar")
        .then()
                .statusCode(400)
                .contentType(containsString("application/problem+json"))
                .body("status", equalTo(400))
                .body("detail", containsString("Parêntese"));
    }

    @Test
    @DisplayName("POST /api/v1/equivalencia/validar: Payload vazio deve responder 400 com violations em Problem Details")
    public void testValidarPayloadVazio() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
        .when()
                .post("/api/v1/equivalencia/validar")
        .then()
                .statusCode(400)
                .contentType(containsString("application/problem+json"))
                .body("status", equalTo(400))
                .body("violations.size()", greaterThan(0));
    }
}
