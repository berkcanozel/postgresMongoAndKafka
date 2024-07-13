package com.example.kafkaexample.service.userservice.mapper;

import com.example.kafkaexample.data.mongo.entity.MongoUser;
import com.example.kafkaexample.data.postgres.entity.PostgresUser;
import com.example.kafkaexample.service.dto.UserDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-13T20:08:27+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 20.0.1 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto mongoToUserDto(MongoUser mongoUser) {
        if ( mongoUser == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setName( mongoUser.getName() );
        userDto.setEmail( mongoUser.getEmail() );

        return userDto;
    }

    @Override
    public UserDto postgresToUserDto(PostgresUser postgresUser) {
        if ( postgresUser == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setName( postgresUser.getName() );
        userDto.setEmail( postgresUser.getEmail() );

        return userDto;
    }
}
