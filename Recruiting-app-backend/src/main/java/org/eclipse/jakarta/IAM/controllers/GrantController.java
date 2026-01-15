package org.eclipse.jakarta.IAM.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.jakarta.IAM.entities.Grant;
import org.eclipse.jakarta.IAM.entities.Identity;
import org.eclipse.jakarta.IAM.entities.Tenant;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Controller responsible for managing OAuth2-style grants between tenants and
 * identities.
 * 
 * A Grant represents permission given by a user (Identity) to a client
 * application (Tenant)
 * to access specific resources on their behalf. This follows the OAuth2
 * authorization model
 * where users can grant, revoke, and verify permissions.
 * 
 * This controller handles:
 * - Issuing new grants with specific scopes
 * - Revoking existing grants
 * - Checking if a grant exists with required scopes
 */
@ApplicationScoped
@Transactional
public class GrantController {

    // Repository for database operations related to IAM entities
    @Inject
    private IAMRepository iamRepository;

    /**
     * Issues a new grant from a candidate (identity) to a tenant (client application).
     * 
     * This method creates a permission record that allows the tenant to access
     * the candidate's resources within the specified scopes.
     * 
     * @param tenantId   ID of the tenant (client application) requesting access
     * @param identityId ID of the candidate (user) granting access
     * @param scopes     Space-separated string of OAuth2 scopes being granted
     *                   (e.g., "profile email read:jobs")
     * @return the persisted Grant object containing the authorization details
     * @throws IllegalArgumentException if either the tenant or identity is not found
     */
    public Grant issueGrant(Short tenantId, Long identityId, String scopes) {
        // Retrieve the tenant and identity from the database
        Optional<Tenant> tenantOpt = iamRepository.findTenantById(tenantId);
        Optional<Identity> identityOpt = iamRepository.findIdentityById(identityId);

        // Validate that both entities exist before proceeding
        if (tenantOpt.isEmpty() || identityOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid tenant or identity");
        }

        Tenant tenant = tenantOpt.get();
        Identity identity = identityOpt.get();

        // Create and populate the new grant
        Grant grant = new Grant();
        grant.setTenant(tenant);
        grant.setIdentity(identity);
        grant.setApprovedScopes(scopes);
        grant.setIssuanceDateTime(LocalDateTime.now()); // Record when the grant was issued

        // Persist and return the grant
        return iamRepository.save(grant);
    }

    /**
     * Revokes a previously issued grant, removing the tenant's access to the identity's resources.
     * 
     * This allows users to withdraw consent they previously gave to a client application.
     * If no matching grant exists, this method silently completes without error.
     * 
     * @param tenantId   ID of the tenant whose access is being revoked
     * @param identityId ID of the identity (user) revoking the access
     */
    public void revokeGrant(Short tenantId, Long identityId) {
        // Look up the existing grant by tenant and identity IDs
        Optional<Grant> grantOpt = iamRepository.findGrant(tenantId, identityId);
        // If found, delete it from the database
        grantOpt.ifPresent(grant -> iamRepository.deleteGrant(grant));
    }

    /**
     * Checks if a valid grant exists between a tenant and identity, optionally verifying
     * that specific scopes are included in the grant.
     * 
     * This method is used during authorization to verify that a client application
     * has been granted the necessary permissions by the user.
     * 
     * @param tenantId       ID of the tenant (client application) to check
     * @param identityId     ID of the identity (user) whose grant is being verified
     * @param requiredScopes Space-separated string of scopes that must be present in the grant.
     *                       If null or empty, only checks for grant existence.
     * @return true if the grant exists and includes all required scopes, false otherwise
     */
    public boolean checkGrant(Short tenantId, Long identityId, String requiredScopes) {
        // Attempt to find an existing grant between the tenant and identity
        Optional<Grant> grantOpt = iamRepository.findGrant(tenantId, identityId);
        
        // If no grant exists, access is denied
        if (grantOpt.isEmpty())
            return false;

        Grant grant = grantOpt.get();
        
        // If no specific scopes are required, the existence of the grant is sufficient
        if (requiredScopes == null || requiredScopes.isEmpty())
            return true;

        // Verify that all required scopes are included in the approved scopes
        // Scopes are space-separated (per OAuth2 spec)
        String[] required = requiredScopes.split(" ");
        String[] approved = grant.getApprovedScopes().split(" ");
        
        // Check each required scope against the approved scopes
        for (String r : required) {
            boolean found = false;
            for (String a : approved) {
                if (r.equals(a)) {
                    found = true;
                    break;
                }
            }
            // If any required scope is missing, return false
            if (!found)
                return false;
        }
        // All required scopes are present
        return true;
    }
}
