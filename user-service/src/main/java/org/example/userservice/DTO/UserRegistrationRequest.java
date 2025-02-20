package org.example.userservice.DTO;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String firstName;
    private String lastName;
    private String address;
    private String cell;
    private String email;
    private String password;
    private String role;
}