package com.sislogica.api.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class CacheMetricasTest {

    @Inject
    MeterRegistry meterRegistry;

    private double getCacheGetsCount(String cacheName, String result) {
        var counter = meterRegistry.find("cache.gets")
                .tag("cache", cacheName)
                .tag("result", result)
                .functionCounter();
        return counter != null ? counter.count() : 0.0;
    }

    private Long criarTurmaAuxiliar(String prefixo) {
        String codigo = prefixo + "-" + UUID.randomUUID().toString().substring(0, 8);
        return given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "codigo", codigo,
                        "professor", "Prof. Cache",
                        "descricao", "Teste de Cache Caffeine"
                ))
        .when()
                .post("/api/v1/turmas")
        .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }

    private Long criarSilogismoAuxiliar(String titulo, Long turmaId) {
        Map<String, Object> payload = Map.of(
                "titulo", titulo,
                "premissaMaior", Map.of("quantificador", "A", "sujeito", "M", "predicado", "T", "transliteracao", "Todo M e T"),
                "premissaMenor", Map.of("quantificador", "A", "sujeito", "t", "predicado", "M", "transliteracao", "Todo t e M"),
                "conclusao", Map.of("quantificador", "A", "sujeito", "t", "predicado", "T", "transliteracao", "Todo t e T"),
                "turmaId", turmaId
        );
        return given()
                .contentType(ContentType.JSON)
                .body(payload)
        .when()
                .post("/api/v1/silogismos")
        .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
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
    @DisplayName("Cache Hit: Segunda chamada com o mesmo ID deve retornar resultado do cache sem reprocessar no banco")
    public void testSegundaChamadaRetornaResultadoCacheado() {
        Long turmaId = criarTurmaAuxiliar("HIT");
        Long silogismoId = criarSilogismoAuxiliar("Silogismo Cache Hit", turmaId);

        try {
            double turmasMissAntes = getCacheGetsCount("turmas", "miss");
            double turmasHitAntes = getCacheGetsCount("turmas", "hit");

            // 1a chamada para a turma: Cache MISS (vai ao banco e armazena no Caffeine)
            given()
                    .pathParam("id", turmaId)
            .when()
                    .get("/api/v1/turmas/{id}")
            .then()
                    .statusCode(200)
                    .body("id", equalTo(turmaId.intValue()));

            assertEquals(turmasMissAntes + 1.0, getCacheGetsCount("turmas", "miss"),
                    "Primeira busca da turma deve registrar 1 cache miss");
            assertEquals(turmasHitAntes, getCacheGetsCount("turmas", "hit"),
                    "Primeira busca da turma não deve registrar cache hit");

            // 2a chamada para a mesma turma: Cache HIT (retorna do Caffeine sem ir ao banco)
            given()
                    .pathParam("id", turmaId)
            .when()
                    .get("/api/v1/turmas/{id}")
            .then()
                    .statusCode(200)
                    .body("id", equalTo(turmaId.intValue()));

            assertEquals(turmasMissAntes + 1.0, getCacheGetsCount("turmas", "miss"),
                    "Segunda busca da mesma turma não deve incrementar cache miss");
            assertEquals(turmasHitAntes + 1.0, getCacheGetsCount("turmas", "hit"),
                    "Segunda busca da mesma turma deve registrar 1 cache hit");

            // Verifica o mesmo comportamento para o cache "silogismos"
            double silMissAntes = getCacheGetsCount("silogismos", "miss");
            double silHitAntes = getCacheGetsCount("silogismos", "hit");

            // 1a chamada do silogismo: Cache MISS
            given()
                    .pathParam("id", silogismoId)
            .when()
                    .get("/api/v1/silogismos/{id}")
            .then()
                    .statusCode(200)
                    .body("titulo", equalTo("Silogismo Cache Hit"));

            assertEquals(silMissAntes + 1.0, getCacheGetsCount("silogismos", "miss"));
            assertEquals(silHitAntes, getCacheGetsCount("silogismos", "hit"));

            // 2a chamada do mesmo silogismo: Cache HIT
            given()
                    .pathParam("id", silogismoId)
            .when()
                    .get("/api/v1/silogismos/{id}")
            .then()
                    .statusCode(200)
                    .body("titulo", equalTo("Silogismo Cache Hit"));

            assertEquals(silMissAntes + 1.0, getCacheGetsCount("silogismos", "miss"));
            assertEquals(silHitAntes + 1.0, getCacheGetsCount("silogismos", "hit"));
        } finally {
            removerTurmaAuxiliar(turmaId);
        }
    }

    @Test
    @DisplayName("Invalidação seletiva (@CacheInvalidate): Update e Delete invalidam apenas a chave alterada")
    public void testInvalidacaoSeletivaDeCacheAposUpdateEDelete() {
        Long turma1Id = criarTurmaAuxiliar("INV1");
        Long turma2Id = criarTurmaAuxiliar("INV2");

        try {
            // Popula ambas as turmas no cache (2 misses)
            given().pathParam("id", turma1Id).when().get("/api/v1/turmas/{id}").then().statusCode(200);
            given().pathParam("id", turma2Id).when().get("/api/v1/turmas/{id}").then().statusCode(200);

            double missAntesUpdate = getCacheGetsCount("turmas", "miss");
            double hitAntesUpdate = getCacheGetsCount("turmas", "hit");

            // Atualiza apenas turma1 -> deve invalidar seletivamente apenas a chave turma1Id
            String novoCodigo = "UPD-" + UUID.randomUUID().toString().substring(0, 8);
            given()
                    .contentType(ContentType.JSON)
                    .pathParam("id", turma1Id)
                    .body(Map.of(
                            "codigo", novoCodigo,
                            "professor", "Prof. Atualizado",
                            "descricao", "Descricao Atualizada"
                    ))
            .when()
                    .put("/api/v1/turmas/{id}")
            .then()
                    .statusCode(200)
                    .body("codigo", equalTo(novoCodigo));

            // turma2 não foi alterada: deve continuar sendo um Cache HIT!
            given()
                    .pathParam("id", turma2Id)
            .when()
                    .get("/api/v1/turmas/{id}")
            .then()
                    .statusCode(200);

            assertEquals(hitAntesUpdate + 1.0, getCacheGetsCount("turmas", "hit"),
                    "Turma não alterada deve permanecer no cache (HIT)");
            assertEquals(missAntesUpdate, getCacheGetsCount("turmas", "miss"),
                    "Turma não alterada não deve gerar MISS");

            // turma1 foi atualizada: próxima busca deve ser Cache MISS e trazer o dado novo
            given()
                    .pathParam("id", turma1Id)
            .when()
                    .get("/api/v1/turmas/{id}")
            .then()
                    .statusCode(200)
                    .body("codigo", equalTo(novoCodigo))
                    .body("professor", equalTo("Prof. Atualizado"));

            assertEquals(missAntesUpdate + 1.0, getCacheGetsCount("turmas", "miss"),
                    "Turma atualizada deve ter sofrido invalidação de cache (MISS na releitura)");

            // Deleta turma1 -> deve invalidar a chave turma1Id do cache
            given()
                    .pathParam("id", turma1Id)
            .when()
                    .delete("/api/v1/turmas/{id}")
            .then()
                    .statusCode(204);

            given()
                    .pathParam("id", turma1Id)
            .when()
                    .get("/api/v1/turmas/{id}")
            .then()
                    .statusCode(404);
            turma1Id = null;
        } finally {
            removerTurmaAuxiliar(turma1Id);
            removerTurmaAuxiliar(turma2Id);
        }
    }

    @Test
    @DisplayName("GET /q/metrics: Deve expor métricas de cache (hits, misses, evictions, puts) no formato Prometheus")
    public void testExposicaoDeMetricasDeCacheNoPrometheus() {
        Long turmaId = criarTurmaAuxiliar("MET");

        try {
            given().pathParam("id", turmaId).when().get("/api/v1/turmas/{id}").then().statusCode(200);
            given().pathParam("id", turmaId).when().get("/api/v1/turmas/{id}").then().statusCode(200);

            given()
            .when()
                    .get("/q/metrics")
            .then()
                    .statusCode(200)
                    .body(containsString("cache_gets_total"))
                    .body(containsString("cache=\"turmas\""))
                    .body(containsString("result=\"hit\""))
                    .body(containsString("result=\"miss\""))
                    .body(containsString("cache_puts_total"))
                    .body(containsString("cache_evictions_total"));
        } finally {
            removerTurmaAuxiliar(turmaId);
        }
    }
}
