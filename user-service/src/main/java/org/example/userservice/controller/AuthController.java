package org.example.userservice.controller;

import jakarta.ws.rs.WebApplicationException;
import lombok.RequiredArgsConstructor;

import org.example.userservice.DTO.UserLoginResponse;
import org.example.userservice.exceptions.InvalidCredentialsException;
import org.example.userservice.exceptions.KeycloakAuthenticationException;
import org.example.userservice.exceptions.UserNotFoundException;
import org.example.userservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.userservice.DTO.UserResponse;
import org.example.userservice.DTO.UserRegistrationRequest;
import org.example.userservice.DTO.UserLoginRequest;

import java.util.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserRegistrationRequest request) {
        Map<String, String> response = new HashMap<>();

        try {
            String keycloakUserId = authService.registerUserInKeycloak(request);

            UserResponse userResponse = authService.registerUser(request);

            response.put("message", "User registered successfully");
            response.put("userId", keycloakUserId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 409) {
                response.put("error", "Email already exists");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            response.put("error", "Error creating user in Keycloak");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            response.put("error", "An unexpected error occurred");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest request) {
        try {
            UserLoginResponse response = authService.loginUser(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException | InvalidCredentialsException | KeycloakAuthenticationException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "An unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
