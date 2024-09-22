package com.example.kafkaexample.service.binance;

import com.example.kafkaexample.data.mongo.entity.KlineData;
import com.example.kafkaexample.data.mongo.repository.BinanceKlineDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

@Service
public class BinanceWebSocketService {

    private static final String BINANCE_WS_URL = "wss://stream.binance.com:9443/ws/btcusdt@aggTrade";

    @Autowired
    private BinanceKlineDataRepository klineDataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();

    /**
     * WebSocket bağlantısını başlatır ve gelen veriyi MongoDB'ye kaydeder.
     */
    public void connectWebSocket() {
        client.execute(
                URI.create(BINANCE_WS_URL),
                session -> session.send(Mono.empty()) // Gönderilecek bir mesaj yok
                        .thenMany(session.receive()
                                .map(WebSocketMessage::getPayloadAsText)
                                .map(this::parseKlineData)
                                .filter(klineData -> klineData != null)
                                .doOnNext(this::saveKlineData))
                        .then()
        ).doOnError(e -> {
            System.err.println("WebSocket bağlantısında hata oluştu: " + e.getMessage());
            e.printStackTrace();
            // Bağlantıyı yeniden denemek için gerekli adımları ekleyebilirsiniz
            // Örneğin, belirli bir süre sonra yeniden bağlanmayı deneyin
            Mono.delay(Duration.ofSeconds(5))
                    .doOnNext(aLong -> connectWebSocket())
                    .subscribe();
        }).subscribe();
    }

    /**
     * Gelen WebSocket mesajını parse eder.
     *
     * @param message Gelen JSON mesajı
     * @return KlineData nesnesi
     */
    private KlineData parseKlineData(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            JsonNode k = root.get("k");
            if (k == null) {
                return null;
            }

            KlineData kline = new KlineData();
            kline.setSymbol("BTCUSDT");
            kline.setInterval("1m");
            kline.setOpenTime(k.get("t").asLong());
            kline.setOpen(k.get("o").asDouble());
            kline.setHigh(k.get("h").asDouble());
            kline.setLow(k.get("l").asDouble());
            kline.setClose(k.get("c").asDouble());
            kline.setVolume(k.get("v").asDouble());
            kline.setCloseTime(k.get("T").asLong());

            return kline;
        } catch (Exception e) {
            System.err.println("WebSocket mesajını parse ederken hata oluştu: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * KlineData'yı MongoDB'ye kaydeder.
     *
     * @param klineData KlineData nesnesi
     */
    private void saveKlineData(KlineData klineData) {
        try {
            // MongoDB'de aynı openTime'a sahip bir kayıt varsa, kaydetmeyi atlayın
            if (klineDataRepository.findByOpenTime(klineData.getOpenTime()).isEmpty()) {
                klineDataRepository.save(klineData);
                System.out.println("Gerçek zamanlı Kline verisi kaydedildi: " + klineData.getOpenTime());
            }
        } catch (Exception e) {
            System.err.println("Kline verisini kaydederken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
