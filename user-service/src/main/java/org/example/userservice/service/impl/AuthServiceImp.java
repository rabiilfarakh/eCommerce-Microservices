package org.example.userservice.service.impl;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.example.userservice.DTO.UserRegistrationRequest;
import org.example.userservice.DTO.UserLoginRequest;
import org.example.userservice.DTO.UserLoginResponse;
import org.example.userservice.DTO.UserResponse;
import org.example.userservice.entities.User;
import org.example.userservice.exceptions.InvalidCredentialsException;
import org.example.userservice.exceptions.KeycloakAuthenticationException;
import org.example.userservice.exceptions.UserNotFoundException;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.service.AuthService;
import org.glassfish.jaxb.runtime.v2.runtime.output.SAXOutput;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;





import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final Logger LOGGER = Logger.getLogger(AuthServiceImp.class.getName());


    private static final String KEYCLOAK_URL = "http://localhost:8080/realms/eCommerce/protocol/openid-connect/token";
    private static final String CLIENT_ID = "eCommerce";
    private static final String CLIENT_SECRET = "eE58f3GkSva66cdXuYchtEwLNKzrqawY";
    private static final String GRANT_TYPE = "password";

    @Override
    public UserResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAddress(request.getAddress());
        user.setCell(request.getCell());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public String registerUserInKeycloak(UserRegistrationRequest request) {
        Keycloak keycloak = Keycloak.getInstance(
                "http://localhost:8080/",
                "master",
                "admin",
                "admin",
                "admin-cli"
        );

        RealmResource realmResource = keycloak.realm("eCommerce");
        UsersResource usersResource = realmResource.users();

        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(request.getEmail());
        userRep.setFirstName(request.getFirstName());
        userRep.setLastName(request.getLastName());
        userRep.setEmail(request.getEmail());
        userRep.setEnabled(true);
        userRep.setEmailVerified(true);

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(request.getPassword());
        passwordCred.setTemporary(false);

        userRep.setCredentials(Collections.singletonList(passwordCred));

        if (userRep.getAttributes() == null) {
            userRep.setAttributes(new HashMap<>());
        }
        userRep.getAttributes().put("cell", Collections.singletonList(request.getCell()));
        userRep.getAttributes().put("address", Collections.singletonList(request.getAddress()));

        Response response = usersResource.create(userRep);
        int status = response.getStatus();
        System.out.println("Response Status: " + status);

        if (status == 201) {
            String keycloakUserId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            assignRoleToUser(keycloak, request.getEmail(), request.getRole());

            return keycloakUserId;
        } else if (status == 409) {
            throw new WebApplicationException("User with this email already exists", Response.Status.CONFLICT);
        } else {
            throw new RuntimeException("Failed to create user in Keycloak");
        }
    }

    private void assignRoleToUser(Keycloak keycloak, String username, String roleName) {
        RealmResource realmResource = keycloak.realm("eCommerce");
        UsersResource usersResource = realmResource.users();

        List<UserRepresentation> users = usersResource.search(username, true);
        if (users == null || users.isEmpty()) {
            System.out.println("User not found: " + username);
            return;
        }

        UserRepresentation user = users.get(0);
        String userId = user.getId();

        RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
        if (role == null) {
            System.out.println("Role not found: " + roleName);
            return;
        }

        realmResource.users().get(userId).roles().realmLevel().add(Collections.singletonList(role));
        System.out.println("Role assigned successfully!");
    }




    @Override
    public UserLoginResponse loginUser(UserLoginRequest request) {
        try {
            LOGGER.info("Login attempt for email: " + request.getEmail());

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new InvalidCredentialsException("Invalid credentials");
            }

            LOGGER.info("User authentication successful for: " + request.getEmail());

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("client_id", CLIENT_ID);
            requestBody.add("client_secret", CLIENT_SECRET);
            requestBody.add("username", request.getEmail());
            requestBody.add("password", request.getPassword());
            requestBody.add("grant_type", GRANT_TYPE);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);


            LOGGER.info("Sending request to Keycloak for token...");
            ResponseEntity<String> response = restTemplate.exchange(KEYCLOAK_URL, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                LOGGER.severe("Keycloak authentication failed!");
                throw new KeycloakAuthenticationException("Failed to authenticate with Keycloak");
            }

            LOGGER.info("Successfully authenticated with Keycloak");

            JSONObject jsonResponse = new JSONObject(response.getBody());
            UserLoginResponse loginResponse = new UserLoginResponse();
            loginResponse.setAccessToken(jsonResponse.getString("access_token"));
            loginResponse.setTokenType(jsonResponse.getString("token_type"));
            loginResponse.setExpiresIn(jsonResponse.getLong("expires_in"));

            return loginResponse;

        } catch (UserNotFoundException | InvalidCredentialsException | KeycloakAuthenticationException e) {
            LOGGER.warning("Authentication error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error during login: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred during login", e);
        }
    }



}
