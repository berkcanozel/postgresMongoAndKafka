package com.example.kafkaexample.service.binance;

import com.example.kafkaexample.data.mongo.entity.KlineData;
import com.example.kafkaexample.data.mongo.repository.BinanceKlineDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BinanceKlineDataScheduler {

    private final BinanceService binanceService;

    private final BinanceKlineDataService binanceKlineDataService;

    private final BinanceKlineDataRepository binanceKlineDataRepository;

    private static final String SYMBOL = "BTCUSDT";
    private static final String INTERVAL = "1m";
    private static final int LIMIT = 1; // Her seferinde son bir veri çekmek için

    @Scheduled(fixedRate = 1000) // Her 1000 milisaniyede bir çalışır (1 saniye)
    public void fetchAndStoreKlineData() {
        try {
            List<KlineData> klineDataList = binanceKlineDataService.fetchAndSaveKlineData(SYMBOL, INTERVAL, true);
            binanceKlineDataRepository.saveAll(klineDataList);
            System.out.println("Kline data inserted at: " + java.time.LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("Error fetching or saving kline data: " + e.getMessage());
        }
    }
}
