package org.eclipse.jakarta.IAM.controllers;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.jakarta.IAM.entities.Identity;
import org.eclipse.jakarta.IAM.entities.Tenant;
import org.eclipse.jakarta.IAM.security.Argon2Utility;

import java.util.logging.Logger;

/**
 * Initializes test data for the IAM system on application startup.
 * Creates a test user and tenant for development purposes.
 */
@Singleton
@Startup
public class DataInitializer {

    private static final Logger LOGGER = Logger.getLogger(DataInitializer.class.getName());

    @Inject
    private IAMRepository iamRepository;

    @PostConstruct
    @Transactional
    public void init() {
        LOGGER.info("=== Initializing IAM Test Data ===");

        // Create test user if not exists
        if (iamRepository.findIdentityByUsername("admin@recruiting.com").isEmpty()) {
            Identity admin = new Identity();
            admin.setUsername("admin@recruiting.com");
            admin.setPassword(Argon2Utility.hash("admin123".toCharArray()));
            admin.setProvidedScopes("profile.read cv.read cv.share jobs.read jobs.write");
            iamRepository.save(admin);
            LOGGER.info("Created test user: admin@recruiting.com / admin123");
        }

        // Create test tenant (OAuth client) if not exists
        if (iamRepository.findTenantByClientId("recruiting-frontend").isEmpty()) {
            Tenant tenant = new Tenant();
            tenant.setClientId("recruiting-frontend");
            tenant.setClientSecret("secret123");
            tenant.setName("Recruiting Frontend App");
            tenant.setRedirectUri("http://localhost:3000/callback");
            tenant.setRequiredScopes("profile.read");
            iamRepository.save(tenant);
            LOGGER.info("Created test tenant: recruiting-frontend");
        }

        // Create a second test tenant for API testing
        if (iamRepository.findTenantByClientId("test-client").isEmpty()) {
            Tenant testTenant = new Tenant();
            testTenant.setClientId("test-client");
            testTenant.setClientSecret("test-secret");
            testTenant.setName("Test Client");
            testTenant.setRedirectUri("http://localhost:8080/callback");
            testTenant.setRequiredScopes("profile.read cv.read");
            iamRepository.save(testTenant);
            LOGGER.info("Created test tenant: test-client");
        }

        LOGGER.info("=== IAM Test Data Initialization Complete ===");
    }
}
