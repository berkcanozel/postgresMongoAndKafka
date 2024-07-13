package com.example.kafkaexample.service.userservice;

import com.example.kafkaexample.service.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto saveUser(UserDto user);
}
