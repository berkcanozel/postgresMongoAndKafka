package com.example.kafkaexample.service.userservice.mapper;

import com.example.kafkaexample.data.mongo.entity.MongoUser;
import com.example.kafkaexample.data.postgres.entity.PostgresUser;
import com.example.kafkaexample.service.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto mongoToUserDto(MongoUser mongoUser);

    UserDto postgresToUserDto(PostgresUser postgresUser);
}
