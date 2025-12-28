package com.recruiting.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Hash a password using BCrypt
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    // Check that a plain password matches a previously hashed one
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
