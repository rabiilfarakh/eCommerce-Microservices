package org.example.userservice.DTO;

import lombok.Data;

@Data
public class UserLoginResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
}
