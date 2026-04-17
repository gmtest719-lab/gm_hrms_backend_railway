package com.gm.hrms.email.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Generates cryptographically-random one-time passwords for salary-slip
 * download approval emails.
 *
 * The generated password is intentionally short (8 chars) and memorable
 * while still being unpredictable.
 *
 * Character set deliberately excludes visually ambiguous characters
 * (0 / O, 1 / l / I) to avoid copy-paste errors in the email.
 */
@Component
public class DownloadPasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghjkmnpqrstuvwxyz";
    private static final String DIGITS    = "23456789";
    private static final String SPECIAL   = "@#$!";

    /** Combined pool used for the bulk of the password. */
    private static final String ALL =
            UPPERCASE + LOWERCASE + DIGITS + SPECIAL;

    private static final int LENGTH = 10;

    private final SecureRandom random = new SecureRandom();

    /**
     * Returns a random password that is guaranteed to contain at least one
     * character from each category.
     */
    public String generate() {
        char[] password = new char[LENGTH];

        // Guarantee at least one character from each required group
        password[0] = pick(UPPERCASE);
        password[1] = pick(LOWERCASE);
        password[2] = pick(DIGITS);
        password[3] = pick(SPECIAL);

        // Fill the rest from the combined pool
        for (int i = 4; i < LENGTH; i++) {
            password[i] = pick(ALL);
        }

        // Shuffle so the guaranteed positions are not predictable
        shuffle(password);
        return new String(password);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private char pick(String pool) {
        return pool.charAt(random.nextInt(pool.length()));
    }

    private void shuffle(char[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = arr[i];
            arr[i]   = arr[j];
            arr[j]   = tmp;
        }
    }
}