package org.example.userservice.exceptions;

public class KeycloakAuthenticationException extends RuntimeException {
    public KeycloakAuthenticationException(String message) {
        super(message);
    }
}
