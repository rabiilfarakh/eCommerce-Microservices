package org.example.userservice.service;


import org.example.userservice.DTO.UserLoginRequest;
import org.example.userservice.DTO.UserLoginResponse;
import org.example.userservice.DTO.UserRegistrationRequest;
import org.example.userservice.DTO.UserResponse;

public interface AuthService {


    UserResponse registerUser(UserRegistrationRequest request);

    String registerUserInKeycloak(UserRegistrationRequest request);

    UserLoginResponse loginUser(UserLoginRequest request);
}
