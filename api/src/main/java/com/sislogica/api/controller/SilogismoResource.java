package com.sislogica.api.controller;

import com.sislogica.api.dto.SilogismoRequestDTO;
import com.sislogica.api.dto.SilogismoResponseDTO;
import com.sislogica.api.service.SilogismoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Silogismos", description = "Operações CRUD e listagem aninhada de silogismos aristotélicos")
public class SilogismoResource {

    @Inject
    public SilogismoService silogismoService;

    @POST
    @Path("/silogismos")
    @Operation(summary = "Cadastrar silogismo", description = "Cria um novo silogismo aristotélico associado a uma turma")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Silogismo criado com sucesso"),
        @APIResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    public Response criarSilogismo(@Valid SilogismoRequestDTO dto) {
        SilogismoResponseDTO criado = silogismoService.criar(dto);
        URI location = URI.create("/api/v1/silogismos/" + criado.id);
        return Response.created(location).entity(criado).build();
    }

    @GET
    @Path("/silogismos")
    @Operation(summary = "Listar silogismos", description = "Lista silogismos de forma paginada no banco com filtro opcional por modo")
    @APIResponse(responseCode = "200", description = "Lista de silogismos retornada com sucesso")
    public Response listarSilogismos(
            @QueryParam("pagina") @DefaultValue("0") int pagina,
            @QueryParam("tamanho") @DefaultValue("10") int tamanho,
            @QueryParam("modo") String modo) {
        List<SilogismoResponseDTO> silogismos = silogismoService.listarPaginado(pagina, tamanho, modo);
        return Response.ok(silogismos).build();
    }

    @GET
    @Path("/silogismos/{id}")
    @Operation(summary = "Buscar silogismo por ID", description = "Retorna os detalhes de um silogismo específico")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Silogismo encontrado"),
        @APIResponse(responseCode = "404", description = "Silogismo não encontrado")
    })
    public Response buscarSilogismoPorId(@PathParam("id") Long id) {
        return silogismoService.buscarPorId(id)
                .map(dto -> Response.ok(dto).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/silogismos/{id}")
    @Operation(summary = "Atualizar silogismo", description = "Atualiza premissas e conclusão de um silogismo existente")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Silogismo atualizado com sucesso"),
        @APIResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @APIResponse(responseCode = "404", description = "Silogismo não encontrado")
    })
    public Response atualizarSilogismo(@PathParam("id") Long id, @Valid SilogismoRequestDTO dto) {
        return silogismoService.atualizar(id, dto)
                .map(atualizado -> Response.ok(atualizado).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/silogismos/{id}")
    @Operation(summary = "Remover silogismo", description = "Remove um silogismo por ID")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Silogismo removido com sucesso"),
        @APIResponse(responseCode = "404", description = "Silogismo não encontrado")
    })
    public Response deletarSilogismo(@PathParam("id") Long id) {
        boolean deletado = silogismoService.deletar(id);
        if (deletado) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // Rota HTTP hospedada em TurmaResource (GET /api/v1/turmas/{turmaId}/silogismos) para evitar conflito de prefixo no JAX-RS
    public Response listarSilogismosDaTurma(
            Long turmaId,
            int pagina,
            int tamanho) {
        List<SilogismoResponseDTO> silogismos = silogismoService.buscarPorTurmaPaginado(turmaId, pagina, tamanho);
        return Response.ok(silogismos).build();
    }
}
