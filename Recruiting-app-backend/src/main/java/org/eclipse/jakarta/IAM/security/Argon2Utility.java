package org.eclipse.jakarta.IAM.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.security.enterprise.identitystore.PasswordHash;

public class Argon2Utility implements PasswordHash {

    // ===== Hardcoded Argon2 parameters =====
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private static final int ITERATIONS = 3;
    private static final int MEMORY = 65536; // in KB
    private static final int THREADS = 1;

    private static final Argon2 argon2 = Argon2Factory.create(
            Argon2Factory.Argon2Types.ARGON2id, SALT_LENGTH, HASH_LENGTH);

    // ===== Hash a password =====
    public static String hash(char[] clientPassword) {
        try {
            return argon2.hash(ITERATIONS, MEMORY, THREADS, clientPassword);
        } finally {
            argon2.wipeArray(clientPassword);
        }
    }

    // ===== Check a password against a hash =====
    public static boolean check(String serverHash, char[] clientPassword) {
        try {
            return argon2.verify(serverHash, clientPassword);
        } finally {
            argon2.wipeArray(clientPassword);
        }
    }

    // ===== Implement PasswordHash interface =====
    @Override
    public String generate(char[] password) {
        return hash(password);
    }

    @Override
    public boolean verify(char[] password, String hashedPassword) {
        return check(hashedPassword, password);
    }
}
