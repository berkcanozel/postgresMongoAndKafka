package com.example.kafkaexample.data.mongo.repository;

import com.example.kafkaexample.data.mongo.entity.KlineData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BinanceKlineDataRepository extends MongoRepository<KlineData, String> {
    KlineData findTopBySymbolAndIntervalOrderByCloseTimeDesc(String symbol, String interval);

    Optional<KlineData> findByOpenTime(Long openTime);

    List<KlineData> findByOpenTimeBetweenOrderByOpenTimeAsc(long startTime, long endTime);

    List<KlineData> findTop1000ByOrderByOpenTimeDesc();
}

