package org.eclipse.jakarta.IAM.entities;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "issued_grants")
public class Grant {

    @EmbeddedId
    private GrantPK id;

    @ManyToOne(optional = false)
    @MapsId("tenantId")
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne(optional = false)
    @MapsId("identityId")
    @JoinColumn(name = "identity_id")
    private Identity identity;

    @Column(name = "approved_scopes", nullable = false, length = 512)
    private String approvedScopes;

    @Column(name = "issuance_date_time", nullable = false)
    private LocalDateTime issuanceDateTime;

    public Grant() {}

    /* ===== Getters / Setters ===== */

    public GrantPK getId() {
        return id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
        syncId();
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
        syncId();
    }

    public String getApprovedScopes() {
        return approvedScopes;
    }

    public void setApprovedScopes(String approvedScopes) {
        this.approvedScopes = approvedScopes;
    }

    public LocalDateTime getIssuanceDateTime() {
        return issuanceDateTime;
    }

    public void setIssuanceDateTime(LocalDateTime issuanceDateTime) {
        this.issuanceDateTime = issuanceDateTime;
    }

    /* ===== Internal helper ===== */

    private void syncId() {
        if (tenant != null && identity != null) {
            this.id = new GrantPK(tenant.getId(), identity.getId());
        }
    }
}
