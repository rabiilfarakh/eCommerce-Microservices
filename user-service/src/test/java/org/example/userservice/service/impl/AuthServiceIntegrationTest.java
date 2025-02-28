package org.example.userservice.service.impl;

import org.example.userservice.DTO.UserLoginRequest;
import org.example.userservice.DTO.UserRegistrationRequest;
import org.example.userservice.DTO.UserResponse;
import org.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private UserRegistrationRequest registrationRequest;
    private UserLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFirstName("John");
        registrationRequest.setLastName("Doe");
        registrationRequest.setEmail("john.doe@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setRole("USER");

        loginRequest = new UserLoginRequest();
        loginRequest.setEmail("lili@gmail.com");
        loginRequest.setPassword("123456");

        userRepository.deleteAll();
    }

    @Test
    void testRegisterUser() {
        System.out.println("Registering user: " + registrationRequest);
        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/auth/register",
                registrationRequest,
                UserResponse.class
        );

        System.out.println("Registration Response: " + response.getStatusCode() + " - " + response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals("john.doe@example.com", response.getBody().getEmail());
    }

    @Test
    void testLoginUser() {
//        ResponseEntity<UserResponse> registerResponse = restTemplate.postForEntity(
//                "http://localhost:" + port + "/auth/register",
//                registrationRequest,
//                UserResponse.class
//        );
//        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/auth/login",
                loginRequest,
                String.class
        );

        System.out.println("Login Response: " + loginResponse.getBody());
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertTrue(loginResponse.getBody().contains("access_token"));
        assertTrue(loginResponse.getBody().contains("token_type"));
        assertTrue(loginResponse.getBody().contains("expires_in"));
    }

    @Test
    void testLoginUser_InvalidCredentials() {
        // Register a user
        ResponseEntity<UserResponse> registerResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/auth/register",
                registrationRequest,
                UserResponse.class
        );
        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());

        // Attempt login with invalid credentials
        UserLoginRequest invalidLoginRequest = new UserLoginRequest();
        invalidLoginRequest.setEmail("john.doe@example.com"); // Use the same email as registration
        invalidLoginRequest.setPassword("wrongpassword"); // Use an incorrect password

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/auth/login",
                invalidLoginRequest,
                String.class
        );

        System.out.println("Invalid Login Response: " + loginResponse.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, loginResponse.getStatusCode());
    }
}