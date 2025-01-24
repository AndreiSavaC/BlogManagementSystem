package com.example.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HashUtil {

    private static final Logger LOGGER = Logger.getLogger(HashUtil.class.getName());

    public static String sha256Hex(String original) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(original.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "SHA-256 algorithm not found", e);
            throw new RuntimeException("Error hashing password", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while hashing password", e);
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
