package com.example.kafkaexample.data.postgres.repository;

import com.example.kafkaexample.data.postgres.entity.PostgresUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostgresUserRepository extends JpaRepository<PostgresUser, Long> {
}
