package org.switchfully.eurder.eurder.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Helper that builds a request header
 */
public class TestAuth {
        public static final String ADMIN = basic("admin@eurder.com", "admin-dev-password");

        private TestAuth() {}

        public static String basic(String email, String password) {
            return "Basic " + Base64.getEncoder().encodeToString((email + ":" + password).getBytes(StandardCharsets.UTF_8));
        }
}
