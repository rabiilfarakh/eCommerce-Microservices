package org.example.userservice.mapper;


import org.example.userservice.DTO.UserResponse;
import org.example.userservice.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
}
