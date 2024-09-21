package com.example.kafkaexample.data.mongo.repository;

import com.example.kafkaexample.data.mongo.entity.KlineData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BinanceKlineDataRepository extends MongoRepository<KlineData, String> {
    KlineData findTopBySymbolAndIntervalOrderByCloseTimeDesc(String symbol, String interval);
}

