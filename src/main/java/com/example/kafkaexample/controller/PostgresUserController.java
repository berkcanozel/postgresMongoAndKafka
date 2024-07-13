package com.example.kafkaexample.controller;

import com.example.kafkaexample.data.postgres.entity.PostgresUser;
import com.example.kafkaexample.service.userservice.MongoUserService;
import com.example.kafkaexample.service.dto.UserDto;
import com.example.kafkaexample.service.userservice.PostgresUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/postgres-users")
public class PostgresUserController {

    @Autowired
    private PostgresUserService postgresUserService;

    @GetMapping(value = "/getAllUsers")
    public List<UserDto> getAllUsers() {
        return postgresUserService.getAllUsers();
    }

    @PostMapping(value = "/createUser")
    public UserDto createUser(@RequestBody UserDto user) {
        return postgresUserService.saveUser(user);
    }
}
