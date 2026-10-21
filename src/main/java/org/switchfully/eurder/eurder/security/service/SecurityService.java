package org.switchfully.eurder.eurder.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.switchfully.eurder.eurder.customer.domain.Customer;
import org.switchfully.eurder.eurder.customer.repository.CustomerRepository;
import org.switchfully.eurder.eurder.security.domain.AuthenticatedUser;
import org.switchfully.eurder.eurder.security.domain.Feature;
import org.switchfully.eurder.eurder.security.domain.Role;
import org.switchfully.eurder.eurder.security.exception.ForbiddenException;
import org.switchfully.eurder.eurder.security.exception.UnauthorizedException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service
public class SecurityService {
    private static final Logger log = LoggerFactory.getLogger(SecurityService.class);

    private static final String BASIC_PREFIX = "Basic ";
    private static final String INVALID_CREDENTIALS = "Invalid email or password";
    
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPasswordHash;

    public SecurityService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            @Value("${eurder.security.admin.email}") String adminEmail,
            @Value("${eurder.security.admin.password}") String adminPasswordHash) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPasswordHash = passwordEncoder.encode(adminPasswordHash);
    }

    //----------Authenticate---------
    public AuthenticatedUser validateAuthorization(String authorization, Feature feature) {
        AuthenticatedUser authenticatedUser = authenticate(authorization);

        if (!authenticatedUser.role().hasFeature(feature)) {
            throw new ForbiddenException("User " + authenticatedUser.email() + " does not have access to " + feature);
        }

        return authenticatedUser;
    }

    private AuthenticatedUser authenticate(String authorization) {

        // Get credentials
        Credentials credentials = decode(authorization);

        // Check for admin
        if (adminEmail.equalsIgnoreCase(credentials.email())) {
            checkPassword(credentials.password(), adminPasswordHash);
            return new AuthenticatedUser(adminEmail, Role.ADMIN);
        }

        // Check if password hash matches
        String passwordHash = customerRepository.findByEmail(credentials.email())
                .map(Customer::getPasswordHash)
                // same message as a wrong password: don't reveal which emails exist
                .orElseThrow(() -> new UnauthorizedException(INVALID_CREDENTIALS));
        checkPassword(credentials.password(), passwordHash);

        // Authorize user
        return new AuthenticatedUser(credentials.email(), Role.CUSTOMER);
    }

    private void checkPassword(String rawPassword, String passwordHash) {
        if (!passwordEncoder.matches(rawPassword, passwordHash)) {
            throw new UnauthorizedException(INVALID_CREDENTIALS);
        }
    }

    //----------Decoding---------
    private Credentials decode(String authorization) {
        if (authorization == null || !authorization.startsWith(BASIC_PREFIX))
            throw new UnauthorizedException("Missing or invalid authorization header");

        // Decode to Base64
        String decoded = decodeBase64(authorization.substring(BASIC_PREFIX.length()).trim())
                .orElseThrow(() -> new UnauthorizedException("Authorization header is not valid Base64"));

        // First colon: passwords may contain ':'
        int separator = decoded.indexOf(':');
        if (separator <= 0) throw new UnauthorizedException("Authorization header must contain email:password");

        // Return email and password as record
        return new Credentials(decoded.substring(0, separator), decoded.substring(separator + 1));
    }

    private Optional<String> decodeBase64(String value) {
        try {
            return Optional.of(new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private record Credentials(String email, String password) {
    }

}
