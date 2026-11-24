package com.sislogica.api.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class SilogismoResourceTest {

    private Long criarTurmaAuxiliar() {
        String codigo = "SIL-" + UUID.randomUUID().toString().substring(0, 8);
        return given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "codigo", codigo,
                        "professor", "Prof. Aristoteles",
                        "descricao", "Turma de teste de silogismos"
                ))
        .when()
                .post("/api/v1/turmas")
        .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }

    private void removerTurmaAuxiliar(Long turmaId) {
        if (turmaId != null) {
            given()
                    .pathParam("id", turmaId)
            .when()
                    .delete("/api/v1/turmas/{id}");
        }
    }

    private Map<String, Object> montarPayloadSilogismo(String titulo, Long turmaId) {
        return Map.of(
                "titulo", titulo,
                "premissaMaior", Map.of(
                        "quantificador", "A",
                        "sujeito", "M",
                        "predicado", "T",
                        "transliteracao", "Todo homem e mortal"
                ),
                "premissaMenor", Map.of(
                        "quantificador", "A",
                        "sujeito", "t",
                        "predicado", "M",
                        "transliteracao", "Socrates e homem"
                ),
                "conclusao", Map.of(
                        "quantificador", "A",
                        "sujeito", "t",
                        "predicado", "T",
                        "transliteracao", "Socrates e mortal"
                ),
                "turmaId", turmaId
        );
    }

    private Long criarSilogismoAuxiliar(String titulo, Long turmaId) {
        return given()
                .contentType(ContentType.JSON)
                .body(montarPayloadSilogismo(titulo, turmaId))
        .when()
                .post("/api/v1/silogismos")
        .then()
                .statusCode(201)
                .header("Location", containsString("/api/v1/silogismos/"))
                .extract()
                .jsonPath()
                .getLong("id");
    }

    @Test
    @DisplayName("CRUD completo de Silogismos com PostgreSQL real: POST (201 + Location), GET, PUT e DELETE (204)")
    public void testFluxoCompletoCrudSilogismo() {
        Long turmaId = criarTurmaAuxiliar();

        try {
            Response createResp = given()
                    .contentType(ContentType.JSON)
                    .body(montarPayloadSilogismo("Silogismo Barbara", turmaId))
            .when()
                    .post("/api/v1/silogismos")
            .then()
                    .statusCode(201)
                    .header("Location", containsString("/api/v1/silogismos/"))
                    .body("id", notNullValue())
                    .body("titulo", equalTo("Silogismo Barbara"))
                    .body("turmaId", equalTo(turmaId.intValue()))
                    .body("premissaMaior.quantificador", equalTo("A"))
                    .body("premissaMaior.sujeito", equalTo("M"))
                    .body("premissaMaior.predicado", equalTo("T"))
                    .body("conclusao.transliteracao", equalTo("Socrates e mortal"))
                    .body("criadoEm", notNullValue())
                    .extract()
                    .response();

            Long silogismoId = createResp.jsonPath().getLong("id");

            given()
                    .pathParam("id", silogismoId)
            .when()
                    .get("/api/v1/silogismos/{id}")
            .then()
                    .statusCode(200)
                    .body("id", equalTo(silogismoId.intValue()))
                    .body("titulo", equalTo("Silogismo Barbara"));

            Map<String, Object> payloadAtualizado = Map.of(
                    "titulo", "Silogismo Celarent",
                    "premissaMaior", Map.of("quantificador", "E", "sujeito", "M", "predicado", "T", "transliteracao", "Nenhum M e T"),
                    "premissaMenor", Map.of("quantificador", "A", "sujeito", "t", "predicado", "M", "transliteracao", "Todo t e M"),
                    "conclusao", Map.of("quantificador", "E", "sujeito", "t", "predicado", "T", "transliteracao", "Nenhum t e T"),
                    "turmaId", turmaId
            );

            given()
                    .contentType(ContentType.JSON)
                    .pathParam("id", silogismoId)
                    .body(payloadAtualizado)
            .when()
                    .put("/api/v1/silogismos/{id}")
            .then()
                    .statusCode(200)
                    .body("titulo", equalTo("Silogismo Celarent"))
                    .body("premissaMaior.quantificador", equalTo("E"))
                    .body("conclusao.quantificador", equalTo("E"));

            given()
                    .pathParam("id", silogismoId)
            .when()
                    .delete("/api/v1/silogismos/{id}")
            .then()
                    .statusCode(204);

            given()
                    .pathParam("id", silogismoId)
            .when()
                    .get("/api/v1/silogismos/{id}")
            .then()
                    .statusCode(404);
        } finally {
            removerTurmaAuxiliar(turmaId);
        }
    }

    @Test
    @DisplayName("GET /api/v1/turmas/{turmaId}/silogismos: Relacionamento 1:N com paginacao real no banco")
    public void testListarSilogismosPorTurmaPaginadoEndpoint() {
        Long turmaId = criarTurmaAuxiliar();

        try {
            criarSilogismoAuxiliar("Silogismo 1", turmaId);
            criarSilogismoAuxiliar("Silogismo 2", turmaId);
            criarSilogismoAuxiliar("Silogismo 3", turmaId);

            given()
                    .pathParam("turmaId", turmaId)
                    .queryParam("pagina", 0)
                    .queryParam("tamanho", 2)
            .when()
                    .get("/api/v1/turmas/{turmaId}/silogismos")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(2));

            given()
                    .pathParam("turmaId", turmaId)
                    .queryParam("pagina", 1)
                    .queryParam("tamanho", 2)
            .when()
                    .get("/api/v1/turmas/{turmaId}/silogismos")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1));

            given()
                    .pathParam("turmaId", turmaId)
                    .queryParam("pagina", 2)
                    .queryParam("tamanho", 2)
            .when()
                    .get("/api/v1/turmas/{turmaId}/silogismos")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        } finally {
            removerTurmaAuxiliar(turmaId);
        }
    }

    @Test
    @DisplayName("GET /api/v1/silogismos: Listagem paginada deve responder 200")
    public void testListarSilogismosPaginadoEndpoint() {
        Long turmaId = criarTurmaAuxiliar();

        try {
            criarSilogismoAuxiliar("Silogismo Listagem 1", turmaId);
            criarSilogismoAuxiliar("Silogismo Listagem 2", turmaId);

            given()
                    .queryParam("pagina", 0)
                    .queryParam("tamanho", 2)
            .when()
                    .get("/api/v1/silogismos")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(2));
        } finally {
            removerTurmaAuxiliar(turmaId);
        }
    }

    @Test
    @DisplayName("POST /api/v1/silogismos: Turma inexistente deve responder 400 em Problem Details")
    public void testCriarSilogismoComTurmaInexistente() {
        given()
                .contentType(ContentType.JSON)
                .body(montarPayloadSilogismo("Silogismo Sem Turma", 999999L))
        .when()
                .post("/api/v1/silogismos")
        .then()
                .statusCode(400)
                .contentType(containsString("application/problem+json"))
                .body("status", equalTo(400))
                .body("detail", containsString("999999"));
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
    @DisplayName("POST /api/v1/silogismos: Payload vazio deve responder 400 com violations em Problem Details")
    public void testCriarSilogismoInvalido() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")
        .when()
            .post("/api/v1/silogismos")
        .then()
            .statusCode(400)
            .contentType(containsString("application/problem+json"))
            .body("status", equalTo(400))
            .body("violations.size()", greaterThan(0));
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
