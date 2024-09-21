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
public class BinanceNewService {

    private static final String BINANCE_BASE_URL = "https://api.binance.com";
    private static final String SYMBOL = "BTCUSDT";
    private static final String INTERVAL = "1m"; // 1 dakika interval
    private static final int LIMIT = 1000; // Binance API'si için maksimum limit

    @Autowired
    private BinanceKlineDataRepository klineDataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private WebClient webClient = WebClient.builder()
            .baseUrl(BINANCE_BASE_URL)
            .build();

    /**
     * Tüm tarihsel veriyi çeker ve MongoDB'ye kaydeder.
     * Başlangıç tarihi 2018-01-01 olarak belirlenmiştir.
     */
    public void fetchAndStoreHistoricalData() {
        try {
            // Mevcut en son zaman damgasını al
            KlineData lastKline = klineDataRepository.findTopBySymbolAndIntervalOrderByCloseTimeDesc(SYMBOL, INTERVAL);
            long startTime = (lastKline != null) ? lastKline.getCloseTime() + 1 :
                    LocalDate.of(2018, 1, 1)
                            .atStartOfDay()
                            .toInstant(ZoneOffset.UTC)
                            .toEpochMilli();
            long endTime = System.currentTimeMillis();
            boolean hasMore = true;

            while (hasMore) {
                List<KlineData> klineDataList = getKlineData(SYMBOL, INTERVAL, LIMIT, startTime, endTime);
                if (klineDataList.isEmpty()) {
                    hasMore = false;
                } else {
                    klineDataRepository.saveAll(klineDataList);
                    System.out.println("Fetched " + klineDataList.size() + " klines starting at " + Instant.ofEpochMilli(startTime));
                    // Son kline'in closeTime'ını bir sonraki startTime olarak ayarla
                    startTime = klineDataList.get(klineDataList.size() - 1).getCloseTime() + 1;
                }
            }

            System.out.println("Tüm tarihsel veriler başarıyla çekildi ve kaydedildi.");
        } catch (Exception e) {
            System.err.println("Tarihsel veri çekerken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Belirli bir zaman aralığında Kline verisini çeker.
     *
     * @param symbol    Ticaret çifti (örn: BTCUSDT)
     * @param interval  Zaman aralığı (örn: 1m, 5m, 1h)
     * @param limit     Çekilecek veri sayısı (maksimum 1000)
     * @param startTime Başlangıç zamanı (milisaniye)
     * @param endTime   Bitiş zamanı (milisaniye)
     * @return KlineData listesi
     */
    private List<KlineData> getKlineData(String symbol, String interval, int limit, long startTime, long endTime) {
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
            System.err.println("Kline verisi çekerken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        return klineDataList;
    }

    /**
     * En son kaydedilen Kline verisini alır.
     *
     * @return En son Kline verisi
     */
    public KlineData getLastKline() {
        return klineDataRepository.findTopBySymbolAndIntervalOrderByCloseTimeDesc(SYMBOL, INTERVAL);
    }
}
