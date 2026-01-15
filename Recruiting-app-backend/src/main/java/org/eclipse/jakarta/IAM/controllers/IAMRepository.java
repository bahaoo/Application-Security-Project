package org.eclipse.jakarta.IAM.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.eclipse.jakarta.IAM.entities.Grant;
import org.eclipse.jakarta.IAM.entities.GrantPK;
import org.eclipse.jakarta.IAM.entities.Identity;
import org.eclipse.jakarta.IAM.entities.Tenant;

import java.util.Optional;

@ApplicationScoped
public class IAMRepository {

    @PersistenceContext(unitName = "iam")
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

    public Optional<Tenant> findTenantByClientId(String clientId) {
        try {
            Tenant tenant = entityManager.createQuery(
                            "SELECT t FROM Tenant t WHERE t.clientId = :clientId",
                            Tenant.class
                    ).setParameter("clientId", clientId)
                    .getSingleResult();
            return Optional.of(tenant);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Tenant> findTenantById(Short id) {
        return Optional.ofNullable(entityManager.find(Tenant.class, id));
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

    public Optional<Identity> findIdentityById(Long id) {
        return Optional.ofNullable(entityManager.find(Identity.class, id));
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
    @Transactional
    public Identity save(Identity identity) {
        if (identity.getId() == null) {
            entityManager.persist(identity);
            return identity;
        } else {
            return entityManager.merge(identity);
        }
    }

    @Transactional
    public Tenant save(Tenant tenant) {
        if (tenant.getId() == null) {
            entityManager.persist(tenant);
            return tenant;
        } else {
            return entityManager.merge(tenant);
        }
    }

    @Transactional
    public Grant save(Grant grant) {
        return entityManager.merge(grant);
    }

    @Transactional
    public void deleteGrant(Grant grant) {
        entityManager.remove(entityManager.contains(grant) ? grant : entityManager.merge(grant));
    }
}
