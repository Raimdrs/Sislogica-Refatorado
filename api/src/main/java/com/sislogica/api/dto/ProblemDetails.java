package com.sislogica.api.dto;

import java.net.URI;
import java.time.Instant;
import java.util.List;

/**
 * Representação padronizada de respostas de erro conforme a RFC 9457 (Problem Details for HTTP APIs).
 */
public class ProblemDetails {

    public URI type;
    public String title;
    public int status;
    public String detail;
    public URI instance;
    public List<Violation> violations;
    public Instant timestamp = Instant.now();

    public ProblemDetails() {
    }

    public ProblemDetails(URI type, String title, int status, String detail, URI instance) {
        this.type = type != null ? type : URI.create("about:blank");
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
    }

    public ProblemDetails(URI type, String title, int status, String detail, URI instance, List<Violation> violations) {
        this(type, title, status, detail, instance);
        this.violations = violations;
    }

    public static class Violation {
        public String campo;
        public String mensagem;

        public Violation() {
        }

        public Violation(String campo, String mensagem) {
            this.campo = campo;
            this.mensagem = mensagem;
        }
    }
}
