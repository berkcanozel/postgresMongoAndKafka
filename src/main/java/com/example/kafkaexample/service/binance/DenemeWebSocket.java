package com.example.kafkaexample.service.binance;


import com.example.kafkaexample.data.mongo.entity.KlineData;
import com.example.kafkaexample.data.mongo.repository.BinanceKlineDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class DenemeWebSocket {

    private static final String BINANCE_WS_URL = "wss://stream.binance.com:9443/ws/btcusdt@aggTrade";

    @Autowired
    private BinanceKlineDataRepository klineDataRepository;

    private List<KlineData> klineDataList = new ArrayList<>();
    private long lastSaveTime = System.currentTimeMillis();
    private double lastClosePrice = 0.0; // Son kapanış fiyatı

    private WebSocketClient client;

    public void start() {
        client = new WebSocketClient(URI.create(BINANCE_WS_URL)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("WebSocket opened");
            }

            @Override
            public void onMessage(String message) {
                collectAggTradeData(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        client.connect();
    }

    public void collectAggTradeData(String jsonMessage) {
        KlineData klineData = convertToKlineData(jsonMessage);

        if (klineData != null) {
            klineDataList.add(klineData);
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSaveTime >= 1000) { // Her 1 saniyede bir kaydet
            saveKlineData(currentTime);
            lastSaveTime = currentTime;
        }
    }

    private void saveKlineData(long currentTime) {
        if (!klineDataList.isEmpty()) {
            KlineData aggregatedData = aggregateKlineData(currentTime);
            try {
                if (klineDataRepository.findByOpenTime(aggregatedData.getOpenTime()).isEmpty()) {
                    klineDataRepository.save(aggregatedData);
                    System.out.println("Gerçek zamanlı Kline verisi kaydedildi: " + aggregatedData.getOpenTime());
                }
            } catch (Exception e) {
                System.err.println("Kline verisini kaydederken hata oluştu: " + e.getMessage());
                e.printStackTrace();
            }
            klineDataList.clear(); // Verileri temizle
        }
    }

    private KlineData aggregateKlineData(long currentTime) {
        if (klineDataList.isEmpty()) {
            return null;
        }

        KlineData aggregatedData = new KlineData();
        aggregatedData.setSymbol(klineDataList.get(0).getSymbol());
        aggregatedData.setOpenTime(currentTime - 1000); // Açılış zamanı, önceki saniye
        aggregatedData.setOpen(lastClosePrice); // Önceki kapanış fiyatı
        aggregatedData.setClose(klineDataList.get(klineDataList.size() - 1).getClose());
        aggregatedData.setVolume(0.0);
        aggregatedData.setHigh(klineDataList.get(0).getHigh());
        aggregatedData.setLow(klineDataList.get(0).getLow());
        aggregatedData.setInterval("1s");

        for (KlineData data : klineDataList) {
            aggregatedData.setVolume(aggregatedData.getVolume() + data.getVolume());
            aggregatedData.setHigh(Math.max(aggregatedData.getHigh(), data.getHigh()));
            aggregatedData.setLow(Math.min(aggregatedData.getLow(), data.getLow()));
        }

        aggregatedData.setCloseTime(currentTime); // Kapanış zamanı

        lastClosePrice = aggregatedData.getClose(); // Son kapanış fiyatını güncelle

        return aggregatedData;
    }

    private KlineData convertToKlineData(String jsonMessage) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonMessage);

            KlineData klineData = new KlineData();
            klineData.setSymbol(jsonNode.get("s").asText());
            klineData.setOpenTime(System.currentTimeMillis()); // Geçerli zaman damgası
            klineData.setOpen(jsonNode.get("p").asDouble()); // İşlem fiyatı
            klineData.setClose(jsonNode.get("p").asDouble()); // İlk başta aynı açılış ve kapanış fiyatı
            klineData.setHigh(jsonNode.get("p").asDouble()); // İlk başta aynı
            klineData.setLow(jsonNode.get("p").asDouble()); // İlk başta aynı
            klineData.setVolume(jsonNode.get("q").asDouble());
            klineData.setCloseTime(System.currentTimeMillis()); // Geçerli kapanış zamanı

            return klineData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
