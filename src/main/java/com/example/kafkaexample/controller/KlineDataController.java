package com.example.kafkaexample.controller;

import com.example.kafkaexample.data.mongo.entity.KlineData;
import com.example.kafkaexample.service.binance.BinanceKlineDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kline")
public class KlineDataController {

    @Autowired
    private BinanceKlineDataService binanceKlineDataService;

    @GetMapping("/fetch")
    public String fetchKlineData(@RequestParam String symbol, @RequestParam String interval) {
        binanceKlineDataService.fetchAndSaveKlineData(symbol, interval);
        return "Veri alındı ve kaydedildi.";
    }

    @GetMapping("/all")
    public List<KlineData> getAllKlineData() {
        return binanceKlineDataService.getAllKlineData();
    }
}

