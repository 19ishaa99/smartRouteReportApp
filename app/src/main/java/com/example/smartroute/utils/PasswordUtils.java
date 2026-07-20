package com.example.smartroute.utils;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtils {

    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private PasswordUtils() {
        // Prevent creating an instance of this utility class.
    }

    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    public static String hashPassword(String password, String saltText) {
        byte[] salt = Base64.decode(saltText, Base64.NO_WRAP);

        PBEKeySpec specification = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] passwordHash =
                    factory.generateSecret(specification).getEncoded();

            return Base64.encodeToString(passwordHash, Base64.NO_WRAP);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new IllegalStateException(
                    "Unable to protect the password.",
                    exception
            );

        } finally {
            specification.clearPassword();
        }
    }

    public static boolean verifyPassword(
            String enteredPassword,
            String savedSalt,
            String savedHash
    ) {
        String enteredPasswordHash =
                hashPassword(enteredPassword, savedSalt);

        return constantTimeEquals(enteredPasswordHash, savedHash);
    }

    private static boolean constantTimeEquals(String first, String second) {

        if (first == null || second == null) {
            return false;
        }

        byte[] firstBytes = first.getBytes();
        byte[] secondBytes = second.getBytes();

        if (firstBytes.length != secondBytes.length) {
            return false;
        }

        int result = 0;

        for (int index = 0; index < firstBytes.length; index++) {
            result |= firstBytes[index] ^ secondBytes[index];
        }

        return result == 0;
    }
}