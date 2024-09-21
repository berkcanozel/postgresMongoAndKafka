package com.example.kafkaexample.data.postgres.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class CustomPostgresUserRepositoryImpl implements CustomPostgresUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void clear() {
        entityManager.clear(); // EntityManager'ı temizler
    }
}

