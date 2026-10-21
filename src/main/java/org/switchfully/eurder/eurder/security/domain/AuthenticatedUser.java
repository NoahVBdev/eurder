package org.switchfully.eurder.eurder.security.domain;


public record AuthenticatedUser(String email, Role role) {
}
