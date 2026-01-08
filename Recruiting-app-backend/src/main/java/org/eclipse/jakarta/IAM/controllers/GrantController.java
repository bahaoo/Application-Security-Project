package org.eclipse.jakarta.IAM.controllers;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.eclipse.jakarta.IAM.entities.Grant;
import org.eclipse.jakarta.IAM.entities.Identity;
import org.eclipse.jakarta.IAM.entities.Tenant;

import java.time.LocalDateTime;
import java.util.Optional;

@Singleton
@Transactional
public class GrantController {

    @Inject
    private IAMRepository iamRepository;

    /**
     * Candidate grants permission to a tenant (client).
     * @param tenantId ID of the tenant requesting access
     * @param identityId ID of the candidate granting access
     * @param scopes space-separated string of scopes granted
     * @return the saved Grant object
     */
    public Grant issueGrant(Long tenantId, Long identityId, String scopes) {
        Optional<Tenant> tenantOpt = iamRepository.findTenantByName(findTenantNameById(tenantId));
        Optional<Identity> identityOpt = iamRepository.findIdentityByUsername(findIdentityUsernameById(identityId));

        if (tenantOpt.isEmpty() || identityOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid tenant or identity");
        }

        Tenant tenant = tenantOpt.get();
        Identity identity = identityOpt.get();

        Grant grant = new Grant();
        grant.setTenant(tenant);
        grant.setIdentity(identity);
        grant.setApprovedScopes(scopes);
        grant.setIssuanceDateTime(LocalDateTime.now());

        return iamRepository.saveGrant(grant);
    }

    /**
     * Candidate revokes previously granted permission
     */
    public void revokeGrant(Long tenantId, Long identityId) {
        Optional<Grant> grantOpt = iamRepository.findGrant(tenantId, identityId);
        grantOpt.ifPresent(grant -> iamRepository.deleteGrant(grant));
    }
    /**
     * Checks if a grant exists and optionally verifies scopes
     */
    public boolean checkGrant(Long tenantId, Long identityId, String requiredScopes) {
        Optional<Grant> grantOpt = iamRepository.findGrant(tenantId, identityId);
        if (grantOpt.isEmpty()) return false;

        Grant grant = grantOpt.get();
        if (requiredScopes == null || requiredScopes.isEmpty()) return true;

        // Check that all required scopes are included in approvedScopes
        String[] required = requiredScopes.split(" ");
        String[] approved = grant.getApprovedScopes().split(" ");
        for (String r : required) {
            boolean found = false;
            for (String a : approved) {
                if (r.equals(a)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    // ===== Helper methods to find names by ID =====
    private String findTenantNameById(Long tenantId) {
        // For simplicity, directly query repository
        Optional<Tenant> t = iamRepository.findTenantByName("tenant-" + tenantId);
        return t.map(Tenant::getName).orElseThrow();
    }

    private String findIdentityUsernameById(Long identityId) {
        Optional<Identity> i = iamRepository.findIdentityByUsername("user-" + identityId);
        return i.map(Identity::getUsername).orElseThrow();
    }
}
