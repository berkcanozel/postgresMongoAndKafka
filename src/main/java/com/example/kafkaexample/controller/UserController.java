package com.example.kafkaexample.controller;

import com.example.kafkaexample.data.mongo.entity.MongoUser;
import com.example.kafkaexample.service.userservice.MongoUserService;
import com.example.kafkaexample.service.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private MongoUserService mongoUserService;

    @GetMapping("/getAllUsers")
    public List<UserDto> getAllUsers() {
        return mongoUserService.getAllUsers();
    }

    @PostMapping(value = "/saveUser")
    public UserDto createUser(@RequestBody UserDto userdto) {
        return mongoUserService.saveUser(userdto);
    }
}
