package com.example.kafkaexample.service.userservice;

import com.example.kafkaexample.data.mongo.entity.MongoUser;
import com.example.kafkaexample.data.mongo.repository.MongoUserRepository;
import com.example.kafkaexample.data.postgres.repository.PostgresUserRepository;
import com.example.kafkaexample.service.dto.UserDto;
import com.example.kafkaexample.service.userservice.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MongoUserService implements UserService{

    @Autowired
    private MongoUserRepository mongoUserRepository;

    @Autowired
    private PostgresUserRepository postgresUserRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        List<MongoUser> mongoUsers = mongoUserRepository.findAll();
        return mongoUsers.stream().map(userMapper::mongoToUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto user) {
        MongoUser savedMongoUser = new MongoUser();
        savedMongoUser.setEmail(user.getEmail());
        savedMongoUser.setName(user.getName());

        mongoUserRepository.save(savedMongoUser);

        return userMapper.mongoToUserDto(savedMongoUser);
    }
}
