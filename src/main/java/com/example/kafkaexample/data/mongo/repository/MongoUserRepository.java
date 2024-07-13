package com.example.kafkaexample.data.mongo.repository;

import com.example.kafkaexample.data.mongo.entity.MongoUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoUserRepository extends MongoRepository<MongoUser, String> {
}
