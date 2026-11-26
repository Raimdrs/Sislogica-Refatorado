package com.sislogica.api.controller;

import com.sislogica.api.dto.EquivalenciaRequestDTO;
import com.sislogica.api.dto.EquivalenciaResponseDTO;
import com.sislogica.api.dto.ProblemDetails;
import com.sislogica.api.service.EquivalenciaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/equivalencia")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Equivalências Lógicas", description = "Validação semântica e comparação de tabelas-verdade entre fórmulas proposicionais")
public class EquivalenciaResource {

    @Inject
    public EquivalenciaService equivalenciaService;

    @POST
    @Path("/validar")
    @Operation(
            summary = "Validar equivalência lógica",
            description = "Avalia se duas fórmulas proposicionais (formulaA e formulaB) são semanticamente equivalentes, "
                    + "unificando suas variáveis, gerando a tabela-verdade comparativa e retornando o primeiro contraexemplo em caso de divergência."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Equivalência avaliada com sucesso",
                    content = @Content(schema = @Schema(implementation = EquivalenciaResponseDTO.class))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Fórmula sintaticamente inválida ou campos obrigatórios ausentes (Problem Details RFC 9457)",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )
            )
    })
    public Response validarEquivalencia(@Valid EquivalenciaRequestDTO dto) {
        EquivalenciaResponseDTO resultado = equivalenciaService.validar(dto);
        return Response.ok(resultado).build();
    }
}
