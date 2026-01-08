package org.eclipse.jakarta.IAM.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@ApplicationScoped
public class JwtManager {

    private final Map<String, Long> keyPairExpirationTimes = new HashMap<>();
    private final Set<OctetKeyPair> cachedKeyPairs = new HashSet<>();
    private final Long keyPairLifetimeDuration = 86400L; // 1 day
    private final Short keyPairCacheSize = 5;
    private final Integer jwtLifetimeDuration = 3600; // 1 hour
    private final String issuer = "my-iam-server";
    private final List<String> audiences = List.of("my-client-app");
    private final String claimRoles = "roles";
    private final OctetKeyPairGenerator keyPairGenerator = new OctetKeyPairGenerator(Curve.Ed25519);

    @PostConstruct
    public void start() {
        while (cachedKeyPairs.size() < keyPairCacheSize) {
            cachedKeyPairs.add(generateKeyPair());
        }
    }

    public String generateAccessToken(String tenantId, String subject, String approvedScopes, String[] roles) {
        try {
            OctetKeyPair octetKeyPair = getKeyPair().orElseThrow(() -> new RuntimeException("No valid KeyPair"));
            JWSSigner signer = new Ed25519Signer(octetKeyPair);
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.EdDSA)
                    .keyID(octetKeyPair.getKeyID())
                    .type(JOSEObjectType.JWT)
                    .build();
            Instant now = Instant.now();
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(issuer)
                    .audience(audiences)
                    .subject(subject)
                    .claim("upn", subject)
                    .claim("tenant_id", tenantId)
                    .claim("scope", approvedScopes)
                    .claim(claimRoles, roles != null ? roles : new String[]{"USER"})
                    .jwtID(UUID.randomUUID().toString())
                    .issueTime(Date.from(now))
                    .notBeforeTime(Date.from(now))
                    .expirationTime(Date.from(now.plus(jwtLifetimeDuration, ChronoUnit.SECONDS)))
                    .build();
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateRefreshToken(String clientId, String subject, String approvedScope) throws Exception {
        OctetKeyPair octetKeyPair = getKeyPair().orElseThrow(() -> new RuntimeException("No valid KeyPair"));
        JWSSigner signer = new Ed25519Signer(octetKeyPair);
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.EdDSA)
                .keyID(octetKeyPair.getKeyID())
                .type(JOSEObjectType.JWT)
                .build();
        Instant now = Instant.now();
        JWTClaimsSet refreshTokenClaims = new JWTClaimsSet.Builder()
                .subject(subject)
                .claim("tenant_id", clientId)
                .claim("scope", approvedScope)
                .expirationTime(Date.from(now.plus(3, ChronoUnit.HOURS)))
                .build();
        SignedJWT signedRefreshToken = new SignedJWT(header, refreshTokenClaims);
        signedRefreshToken.sign(signer);
        return signedRefreshToken.serialize();
    }

    public Optional<JWT> validateJWT(String token) {
        try {
            SignedJWT parsed = SignedJWT.parse(token);
            OctetKeyPair publicKey = cachedKeyPairs.stream()
                    .filter(kp -> kp.getKeyID().equals(parsed.getHeader().getKeyID()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("KeyPair not found"))
                    .toPublicJWK();
            JWSVerifier verifier = new Ed25519Verifier(publicKey);
            if (parsed.verify(verifier)) {
                if (parsed.getJWTClaimsSet().getExpirationTime().toInstant().isBefore(Instant.now())) {
                    return Optional.empty();
                }
                return Optional.of(JWTParser.parse(token));
            }
            return Optional.empty();
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public OctetKeyPair getPublicValidationKey(String kid) {
        return cachedKeyPairs.stream()
                .filter(kp -> kp.getKeyID().equals(kid))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("KeyPair not found"))
                .toPublicJWK();
    }

    private OctetKeyPair generateKeyPair() {
        try {
            long now = LocalDateTime.now(ZoneId.of("UTC")).toEpochSecond(ZoneOffset.UTC);
            String kid = UUID.randomUUID().toString();
            keyPairExpirationTimes.put(kid, now + keyPairLifetimeDuration);
            return keyPairGenerator.keyUse(KeyUse.SIGNATURE)
                    .keyID(kid).generate();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasNotExpired(OctetKeyPair keyPair) {
        long now = LocalDateTime.now(ZoneId.of("UTC")).toEpochSecond(ZoneOffset.UTC);
        return now <= keyPairExpirationTimes.get(keyPair.getKeyID());
    }

    private boolean isPublicKeyExpired(OctetKeyPair keyPair) {
        long now = LocalDateTime.now(ZoneId.of("UTC")).toEpochSecond(ZoneOffset.UTC);
        return now > (keyPairExpirationTimes.get(keyPair.getKeyID()) + jwtLifetimeDuration);
    }

    private Optional<OctetKeyPair> getKeyPair() {
        cachedKeyPairs.removeIf(this::isPublicKeyExpired);
        while (cachedKeyPairs.stream().filter(this::hasNotExpired).count() < keyPairCacheSize) {
            cachedKeyPairs.add(generateKeyPair());
        }
        return cachedKeyPairs.stream().filter(this::hasNotExpired).findAny();
    }

    public String getClaimRoles() {
        return claimRoles;
    }
}
