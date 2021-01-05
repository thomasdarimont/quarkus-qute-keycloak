package dev.morling.demos.quarkus;

import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;

public class TodoUser {

    private final OidcJwtCallerPrincipal principal;

    public TodoUser() {
        this(null); // to get rid of proxy warning
    }

    public TodoUser(OidcJwtCallerPrincipal principal) {
        this.principal = principal;
    }

    public String getUserId() {
        return principal.getSubject();
    }

    public String getUsername() {
        return principal.getName();
    }
}
