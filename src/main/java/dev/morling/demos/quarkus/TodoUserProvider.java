package dev.morling.demos.quarkus;

import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class TodoUserProvider {

    @Inject
    SecurityIdentity identity;

    @Produces
    @RequestScoped
    public TodoUser get() {
        return new TodoUser((OidcJwtCallerPrincipal) identity.getPrincipal());
    }
}
