package br.com.sislogica.resource;

import br.com.sislogica.domain.entity.SilogismoPanacheEntity;
import br.com.sislogica.service.SilogismoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/v1/turmas/{turmaId}/silogismos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SilogismoResource {

    @Inject
    SilogismoService silogismoService;

    @GET
    public Response listarSilogismosDaTurma(
            @PathParam("turmaId") Long turmaId,
            @QueryParam("pagina") @DefaultValue("0") int pagina,
            @QueryParam("tamanho") @DefaultValue("10") int tamanho) {

        List<SilogismoPanacheEntity> silogismos = silogismoService.buscarPorTurmaPaginado(turmaId, pagina, tamanho);
        
        return Response.ok(silogismos).build();
    }
}
