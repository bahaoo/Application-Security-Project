package org.eclipse.jakarta.IAM.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "identities")
public class Identity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 191, unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    /**
     * Space-separated OAuth scopes the candidate is allowed to grant
     * Example: "profile.read cv.read cv.share"
     */
    @Column(name = "provided_scopes", nullable = false)
    private String providedScopes;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Must be Argon2-hashed before persisting
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String getProvidedScopes() {
        return providedScopes;
    }

    public void setProvidedScopes(String providedScopes) {
        this.providedScopes = providedScopes;
    }

    @Override
    public String toString() {
        return "Identity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", providedScopes='" + providedScopes + '\'' +
                '}';
    }
}
