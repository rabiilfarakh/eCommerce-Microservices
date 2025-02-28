package org.example.userservice.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.example.userservice.DTO.UserRegistrationRequest;
import org.example.userservice.DTO.UserLoginRequest;
import org.example.userservice.DTO.UserLoginResponse;
import org.example.userservice.DTO.UserResponse;
import org.example.userservice.entities.User;
import org.example.userservice.exceptions.InvalidCredentialsException;
import org.example.userservice.exceptions.UserNotFoundException;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.*;

import java.util.Optional;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

class AuthServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthServiceImp authService;

    private User user;
    private UserRegistrationRequest registrationRequest;
    private UserLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFirstName("John");
        registrationRequest.setLastName("Doe");
        registrationRequest.setAddress("123 Street");
        registrationRequest.setCell("1234567890");
        registrationRequest.setEmail("john.doe@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setRole("USER");

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("encodedPassword");
        user.setRole("USER");

        loginRequest = new UserLoginRequest();
        loginRequest.setEmail("lili@gmail.com");
        loginRequest.setPassword("123456");
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");
        userResponse.setEmail("john.doe@example.com");
        userResponse.setRole("USER");

        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        UserResponse response = authService.registerUser(registrationRequest);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());
    }

    @Test
    void testRegisterUser_AlreadyExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.registerUser(registrationRequest));
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void testLoginUser_Success() throws JSONException {
        lenient().when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("access_token", "fake_token");
        jsonResponse.put("token_type", "Bearer");
        jsonResponse.put("expires_in", 300);

        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse.toString(), headers, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(responseEntity);

        System.out.println("Response from Keycloak: " + responseEntity.getBody());
        UserLoginResponse response = authService.loginUser(loginRequest);

        assertNotNull(response);
        assertTrue(response.getAccessToken().length() > 10);
        assertEquals("Bearer", response.getTokenType());
        assertEquals(300, response.getExpiresIn());
    }

    @Test
    void testLoginUser_InvalidPassword() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(loginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void testLoginUser_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authService.loginUser(loginRequest));
        assertEquals("User not found", exception.getMessage());
    }
}