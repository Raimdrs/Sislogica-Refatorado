package com.sislogica.api.controller;

import com.sislogica.api.dto.TurmaRequestDTO;
import com.sislogica.api.dto.TurmaResponseDTO;
import com.sislogica.api.service.TurmaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

@Path("/api/v1/turmas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Turmas", description = "Operações CRUD e gerenciamento de turmas/classes de lógica")
public class TurmaResource {

    @Inject
    public TurmaService turmaService;

    @Inject
    public com.sislogica.api.service.SilogismoService silogismoService;

    @POST
    @Operation(summary = "Criar turma", description = "Cadastra uma nova turma com código de acesso único e professor")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Turma criada com sucesso"),
        @APIResponse(responseCode = "400", description = "Dados de entrada inválidos ou código duplicado")
    })
    public Response criarTurma(@Valid TurmaRequestDTO dto) {
        TurmaResponseDTO criada = turmaService.criar(dto);
        URI location = URI.create("/api/v1/turmas/" + criada.id());
        return Response.created(location).entity(criada).build();
    }

    @GET
    @Operation(summary = "Listar turmas", description = "Lista turmas paginadas no banco com filtro opcional por professor")
    @APIResponse(responseCode = "200", description = "Lista de turmas retornada com sucesso")
    public Response listarTurmas(
            @QueryParam("pagina") @PositiveOrZero(message = "A página deve ser maior ou igual a zero") @DefaultValue("0") int pagina,
            @QueryParam("tamanho") @Min(value = 1, message = "O tamanho deve ser de no mínimo 1") @DefaultValue("10") int tamanho,
            @QueryParam("professor") String professor) {
        List<TurmaResponseDTO> turmas = turmaService.listarPaginado(pagina, tamanho, professor);
        return Response.ok(turmas).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar turma por ID", description = "Retorna os detalhes de uma turma específica")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Turma encontrada"),
        @APIResponse(responseCode = "404", description = "Turma não encontrada")
    })
    public Response buscarTurmaPorId(@PathParam("id") @Positive(message = "O ID deve ser um número positivo") Long id) {
        return turmaService.buscarPorId(id)
                .map(dto -> Response.ok(dto).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualizar turma", description = "Atualiza os dados de uma turma existente")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Turma atualizada com sucesso"),
        @APIResponse(responseCode = "400", description = "Dados de entrada inválidos ou conflito de código"),
        @APIResponse(responseCode = "404", description = "Turma não encontrada")
    })
    public Response atualizarTurma(@PathParam("id") @Positive(message = "O ID deve ser um número positivo") Long id, @Valid TurmaRequestDTO dto) {
        return turmaService.atualizar(id, dto)
                .map(atualizada -> Response.ok(atualizada).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Remover turma", description = "Remove uma turma pelo ID")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Turma removida com sucesso"),
        @APIResponse(responseCode = "404", description = "Turma não encontrada")
    })
    public Response deletarTurma(@PathParam("id") @Positive(message = "O ID deve ser um número positivo") Long id) {
        boolean deletada = turmaService.deletar(id);
        if (deletada) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{turmaId}/silogismos")
    @Operation(summary = "Listar silogismos da turma", description = "Rota aninhada comprovando o relacionamento 1:N entre Turma e Silogismos")
    @APIResponse(responseCode = "200", description = "Lista de silogismos da turma retornada com sucesso")
    public Response listarSilogismosDaTurma(
            @PathParam("turmaId") @Positive(message = "O ID deve ser um número positivo") Long turmaId,
            @QueryParam("pagina") @PositiveOrZero(message = "A página deve ser maior ou igual a zero") @DefaultValue("0") int pagina,
            @QueryParam("tamanho") @Min(value = 1, message = "O tamanho deve ser de no mínimo 1") @DefaultValue("10") int tamanho) {
        List<com.sislogica.api.dto.SilogismoResponseDTO> silogismos = silogismoService.buscarPorTurmaPaginado(turmaId, pagina, tamanho);
        return Response.ok(silogismos).build();
    }
}
