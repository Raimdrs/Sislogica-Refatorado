package br.com.sislogica.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class SilogismoResourceTest {

    @Test
    public void testListarSilogismosPorTurmaPaginadoEndpoint() {
        // Define o ID de uma turma que exista no seu banco de dados de teste
        Long turmaIdTeste = 1L;

        given()
            .pathParam("turmaId", turmaIdTeste)
            .queryParam("pagina", 0)
            .queryParam("tamanho", 5)
        .when()
            .get("/api/v1/turmas/{turmaId}/silogismos")
        .then()
            .statusCode(200)
            .body(notNullValue()); // Verifica se o corpo da resposta não é nulo (retorna um array JSON)
    }
}
