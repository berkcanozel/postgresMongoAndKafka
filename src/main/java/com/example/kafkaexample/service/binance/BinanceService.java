package com.example.kafkaexample.service.binance;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Service
public class BinanceService {

    private final WebClient webClient;

    public BinanceService() {
        this.webClient = WebClient.create("https://api.binance.com");
    }

    public String getKlineData(String symbol, String interval) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/klines")
                        .queryParam("symbol", symbol)
                        .queryParam("interval", interval)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getKlineData(String symbol, String interval, int limit) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/klines")
                        .queryParam("symbol", symbol)
                        .queryParam("interval", interval)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

