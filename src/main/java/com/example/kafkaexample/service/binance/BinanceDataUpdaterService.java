package com.example.kafkaexample.service.binance;

import com.example.kafkaexample.data.mongo.entity.KlineData;
import com.example.kafkaexample.data.mongo.repository.BinanceKlineDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class BinanceDataUpdaterService {

    private static final String SYMBOL = "BTCUSDT";
    private static final String INTERVAL = "1m"; // 1 dakika interval
    private static final int LIMIT = 1000; // Binance API'si için maksimum limit

    @Autowired
    private BinanceKlineDataRepository klineDataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private WebClient webClient = WebClient.builder()
            .baseUrl("https://api.binance.com")
            .build();

    public void updateMissingData() {
        try {
            // Veritabanındaki en son kapanış zamanını al
            KlineData lastKline = klineDataRepository.findTopBySymbolAndIntervalOrderByCloseTimeDesc(SYMBOL, INTERVAL);
            long startTime = (lastKline != null) ? lastKline.getCloseTime() + 1 :
                    LocalDate.of(2018, 1, 1)
                            .atStartOfDay()
                            .toInstant(ZoneOffset.UTC)
                            .toEpochMilli();

            long endTime = System.currentTimeMillis();
            boolean hasMore = true;

            while (hasMore && startTime < endTime) {
                List<KlineData> klineDataList = fetchKlineData(SYMBOL, INTERVAL, LIMIT, startTime, endTime);
                if (klineDataList.isEmpty()) {
                    hasMore = false;
                } else {
                    klineDataRepository.saveAll(klineDataList);
                    System.out.println("Updated " + klineDataList.size() + " klines starting at " + Instant.ofEpochMilli(startTime));
                    startTime = klineDataList.get(klineDataList.size() - 1).getCloseTime() + 1;
                }
            }

            System.out.println("Eksik veriler başarıyla tamamlandı.");
        } catch (Exception e) {
            System.err.println("Eksik veriler tamamlanırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<KlineData> fetchKlineData(String symbol, String interval, int limit, long startTime, long endTime) {
        List<KlineData> klineDataList = new ArrayList<>();
        try {
            JsonNode response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v3/klines")
                            .queryParam("symbol", symbol)
                            .queryParam("interval", interval)
                            .queryParam("limit", limit)
                            .queryParam("startTime", startTime)
                            .queryParam("endTime", endTime)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.isArray()) {
                for (JsonNode node : response) {
                    KlineData kline = new KlineData();
                    kline.setSymbol(symbol);
                    kline.setInterval(interval);
                    kline.setOpenTime(node.get(0).asLong());
                    kline.setOpen(node.get(1).asDouble());
                    kline.setHigh(node.get(2).asDouble());
                    kline.setLow(node.get(3).asDouble());
                    kline.setClose(node.get(4).asDouble());
                    kline.setVolume(node.get(5).asDouble());
                    kline.setCloseTime(node.get(6).asLong());

                    klineDataList.add(kline);
                }
            }
        } catch (Exception e) {
            System.err.println("Veri çekme sırasında hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        return klineDataList;
    }
}
