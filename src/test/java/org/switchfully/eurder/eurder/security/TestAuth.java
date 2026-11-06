package org.switchfully.eurder.eurder.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Helper that builds a request header
 */
@TestComponent
public class TestAuth {

    private final String email;
    private final String password;

    public TestAuth(
            @Value("${eurder.security.admin.email}") String email,
            @Value("${eurder.security.admin.password}") String password) {
        this.email = email;
        this.password = password;
    }

    public static String basic(String email, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((email + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    public String admin() {
        return basic(email, password);
    }
}
