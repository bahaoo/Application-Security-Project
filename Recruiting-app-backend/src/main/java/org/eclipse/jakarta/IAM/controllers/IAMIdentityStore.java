package org.eclipse.jakarta.IAM.controllers;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.transaction.Transactional;
import org.eclipse.jakarta.IAM.entities.Identity;
import org.eclipse.jakarta.IAM.security.Argon2Utility;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Singleton
@Transactional
public class IAMIdentityStore implements IdentityStore {

    @Inject
    private EntityManager entityManager;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (!(credential instanceof UsernamePasswordCredential upc)) {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }
        return validate(upc);
    }

    private CredentialValidationResult validate(UsernamePasswordCredential upc) {
        try {
            Identity identity = entityManager.createQuery(
                            "SELECT i FROM Identity i WHERE i.username = :username", Identity.class)
                    .setParameter("username", upc.getCaller())
                    .getSingleResult();

            Objects.requireNonNull(identity, "Identity should not be null");

            // Verify password with Argon2
            if (Argon2Utility.check(identity.getPassword(), upc.getPassword().getValue())) {
                // For simplicity, everyone gets a default "USER" group
                Set<String> groups = Collections.singleton("USER");
                return new CredentialValidationResult(upc.getCaller(), groups);
            }
            return CredentialValidationResult.INVALID_RESULT;

        } catch (Exception e) {
            return CredentialValidationResult.INVALID_RESULT;
        }
    }

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        return validationResult.getCallerGroups();
    }

    /**
     * Helper method for SignupController or any custom validation logic.
     * Returns true if username exists and password is correct.
     */
    public boolean validateCredentials(String username, String rawPassword) {
        Optional<Identity> identityOpt;
        try {
            identityOpt = Optional.ofNullable(
                    entityManager.createQuery(
                                    "SELECT i FROM Identity i WHERE i.username = :username", Identity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return false;
        }

        if (identityOpt.isEmpty()) return false;

        Identity identity = identityOpt.get();
        return Argon2Utility.check(identity.getPassword(), rawPassword.toCharArray());
    }
}
