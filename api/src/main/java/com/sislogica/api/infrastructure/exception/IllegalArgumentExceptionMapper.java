package com.sislogica.api.infrastructure.exception;

import com.sislogica.api.dto.ProblemDetails;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.net.URI;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        URI instanceUri = uriInfo != null ? uriInfo.getRequestUri() : null;
        ProblemDetails problem = new ProblemDetails(
                URI.create("urn:problem-type:bad-request"),
                "Requisição Inválida",
                Response.Status.BAD_REQUEST.getStatusCode(),
                exception.getMessage(),
                instanceUri
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(problem)
                .build();
    }
}
