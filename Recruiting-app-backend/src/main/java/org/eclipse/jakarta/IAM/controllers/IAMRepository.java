package org.eclipse.jakarta.IAM.controllers;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.eclipse.jakarta.IAM.entities.Grant;
import org.eclipse.jakarta.IAM.entities.Identity;
import org.eclipse.jakarta.IAM.entities.Tenant;

import java.util.Optional;

@Singleton
public class IAMRepository {

    @Inject
    private EntityManager entityManager;

    // ===== Tenant =====
    public Optional<Tenant> findTenantByName(String name) {
        try {
            Tenant tenant = entityManager.createQuery(
                            "SELECT t FROM Tenant t WHERE t.name = :name",
                            Tenant.class
                    ).setParameter("name", name)
                    .getSingleResult();
            return Optional.of(tenant);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    // ===== Identity =====
    public Optional<Identity> findIdentityByUsername(String username) {
        try {
            Identity identity = entityManager.createQuery(
                            "SELECT i FROM Identity i WHERE i.username = :username",
                            Identity.class
                    ).setParameter("username", username)
                    .getSingleResult();
            return Optional.of(identity);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    // ===== Grant =====
    public Optional<Grant> findGrant(Short tenantId, Long identityId) {
        try {
            Grant grant = entityManager.createQuery(
                            "SELECT g FROM Grant g " +
                                    "WHERE g.id.tenantId = :tenantId " +
                                    "AND g.id.identityId = :identityId",
                            Grant.class
                    )
                    .setParameter("tenantId", tenantId)
                    .setParameter("identityId", identityId)
                    .getSingleResult();

            return Optional.of(grant);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    // ===== Save operations =====
    public Identity saveIdentity(Identity identity) {
        entityManager.persist(identity);
        return identity;
    }

    public Tenant saveTenant(Tenant tenant) {
        entityManager.persist(tenant);
        return tenant;
    }

    public Grant saveGrant(Grant grant) {
        entityManager.persist(grant);
        return grant;
    }
}
