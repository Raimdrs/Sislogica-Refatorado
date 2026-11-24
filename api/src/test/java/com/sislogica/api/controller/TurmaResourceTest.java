package com.sislogica.api.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class TurmaResourceTest {

    private String codigoUnico(String prefixo) {
        return prefixo + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private Long criarTurmaAuxiliar(String codigo, String professor, String descricao) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "codigo", codigo,
                        "professor", professor,
                        "descricao", descricao
                ))
        .when()
                .post("/api/v1/turmas")
        .then()
                .statusCode(201)
                .header("Location", containsString("/api/v1/turmas/"))
                .extract()
                .response();

        return response.jsonPath().getLong("id");
    }

    private void removerTurmaAuxiliar(Long id) {
        if (id != null) {
            given()
                    .pathParam("id", id)
            .when()
                    .delete("/api/v1/turmas/{id}");
        }
    }

    @Test
    @DisplayName("CRUD completo de Turmas: POST (201 + Location), GET, PUT, DELETE (204) e GET apos remocao (404)")
    public void testFluxoCompletoCrudTurma() {
        String codigo = codigoUnico("CRUD");
        Long id = null;

        try {
            Response createResp = given()
                    .contentType(ContentType.JSON)
                    .body(Map.of(
                            "codigo", codigo,
                            "professor", "Prof. Aristoteles",
                            "descricao", "Logica Formal I"
                    ))
            .when()
                    .post("/api/v1/turmas")
            .then()
                    .statusCode(201)
                    .header("Location", containsString("/api/v1/turmas/"))
                    .body("id", notNullValue())
                    .body("codigo", equalTo(codigo))
                    .body("professor", equalTo("Prof. Aristoteles"))
                    .body("descricao", equalTo("Logica Formal I"))
                    .body("criadoEm", notNullValue())
                    .extract()
                    .response();

            id = createResp.jsonPath().getLong("id");

            given()
                    .pathParam("id", id)
            .when()
                    .get("/api/v1/turmas/{id}")
            .then()
                    .statusCode(200)
                    .body("id", equalTo(id.intValue()))
                    .body("codigo", equalTo(codigo));

            String codigoAtualizado = codigoUnico("UPD");
            given()
                    .contentType(ContentType.JSON)
                    .pathParam("id", id)
                    .body(Map.of(
                            "codigo", codigoAtualizado,
                            "professor", "Prof. Frege",
                            "descricao", "Logica Simbolica"
                    ))
            .when()
                    .put("/api/v1/turmas/{id}")
            .then()
                    .statusCode(200)
                    .body("codigo", equalTo(codigoAtualizado))
                    .body("professor", equalTo("Prof. Frege"))
                    .body("descricao", equalTo("Logica Simbolica"));

            given()
                    .pathParam("id", id)
            .when()
                    .delete("/api/v1/turmas/{id}")
            .then()
                    .statusCode(204);

            given()
                    .pathParam("id", id)
            .when()
                    .get("/api/v1/turmas/{id}")
            .then()
                    .statusCode(404);

            id = null;
        } finally {
            removerTurmaAuxiliar(id);
        }
    }

    @Test
    @DisplayName("GET /api/v1/turmas: Paginacao real no PostgreSQL e filtro por professor")
    public void testPaginacaoEFiltroPorProfessor() {
        String professorUnico = "Prof. Paginacao " + UUID.randomUUID().toString().substring(0, 8);
        List<Long> idsCriados = new ArrayList<>();

        try {
            idsCriados.add(criarTurmaAuxiliar(codigoUnico("PAG1"), professorUnico, "Turma 1"));
            idsCriados.add(criarTurmaAuxiliar(codigoUnico("PAG2"), professorUnico, "Turma 2"));
            idsCriados.add(criarTurmaAuxiliar(codigoUnico("PAG3"), professorUnico, "Turma 3"));

            given()
                    .queryParam("professor", professorUnico)
                    .queryParam("pagina", 0)
                    .queryParam("tamanho", 2)
            .when()
                    .get("/api/v1/turmas")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(2));

            given()
                    .queryParam("professor", professorUnico)
                    .queryParam("pagina", 1)
                    .queryParam("tamanho", 2)
            .when()
                    .get("/api/v1/turmas")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1));
        } finally {
            idsCriados.forEach(this::removerTurmaAuxiliar);
        }
    }

    @Test
    @DisplayName("POST /api/v1/turmas: Codigo duplicado deve responder 400 em Problem Details (RFC 9457)")
    public void testCriarTurmaCodigoDuplicado() {
        String codigo = codigoUnico("DUP");
        Long id = criarTurmaAuxiliar(codigo, "Prof. Boole", "Original");

        try {
            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of(
                            "codigo", codigo,
                            "professor", "Prof. Outro",
                            "descricao", "Duplicada"
                    ))
            .when()
                    .post("/api/v1/turmas")
            .then()
                    .statusCode(400)
                    .contentType(containsString("application/problem+json"))
                    .body("status", equalTo(400))
                    .body("detail", containsString(codigo));
        } finally {
            removerTurmaAuxiliar(id);
        }
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
    @DisplayName("POST /api/v1/turmas: Payload vazio deve responder 400 com violations em Problem Details")
    public void testCriarTurmaInvalida() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")
        .when()
            .post("/api/v1/turmas")
        .then()
            .statusCode(400)
            .contentType(containsString("application/problem+json"))
            .body("status", equalTo(400))
            .body("violations.size()", greaterThan(0));
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
