package com.sislogica.api.infrastructure.exception;

import com.sislogica.api.dto.ProblemDetails;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<ProblemDetails.Violation> violations = new ArrayList<>();

        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String campo = violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "";
            if (campo.contains(".")) {
                campo = campo.substring(campo.lastIndexOf('.') + 1);
            }
            violations.add(new ProblemDetails.Violation(campo, violation.getMessage()));
        }

        URI instanceUri = uriInfo != null ? uriInfo.getRequestUri() : null;
        ProblemDetails problem = new ProblemDetails(
                URI.create("urn:problem-type:validation-error"),
                "Erro de Validação",
                Response.Status.BAD_REQUEST.getStatusCode(),
                "Um ou mais campos da requisição são inválidos.",
                instanceUri,
                violations
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(problem)
                .build();
    }
}
