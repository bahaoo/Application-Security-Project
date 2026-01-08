package org.eclipse.jakarta.IAM.controllers;

import jakarta.annotation.Priority;
import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.jakarta.IAM.entities.Grant;

import java.io.Serializable;
import java.util.Optional;

@Decorator
@Priority(Interceptor.Priority.APPLICATION)
public abstract class AuthorizationDecorator implements Serializable {

    @Any
    @Inject
    @Delegate
    private IAMRepository delegate;

    /**
     * Ensures that the currently authenticated identity
     * has an active grant for the given tenant.
     */
    protected void authorizeGrantAccess(Short tenantId, Long identityId) {
        Optional<Grant> grant = delegate.findGrant(tenantId, identityId);
        if (grant.isEmpty()) {
            throw new NotAuthorizedException("Access denied: no valid grant found");
        }
    }
}
