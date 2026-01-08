package org.eclipse.jakarta.IAM.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    /**
     * OAuth client_id
     * Public identifier of the client application
     */
    @Column(name = "client_id", nullable = false, unique = true, length = 191)
    private String clientId;

    /**
     * OAuth client_secret
     * Must be securely stored (hashed in real systems)
     */
    @Column(name = "client_secret", nullable = false)
    private String clientSecret;

    /**
     * Redirect URI registered for this client
     */
    @Column(name = "redirect_uri", nullable = false)
    private String redirectUri;

    /**
     * Scopes this client requires to function
     * Example: "profile.read cv.read"
     */
    @Column(name = "required_scopes", nullable = false)
    private String requiredScopes;

    /**
     * Human-readable name (e.g. "LinkedIn Recruiter App")
     */
    @Column(nullable = false)
    private String name;

    // ===== Getters & Setters =====

    public Short getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Should be hashed before persisting
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getRequiredScopes() {
        return requiredScopes;
    }

    public void setRequiredScopes(String requiredScopes) {
        this.requiredScopes = requiredScopes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
