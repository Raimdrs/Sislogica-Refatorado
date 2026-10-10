package com.sislogica.api.dto;

import java.net.URI;
import java.time.Instant;
import java.util.List;

/**
 * Representação padronizada de erros HTTP conforme a RFC 9457 (Problem Details for HTTP APIs).
 */
public record ProblemDetails(
        URI type,
        String title,
        int status,
        String detail,
        URI instance,
        List<Violation> violations,
        Instant timestamp
) {

    public ProblemDetails(URI type, String title, int status, String detail, URI instance) {
        this(type != null ? type : URI.create("about:blank"), title, status, detail, instance, null, Instant.now());
    }

    public ProblemDetails(URI type, String title, int status, String detail, URI instance, List<Violation> violations) {
        this(type != null ? type : URI.create("about:blank"), title, status, detail, instance, violations, Instant.now());
    }

    public record Violation(String campo, String mensagem) {}
}
