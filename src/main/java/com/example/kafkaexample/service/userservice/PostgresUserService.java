package com.example.kafkaexample.service.userservice;

import com.example.kafkaexample.data.postgres.entity.PostgresUser;
import com.example.kafkaexample.data.postgres.repository.PostgresUserRepository;
import com.example.kafkaexample.service.dto.UserDto;
import com.example.kafkaexample.service.userservice.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostgresUserService implements UserService{

    @Autowired
    private PostgresUserRepository postgresUserRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return postgresUserRepository.findAll().stream().map(userMapper::postgresToUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto user) {
        PostgresUser savedUser = new PostgresUser();
        savedUser.setEmail(user.getEmail());
        savedUser.setName(user.getName());

        postgresUserRepository.save(savedUser);

        return userMapper.postgresToUserDto(savedUser);
    }
}
